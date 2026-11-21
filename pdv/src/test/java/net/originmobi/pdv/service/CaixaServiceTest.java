package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;

import static org.junit.Assert.assertNotNull;
import java.util.NoSuchElementException;
import org.mockito.ArgumentCaptor;

public class CaixaServiceTest {

    @Mock private CaixaRepository caixas;
    @Mock private UsuarioService usuarios;
    @Mock private CaixaLancamentoService lancamentos;

    @InjectMocks private CaixaService service;

    private AutoCloseable closeable;
    private Usuario usuario;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("gerente", "senha"));
        usuario = new Usuario();
        usuario.setCodigo(7L);
        usuario.setSenha(new BCryptPasswordEncoder(4).encode("correta"));
        when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        closeable.close();
    }

    @Test
    public void naoAbreSegundoCaixaDiario() {
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

        expect("Existe caixa de dias anteriores em aberto, favor verifique",
                () -> service.cadastro(caixa(CaixaTipo.CAIXA, "", 0.0)));
    }

    @Test
    public void abreCaixaSemValorUsandoPadroes() {
        Caixa caixa = caixa(CaixaTipo.CAIXA, "", null);
        caixa.setCodigo(10L);
        when(caixas.caixaAberto()).thenReturn(Optional.empty());

        assertEquals(Long.valueOf(10L), service.cadastro(caixa));
        assertEquals("Caixa diário", caixa.getDescricao());
        assertEquals(Double.valueOf(0.0), caixa.getValor_abertura());
        assertEquals(Double.valueOf(0.0), caixa.getValor_total());
        assertSame(usuario, caixa.getUsuario());
        verify(caixas).save(caixa);
    }

    @Test
    public void rejeitaValorDeAberturaNegativo() {
        when(caixas.caixaAberto()).thenReturn(Optional.empty());

        expect("Valor informado é inválido",
                () -> service.cadastro(caixa(CaixaTipo.CAIXA, "", -0.01)));
    }

    @Test
    public void abreCaixaComDescricaoELancamento() {
        Caixa caixa = caixa(CaixaTipo.CAIXA, "Turno manhã", 20.0);
        when(caixas.caixaAberto()).thenReturn(Optional.empty());

        service.cadastro(caixa);

        assertEquals("Turno manhã", caixa.getDescricao());
        verify(lancamentos).lancamento(any(CaixaLancamento.class));
    }

    @Test
    public void abreCofreComDescricaoPadraoELancamento() {
        Caixa caixa = caixa(CaixaTipo.COFRE, "", 20.0);

        service.cadastro(caixa);

        assertEquals("Cofre", caixa.getDescricao());
        verify(lancamentos).lancamento(any(CaixaLancamento.class));
    }

    @Test
    public void abreBancoELimpaAgenciaEConta() {
        Caixa caixa = caixa(CaixaTipo.BANCO, "", 20.0);
        caixa.setAgencia("123-4");
        caixa.setConta("99.888-7");

        service.cadastro(caixa);

        assertEquals("Banco", caixa.getDescricao());
        assertEquals("1234", caixa.getAgencia());
        assertEquals("998887", caixa.getConta());
        verify(lancamentos).lancamento(any(CaixaLancamento.class));
    }

    @Test
    public void preservaDescricaoPersonalizadaDoBanco() {
        Caixa caixa = caixa(CaixaTipo.BANCO, "Conta principal", 0.0);
        caixa.setAgencia("1");
        caixa.setConta("2");

        service.cadastro(caixa);

        assertEquals("Conta principal", caixa.getDescricao());
    }

    @Test
    public void informaFalhaAoSalvarCaixa() {
        Caixa caixa = caixa(CaixaTipo.COFRE, "Principal", 0.0);
        doThrow(new RuntimeException("falha")).when(caixas).save(caixa);

        expect("Erro no processo de abertura, chame o suporte técnico", () -> service.cadastro(caixa));
    }

    @Test
    public void informaFalhaAoLancarSaldoInicial() {
        Caixa caixa = caixa(CaixaTipo.COFRE, "Principal", 20.0);
        doThrow(new RuntimeException("falha")).when(lancamentos).lancamento(any(CaixaLancamento.class));

        expect("Erro no processo, chame o suporte", () -> service.cadastro(caixa));
    }

    @Test
    public void pedeSenhaParaFecharCaixa() {
        assertEquals("Favor, informe a senha", service.fechaCaixa(1L, ""));
    }

    @Test
    public void rejeitaSenhaIncorreta() {
        assertEquals("Senha incorreta, favor verifique", service.fechaCaixa(1L, "errada"));
    }

    @Test
    public void naoFechaCaixaJaFechado() {
        Caixa caixa = new Caixa();
        caixa.setData_fechamento(new Timestamp(System.currentTimeMillis()));
        when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

        expect("Caixa já esta fechado", () -> service.fechaCaixa(1L, "correta"));
    }

    @Test
    public void fechaCaixaSemMovimentoComTotalZero() {
        Caixa caixa = new Caixa();
        when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

        assertEquals("Caixa fechado com sucesso", service.fechaCaixa(1L, "correta"));
        assertEquals(Double.valueOf(0.0), caixa.getValor_fechamento());
        assertTrue(caixa.getData_fechamento() != null);
    }

    @Test
    public void fechaCaixaComValorTotal() {
        Caixa caixa = new Caixa();
        caixa.setValor_total(87.50);
        when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

        assertEquals("Caixa fechado com sucesso", service.fechaCaixa(1L, "correta"));
        assertEquals(Double.valueOf(87.50), caixa.getValor_fechamento());
    }

    @Test
    public void informaFalhaAoSalvarFechamento() {
        Caixa caixa = new Caixa();
        when(caixas.findById(1L)).thenReturn(Optional.of(caixa));
        doThrow(new RuntimeException("falha")).when(caixas).save(caixa);

        expect("Ocorreu um erro ao fechar o caixa, chame o suporte",
                () -> service.fechaCaixa(1L, "correta"));
    }

    @Test
    public void consultaEstadoEListasDeCaixa() {
        Caixa caixa = new Caixa();
        List<Caixa> lista = Collections.singletonList(caixa);
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));
        when(caixas.findByCodigoOrdenado()).thenReturn(lista);
        when(caixas.caixasAbertos()).thenReturn(lista);
        when(caixas.findById(1L)).thenReturn(Optional.of(caixa));
        when(caixas.buscaBancos(CaixaTipo.BANCO)).thenReturn(lista);
        when(caixas.buscaCaixaTipo(CaixaTipo.COFRE)).thenReturn(lista);

        assertTrue(service.caixaIsAberto());
        assertSame(lista, service.listaTodos());
        assertEquals(Optional.of(caixa), service.caixaAberto());
        assertSame(lista, service.caixasAbertos());
        assertEquals(Optional.of(caixa), service.busca(1L));
        assertSame(lista, service.listaBancos());
        assertSame(lista, service.listaCaixasAbertosTipo(CaixaTipo.COFRE));
    }

    @Test
    public void informaQuandoNaoExisteCaixaAberto() {
        when(caixas.caixaAberto()).thenReturn(Optional.empty());

        assertFalse(service.caixaIsAberto());
    }

    @Test
    public void listaCaixasPelaDataInformada() {
        CaixaFilter filter = new CaixaFilter();
        filter.setData_cadastro("2024/05/10");
        List<Caixa> lista = Arrays.asList(new Caixa(), new Caixa());
        when(caixas.buscaCaixasPorDataAbertura(java.sql.Date.valueOf("2024-05-10"))).thenReturn(lista);

        assertSame(lista, service.listarCaixas(filter));
        assertEquals("2024-05-10", filter.getData_cadastro());
    }

    @Test
    public void listaCaixasAbertosSemData() {
        List<Caixa> lista = Collections.singletonList(new Caixa());
        when(caixas.listaCaixasAbertos()).thenReturn(lista);

        CaixaFilter nulo = new CaixaFilter();
        assertSame(lista, service.listarCaixas(nulo));
        CaixaFilter vazio = new CaixaFilter();
        vazio.setData_cadastro("");
        assertSame(lista, service.listarCaixas(vazio));
    }

    @Test
    public void buscaCaixaAbertoDoUsuario() {
        Caixa caixa = new Caixa();
        when(usuarios.buscaUsuario("maria")).thenReturn(usuario);
        when(caixas.findByCaixaAbertoUsuario(7L)).thenReturn(caixa);

        assertEquals(Optional.of(caixa), service.buscaCaixaUsuario("maria"));
        when(caixas.findByCaixaAbertoUsuario(7L)).thenReturn(null);
        assertEquals(Optional.empty(), service.buscaCaixaUsuario("maria"));
    }

    @Test
    public void listaBancosPelaDataInformada() {
        BancoFilter filter = new BancoFilter();
        filter.setData_cadastro("2024/05/10");
        List<Caixa> lista = Collections.singletonList(new Caixa());
        when(caixas.buscaCaixaTipoData(CaixaTipo.BANCO, java.sql.Date.valueOf("2024-05-10"))).thenReturn(lista);

        assertSame(lista, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter));
        assertEquals("2024-05-10", filter.getData_cadastro());
    }

    @Test
    public void listaBancosAbertosSemData() {
        List<Caixa> lista = Collections.singletonList(new Caixa());
        when(caixas.buscaCaixaTipo(CaixaTipo.BANCO)).thenReturn(lista);

        BancoFilter nulo = new BancoFilter();
        assertSame(lista, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.COFRE, nulo));
        BancoFilter vazio = new BancoFilter();
        vazio.setData_cadastro("");
        assertSame(lista, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.COFRE, vazio));
        verify(caixas, times(2)).buscaCaixaTipo(eq(CaixaTipo.BANCO));
    }

    // Novos Testes para Cobertura de Código e matar mutantes restantes
    @Test
    public void gravaDataDeCadastroAoAbrirCaixa() {
        Caixa caixa = caixa(CaixaTipo.CAIXA, "", 0.0);
        when(caixas.caixaAberto()).thenReturn(Optional.empty());

        service.cadastro(caixa);

        assertNotNull(caixa.getData_cadastro());
    }

    @Test
    public void observacaoDoSaldoInicialDeCaixa() {
        Caixa caixa = caixa(CaixaTipo.CAIXA, "", 20.0);
        when(caixas.caixaAberto()).thenReturn(Optional.empty());

        assertEquals("Abertura de caixa", observacaoDoLancamento(caixa));
    }

    @Test
    public void observacaoDoSaldoInicialDeCofre() {
        assertEquals("Abertura de cofre", observacaoDoLancamento(caixa(CaixaTipo.COFRE, "", 20.0)));
    }

    @Test
    public void observacaoDoSaldoInicialDeBanco() {
        Caixa caixa = caixa(CaixaTipo.BANCO, "", 20.0);
        caixa.setAgencia("1");
        caixa.setConta("2");

        assertEquals("Abertura de banco", observacaoDoLancamento(caixa));
    }

    @Test(expected = NoSuchElementException.class)
    public void fechaCaixaInexistenteLancaNoSuchElement() {
        when(caixas.findById(99L)).thenReturn(Optional.empty());

        service.fechaCaixa(99L, "correta");
    }

    // Métodos auxiliares para os testes, evitando repetição de código
    private Caixa caixa(CaixaTipo tipo, String descricao, Double abertura) {
        Caixa caixa = new Caixa();
        caixa.setTipo(tipo);
        caixa.setDescricao(descricao);
        caixa.setValor_abertura(abertura);
        return caixa;
    }

    private String observacaoDoLancamento(Caixa caixa) {
        ArgumentCaptor<CaixaLancamento> captor = ArgumentCaptor.forClass(CaixaLancamento.class);
        service.cadastro(caixa);
        verify(lancamentos).lancamento(captor.capture());
        return captor.getValue().getObservacao();
    }

    private void expect(String mensagem, ThrowingRunnable acao) {
        try {
            acao.run();
            fail("Era esperada uma RuntimeException");
        } catch (RuntimeException e) {
            assertEquals(mensagem, e.getMessage());
        }
    }

    private interface ThrowingRunnable {
        void run();
    }
}
