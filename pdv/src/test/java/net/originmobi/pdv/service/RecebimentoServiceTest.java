package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.TituloTipo;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;

public class RecebimentoServiceTest {

    @Mock private RecebimentoRepository recebimentos;
    @Mock private PessoaService pessoas;
    @Mock private RecebimentoParcelaService receParcelas;
    @Mock private ParcelaService parcelas;
    @Mock private CaixaService caixas;
    @Mock private UsuarioService usuarios;
    @Mock private CaixaLancamentoService lancamentos;
    @Mock private TituloService titulos;
    @Mock private CartaoLancamentoService cartaoLancamentos;

    @InjectMocks private RecebimentoService service;

    private Pessoa pessoa;
    private Usuario usuario;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("gerente", "senha"));
        pessoa = new Pessoa();
        pessoa.setCodigo(1L);
        usuario = new Usuario();
        usuario.setCodigo(2L);
        when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
    }

    @Test
    public void abreRecebimentoSomandoParcelas() {
        Parcela primeira = parcela(10L, 30.0, 0, pessoa);
        Parcela segunda = parcela(11L, 20.0, 0, pessoa);
        when(parcelas.busca(10L)).thenReturn(primeira);
        when(parcelas.busca(11L)).thenReturn(segunda);
        when(pessoas.buscaPessoa(1L)).thenReturn(Optional.of(pessoa));
        doAnswer(invocation -> {
            Recebimento salvo = invocation.getArgument(0);
            salvo.setCodigo(99L);
            return salvo;
        }).when(recebimentos).save(any(Recebimento.class));

        assertEquals("99", service.abrirRecebimento(1L, new String[] {"10", "11"}));
        verify(recebimentos).save(any(Recebimento.class));
    }

    @Test
    public void naoAbreRecebimentoComParcelaQuitada() {
        when(parcelas.busca(10L)).thenReturn(parcela(10L, 30.0, 1, pessoa));

        expect("Parcela 10 já esta quitada, verifique.",
                () -> service.abrirRecebimento(1L, new String[] {"10"}));
    }

    @Test
    public void naoAbreRecebimentoComParcelaDeOutroCliente() {
        Pessoa outra = new Pessoa();
        outra.setCodigo(2L);
        when(parcelas.busca(10L)).thenReturn(parcela(10L, 30.0, 0, outra));

        expect("A parcela 10 não pertence ao cliente selecionado",
                () -> service.abrirRecebimento(1L, new String[] {"10"}));
    }

    @Test
    public void informaFalhaAoSomarParcelaInvalida() {
        when(parcelas.busca(10L)).thenReturn(parcela(10L, null, 0, pessoa));

        expect("Erro ao receber, chame o suporte",
                () -> service.abrirRecebimento(1L, new String[] {"10"}));
    }

    @Test
    public void naoAbreRecebimentoSemCliente() {
        when(parcelas.busca(10L)).thenReturn(parcela(10L, 30.0, 0, pessoa));
        when(pessoas.buscaPessoa(1L)).thenReturn(Optional.empty());

        expect("Cliente não encontrado",
                () -> service.abrirRecebimento(1L, new String[] {"10"}));
    }

    @Test
    public void informaFalhaAoSalvarAbertura() {
        when(parcelas.busca(10L)).thenReturn(parcela(10L, 30.0, 0, pessoa));
        when(pessoas.buscaPessoa(1L)).thenReturn(Optional.of(pessoa));
        doThrow(new RuntimeException("falha")).when(recebimentos).save(any(Recebimento.class));

        expect("Erro ao receber, chame o suporte",
                () -> service.abrirRecebimento(1L, new String[] {"10"}));
    }

    @Test
    public void exigeTituloParaReceber() {
        when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento(100.0)));

        expect("Selecione um título para realizar o recebimento",
                () -> service.receber(1L, 10.0, 0.0, 0.0, 0L));
    }

    @Test
    public void naoProcessaRecebimentoJaFechado() {
        Recebimento recebimento = recebimento(100.0);
        recebimento.setData_processamento(new Timestamp(System.currentTimeMillis()));
        prepararReceber(recebimento, titulo(TituloTipo.DIN), Collections.singletonList(parcela(1L, 100.0, 0, pessoa)));

        expect("Recebimento já esta fechado",
                () -> service.receber(1L, 10.0, 0.0, 0.0, 1L));
    }

    @Test
    public void rejeitaValorSuperiorAoTotal() {
        prepararReceber(recebimento(100.0), titulo(TituloTipo.DIN),
                Collections.singletonList(parcela(1L, 100.0, 0, pessoa)));

        expect("Valor de recebimento é superior aos títulos",
                () -> service.receber(1L, 101.0, 0.0, 0.0, 1L));
    }

    @Test
    public void rejeitaRecebimentoSemParcelas() {
        prepararReceber(recebimento(100.0), titulo(TituloTipo.DIN), Collections.emptyList());

        expect("Recebimento não possue parcelas",
                () -> service.receber(1L, 10.0, 0.0, 0.0, 1L));
    }

    @Test
    public void rejeitaValorRecebidoInvalido() {
        prepararReceber(recebimento(100.0), titulo(TituloTipo.DIN),
                Collections.singletonList(parcela(1L, 100.0, 0, pessoa)));

        expect("Valor de recebimento inválido",
                () -> service.receber(1L, 0.0, 0.0, 0.0, 1L));
    }

    @Test
    public void recebeEmDinheiroEDistribuiValorEntreParcelas() {
        Recebimento recebimento = recebimento(100.0);
        Parcela primeira = parcela(10L, 30.0, 0, pessoa);
        Parcela segunda = parcela(11L, 70.0, 0, pessoa);
        Parcela terceira = parcela(12L, 10.0, 0, pessoa);
        prepararReceber(recebimento, titulo(TituloTipo.DIN), Arrays.asList(primeira, segunda, terceira));
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

        assertEquals("Recebimento realizado com sucesso", service.receber(1L, 50.0, 2.0, 1.0, 1L));

        verify(parcelas).receber(10L, 30.0, 0.0, 0.0);
        verify(parcelas).receber(11L, 20.0, 0.0, 0.0);
        verify(parcelas, never()).receber(eq(12L), anyDouble(), anyDouble(), anyDouble());
        verify(lancamentos).lancamento(any());
        assertEquals(Double.valueOf(50.0), recebimento.getValor_recebido());
        assertEquals(Double.valueOf(2.0), recebimento.getValor_acrescimo());
        assertEquals(Double.valueOf(1.0), recebimento.getValor_desconto());
        assertTrue(recebimento.getData_processamento() != null);
    }

    @Test
    public void informaFalhaAoReceberUmaParcela() {
        prepararReceber(recebimento(100.0), titulo(TituloTipo.DIN),
                Collections.singletonList(parcela(10L, 100.0, 0, pessoa)));
        doThrow(new RuntimeException("falha")).when(parcelas).receber(anyLong(), anyDouble(), anyDouble(), anyDouble());

        expect("Ocorreu um erro ao realizar o recebimento, chame o suporte",
                () -> service.receber(1L, 50.0, 0.0, 0.0, 1L));
    }

    @Test
    public void recebeNoCartaoDeDebito() {
        Recebimento recebimento = recebimento(100.0);
        Titulo titulo = titulo(TituloTipo.CARTDEB);
        prepararReceber(recebimento, titulo, Collections.singletonList(parcela(10L, 100.0, 0, pessoa)));

        assertEquals("Recebimento realizado com sucesso", service.receber(1L, 50.0, 0.0, 0.0, 1L));
        verify(cartaoLancamentos).lancamento(50.0, Optional.of(titulo));
    }

    @Test
    public void recebeNoCartaoDeCredito() {
        Recebimento recebimento = recebimento(100.0);
        Titulo titulo = titulo(TituloTipo.CARTCRED);
        prepararReceber(recebimento, titulo, Collections.singletonList(parcela(10L, 100.0, 0, pessoa)));

        assertEquals("Recebimento realizado com sucesso", service.receber(1L, 50.0, 0.0, 0.0, 1L));
        verify(cartaoLancamentos).lancamento(50.0, Optional.of(titulo));
    }

    @Test
    public void trataParcelaSemSaldoRestante() {
        Recebimento recebimento = recebimento(100.0);
        Titulo titulo = titulo(TituloTipo.CARTDEB);
        prepararReceber(recebimento, titulo, Collections.singletonList(parcela(10L, 0.0, 0, pessoa)));

        assertEquals("Recebimento realizado com sucesso", service.receber(1L, 50.0, 0.0, 0.0, 1L));
        verify(parcelas).receber(10L, 0.0, 0.0, 0.0);
    }

    @Test
    public void informaFalhaNoLancamentoDeCaixa() {
        prepararReceber(recebimento(100.0), titulo(TituloTipo.DIN),
                Collections.singletonList(parcela(10L, 100.0, 0, pessoa)));
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));
        doThrow(new RuntimeException("falha")).when(lancamentos).lancamento(any());

        expect("Ocorreu um erro ao realizar o recebimento, chame o suporte",
                () -> service.receber(1L, 50.0, 0.0, 0.0, 1L));
    }

    @Test
    public void informaFalhaAoSalvarRecebimentoProcessado() {
        Recebimento recebimento = recebimento(100.0);
        Titulo titulo = titulo(TituloTipo.CARTDEB);
        prepararReceber(recebimento, titulo, Collections.singletonList(parcela(10L, 100.0, 0, pessoa)));
        doThrow(new RuntimeException("falha")).when(recebimentos).save(recebimento);

        expect("Ocorreu um erro ao realizar o recebimento, chame o suporte",
                () -> service.receber(1L, 50.0, 0.0, 0.0, 1L));
    }

    @Test
    public void removeRecebimentoAberto() {
        Recebimento recebimento = recebimento(100.0);
        when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento));

        assertEquals("removido com sucesso", service.remover(1L));
        verify(recebimentos).deleteById(1L);
    }

    @Test
    public void naoRemoveRecebimentoProcessado() {
        Recebimento recebimento = recebimento(100.0);
        recebimento.setData_processamento(new Timestamp(System.currentTimeMillis()));
        when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento));

        expect("Esse recebimento não pode ser removido, pois ele já esta processado",
                () -> service.remover(1L));
    }

    @Test
    public void informaFalhaAoRemover() {
        when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento(100.0)));
        doThrow(new RuntimeException("falha")).when(recebimentos).deleteById(1L);

        expect("Erro ao remover orçamento, chame o suporte", () -> service.remover(1L));
    }

    private void prepararReceber(Recebimento recebimento, Titulo titulo, java.util.List<Parcela> lista) {
        when(recebimentos.findById(1L)).thenReturn(Optional.of(recebimento));
        when(titulos.busca(1L)).thenReturn(Optional.of(titulo));
        when(receParcelas.parcelasDoReceber(1L)).thenReturn(lista);
    }

    private Recebimento recebimento(double total) {
        Recebimento recebimento = new Recebimento();
        recebimento.setCodigo(1L);
        recebimento.setValor_total(total);
        return recebimento;
    }

    private Parcela parcela(Long codigo, Double restante, int quitado, Pessoa dono) {
        Receber receber = new Receber();
        receber.setPessoa(dono);
        Parcela parcela = new Parcela();
        parcela.setCodigo(codigo);
        parcela.setValor_restante(restante);
        parcela.setQuitado(quitado);
        parcela.setReceber(receber);
        return parcela;
    }

    private Titulo titulo(TituloTipo tipo) {
        net.originmobi.pdv.model.TituloTipo tituloTipo = new net.originmobi.pdv.model.TituloTipo();
        tituloTipo.setSigla(tipo.toString());
        Titulo titulo = new Titulo();
        titulo.setCodigo(1L);
        titulo.setTipo(tituloTipo);
        return titulo;
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
