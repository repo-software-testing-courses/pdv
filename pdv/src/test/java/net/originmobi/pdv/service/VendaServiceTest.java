package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.TituloTipo;
import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.PagamentoTipo;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.model.VendaProduto;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;

public class VendaServiceTest {

    @Mock private VendaRepository vendas;
    @Mock private UsuarioService usuarios;
    @Mock private VendaProdutoService vendaProdutos;
    @Mock private PagamentoTipoService formaPagamentos;
    @Mock private CaixaService caixas;
    @Mock private ReceberService receberServ;
    @Mock private ParcelaService parcelas;
    @Mock private CaixaLancamentoService lancamentos;
    @Mock private TituloService tituloService;
    @Mock private CartaoLancamentoService cartaoLancamento;
    @Mock private ProdutoService produtos;

    @InjectMocks private VendaService service;

    private Usuario usuario;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("gerente", "senha"));
        usuario = new Usuario();
        usuario.setCodigo(1L);
        when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
    }

    @Test
    public void abreVendaNovaPreencheDadosESalva() {
        Venda venda = new Venda();

        assertNull(service.abreVenda(venda));

        assertEquals(VendaSituacao.ABERTA, venda.getSituacao());
        assertEquals(Double.valueOf(0.0), venda.getValor_produtos());
        assertSame(usuario, venda.getUsuario());
        verify(vendas).save(venda);
    }

    @Test
    public void abreVendaNovaMantemRetornoMesmoQuandoRepositorioFalha() {
        Venda venda = new Venda();
        doThrow(new RuntimeException("falha")).when(vendas).save(venda);

        assertNull(service.abreVenda(venda));
    }

    @Test
    public void abreVendaExistenteAtualizaDados() {
        Venda venda = venda(9L, VendaSituacao.ABERTA);
        Pessoa pessoa = new Pessoa();
        venda.setPessoa(pessoa);
        venda.setObservacao("obs");

        assertEquals(Long.valueOf(9L), service.abreVenda(venda));
        verify(vendas).updateDadosVenda(pessoa, "obs", 9L);
    }

    @Test
    public void abreVendaExistenteMantemRetornoMesmoQuandoAtualizacaoFalha() {
        Venda venda = venda(9L, VendaSituacao.ABERTA);
        doThrow(new RuntimeException("falha")).when(vendas)
                .updateDadosVenda(any(Pessoa.class), any(String.class), eq(9L));
        venda.setPessoa(new Pessoa());
        venda.setObservacao("obs");

        assertEquals(Long.valueOf(9L), service.abreVenda(venda));
    }

    @Test
    public void buscaPorCodigo() {
        VendaFilter filter = new VendaFilter();
        filter.setCodigo(3L);
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked") Page<Venda> page = org.mockito.Mockito.mock(Page.class);
        when(vendas.findByCodigoIn(3L, pageable)).thenReturn(page);

        assertSame(page, service.busca(filter, "ABERTA", pageable));
    }

    @Test
    public void buscaPorSituacaoFechada() {
        VendaFilter filter = new VendaFilter();
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked") Page<Venda> page = org.mockito.Mockito.mock(Page.class);
        when(vendas.findBySituacaoEquals(VendaSituacao.FECHADA, pageable)).thenReturn(page);

        assertSame(page, service.busca(filter, "FECHADA", pageable));
    }

    @Test
    public void adicionaProdutoEmVendaAberta() {
        when(vendas.verificaSituacao(1L)).thenReturn("ABERTA");

        assertEquals("ok", service.addProduto(1L, 2L, 3.0));
        verify(vendaProdutos).salvar(any(VendaProduto.class));
    }

    @Test
    public void adicionarProdutoAbsorveFalhaDoRepositorio() {
        when(vendas.verificaSituacao(1L)).thenReturn("ABERTA");
        doThrow(new RuntimeException("falha")).when(vendaProdutos).salvar(any(VendaProduto.class));

        assertEquals("ok", service.addProduto(1L, 2L, 3.0));
    }

    @Test
    public void naoAdicionaProdutoEmVendaFechada() {
        when(vendas.verificaSituacao(1L)).thenReturn("FECHADA");

        assertEquals("Venda fechada", service.addProduto(1L, 2L, 3.0));
        verify(vendaProdutos, never()).salvar(any(VendaProduto.class));
    }

    @Test
    public void removeProdutoDeVendaAberta() {
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda(1L, VendaSituacao.ABERTA));

        assertEquals("ok", service.removeProduto(4L, 1L));
        verify(vendaProdutos).removeProduto(4L);
    }

    @Test
    public void naoRemoveProdutoDeVendaFechada() {
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda(1L, VendaSituacao.FECHADA));

        assertEquals("Venda fechada", service.removeProduto(4L, 1L));
    }

    @Test
    public void removerProdutoAbsorveFalha() {
        when(vendas.findByCodigoEquals(1L)).thenThrow(new RuntimeException("falha"));

        assertEquals("ok", service.removeProduto(4L, 1L));
    }

    @Test
    public void listaEContaVendasAbertas() {
        when(vendas.findAll()).thenReturn(Collections.singletonList(new Venda()));
        when(vendas.qtdVendasEmAberto()).thenReturn(2);

        assertEquals(1, service.lista().size());
        assertEquals(2, service.qtdAbertos());
    }

    @Test
    public void naoFechaVendaJaFechada() {
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda(1L, VendaSituacao.FECHADA));

        expect("venda fechada", () -> fechar(10.0, new String[] {"10"}, "00", titulo(TituloTipo.DIN)));
    }

    @Test
    public void naoFechaVendaSemValor() {
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda(1L, VendaSituacao.ABERTA));

        expect("Venda sem valor, verifique",
                () -> service.fechaVenda(1L, 1L, 0.0, 0.0, 0.0, new String[] {"0"}, new String[] {"1"}));
    }

    @Test
    public void informaFalhaAoCriarReceber() {
        prepararFechamento("00", titulo(TituloTipo.DIN), vendaComPessoa());
        doThrow(new RuntimeException("falha")).when(receberServ).cadastrar(any());

        expect("Erro ao fechar a venda, chame o suporte",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
    }

    @Test
    public void naoFechaVendaEmDinheiroSemCaixaAberto() {
        prepararFechamento("00", titulo(TituloTipo.DIN), vendaComPessoa());
        when(caixas.caixaIsAberto()).thenReturn(false);

        expect("nenhum caixa aberto",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
    }

    @Test
    public void fechaVendaAVistaEmDinheiro() {
        prepararDinheiro();

        assertEquals("Venda finalizada com sucesso",
                service.fechaVenda(1L, 1L, 10.0, 1.0, 1.0, new String[] {"10"}, new String[] {"1"}));

        verify(lancamentos).lancamento(any());
        verify(vendas).fechaVenda(eq(1L), eq(VendaSituacao.FECHADA), eq(10.0), eq(1.0), eq(1.0), any(), any());
        verify(produtos).movimentaEstoque(1L, EntradaSaida.SAIDA);
    }

    @Test
    public void rejeitaParcelaAVistaVazia() {
        prepararDinheiro();

        expect("Parcela sem valor, verifique",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {""}, new String[] {"1"}));
    }

    @Test
    public void rejeitaSomaDeParcelasAVistaDiferenteDoProduto() {
        prepararDinheiro();

        expect("Valor das parcelas diferente do valor total de produtos, verifique",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0,
                        new String[] {"4", "4"}, new String[] {"1"}));
    }

    @Test
    public void informaFalhaNoLancamentoEmDinheiro() {
        prepararDinheiro();
        doThrow(new RuntimeException("falha")).when(lancamentos).lancamento(any());

        expect("Erro ao fechar a venda, chame o suporte",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
    }

    @Test
    public void fechaVendaAVistaNoCartao() {
        Titulo titulo = titulo(TituloTipo.CARTDEB);
        prepararFechamento("00", titulo, vendaComPessoa());

        assertEquals("Venda finalizada com sucesso",
                service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
        verify(cartaoLancamento).lancamento(eq(10.0), eq(Optional.of(titulo)));
    }

    @Test
    public void fechaVendaAVistaComTituloNaoTratadoSemGerarLancamento() {
        net.originmobi.pdv.model.TituloTipo tipo = new net.originmobi.pdv.model.TituloTipo();
        tipo.setSigla("CHEQUE");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipo);
        prepararFechamento("00", titulo, vendaComPessoa());

        assertEquals("Venda finalizada com sucesso",
                service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
        verify(cartaoLancamento, never()).lancamento(anyDouble(), any());
    }

    @Test
    public void naoFechaVendaAPrazoSemCliente() {
        prepararFechamento("30", titulo(TituloTipo.DIN), venda(1L, VendaSituacao.ABERTA));

        expect("Venda sem cliente, verifique",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
    }

    @Test
    public void rejeitaParcelaAPrazoVazia() {
        prepararFechamento("30", titulo(TituloTipo.DIN), vendaComPessoa());

        expect("valor de recebimento invalido",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {""}, new String[] {"1"}));
    }

    @Test
    public void fechaVendaAPrazoEGeraParcela() {
        prepararFechamento("30", titulo(TituloTipo.DIN), vendaComPessoa());

        assertEquals("Venda finalizada com sucesso",
                service.fechaVenda(1L, 1L, 10.0, 2.0, 1.0, new String[] {"10"}, new String[] {"1"}));
        verify(parcelas).gerarParcela(eq(11.0), anyDouble(), anyDouble(), anyDouble(), eq(11.0), any(), anyInt(),
                eq(1), any(), any());
    }

    @Test
    public void informaFalhaAoGerarParcelaAPrazo() {
        prepararFechamento("30", titulo(TituloTipo.DIN), vendaComPessoa());
        doThrow(new RuntimeException("falha")).when(parcelas).gerarParcela(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(), any(), anyInt(),
                anyInt(), any(), any());

        expect(null,
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
    }

    @Test
    public void informaFalhaAoPersistirFechamento() {
        Titulo titulo = titulo(TituloTipo.CARTCRED);
        prepararFechamento("00", titulo, vendaComPessoa());
        doThrow(new RuntimeException("falha")).when(vendas).fechaVenda(
                anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());

        expect("Erro ao fechar a venda, chame o suporte",
                () -> service.fechaVenda(1L, 1L, 10.0, 0.0, 0.0, new String[] {"10"}, new String[] {"1"}));
    }

    private void prepararDinheiro() {
        prepararFechamento("00", titulo(TituloTipo.DIN), vendaComPessoa());
        when(caixas.caixaIsAberto()).thenReturn(true);
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));
    }

    private void prepararFechamento(String forma, Titulo titulo, Venda venda) {
        PagamentoTipo pagamento = new PagamentoTipo();
        pagamento.setFormaPagamento(forma);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);
        when(formaPagamentos.busca(1L)).thenReturn(pagamento);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));
    }

    private String fechar(double valor, String[] parcelasVenda, String forma, Titulo titulo) {
        prepararFechamento(forma, titulo, venda(1L, VendaSituacao.FECHADA));
        return service.fechaVenda(1L, 1L, valor, 0.0, 0.0, parcelasVenda, new String[] {"1"});
    }

    private Venda vendaComPessoa() {
        Venda venda = venda(1L, VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        return venda;
    }

    private Venda venda(Long codigo, VendaSituacao situacao) {
        Venda venda = new Venda();
        venda.setCodigo(codigo);
        venda.setSituacao(situacao);
        return venda;
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
            if (mensagem != null) {
                assertEquals(mensagem, e.getMessage());
            }
        }
    }

    private interface ThrowingRunnable {
        void run();
    }
}
