package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;

@RunWith(MockitoJUnitRunner.class)
public class RecebimentoServiceTest {

    private static final double DELTA = 0.000001;
    private static final Long COD_RECEBIMENTO = 10L;
    private static final Long COD_TITULO = 1L;

    @InjectMocks
    private RecebimentoService recebimentos;

    @Mock
    private RecebimentoRepository recebimentoRepository;

    @Mock
    private PessoaService pessoas;

    @Mock
    private RecebimentoParcelaService receParcelas;

    @Mock
    private ParcelaService parcelas;

    @Mock
    private CaixaService caixas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private TituloService titulos;

    @Mock
    private CartaoLancamentoService cartaoLancamentos;

    private Locale localeOriginal;

    @Before
    public void preparar() {
        localeOriginal = Locale.getDefault();
        Locale.setDefault(new Locale("pt", "BR"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("usuarioTeste", "naoUtilizada"));
    }

    @After
    public void finalizar() {
        SecurityContextHolder.clearContext();
        Locale.setDefault(localeOriginal);
    }

    @Test
    public void testReceberTituloZero() {
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimentoAberto()));
        when(titulos.busca(0L)).thenReturn(Optional.<Titulo>empty());

        try {
            recebimentos.receber(COD_RECEBIMENTO, 50.00, 0.00, 0.00, 0L);
            fail("Era esperado rejeitar o título zero");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Selecione um título para realizar o recebimento";
            assertEquals(esperado, obtido);
        }
        verify(recebimentoRepository).findById(COD_RECEBIMENTO);
        verify(titulos).busca(0L);
        verifyZeroInteractions(receParcelas, parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberTituloNuloComportamentoAtual() {
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimentoAberto()));
        when(titulos.busca(null)).thenReturn(Optional.<Titulo>empty());

        try {
            recebimentos.receber(COD_RECEBIMENTO, 50.00, 0.00, 0.00, null);
            fail("Era esperado caracterizar a falha de desempacotamento do título nulo");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
        verify(titulos).busca(null);
        verifyZeroInteractions(receParcelas, parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
    }

    @Test
    public void testReceberJaProcessado() {
        Recebimento recebimento = recebimentoAberto();
        recebimento.setData_processamento(new java.sql.Timestamp(System.currentTimeMillis()));
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo("DIN")));

