package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import net.originmobi.pdv.repository.CaixaRepository;
import net.originmobi.pdv.service.CaixaException;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.UsuarioService;

/**
 * Testes de INTEGRAÇÃO de {@link CaixaService}.
 *
 * <p>O serviço roda com os componentes reais (repositórios JPA,
 * {@code CaixaLancamentoService}, BCrypt e o MySQL com o trigger
 * {@code tr_atualizaValoresCaixa_AFTER_INSERT}). Cada teste roda em
 * {@link Transactional} e sofre rollback ao fim, sem poluir o banco.
 *
 * <p>Pré-requisito: {@code docker compose up -d} (banco {@code pdv-db}).
 *
 * <p>{@code @ExtendWith} é necessário: no Spring Boot 2.0 o {@code @SpringBootTest}
 * ainda não registra a {@code SpringExtension} (isso só veio na 2.1).
 */
@ExtendWith(SpringExtension.class) // NOSONAR java:S9341
@SpringBootTest
@Transactional
class CaixaServiceIT {

    private static final String USUARIO = "gerente";
    private static final String SENHA = "123";

    @Autowired
    private CaixaService caixaService;

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private CaixaLancamentoRepository lancamentoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void preparaCenario() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USUARIO, "x"));

        // Garante que não há CAIXA aberto vindo do uso manual do sistema
        // (alteração desfeita pelo rollback ao fim do teste).
        caixaRepository.caixaAberto().ifPresent(c -> {
            c.setData_fechamento(new Timestamp(System.currentTimeMillis()));
            caixaRepository.saveAndFlush(c);
        });
    }

    @AfterEach
    void limpaContexto() {
        SecurityContextHolder.clearContext();
    }

    private static Caixa novoCaixa(CaixaTipo tipo, Double valorAbertura) {
        Caixa caixa = new Caixa();
        caixa.setTipo(tipo);
        caixa.setDescricao("");
        caixa.setValor_abertura(valorAbertura);
        return caixa;
    }

    /** Envia as alterações ao banco e descarta o cache do JPA, para reler o que o MySQL gravou. */
    private void sincronizaComBanco() {
        em.flush();
        em.clear();
    }

    // ------------------------------------------------------------------
    // Abertura (cadastro)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("1. Abrir CAIXA com valor grava o caixa e o lançamento de saldo inicial; o trigger atualiza o total")
    void abrirCaixaComValor_gravaCaixaELancamento() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 150.0));
        sincronizaComBanco();

        Caixa salvo = caixaRepository.findById(codigo).orElseThrow(AssertionError::new);
        assertEquals("Caixa diário", salvo.getDescricao());
        assertEquals(USUARIO, salvo.getUsuario().getUser());
        assertEquals(Date.valueOf(LocalDate.now(ZoneId.of("America/Sao_Paulo"))), salvo.getData_cadastro());
        assertNull(salvo.getData_fechamento());
        assertEquals(150.0, salvo.getValor_total(), 0.001, "trigger deveria somar o saldo inicial ao total");

        List<CaixaLancamento> lancamentos = lancamentoRepository.findByCaixaEquals(salvo);
        assertEquals(1, lancamentos.size());
        CaixaLancamento lanc = lancamentos.get(0);
        assertEquals(TipoLancamento.SALDOINICIAL, lanc.getTipo());
        assertEquals(EstiloLancamento.ENTRADA, lanc.getEstilo());
        assertEquals(150.0, lanc.getValor(), 0.001);
        assertEquals("Abertura de caixa", lanc.getObservacao());
    }

    @Test
    @DisplayName("2. Abrir COFRE e BANCO grava descrição padrão e limpa agência/conta do banco")
    void abrirCofreEBanco_gravaDadosPadrao() {
        Long cofre = caixaService.cadastro(novoCaixa(CaixaTipo.COFRE, 50.0));

        Caixa banco = novoCaixa(CaixaTipo.BANCO, 0.0);
        banco.setAgencia("12.34-5");
        banco.setConta("678.90-1");
        Long codBanco = caixaService.cadastro(banco);
        sincronizaComBanco();

        Caixa cofreSalvo = caixaRepository.findById(cofre).orElseThrow(AssertionError::new);
        assertEquals("Cofre", cofreSalvo.getDescricao());
        assertEquals("Abertura de cofre", lancamentoRepository.findByCaixaEquals(cofreSalvo).get(0).getObservacao());

        Caixa bancoSalvo = caixaRepository.findById(codBanco).orElseThrow(AssertionError::new);
        assertEquals("Banco", bancoSalvo.getDescricao());
        assertEquals("12345", bancoSalvo.getAgencia());
        assertEquals("678901", bancoSalvo.getConta());
    }

    @Test
    @DisplayName("3. Abrir caixa sem valor inicial grava o caixa e nenhum lançamento")
    void abrirCaixaSemValor_naoCriaLancamento() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, null));
        sincronizaComBanco();

        Caixa salvo = caixaRepository.findById(codigo).orElseThrow(AssertionError::new);
        assertEquals(0.0, salvo.getValor_abertura(), 0.001);
        assertTrue(lancamentoRepository.findByCaixaEquals(salvo).isEmpty());
    }

    @Test
    @DisplayName("4. Com CAIXA de dia anterior aberto, nova abertura é bloqueada e nada é gravado")
    void abrirComCaixaAnteriorAberto_lancaExcecao() {
        Caixa antigo = new Caixa("Caixa antigo", CaixaTipo.CAIXA, 0.0, 0.0, null,
                Date.valueOf(LocalDate.now().minusDays(1)), null, usuarioService.buscaUsuario(USUARIO));
        caixaRepository.saveAndFlush(antigo);
        long antes = caixaRepository.count();
        Caixa novo = novoCaixa(CaixaTipo.CAIXA, 10.0);

        CaixaException ex = assertThrows(CaixaException.class, () -> caixaService.cadastro(novo));

        assertEquals("Existe caixa de dias anteriores em aberto, favor verifique", ex.getMessage());
        assertEquals(antes, caixaRepository.count());
    }

    @Test
    @DisplayName("5. Valor de abertura negativo é rejeitado e nada é gravado")
    void abrirComValorNegativo_lancaExcecao() {
        long antes = caixaRepository.count();
        Caixa negativo = novoCaixa(CaixaTipo.CAIXA, -1.0);

        CaixaException ex = assertThrows(CaixaException.class, () -> caixaService.cadastro(negativo));

        assertEquals("Valor informado é inválido", ex.getMessage());
        assertEquals(antes, caixaRepository.count());
    }

    // ------------------------------------------------------------------
    // Fechamento (fechaCaixa)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("6. Fechar com a senha correta grava data e valor de fechamento (total vindo do trigger)")
    void fecharComSenhaCorreta_gravaFechamento() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 150.0));
        sincronizaComBanco();

        assertEquals("Caixa fechado com sucesso", caixaService.fechaCaixa(codigo, SENHA));
        sincronizaComBanco();

        Caixa fechado = caixaRepository.findById(codigo).orElseThrow(AssertionError::new);
        assertNotNull(fechado.getData_fechamento());
        assertEquals(150.0, fechado.getValor_fechamento(), 0.001);
    }

    @Test
    @DisplayName("7. Fechar com senha errada devolve mensagem e o caixa continua aberto")
    void fecharComSenhaErrada_mantemAberto() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 20.0));
        sincronizaComBanco();

        assertEquals("Senha incorreta, favor verifique", caixaService.fechaCaixa(codigo, "senha-errada"));
        sincronizaComBanco();

        assertNull(caixaRepository.findById(codigo).orElseThrow(AssertionError::new).getData_fechamento());
    }

    @Test
    @DisplayName("8. Fechar um caixa já fechado lança exceção")
    void fecharCaixaJaFechado_lancaExcecao() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 20.0));
        sincronizaComBanco();
        caixaService.fechaCaixa(codigo, SENHA);
        sincronizaComBanco();

        CaixaException ex = assertThrows(CaixaException.class, () -> caixaService.fechaCaixa(codigo, SENHA));
        assertEquals("Caixa já esta fechado", ex.getMessage());
    }

    // ------------------------------------------------------------------
    // Consultas (queries reais do CaixaRepository)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("9. caixaAberto e buscaCaixaUsuario encontram o caixa aberto e deixam de encontrar após o fechamento")
    void consultasDeCaixaAberto_refletemAberturaEFechamento() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 10.0));
        sincronizaComBanco();

        assertTrue(caixaService.caixaIsAberto());
        assertEquals(codigo, caixaService.caixaAberto().orElseThrow(AssertionError::new).getCodigo());
        assertEquals(codigo, caixaService.buscaCaixaUsuario(USUARIO).orElseThrow(AssertionError::new).getCodigo());

        caixaService.fechaCaixa(codigo, SENHA);
        sincronizaComBanco();

        assertFalse(caixaService.caixaIsAberto());
        assertFalse(caixaService.buscaCaixaUsuario(USUARIO).isPresent());
    }

    @Test
    @DisplayName("10. listaCaixasAbertosTipo(COFRE) traz apenas cofres abertos")
    void listaCaixasAbertosTipo_filtraPorTipo() {
        Long cofre = caixaService.cadastro(novoCaixa(CaixaTipo.COFRE, 0.0));
        caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 0.0));
        sincronizaComBanco();

        List<Caixa> cofres = caixaService.listaCaixasAbertosTipo(CaixaTipo.COFRE);

        assertTrue(cofres.stream().anyMatch(c -> c.getCodigo().equals(cofre)));
        assertTrue(cofres.stream().allMatch(c -> c.getTipo() == CaixaTipo.COFRE && c.getData_fechamento() == null));
    }

    /**
     * DEFEITO: a query usa {@code like} sobre a data e o Hibernate envia o
     * parâmetro como TIMESTAMP ({@code '2026-11-08 00:00:00'}), que nunca casa
     * com a coluna DATE. O filtro por data sempre volta vazio.
     */
    @Test
    @Tag("defeito")
    @DisplayName("11. listarCaixas com data de abertura traz os caixas abertos naquele dia")
    void listarCaixasPorData_trazCaixasDoDia() {
        Long codigo = caixaService.cadastro(novoCaixa(CaixaTipo.CAIXA, 0.0));
        sincronizaComBanco();

        CaixaFilter filtro = new CaixaFilter();
        filtro.setData_cadastro(LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString());

        List<Caixa> doDia = caixaService.listarCaixas(filtro);

        assertTrue(doDia.stream().anyMatch(c -> c.getCodigo().equals(codigo)));
    }

    /**
     * DEFEITO: {@code buscaBancos} é uma native query que seleciona só
     * {@code b.codigo}, mas declara retorno {@code List<Caixa>}; o Hibernate
     * não consegue montar a entidade ("Column 'descricao' not found").
     */
    @Test
    @Tag("defeito")
    @DisplayName("12. listaBancos devolve os bancos abertos como objetos Caixa")
    void listaBancos_devolveCaixasDoTipoBanco() {
        Caixa banco = novoCaixa(CaixaTipo.BANCO, 0.0);
        banco.setAgencia("1");
        banco.setConta("2");
        Long codigo = caixaService.cadastro(banco);
        sincronizaComBanco();

        List<Caixa> bancos = caixaService.listaBancos();

        assertFalse(bancos.isEmpty());
        assertTrue(bancos.stream().anyMatch(c -> c.getCodigo().equals(codigo)));
        assertTrue(bancos.stream().allMatch(c -> c.getTipo() == CaixaTipo.BANCO));
    }
}
