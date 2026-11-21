package net.originmobi.pdv.service.notafiscal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.model.CFOP;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.CstCsosn;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Estado;
import net.originmobi.pdv.model.ModBcIcms;
import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.model.NotaFiscalItem;
import net.originmobi.pdv.model.NotaFiscalItemImposto;
import net.originmobi.pdv.model.NotaFiscalTotais;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.model.Tributacao;
import net.originmobi.pdv.model.TributacaoRegra;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalItemRepository;
import net.originmobi.pdv.service.ProdutoService;

public class NotaFiscalItemServiceTest {

    @Mock private NotaFiscalItemRepository itemServer;
    @Mock private NotaFiscalItemImpostoService impostos;
    @Mock private NotaFiscalTotaisServer totais;
    @Mock private ProdutoService produtos;
    @Mock private NotaFiscalService notas;

    @InjectMocks private NotaFiscalItemService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void rejeitaProdutoInexistente() {
        when(produtos.buscaProduto(1L)).thenReturn(Optional.empty());

        expect("Nenhum produto encontrado, favor verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaProdutoSemTributacao() {
        Produto produto = produtoBasico();
        produto.setTributacao(null);
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));

        expect("Produto sem tributação, favor verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaProdutoSemNcm() {
        Produto produto = produtoBasico();
        produto.setNcm("");
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));

        expect("Produto sem código NCM, favor verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaSubstituicaoTributariaSemCest() {
        Produto produto = produtoBasico();
        produto.setSubtributaria(ProdutoSubstTributaria.SIM);
        produto.setCest("");
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));

        expect("Produto de substituição tributária sem código CEST, favor verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaProdutoSemUnidade() {
        Produto produto = produtoBasico();
        produto.setUnidade("");
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));

        expect("Produto sem unidade, favor verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaTributacaoSemRegraDeSaida() {
        Produto produto = produtoBasico();
        produto.getTributacao().setRegra(Collections.singletonList(regra(EntradaSaida.ENTRADA, "SP")));
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));

        expect("Tributação sem regra de saída, verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaTributacaoSemRegraDeEntrada() {
        Produto produto = produtoBasico();
        produto.getTributacao().setRegra(Collections.singletonList(regra(EntradaSaida.SAIDA, "SP")));
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));

        expect("Tributação sem regra de entrada, verifique",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.ENTRADA));
    }

    @Test
    public void rejeitaQuandoNaoHaRegraParaUfDoDestinatario() {
        Produto produto = produtoBasico();
        produto.getTributacao().setRegra(Collections.singletonList(regra(EntradaSaida.SAIDA, "RJ")));
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        preparar(produto, nota);

        expect("Nenhuma regra de tributação cadastrada para a UF do destinatário",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void rejeitaQuandoRegraDaUfTemTipoDiferente() {
        Produto produto = produtoBasico();
        produto.getTributacao().setRegra(java.util.Arrays.asList(
                regra(EntradaSaida.SAIDA, "RJ"), regra(EntradaSaida.ENTRADA, "SP")));
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        preparar(produto, nota);

        expect("Nenhuma regra de tributação cadastrada para a UF do destinatário",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void insereNovoItemDeSaidaEAtualizaTotais() {
        Produto produto = produtoBasico();
        TributacaoRegra regra = regra(EntradaSaida.SAIDA, "SP");
        produto.getTributacao().setRegra(Collections.singletonList(regra));
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        preparar(produto, nota);
        NotaFiscalItemImposto imposto = new NotaFiscalItemImposto();
        when(impostos.calcula(null, 30.0, regra, '0', 3)).thenReturn(imposto);

        assertEquals("ok", service.insere(1L, 1L, 3, NotaFiscalTipo.SAIDA));

        ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
        verify(itemServer).save(captor.capture());
        NotaFiscalItem item = captor.getValue();
        assertEquals(Long.valueOf(1L), item.getCodProd());
        assertEquals(3, item.getQtd());
        assertEquals(Double.valueOf(30.0), item.getVlTotal());
        assertEquals("5102", item.getCfop());
        verify(totais).atualiza(1L, nota.getTotais());
    }

    @Test
    public void atualizaItemExistenteReaproveitandoCodigos() {
        Produto produto = produtoBasico();
        TributacaoRegra regra = regra(EntradaSaida.SAIDA, "SP");
        produto.getTributacao().setRegra(Collections.singletonList(regra));
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        NotaFiscalItemImposto impostoExistente = new NotaFiscalItemImposto();
        impostoExistente.setCodigo(20L);
        NotaFiscalItem existente = new NotaFiscalItem();
        existente.setCodigo(30L);
        existente.setCodProd(1L);
        existente.setQtd(2);
        existente.setImpostos(impostoExistente);
        nota.setItens(Collections.singletonList(existente));
        preparar(produto, nota);
        when(impostos.calcula(eq(20L), eq(50.0), eq(regra), eq('0'), eq(3)))
                .thenReturn(new NotaFiscalItemImposto());

        assertEquals("ok", service.insere(1L, 1L, 3, NotaFiscalTipo.SAIDA));

        ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
        verify(itemServer).save(captor.capture());
        assertEquals(Long.valueOf(30L), captor.getValue().getCodigo());
        assertEquals(5, captor.getValue().getQtd());
    }

    @Test
    public void insereItemDeEntrada() {
        Produto produto = produtoBasico();
        TributacaoRegra regra = regra(EntradaSaida.ENTRADA, "SP");
        produto.getTributacao().setRegra(Collections.singletonList(regra));
        NotaFiscal nota = nota(NotaFiscalTipo.ENTRADA, "SP");
        preparar(produto, nota);
        when(impostos.calcula(null, 10.0, regra, '0', 3)).thenReturn(new NotaFiscalItemImposto());

        assertEquals("ok", service.insere(1L, 1L, 1, NotaFiscalTipo.ENTRADA));
    }

    @Test
    public void ignoraItemExistenteDeOutroProdutoEAceitaCestPreenchido() {
        Produto produto = produtoBasico();
        produto.setSubtributaria(ProdutoSubstTributaria.SIM);
        TributacaoRegra regra = regra(EntradaSaida.SAIDA, "SP");
        produto.getTributacao().setRegra(Collections.singletonList(regra));
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        NotaFiscalItem outro = new NotaFiscalItem();
        outro.setCodProd(2L);
        nota.setItens(Collections.singletonList(outro));
        preparar(produto, nota);
        when(impostos.calcula(null, 10.0, regra, '0', 3)).thenReturn(new NotaFiscalItemImposto());

        assertEquals("ok", service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void informaFalhaAoSalvarItem() {
        Produto produto = produtoBasico();
        TributacaoRegra regra = regra(EntradaSaida.SAIDA, "SP");
        produto.getTributacao().setRegra(Collections.singletonList(regra));
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        preparar(produto, nota);
        when(impostos.calcula(null, 10.0, regra, '0', 3)).thenReturn(new NotaFiscalItemImposto());
        doThrow(new RuntimeException("falha")).when(itemServer).save(any(NotaFiscalItem.class));

        expect("Erro ao salvar item na nota, chame o suporte",
                () -> service.insere(1L, 1L, 1, NotaFiscalTipo.SAIDA));
    }

    @Test
    public void removeItemEAtualizaTotais() {
        NotaFiscal nota = nota(NotaFiscalTipo.SAIDA, "SP");
        when(notas.busca(1L)).thenReturn(Optional.of(nota));

        service.remove(2L, 1L);

        verify(itemServer).deleteById(2L);
        verify(totais).atualiza(1L, nota.getTotais());
    }

    @Test
    public void informaFalhaAoRemoverItem() {
        doThrow(new RuntimeException("falha")).when(itemServer).deleteById(2L);

        expect("Erro ao tentar remover o item da nota, chame o suporte",
                () -> service.remove(2L, 1L));
    }

    @Test
    public void buscaItensDaNota() {
        List<Object> itens = new ArrayList<>();
        itens.add(new Object());
        when(itemServer.findByNotaFiscalCodigoEquals(1L)).thenReturn(itens);

        assertSame(itens, service.buscaItensNota(1L));
    }

    private void preparar(Produto produto, NotaFiscal nota) {
        when(produtos.buscaProduto(1L)).thenReturn(Optional.of(produto));
        when(notas.busca(1L)).thenReturn(Optional.of(nota));
    }

    private Produto produtoBasico() {
        Produto produto = new Produto();
        produto.setCodigo(1L);
        produto.setValor_venda(10.0);
        produto.setUnidade("UN");
        produto.setNcm("12345678");
        produto.setCest("1234567");
        produto.setSubtributaria(ProdutoSubstTributaria.NAO);
        ModBcIcms mod = new ModBcIcms();
        mod.setTipo(3);
        produto.setModBcIcms(mod);
        Tributacao tributacao = new Tributacao();
        tributacao.setRegra(new ArrayList<>());
        produto.setTributacao(tributacao);
        return produto;
    }

    private TributacaoRegra regra(EntradaSaida tipo, String uf) {
        CstCsosn cst = new CstCsosn();
        cst.setCst_csosn("00");
        Estado estado = new Estado();
        estado.setSigla(uf);
        CFOP cfop = new CFOP();
        cfop.setCfop("5102");
        TributacaoRegra regra = new TributacaoRegra();
        regra.setTipo(tipo);
        regra.setUf(estado);
        regra.setCst_csosn(cst);
        regra.setCfop(cfop);
        return regra;
    }

    private NotaFiscal nota(NotaFiscalTipo tipo, String uf) {
        Estado estado = new Estado();
        estado.setSigla(uf);
        Cidade cidade = new Cidade();
        cidade.setEstado(estado);
        Endereco endereco = new Endereco();
        endereco.setCidade(cidade);
        Pessoa destinatario = new Pessoa();
        destinatario.setEndereco(endereco);
        NotaFiscal nota = new NotaFiscal();
        nota.setCodigo(1L);
        nota.setTipo(tipo);
        nota.setDestinatario(destinatario);
        nota.setItens(new ArrayList<>());
        nota.setTotais(new NotaFiscalTotais());
        return nota;
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