        try {
            recebimentos.receber(COD_RECEBIMENTO, 50.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado rejeitar o recebimento fechado");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Recebimento já esta fechado";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(receParcelas, parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberAcimaDoTotal() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));

        try {
            recebimentos.receber(COD_RECEBIMENTO, 100.01, 0.00, 0.00, COD_TITULO);
            fail("Era esperado rejeitar valor superior ao total");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Valor de recebimento é superior aos títulos";
            assertEquals(esperado, obtido);
        }
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
        verifyZeroInteractions(receParcelas, parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
    }

    @Test
    public void testReceberSemParcelas() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(Collections.<Parcela>emptyList());

        try {
            recebimentos.receber(COD_RECEBIMENTO, 50.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado rejeitar o recebimento sem parcelas");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Recebimento não possue parcelas";
            assertEquals(esperado, obtido);
        }
        verify(receParcelas).parcelasDoReceber(COD_RECEBIMENTO);
        verifyZeroInteractions(parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberValorZero() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());

        try {
            recebimentos.receber(COD_RECEBIMENTO, 0.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado rejeitar o valor zero");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Valor de recebimento inválido";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberValorNegativo() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());

        try {
            recebimentos.receber(COD_RECEBIMENTO, -1.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado rejeitar o valor negativo");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Valor de recebimento inválido";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(parcelas, caixas, usuarios, lancamentos, cartaoLancamentos);
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberParcial() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 30.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        verify(parcelas).receber(11L, 30.00, 0.00, 0.00);
        verify(parcelas, never()).receber(eq(12L), any(Double.class), any(Double.class), any(Double.class));
    }

    @Test
    public void testReceberValorIgualPrimeiraParcela() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 50.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        verify(parcelas).receber(11L, 50.00, 0.00, 0.00);
        verify(parcelas, never()).receber(eq(12L), any(Double.class), any(Double.class), any(Double.class));
    }

    @Test
    public void testReceberDistribuido() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 80.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        InOrder ordem = inOrder(parcelas);
        ordem.verify(parcelas).receber(11L, 50.00, 0.00, 0.00);
        ordem.verify(parcelas).receber(12L, 30.00, 0.00, 0.00);
    }

    @Test
    public void testReceberTotal() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 100.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        InOrder ordem = inOrder(parcelas);
        ordem.verify(parcelas).receber(11L, 50.00, 0.00, 0.00);
        ordem.verify(parcelas).receber(12L, 50.00, 0.00, 0.00);
        ArgumentCaptor<Recebimento> captor = ArgumentCaptor.forClass(Recebimento.class);
        verify(recebimentoRepository).save(captor.capture());
        Recebimento salvo = captor.getValue();
        assertEquals(100.00, salvo.getValor_recebido(), DELTA);
        assertNotNull(salvo.getData_processamento());
    }

    @Test
    public void testReceberDinheiro() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        caixa.setCodigo(3L);
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        Usuario usuario = usuario();
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario);
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 80.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        ArgumentCaptor<CaixaLancamento> captor = ArgumentCaptor.forClass(CaixaLancamento.class);
        verify(lancamentos).lancamento(captor.capture());
        CaixaLancamento lancamento = captor.getValue();
        assertEquals(80.00, lancamento.getValor(), DELTA);
        assertEquals(TipoLancamento.RECEBIMENTO, lancamento.getTipo());
        assertEquals(EstiloLancamento.ENTRADA, lancamento.getEstilo());
        assertEquals(caixa, lancamento.getCaixa().get());
        assertEquals(usuario, lancamento.getUsuario());
        assertEquals(recebimento, ReflectionTestUtils.getField(lancamento, "recebimento"));
        verifyZeroInteractions(cartaoLancamentos);
    }

    @Test
    public void testReceberCartaoDebito() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("CARTDEB");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 100.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        verify(cartaoLancamentos).lancamento(100.00, Optional.of(titulo));
        verifyZeroInteractions(caixas, lancamentos);
    }

    @Test
    public void testReceberCartaoCredito() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("CARTCRED");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 100.00, 0.00, 0.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        verify(cartaoLancamentos).lancamento(100.00, Optional.of(titulo));
        verifyZeroInteractions(caixas, lancamentos);
    }

    @Test
    public void testReceberFalhaNaParcela() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        doThrow(new RuntimeException("falha")).when(parcelas).receber(11L, 50.00, 0.00, 0.00);

        try {
            recebimentos.receber(COD_RECEBIMENTO, 100.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado tratar a falha da parcela");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Ocorreu um erro ao realizar o recebimento, chame o suporte";
            assertEquals(esperado, obtido);
        }
        verify(parcelas, never()).receber(eq(12L), any(Double.class), any(Double.class), any(Double.class));
        verifyZeroInteractions(caixas, usuarios, lancamentos, cartaoLancamentos);
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberFalhaNoCaixa() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));
        doThrow(new RuntimeException("falha")).when(lancamentos).lancamento(any(CaixaLancamento.class));

        try {
            recebimentos.receber(COD_RECEBIMENTO, 100.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado tratar a falha do caixa");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Ocorreu um erro ao realizar o recebimento, chame o suporte";
            assertEquals(esperado, obtido);
        }
        verify(recebimentoRepository, never()).save(any(Recebimento.class));
    }

    @Test
    public void testReceberFalhaAoSalvar() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));
        doThrow(new RuntimeException("falha")).when(recebimentoRepository).save(recebimento);

        try {
            recebimentos.receber(COD_RECEBIMENTO, 100.00, 0.00, 0.00, COD_TITULO);
            fail("Era esperado tratar a falha ao salvar");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Ocorreu um erro ao realizar o recebimento, chame o suporte";
            assertEquals(esperado, obtido);
        }
        verify(recebimentoRepository).save(recebimento);
    }

    @Test
    public void testReceberRegistraAcrescimoEDesconto() {
        Recebimento recebimento = recebimentoAberto();
        Titulo titulo = titulo("DIN");
        Caixa caixa = new Caixa();
        when(recebimentoRepository.findById(COD_RECEBIMENTO)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(COD_TITULO)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(COD_RECEBIMENTO)).thenReturn(parcelasPadrao());
        when(usuarios.buscaUsuario("usuarioTeste")).thenReturn(usuario());
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        String obtido = recebimentos.receber(COD_RECEBIMENTO, 80.00, 5.00, 2.00, COD_TITULO);
        String esperado = "Recebimento realizado com sucesso";

        assertEquals(esperado, obtido);
        ArgumentCaptor<Recebimento> captor = ArgumentCaptor.forClass(Recebimento.class);
        verify(recebimentoRepository).save(captor.capture());
        Recebimento salvo = captor.getValue();
        assertEquals(80.00, salvo.getValor_recebido(), DELTA);
        assertEquals(5.00, salvo.getValor_acrescimo(), DELTA);
        assertEquals(2.00, salvo.getValor_desconto(), DELTA);
        assertNotNull(salvo.getData_processamento());
        InOrder ordem = inOrder(parcelas);
        ordem.verify(parcelas).receber(11L, 50.00, 0.00, 0.00);
        ordem.verify(parcelas).receber(12L, 30.00, 0.00, 0.00);
    }

    @Test
    public void testAbrirRecebimento() {
        Pessoa pessoa = pessoa(7L);
        Parcela primeira = parcela(11L, 30.00, pessoa);
        Parcela segunda = parcela(12L, 70.00, pessoa);
        when(parcelas.busca(11L)).thenReturn(primeira);
        when(parcelas.busca(12L)).thenReturn(segunda);
        when(pessoas.buscaPessoa(7L)).thenReturn(Optional.of(pessoa));
        doAnswer(invocacao -> {
            Recebimento recebimento = invocacao.getArgument(0);
            recebimento.setCodigo(20L);
            return recebimento;
        }).when(recebimentoRepository).save(any(Recebimento.class));

        String obtido = recebimentos.abrirRecebimento(7L, new String[] { "11", "12" });
        String esperado = "20";

        assertEquals(esperado, obtido);
        ArgumentCaptor<Recebimento> captor = ArgumentCaptor.forClass(Recebimento.class);
        verify(recebimentoRepository).save(captor.capture());
        Recebimento salvo = captor.getValue();
        assertEquals(100.00, salvo.getValor_total(), DELTA);
        assertEquals(pessoa, salvo.getPessoa());
        assertEquals(Arrays.asList(primeira, segunda), salvo.getParcela());
    }

    @Test
    public void testAbrirRecebimentoParcelaQuitada() {
        Parcela parcela = parcela(11L, 30.00, pessoa(7L));
        parcela.setQuitado(1);
        when(parcelas.busca(11L)).thenReturn(parcela);

        try {
            recebimentos.abrirRecebimento(7L, new String[] { "11" });
            fail("Era esperado rejeitar a parcela quitada");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Parcela 11 já esta quitada, verifique.";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(recebimentoRepository, pessoas);
    }

    @Test
    public void testAbrirRecebimentoOutroCliente() {
        Parcela parcela = parcela(11L, 30.00, pessoa(8L));
        when(parcelas.busca(11L)).thenReturn(parcela);

        try {
            recebimentos.abrirRecebimento(7L, new String[] { "11" });
            fail("Era esperado rejeitar a parcela de outro cliente");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "A parcela 11 não pertence ao cliente selecionado";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(recebimentoRepository, pessoas);
    }

    @Test
    @SuppressWarnings("removal")
    public void testAbrirRecebimentoMesmoClienteIdsDistintos() {
        Long codpes = new Long(1000L);
        Pessoa pessoa = pessoa(new Long(1000L));
        Parcela parcela = parcela(11L, 30.00, pessoa);
        when(parcelas.busca(11L)).thenReturn(parcela);

        assertEquals(codpes.longValue(), pessoa.getCodigo().longValue());
        assertNotSame(codpes, pessoa.getCodigo());
        try {
            recebimentos.abrirRecebimento(codpes, new String[] { "11" });
            fail("Era esperado caracterizar a rejeição indevida do mesmo cliente");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "A parcela 11 não pertence ao cliente selecionado";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(recebimentoRepository, pessoas);
    }

    @Test
    public void testAbrirRecebimentoClienteInexistente() {
        when(pessoas.buscaPessoa(7L)).thenReturn(Optional.<Pessoa>empty());

        try {
            recebimentos.abrirRecebimento(7L, new String[0]);
            fail("Era esperado rejeitar o cliente inexistente");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Cliente não encontrado";
            assertEquals(esperado, obtido);
        }
        verifyZeroInteractions(recebimentoRepository);
    }

    @Test
    public void testAbrirRecebimentoFalhaAoSalvar() {
        Pessoa pessoa = pessoa(7L);
        when(pessoas.buscaPessoa(7L)).thenReturn(Optional.of(pessoa));
        doThrow(new RuntimeException("falha")).when(recebimentoRepository).save(any(Recebimento.class));

        try {
            recebimentos.abrirRecebimento(7L, new String[0]);
            fail("Era esperado tratar a falha ao salvar");
        } catch (RuntimeException e) {
            String obtido = e.getMessage();
            String esperado = "Erro ao receber, chame o suporte";
            assertEquals(esperado, obtido);
        }
    }

    private Recebimento recebimentoAberto() {
        Recebimento recebimento = new Recebimento();
        recebimento.setCodigo(COD_RECEBIMENTO);
        recebimento.setValor_total(100.00);
        return recebimento;
    }

    private List<Parcela> parcelasPadrao() {
        Pessoa pessoa = pessoa(7L);
        return Arrays.asList(parcela(11L, 50.00, pessoa), parcela(12L, 50.00, pessoa));
    }

    private Parcela parcela(Long codigo, Double valorRestante, Pessoa pessoa) {
        Receber receber = new Receber();
        receber.setPessoa(pessoa);
        Parcela parcela = new Parcela();
        parcela.setCodigo(codigo);
        parcela.setValor_restante(valorRestante);
        parcela.setReceber(receber);
        return parcela;
    }

    private Pessoa pessoa(Long codigo) {
        Pessoa pessoa = new Pessoa();
        pessoa.setCodigo(codigo);
        return pessoa;
    }

    private Titulo titulo(String sigla) {
        net.originmobi.pdv.model.TituloTipo tipo = new net.originmobi.pdv.model.TituloTipo();
        tipo.setSigla(sigla);
        Titulo titulo = new Titulo();
        titulo.setCodigo(COD_TITULO);
        titulo.setTipo(tipo);
        return titulo;
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setCodigo(4L);
        return usuario;
    }
}
