package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.ajuste.AjusteStatus;
import net.originmobi.pdv.model.Ajuste;
import net.originmobi.pdv.repository.AjusteProdutoRepository;
import net.originmobi.pdv.service.AjusteException;
import net.originmobi.pdv.service.AjusteService;

/**
 * Testes de INTEGRAÇÃO de {@link AjusteService}.
 *
 * <p>Diferente de {@code AjusteServiceTest} (que isola o serviço com mocks),
 * aqui o {@code AjusteService} roda com os componentes REAIS: o
 * {@code ProdutoService}, os repositórios JPA e o banco MySQL com as migrações
 * Flyway e os <b>triggers</b> de estoque. O objetivo é verificar que as peças
 * conversam: gravação do ajuste, registro da movimentação de estoque e
 * atualização do saldo do produto, feita pelo próprio banco.
 *
 * <p>Pré-requisito: o banco {@code pdv-db} (MySQL via Docker) precisa estar no
 * ar. Cada teste roda em {@link Transactional}, portanto sofre rollback ao fim
 * e não polui o banco.
 *
 * <p>Os testes marcados com {@code @Tag("defeito")} asserem o comportamento
 * esperado (o saldo deve aumentar numa entrada e diminuir numa saída) e falham
 * de propósito, evidenciando o defeito do trigger de estoque. Nenhum defeito é
 * corrigido.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AjusteServiceIT {

    private static final String USUARIO_LOGADO = "gerente";

    @Autowired
    private AjusteService ajusteService;

    @Autowired
    private AjusteProdutoRepository ajusteProdutoRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @PersistenceContext
    private EntityManager em;

    /** Produto "Picolé" da carga inicial (controla estoque). */
    private Long codPicole;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USUARIO_LOGADO, "x"));
        codPicole = jdbc.queryForObject(
                "select codigo from produto where descricao like 'Picol%'", Long.class);
    }

    @AfterEach
    void limpaContexto() {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void defineSaldo(Long codProduto, int qtd) {
        jdbc.update("update produto_estoque set qtd = ? where produto_codigo = ?", qtd, codProduto);
    }

    private int saldo(Long codProduto) {
        return jdbc.queryForObject(
                "select qtd from produto_estoque where produto_codigo = ?", Integer.class, codProduto);
    }

    /**
     * Cria um ajuste pelo próprio serviço e inclui um item, do mesmo jeito que a
     * tela faz (via {@code AjusteProdutoRepository.insereProduto}). Ao final
     * limpa o contexto do JPA, para que o {@code processar} leia o ajuste e os
     * itens direto do banco.
     */
    private Long novoAjusteComItem(Long codProduto, int qtdAlteracao) {
        Long codAjuste = ajusteService.novo();
        int estoqueAtual = saldo(codProduto);
        ajusteProdutoRepository.insereProduto(codAjuste, codProduto, estoqueAtual, qtdAlteracao,
                estoqueAtual + qtdAlteracao);
        em.flush();
        em.clear();
        return codAjuste;
    }

    // ------------------------------------------------------------------
    // 1. novo persiste de fato no banco
    // ------------------------------------------------------------------

    @Test
    @DisplayName("novo: deve gravar no banco um ajuste A PROCESSAR com o usuário logado")
    void novo_DevePersistirAjusteAProcessar() {
        Long codigo = ajusteService.novo();
        em.flush();
        em.clear();

        Optional<Ajuste> salvo = ajusteService.busca(codigo);

        assertTrue(salvo.isPresent());
        assertEquals(AjusteStatus.APROCESSAR, salvo.get().getStatus());
        assertEquals(USUARIO_LOGADO, salvo.get().getUsuario());
        assertNotNull(salvo.get().getData_cadastro());
    }

    // ------------------------------------------------------------------
    // 2. processar grava o status e a movimentação de estoque
    // ------------------------------------------------------------------

    @Test
    @DisplayName("processar: deve marcar o ajuste como PROCESSADO e registrar a movimentação de ENTRADA")
    void processar_DeveGravarStatusEMovimentacao() {
        defineSaldo(codPicole, 10);
        Long codAjuste = novoAjusteComItem(codPicole, 5);

        String resultado = ajusteService.processar(codAjuste, "Inventario mensal");
        em.flush();
        em.clear();

        assertEquals("Ajuste realizado com sucesso", resultado);
        Ajuste processado = ajusteService.busca(codAjuste).get();
        assertEquals(AjusteStatus.PROCESSADO, processado.getStatus());
        assertEquals("Inventario mensal", processado.getObservacao());
        assertNotNull(processado.getData_processamento());

        Integer movimentacoes = jdbc.queryForObject(
                "select count(*) from estoque_movimentacao "
                        + "where produto_codigo = ? and tipo = 'ENTRADA' and qtd = 5 and origem_operacao = ?",
                Integer.class, codPicole, "Referente ao ajuste de estoque " + codAjuste);
        assertEquals(1, movimentacoes);
    }

    // ------------------------------------------------------------------
    // 3 e 4. saldo do produto, atualizado pelo trigger do banco
    // ------------------------------------------------------------------

    @Test
    @Tag("defeito")
    @DisplayName("processar: ajuste de ENTRADA (+5) deve aumentar o saldo de 10 para 15 [DEFEITO: trigger subtrai]")
    void processar_Entrada_DeveAumentarSaldo() {
        defineSaldo(codPicole, 10);
        Long codAjuste = novoAjusteComItem(codPicole, 5);

        ajusteService.processar(codAjuste, "Entrada");

        assertEquals(15, saldo(codPicole),
                "Uma entrada de 5 unidades deveria levar o saldo de 10 para 15");
    }

    @Test
    @Tag("defeito")
    @DisplayName("processar: ajuste de SAIDA (-3) deve diminuir o saldo de 10 para 7 [DEFEITO: saldo aumenta]")
    void processar_Saida_DeveDiminuirSaldo() {
        defineSaldo(codPicole, 10);
        Long codAjuste = novoAjusteComItem(codPicole, -3);

        ajusteService.processar(codAjuste, "Saida");

        assertEquals(7, saldo(codPicole),
                "Uma saída de 3 unidades deveria levar o saldo de 10 para 7");
    }

    // ------------------------------------------------------------------
    // 5. regras de negócio contra o estado real do banco
    // ------------------------------------------------------------------

    @Test
    @DisplayName("processar: ajuste já processado no banco não pode ser processado de novo")
    void processar_DuasVezes_DeveRecusar() {
        defineSaldo(codPicole, 10);
        Long codAjuste = novoAjusteComItem(codPicole, 1);
        ajusteService.processar(codAjuste, "Primeira vez");
        em.flush();
        em.clear();

        AjusteException erro = assertThrows(AjusteException.class,
                () -> ajusteService.processar(codAjuste, "Segunda vez"));

        assertEquals("Ajuste já processado", erro.getMessage());
    }

    @Test
    @DisplayName("processar: produto que não controla estoque deve ser recusado pelo ProdutoService real")
    void processar_ProdutoSemControleDeEstoque_DeveRecusar() {
        Long codSorvete = jdbc.queryForObject(
                "select codigo from produto where descricao like 'Sorvete%'", Long.class);
        jdbc.update("update produto set controla_estoque = 'NAO' where codigo = ?", codSorvete);
        Long codAjuste = novoAjusteComItem(codSorvete, 2);

        AjusteException erro = assertThrows(AjusteException.class,
                () -> ajusteService.processar(codAjuste, "Produto sem controle"));

        assertEquals("Erro ao tentar processar o ajuste, chame o suporte", erro.getMessage());
    }

    @Test
    @DisplayName("remover: ajuste A PROCESSAR deve ser excluído do banco")
    void remover_AjusteAProcessar_DeveExcluirDoBanco() {
        Long codigo = ajusteService.novo();
        em.flush();
        em.clear();

        ajusteService.remover(ajusteService.busca(codigo).get());
        em.flush();
        em.clear();

        assertFalse(ajusteService.busca(codigo).isPresent());
    }
}