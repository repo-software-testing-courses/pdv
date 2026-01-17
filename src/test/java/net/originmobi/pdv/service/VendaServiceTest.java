package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.*;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @InjectMocks
    private VendaService service;

    @Mock private VendaRepository vendas;
    @Mock private UsuarioService usuarios;
    @Mock private VendaProdutoService vendaProdutos;
    @Mock private PagamentoTipoService formaPagamentos;
    @Mock private CaixaService caixas;
    @Mock private ReceberService receberService;
    @Mock private ParcelaService parcelas;
    @Mock private CaixaLancamentoService lancamentos;
    @Mock private TituloService tituloService;
    @Mock private CartaoLancamentoService cartaoLancamento;
    @Mock private ProdutoService produtos;

    @Test
    @DisplayName("Deve abrir uma NOVA venda (código null) mockando a classe estática Aplicacao")
    void deveAbrirNovaVenda() {
        Venda venda = new Venda();
        venda.setCodigo(null);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setUser("Admin");

        try (MockedStatic<Aplicacao> aplicacaoStatic = Mockito.mockStatic(Aplicacao.class)) {
            Aplicacao appInstancia = mock(Aplicacao.class);
            aplicacaoStatic.when(Aplicacao::getInstancia).thenReturn(appInstancia);
            when(appInstancia.getUsuarioAtual()).thenReturn("admin");

            when(usuarios.buscaUsuario("admin")).thenReturn(usuarioMock);

            service.abreVenda(venda);

            verify(vendas).save(venda);
            assertEquals(VendaSituacao.ABERTA, venda.getSituacao());
            assertEquals(0.00, venda.getValor_produtos());
            assertEquals(usuarioMock, venda.getUsuario());
        }
    }

    @Test
    @DisplayName("Deve capturar exceção silenciosamente ao falhar SAVE de nova venda")
    void deveTratarErroAoSalvarNovaVenda() {
        Venda venda = new Venda();
        venda.setCodigo(null);

        try (MockedStatic<Aplicacao> appMock = Mockito.mockStatic(Aplicacao.class)) {
            Aplicacao app = mock(Aplicacao.class);
            appMock.when(Aplicacao::getInstancia).thenReturn(app);
            when(app.getUsuarioAtual()).thenReturn("admin");
            when(usuarios.buscaUsuario("admin")).thenReturn(new Usuario());

            doThrow(new RuntimeException("Erro de conexão")).when(vendas).save(any(Venda.class));

            Long resultado = service.abreVenda(venda);

            assertNull(resultado);

            verify(vendas).save(venda);
        }
    }

    @Test
    @DisplayName("Deve capturar exceção silenciosamente ao falhar UPDATE de venda existente")
    void deveTratarErroAoAtualizarVenda() {
        Venda venda = new Venda();
        venda.setCodigo(10L);
        venda.setPessoa(new Pessoa());
        venda.setObservacao("Obs");

        doThrow(new RuntimeException("Erro ao atualizar")).when(vendas).updateDadosVenda(any(), anyString(), anyLong());

        Long resultado = service.abreVenda(venda);

        assertEquals(10L, resultado);

        verify(vendas).updateDadosVenda(any(), anyString(), eq(10L));
    }

    @Test
    @DisplayName("Deve atualizar venda existente (código preenchido)")
    void deveAtualizarVendaExistente() {
        Venda venda = new Venda();
        venda.setCodigo(10L);
        Pessoa pessoa = new Pessoa();
        venda.setPessoa(pessoa);
        venda.setObservacao("Obs Atualizada");

        service.abreVenda(venda);

        verify(vendas).updateDadosVenda(pessoa, "Obs Atualizada", 10L);
        verify(vendas, never()).save(any());
    }

    @Test
    @DisplayName("Cenário IF: Deve buscar por Código quando o filtro possui código preenchido")
    void deveBuscarPorCodigoQuandoFiltroTemId() {
        Long codigoPesquisa = 123L;

        VendaFilter filter = new VendaFilter();
        filter.setCodigo(codigoPesquisa);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Venda> pageMock = new PageImpl<>(Collections.emptyList());
        when(vendas.findByCodigoIn(codigoPesquisa, pageable)).thenReturn(pageMock);

        Page<Venda> resultado = service.busca(filter, "ABERTA", pageable);

        verify(vendas).findByCodigoIn(codigoPesquisa, pageable);

        verify(vendas, never()).findBySituacaoEquals(any(), any());

        assertEquals(pageMock, resultado);
    }

    @Test
    @DisplayName("Cenário ELSE: Deve buscar por Situação ABERTA quando filtro NÃO tem código")
    void deveBuscarPorSituacaoAbertaQuandoFiltroSemId() {
        VendaFilter filter = new VendaFilter();
        filter.setCodigo(null);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Venda> pageMock = new PageImpl<>(Collections.emptyList());

        when(vendas.findBySituacaoEquals(VendaSituacao.ABERTA, pageable)).thenReturn(pageMock);

        service.busca(filter, "ABERTA", pageable);

        verify(vendas).findBySituacaoEquals(VendaSituacao.ABERTA, pageable);

        verify(vendas, never()).findByCodigoIn(anyLong(), any());
    }

    @Test
    @DisplayName("Cenário ELSE: Deve buscar por Situação FECHADA quando string não é 'ABERTA'")
    void deveBuscarPorSituacaoFechadaQuandoFiltroSemId() {
        VendaFilter filter = new VendaFilter();
        filter.setCodigo(null);

        Pageable pageable = PageRequest.of(0, 10);

        when(vendas.findBySituacaoEquals(VendaSituacao.FECHADA, pageable)).thenReturn(Page.empty());

        service.busca(filter, "FECHADA", pageable);

        verify(vendas).findBySituacaoEquals(VendaSituacao.FECHADA, pageable);
    }

    @Test
    void deveAdicionarProdutoQuandoVendaAberta() {
        when(vendas.verificaSituacao(1L)).thenReturn("ABERTA");

        String resultado = service.addProduto(1L, 100L, 50.0);

        assertEquals("ok", resultado);
        verify(vendaProdutos).salvar(any(VendaProduto.class));
    }

    @Test
    void naoDeveAdicionarProdutoQuandoVendaFechada() {
        when(vendas.verificaSituacao(1L)).thenReturn("FECHADA");

        String resultado = service.addProduto(1L, 100L, 50.0);

        assertEquals("Venda fechada", resultado);
        verify(vendaProdutos, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve capturar exceção silenciosamente ao falhar adição de produto")
    void deveTratarErroAoAdicionarProduto() {
        Long codVenda = 1L;
        Long codProd = 50L;
        Double valor = 10.0;

        when(vendas.verificaSituacao(codVenda)).thenReturn(VendaSituacao.ABERTA.toString());

       doThrow(new RuntimeException("Erro de banco de dados"))
                .when(vendaProdutos).salvar(any(VendaProduto.class));

        String resultado = service.addProduto(codVenda, codProd, valor);

        assertEquals("ok", resultado);

        verify(vendaProdutos).salvar(any(VendaProduto.class));
    }

    @Test
    @DisplayName("Cenário 1 (Novo): Deve retornar mensagem específica se Venda não for encontrada")
    void deveRetornarErroQuandoVendaNaoExiste() {
        Long codVenda = 99L;
        Long posicaoProd = 5L;

        when(vendas.findByCodigoEquals(codVenda)).thenReturn(null);

        String resultado = service.removeProduto(posicaoProd, codVenda);

        assertEquals("Venda não encontrada", resultado);

        verify(vendaProdutos, never()).removeProduto(anyLong());
    }

    @Test
    @DisplayName("Cenário 2 (Sucesso): Deve remover produto quando Venda está ABERTA")
    void deveRemoverProdutoComSucesso() {
        Long codVenda = 1L;
        Long posicaoProd = 10L;

        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);

        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        String resultado = service.removeProduto(posicaoProd, codVenda);

        assertEquals("ok", resultado);

        verify(vendaProdutos).removeProduto(posicaoProd);
    }

    @Test
    @DisplayName("Cenário 3 (Bloqueio): NÃO deve remover produto quando Venda está FECHADA")
    void naoDeveRemoverProdutoSeVendaFechada() {
        Long codVenda = 1L;
        Long posicaoProd = 10L;

        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.FECHADA);

        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        String resultado = service.removeProduto(posicaoProd, codVenda);

        assertEquals("Venda fechada", resultado);

        verify(vendaProdutos, never()).removeProduto(anyLong());
    }

    @Test
    @DisplayName("Cenário 4 (Exception): Erro interno deve cair no catch e retornar 'ok'")
    void deveTratarErroInterno() {
        Long codVenda = 1L;
        Long posicaoProd = 10L;

        doThrow(new RuntimeException("Erro de conexão"))
                .when(vendas).findByCodigoEquals(codVenda);

        String resultado = service.removeProduto(posicaoProd, codVenda);

        assertEquals("ok", resultado);

        verify(vendaProdutos, never()).removeProduto(anyLong());
    }

    @Test
    @DisplayName("Deve retornar uma lista de vendas quando existirem registros")
    void deveRetornarListaDeVendas() {
        Venda venda1 = new Venda();
        venda1.setCodigo(1L);

        Venda venda2 = new Venda();
        venda2.setCodigo(2L);

        List<Venda> listaMock = Arrays.asList(venda1, venda2);

        when(vendas.findAll()).thenReturn(listaMock);

        List<Venda> resultado = service.lista();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getCodigo());

        verify(vendas).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver registros")
    void deveRetornarListaVazia() {
        when(vendas.findAll()).thenReturn(Collections.emptyList());

        List<Venda> resultado = service.lista();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(vendas).findAll();
    }

    @Test
    @DisplayName("Cenário: Pagamento em DINHEIRO (À Vista) com Sucesso")
    void deveFecharVendaNoDinheiro() {
        Long codVenda = 1L;
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipoDin = new TituloTipo();
        tipoDin.setSigla("DIN");
        titulo.setTipo(tipoDin);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        when(caixas.caixaIsAberto()).thenReturn(true);
        Caixa caixa = new Caixa();
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        try (MockedStatic<Aplicacao> aplicacaoStatic = Mockito.mockStatic(Aplicacao.class)) {
            Aplicacao appInstancia = mock(Aplicacao.class);
            aplicacaoStatic.when(Aplicacao::getInstancia).thenReturn(appInstancia);
            when(appInstancia.getUsuarioAtual()).thenReturn("user_test");
            when(usuarios.buscaUsuario("user_test")).thenReturn(new Usuario());

            String resultado = service.fechaVenda(
                    codVenda,
                    1L,
                    100.0,
                    0.0,
                    0.0,
                    new String[]{"100"},
                    new String[]{"10"}
            );

            assertEquals("Venda finalizada com sucesso", resultado);

            verify(receberService).cadastrar(any(Receber.class));
            verify(lancamentos).lancamento(any(CaixaLancamento.class));
            verify(cartaoLancamento, never()).lancamento(anyDouble(), any());
            verify(produtos).movimentaEstoque(codVenda, EntradaSaida.SAIDA);
        }
    }

    @Test
    @DisplayName("Erro: Deve lançar exceção se tentar pagar em DINHEIRO com CAIXA FECHADO")
    void deveFalharSePagarDinheiroSemCaixaAberto() {
        Long codVenda = 1L;

        Venda vendaMock = new Venda();
        vendaMock.setSituacao(VendaSituacao.ABERTA);
        vendaMock.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaMock);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipoDin = new TituloTipo();
        tipoDin.setSigla("DIN");
        titulo.setTipo(tipoDin);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        when(caixas.caixaIsAberto()).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(
                        codVenda,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100"},
                        new String[]{"1"}
                )
        );

        assertEquals("nenhum caixa aberto", ex.getMessage());
    }

    @Test
    @DisplayName("Cenário: Pagamento em CARTÃO DE CRÉDITO")
    void deveFecharVendaNoCartaoCredito() {
        Long codVenda = 1L;
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipoCart = new TituloTipo();
        tipoCart.setSigla("CARTCRED");
        titulo.setTipo(tipoCart);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        service.fechaVenda(
                codVenda,
                1L,
                100.0,
                0.0,
                0.0,
                new String[]{"100"},
                new String[]{"20"});

        verify(cartaoLancamento).lancamento(eq(100.0), eq(Optional.of(titulo)));
        verify(lancamentos, never()).lancamento(any());
    }

    @Test
    @DisplayName("Cenário: Pagamento em CARTÃO DE DÉBITO")
    void deveFecharVendaNoCartaoDebito() {
        Long codVenda = 1L;
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipoCart = new TituloTipo();
        tipoCart.setSigla("CARTDEB");
        titulo.setTipo(tipoCart);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        service.fechaVenda(
                codVenda,
                1L,
                100.0,
                0.0,
                0.0,
                new String[]{"100"},
                new String[]{"20"});

        verify(cartaoLancamento).lancamento(eq(100.0), eq(Optional.of(titulo)));
        verify(lancamentos, never()).lancamento(any());
    }

    @Test
    @DisplayName("Cenário: Pagamento à Vista mas com Título desconhecido (Nem Din, Nem Cartão)")
    void deveIgnorarLancamentoSeTituloNaoForDinheiroNemCartao() {
        Long codVenda = 1L;
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipoCart = new TituloTipo();
        tipoCart.setSigla("CHEQUE");
        titulo.setTipo(tipoCart);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        service.fechaVenda(
                codVenda,
                1L,
                100.0,
                0.0,
                0.0,
                new String[]{"100"},
                new String[]{"99"});

        verify(cartaoLancamento, never()).lancamento(anyDouble(), any());
        verify(lancamentos, never()).lancamento(any());
        verify(vendas).fechaVenda(eq(codVenda), eq(VendaSituacao.FECHADA), anyDouble(), anyDouble(), anyDouble(), any(), any());
    }

    @Test
    @DisplayName("Erro: Deve lançar exceção ao tentar vender A PRAZO sem um cliente vinculado")
    void deveFalharSeVendaPrazoSemCliente() {
        Long codVenda = 1L;

        Venda vendaMock = mock(Venda.class);
        when(vendaMock.isAberta()).thenReturn(true);
        when(vendaMock.getPessoa()).thenReturn(null);

        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaMock);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("30");

        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(new Titulo()));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(
                        codVenda,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100"},
                        new String[]{"1"}
                )
        );

        assertEquals("Venda sem cliente, verifique", ex.getMessage());
    }



    @Test
    @DisplayName("Cenário: Pagamento A PRAZO (Duas Parcelas)")
    void deveFecharVendaAPrazo() {
        Long codVenda = 1L;
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("30/60");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipo = new TituloTipo();
        tipo.setSigla("CARTCRED");
        titulo.setTipo(tipo);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        service.fechaVenda(
                codVenda,
                1L,
                100.0,
                0.0,
                0.0,
                new String[]{"50", "50"},
                new String[]{"1", "1"}
        );

        verify(parcelas, times(2)).gerarParcela(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(),
                any(Receber.class), anyInt(), anyInt(), any(Timestamp.class), any(java.sql.Date.class)
        );
    }

    @Test
    @DisplayName("Erro: Deve lançar exceção se valor dos produtos for Zero ou Negativo")
    void deveLancarErroSeValorProdutosInvalido() {
        Long codVenda = 1L;

        Venda vendaMock = new Venda();
        vendaMock.setSituacao(VendaSituacao.ABERTA);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaMock);

        RuntimeException exZero = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(
                        codVenda,
                        1L,
                        0.0,
                        0.0,
                        0.0,
                        new String[]{},
                        new String[]{}
                )
        );
        assertEquals("Venda sem valor, verifique", exZero.getMessage());

        RuntimeException exNegativo = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(
                        codVenda,
                        1L,
                        -10.50,
                        0.0,
                        0.0,
                        new String[]{},
                        new String[]{}
                )
        );
        assertEquals("Venda sem valor, verifique", exNegativo.getMessage());
    }

    @Test
    @DisplayName("Erro: Deve tratar exceção no UPDATE final da venda (fechaVenda)")
    void deveTratarErroUpdateFinalVenda() {
        Long codVenda = 1L;

       Venda vendaMock = criarVendaMock(true);
        when(vendaMock.isAberta()).thenReturn(true);
        when(vendaMock.getPessoa()).thenReturn(new Pessoa());

        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaMock);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        TituloTipo tipoCart = new TituloTipo();
        tipoCart.setSigla("CARTCRED");
        titulo.setTipo(tipoCart);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));
        doThrow(new RuntimeException("Erro de conexão SQL"))
                .when(vendas).fechaVenda(
                        eq(codVenda),
                        eq(VendaSituacao.FECHADA),
                        anyDouble(),
                        anyDouble(),
                        anyDouble(),
                        any(Timestamp.class),
                        eq(pagTipo)
                );

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(
                        codVenda,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100"},
                        new String[]{"20"}
                )
        );

        assertEquals("Erro ao fechar a venda, chame o suporte", ex.getMessage());

        verify(vendas).fechaVenda(
                eq(codVenda),
                eq(VendaSituacao.FECHADA),
                anyDouble(),
                anyDouble(),
                anyDouble(),
                any(Timestamp.class),
                eq(pagTipo)
        );
    }

    @Test
    @DisplayName("Erro: Pagamento A Prazo com valor de parcela vazio")
    void deveFalharAprazoParcelaVazia() {
        Venda vendaMock = criarVendaMock(true);
        vendaMock.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(vendaMock);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("30");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        when(tituloService.busca(anyLong())).thenReturn(Optional.of(new Titulo()));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0, new String[]{""}, new String[]{"1"})
        );

        assertEquals("valor de recebimento invalido", ex.getMessage());
    }

    @Test
    @DisplayName("Erro: Falha ao gerar parcela (A Prazo)")
    void deveTratarErroGerarParcela() {
        Venda venda = criarVendaMock(true);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("30");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(new Titulo()));

        doThrow(new RuntimeException("Erro DB")).when(parcelas).gerarParcela(
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble(),
                any(), anyInt(), anyInt(), any(), any()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0, new String[]{"100"}, new String[]{"1"})
        );

        assertNull(ex.getMessage());
    }

    @Test
    @DisplayName("Erro: Pagamento à Vista com valor de parcela vazio")
    void deveFalharAvistaDinheiroParcelaVazia() {
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        titulo.setTipo(new TituloTipo());
        titulo.getTipo().setSigla("DIN");
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        when(caixas.caixaIsAberto()).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0, new String[]{""}, new String[]{"1"})
        );

        assertEquals("Parcela sem valor, verifique", ex.getMessage());
    }

    @Test
    @DisplayName("Erro: Falha ao realizar lançamento no caixa (À Vista Dinheiro)")
    void deveTratarErroLancamentoCaixa() {
        Venda vendaMock = criarVendaMock(true);
        when(vendas.findByCodigoEquals(1L)).thenReturn(vendaMock);
        when(caixas.caixaIsAberto()).thenReturn(true);
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        Titulo titulo = new Titulo();
        titulo.setTipo(new TituloTipo());
        titulo.getTipo().setSigla("DIN");
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(titulo));

        try (MockedStatic<Aplicacao> appMock = Mockito.mockStatic(Aplicacao.class)) {
            Aplicacao app = mock(Aplicacao.class);
            appMock.when(Aplicacao::getInstancia).thenReturn(app);
            when(app.getUsuarioAtual()).thenReturn("user");
            when(usuarios.buscaUsuario("user")).thenReturn(new Usuario());

            doThrow(new RuntimeException("Erro SQL")).when(lancamentos).lancamento(any(CaixaLancamento.class));

            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0, new String[]{"100"}, new String[]{"1"})
            );

            assertEquals("Erro ao fechar a venda, chame o suporte", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Erro: Deve capturar exceção no cadastro do Receber e lançar mensagem de suporte")
    void deveTratarErroAoCadastrarReceber() {
        Long codVenda = 1L;

        Venda vendaMock = new Venda();
        vendaMock.setSituacao(VendaSituacao.ABERTA);
        vendaMock.setPessoa(new Pessoa());

        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaMock);

        PagamentoTipo pagTipo = new PagamentoTipo();
        pagTipo.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pagTipo);

        doThrow(new RuntimeException("Erro de conexão com banco de dados"))
                .when(receberService).cadastrar(any(Receber.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(
                        codVenda,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{"100"},
                        new String[]{"1"}
                )
        );

        assertEquals("Erro ao fechar a venda, chame o suporte", ex.getMessage());

        verify(receberService).cadastrar(any(Receber.class));
    }

    @Test
    @DisplayName("Erro: Venda Fechada")
    void deveLancarErroSeVendaJaFechada() {
        Venda venda = criarVendaMock(false);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(1L,
                        1L,
                        100.0,
                        0.0,
                        0.0,
                        new String[]{},
                        new String[]{})
        );
        assertEquals("venda fechada", ex.getMessage());
    }

    @Test
    @DisplayName("Erro: Valor das parcelas não bate com total (Dinheiro)")
    void deveLancarErroSeValorParcelasIncorreto() {
        Venda venda = criarVendaMock(true);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);
        when(caixas.caixaIsAberto()).thenReturn(true);

        PagamentoTipo pag = new PagamentoTipo();
        pag.setFormaPagamento("00");
        when(formaPagamentos.busca(anyLong())).thenReturn(pag);

        Titulo t = new Titulo();
        t.setTipo(new TituloTipo());
        t.getTipo().setSigla("DIN");
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(t));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0, new String[]{"90"}, new String[]{"1"})
        );

        assertEquals("Valor das parcelas diferente do valor total de produtos, verifique", ex.getMessage());
    }

    @Test
    @DisplayName("Deve retornar a quantidade de vendas em aberto")
    void deveRetornarQtdVendasEmAberto() {
        when(vendas.qtdVendasEmAberto()).thenReturn(5);

        int qtd = service.qtdAbertos();

        assertEquals(5, qtd);
        verify(vendas).qtdVendasEmAberto();
    }

    private Venda criarVendaMock(boolean aberta) {
        Venda venda = mock(Venda.class);
        when(venda.isAberta()).thenReturn(aberta);
        if (aberta) {
            when(venda.getPessoa()).thenReturn(new Pessoa());
        }
        return venda;
    }
}