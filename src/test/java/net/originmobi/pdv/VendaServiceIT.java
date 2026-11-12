package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.repository.ProdutoRepository;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.VendaProdutoService;
import net.originmobi.pdv.service.VendaService;

/**
 * Testes de INTEGRAÇÃO de {@link VendaService}.
 *
 * <p>Diferente dos testes unitários (que isolam o serviço com dublês), aqui o
 * {@code VendaService} roda com os componentes REAIS — repositórios JPA,
 * serviços colaboradores e o banco MySQL com as migrações Flyway e os
 * <b>triggers</b>. O objetivo é verificar que as peças conversam: mapeamento
 * JPA, SQL gerado, transações e as regras implementadas no próprio banco.
 *
 * <p>Pré-requisito: o banco {@code pdv-db} (MySQL via Docker) precisa estar no
 * ar. Cada teste roda em {@link Transactional}, portanto sofre rollback ao fim
 * e não polui o banco.
 *
 * <p>Perspectiva de testador: as assercoes refletem o contrato esperado.
 * Nenhum defeito do código legado é corrigido.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class VendaServiceIT {

    private static final String USUARIO_LOGADO = "gerente";

    @Autowired
    private VendaService vendaService;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private VendaProdutoService vendaProdutoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void autenticaUsuario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USUARIO_LOGADO, "x"));
    }

    @AfterEach
    void limpaContexto() {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------
    // 1. abreVenda (venda nova) persiste de fato no banco
    // ------------------------------------------------------------------

    @Test
    @DisplayName("IT: abreVenda deve persistir a venda no banco com situacao ABERTA e codigo gerado")
    void abreVenda_devePersistirNoBanco() {
        Venda venda = new Venda();

        Long codigo = vendaService.abreVenda(venda);

        assertNotNull(codigo, "o banco deve gerar o codigo da venda");

        Optional<Venda> persistida = vendaRepository.findById(codigo);
        assertTrue(persistida.isPresent(), "a venda deve existir no banco apos abreVenda");
        assertEquals(VendaSituacao.ABERTA, persistida.get().getSituacao(),
                "venda nova deve ser gravada como ABERTA");
        assertEquals(USUARIO_LOGADO, persistida.get().getUsuario().getUser(),
                "a venda deve ser vinculada ao usuario autenticado");
    }

    // ------------------------------------------------------------------
    // 2. abreVenda (venda existente) atualiza os dados via updateDadosVenda
    // ------------------------------------------------------------------

    @Test
    @DisplayName("IT: abreVenda em venda existente deve atualizar a observacao no banco")
    void abreVenda_vendaExistente_deveAtualizarObservacao() {
        Long codigo = vendaService.abreVenda(new Venda());

        Venda existente = vendaRepository.findById(codigo).get();
        existente.setObservacao("observacao atualizada IT");

        vendaService.abreVenda(existente);

        Venda recarregada = vendaRepository.findById(codigo).get();
        assertEquals("observacao atualizada IT", recarregada.getObservacao(),
                "updateDadosVenda deve ter persistido a nova observacao");
    }

    // ------------------------------------------------------------------
    // 3. busca e qtdAbertos refletem o estado real do banco
    // ------------------------------------------------------------------

    @Test
    @DisplayName("IT: busca por situacao ABERTA e qtdAbertos devem refletir vendas gravadas")
    void busca_e_qtdAbertos_refletemBanco() {
        int abertosAntes = vendaService.qtdAbertos();

        // grava duas vendas novas (ambas ABERTA)
        vendaService.abreVenda(new Venda());
        vendaService.abreVenda(new Venda());

        int abertosDepois = vendaService.qtdAbertos();
        assertEquals(abertosAntes + 2, abertosDepois,
                "qtdAbertos deve contar as 2 novas vendas em aberto");

        Page<Venda> abertas = vendaService.busca(
                new VendaFilter(), "ABERTA", PageRequest.of(0, 50));
        assertTrue(abertas.getTotalElements() >= 2,
                "a busca por ABERTA deve retornar ao menos as 2 vendas criadas");
        assertTrue(abertas.getContent().stream()
                        .allMatch(v -> v.getSituacao() == VendaSituacao.ABERTA),
                "todas as vendas retornadas devem estar ABERTA");
    }

    // ------------------------------------------------------------------
    // 4. addProduto dispara o trigger tr_atualizaTotalProdutosVenda:
    //    o valor_produtos da venda é atualizado pelo BANCO, não pelo Java
    // ------------------------------------------------------------------

    @Test
    @DisplayName("IT: addProduto deve atualizar valor_produtos via trigger do banco")
    void addProduto_deveAtualizarTotalViaTrigger() {
        // usa um produto real do seed (vendável)
        Produto produto = produtoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "seed sem produtos — verifique as migracoes Flyway"));

        Long codigoVenda = vendaService.abreVenda(new Venda());

        Double totalAntes = vendaRepository.verificaValorProdutos(codigoVenda);
        assertEquals(0.00, totalAntes, 0.0001, "venda nova deve iniciar com total 0.00");

        String retorno = vendaService.addProduto(codigoVenda, produto.getCodigo(), 0.0);
        assertEquals("ok", retorno, "addProduto em venda aberta deve retornar ok");

        Double totalDepois = vendaRepository.verificaValorProdutos(codigoVenda);
        assertEquals(totalAntes + produto.getValor_venda(), totalDepois, 0.0001,
                "o trigger deve ter somado o valor_venda do produto ao total da venda");
    }
}
