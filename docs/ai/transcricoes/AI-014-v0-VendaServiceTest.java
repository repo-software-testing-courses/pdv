package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.PagamentoTipo;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.ParcelaService;
import net.originmobi.pdv.service.PagamentoTipoService;
import net.originmobi.pdv.service.ProdutoService;
import net.originmobi.pdv.service.ReceberService;
import net.originmobi.pdv.service.UsuarioService;
import net.originmobi.pdv.service.VendaService;
import net.originmobi.pdv.service.VendaProdutoService;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;

/**
 * Testes unitarios de {@link VendaService}.
 *
 * <p>Perspectiva de testador buscando defeitos: as assercoes refletem o
 * <b>contrato esperado</b> do metodo, nao o comportamento atual do codigo.
 * Falhas sao evidencias de defeitos a reportar.
 */
@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    private static final String USUARIO_LOGADO = "gerente";

    // ------------------------------------------------------------------
    // Mocks de todas as dependencias injetadas em VendaService
    // ------------------------------------------------------------------

    @Mock
    private VendaRepository vendas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private VendaProdutoService vendaProdutos;

    @Mock
    private PagamentoTipoService formaPagamentos;

    @Mock
    private CaixaService caixas;

    @Mock
    private ReceberService receberServ;

    @Mock
    private ParcelaService parcelas;

    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private TituloService tituloService;

    @Mock
    private CartaoLancamentoService cartaoLancamento;

    @Mock
    private ProdutoService produtos;

    @InjectMocks
    private VendaService service;

    /**
     * Mock estatico do singleton Aplicacao.
     * Fechado no @AfterEach para nao vazar entre testes.
     */
    private MockedStatic<Aplicacao> aplicacaoEstatica;

    @AfterEach
    void fechaMockEstatico() {
        if (aplicacaoEstatica != null)
            aplicacaoEstatica.close();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Configura o singleton {@link Aplicacao} para retornar USUARIO_LOGADO
     * e o {@link UsuarioService} para devolver um {@link Usuario} correspondente.
     *
     * Deve ser chamado SOMENTE nos testes que exercitam fluxos que passam pelo
     * singleton — evita stub nao utilizado no modo estrito do Mockito.
     */
    private Usuario dadoUsuarioAutenticado() {
        Usuario usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setUser(USUARIO_LOGADO);

        when(usuarios.buscaUsuario(anyString())).thenReturn(usuario);

        Aplicacao aplicacaoMock = mock(Aplicacao.class);
        when(aplicacaoMock.getUsuarioAtual()).thenReturn(USUARIO_LOGADO);

        aplicacaoEstatica = mockStatic(Aplicacao.class);
        aplicacaoEstatica.when(Aplicacao::getInstancia).thenReturn(aplicacaoMock);

        return usuario;
    }

    /**
     * Cria uma {@link Venda} sem codigo (venda nova, ainda nao persistida).
     */
    private Venda novaVenda() {
        // codigo null == venda nova para o abreVenda
        return new Venda();
    }

    /**
     * Cria uma {@link Venda} ja existente, com codigo e dados de atualizacao.
     */
    private Venda vendaExistente(Long codigo) {
        Venda venda = new Venda();
        venda.setCodigo(codigo);
        venda.setPessoa(new Pessoa());
        venda.setObservacao("obs existente");
        return venda;
    }

    // ------------------------------------------------------------------
    // abreVenda — ramo 1: venda nova (codigo == null)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("abreVenda: venda nova deve setar situacao ABERTA")
    void abreVenda_vendaNova_deveSetarSituacaoAberta() {
        dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        service.abreVenda(venda);

        assertEquals(VendaSituacao.ABERTA, venda.getSituacao(),
                "Situacao deve ser ABERTA para uma venda nova");
    }

    @Test
    @DisplayName("abreVenda: venda nova deve inicializar valor_produtos com 0.00")
    void abreVenda_vendaNova_deveInicializarValorProdutosZero() {
        dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        service.abreVenda(venda);

        assertEquals(0.00, venda.getValor_produtos(),
                "valor_produtos deve ser inicializado em 0.00 na abertura");
    }

    @Test
    @DisplayName("abreVenda: venda nova deve preencher data_cadastro")
    void abreVenda_vendaNova_devePreencherDataCadastro() {
        dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        service.abreVenda(venda);

        assertNotNull(venda.getData_cadastro(),
                "data_cadastro deve ser preenchida ao abrir uma venda nova");
    }

    @Test
    @DisplayName("abreVenda: venda nova deve vincular o usuario autenticado")
    void abreVenda_vendaNova_deveVincularUsuarioAutenticado() {
        Usuario usuarioEsperado = dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        service.abreVenda(venda);

        assertEquals(usuarioEsperado, venda.getUsuario(),
                "O usuario logado deve ser vinculado a venda");
    }

    @Test
    @DisplayName("abreVenda: venda nova deve chamar vendas.save")
    void abreVenda_vendaNova_deveChamarSave() {
        dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        service.abreVenda(venda);

        verify(vendas).save(venda);
    }

    @Test
    @DisplayName("abreVenda: venda nova deve retornar o codigo gerado pelo repositorio apos o save")
    void abreVenda_vendaNova_deveRetornarCodigoGeradoPeloSave() {
        dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        // Simula o comportamento real do JPA: save preenche o @Id na propria entidade
        when(vendas.save(any(Venda.class))).thenAnswer(inv -> {
            Venda v = inv.getArgument(0);
            v.setCodigo(42L);
            return v;
        });

        Long resultado = service.abreVenda(venda);

        assertEquals(42L, resultado,
                "O codigo retornado deve ser o gerado pelo repositorio apos o save");
    }

    /**
     * DEFEITO documentado: quando vendas.save() lanca excecao, o catch
     * a descarta silenciosamente com {@code e.getStackTrace()} (sem relancar,
     * sem logar) e o metodo retorna {@code null}.
     *
     * <p>O chamador nao recebe nenhuma indicacao de falha — a venda
     * nao foi persistida mas o fluxo continua normalmente.
     *
     * <p>Comportamento esperado: propagar RuntimeException para que o chamador
     * possa tratar o erro (ex.: exibir mensagem ao usuario, rollback).
     *
     * <p>Este teste FALHA (assertThrows nao satisfeito) expondo o defeito.
     */
    @Test
    @DisplayName("abreVenda: venda nova — falha no save deve lancar excecao ao chamador [DEFEITO: excecao silenciada]")
    void abreVenda_vendaNova_saveComExcecao_devePropagarErro() {
        dadoUsuarioAutenticado();
        Venda venda = novaVenda();

        when(vendas.save(any(Venda.class))).thenThrow(new RuntimeException("falha no banco"));

        // Comportamento CORRETO esperado: falha no save deve chegar ao chamador.
        // O codigo atual SILENCIA a excecao — este assertThrows vai FALHAR,
        // evidenciando o defeito para registro como Issue.
        assertThrows(RuntimeException.class,
                () -> service.abreVenda(venda));
    }

    // ------------------------------------------------------------------
    // abreVenda — ramo 2: venda existente (codigo != null)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("abreVenda: venda existente deve chamar updateDadosVenda com pessoa, observacao e codigo")
    void abreVenda_vendaExistente_deveChamarUpdateDadosVenda() {
        Venda venda = vendaExistente(10L);

        service.abreVenda(venda);

        verify(vendas).updateDadosVenda(venda.getPessoa(), venda.getObservacao(), 10L);
    }

    @Test
    @DisplayName("abreVenda: venda existente nao deve chamar save")
    void abreVenda_vendaExistente_naoDeveChamarSave() {
        Venda venda = vendaExistente(10L);

        service.abreVenda(venda);

        verify(vendas, never()).save(any());
    }

    @Test
    @DisplayName("abreVenda: venda existente deve retornar o codigo original")
    void abreVenda_vendaExistente_deveRetornarCodigoOriginal() {
        Venda venda = vendaExistente(10L);

        Long resultado = service.abreVenda(venda);

        assertEquals(10L, resultado,
                "O codigo retornado deve ser o mesmo da venda existente");
    }

    @Test
    @DisplayName("abreVenda: venda existente nao deve alterar situacao nem valor_produtos")
    void abreVenda_vendaExistente_naoDeveAlterarCamposDaVenda() {
        Venda venda = vendaExistente(10L);
        // venda existente nao tem situacao/valor_produtos definidos aqui —
        // o service nao deve tocar esses campos no ramo de atualizacao
        VendaSituacao situacaoAntes = venda.getSituacao();
        Double valorAntes = venda.getValor_produtos();

        service.abreVenda(venda);

        assertEquals(situacaoAntes, venda.getSituacao(),
                "Situacao nao deve ser alterada ao atualizar venda existente");
        assertEquals(valorAntes, venda.getValor_produtos(),
                "valor_produtos nao deve ser alterado ao atualizar venda existente");
    }

    @Test
    @DisplayName("abreVenda: venda existente nao deve consultar o usuario autenticado")
    void abreVenda_vendaExistente_naoDeveConsultarUsuario() {
        Venda venda = vendaExistente(10L);

        service.abreVenda(venda);

        // O ramo de venda existente nao precisa do usuario logado
        verify(usuarios, never()).buscaUsuario(anyString());
    }

    // ------------------------------------------------------------------
    // busca
    // ------------------------------------------------------------------

    @Test
    @DisplayName("busca: filtro com codigo deve chamar findByCodigoIn")
    void busca_filtroComCodigo_deveChamarFindByCodigoIn() {
        VendaFilter filter = new VendaFilter();
        filter.setCodigo(5L);
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        service.busca(filter, "ABERTA", pageable);

        verify(vendas).findByCodigoIn(5L, pageable);
        verify(vendas, never()).findBySituacaoEquals(any(), any());
    }

    @Test
    @DisplayName("busca: filtro sem codigo e situacao ABERTA deve chamar findBySituacaoEquals com ABERTA")
    void busca_semCodigo_situacaoAberta_deveFiltrarPorSituacaoAberta() {
        VendaFilter filter = new VendaFilter();
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        service.busca(filter, "ABERTA", pageable);

        verify(vendas).findBySituacaoEquals(VendaSituacao.ABERTA, pageable);
        verify(vendas, never()).findByCodigoIn(any(), any());
    }

    @Test
    @DisplayName("busca: filtro sem codigo e situacao diferente de ABERTA deve chamar findBySituacaoEquals com FECHADA")
    void busca_semCodigo_situacaoFechada_deveFiltrarPorSituacaoFechada() {
        VendaFilter filter = new VendaFilter();
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        service.busca(filter, "FECHADA", pageable);

        verify(vendas).findBySituacaoEquals(VendaSituacao.FECHADA, pageable);
    }

    // ------------------------------------------------------------------
    // addProduto
    // ------------------------------------------------------------------

    @Test
    @DisplayName("addProduto: venda aberta deve salvar o produto e retornar ok")
    void addProduto_vendaAberta_deveSalvarProdutoERetornarOk() {
        when(vendas.verificaSituacao(1L)).thenReturn(VendaSituacao.ABERTA.toString());

        String resultado = service.addProduto(1L, 10L, 0.0);

        verify(vendaProdutos).salvar(any());
        assertEquals("ok", resultado);
    }

    @Test
    @DisplayName("addProduto: venda fechada deve retornar 'Venda fechada' sem salvar")
    void addProduto_vendaFechada_deveRetornarMensagemSemSalvar() {
        when(vendas.verificaSituacao(1L)).thenReturn(VendaSituacao.FECHADA.toString());

        String resultado = service.addProduto(1L, 10L, 0.0);

        assertEquals("Venda fechada", resultado);
        verify(vendaProdutos, never()).salvar(any());
    }

    /**
     * DEFEITO documentado: quando vendaProdutos.salvar() lanca excecao, o catch
     * a descarta com e.getStackTrace() e o metodo retorna "ok" — como se o produto
     * tivesse sido adicionado com sucesso. O chamador nao tem como detectar a falha.
     *
     * Este teste documenta o defeito: o esperado correto seria lancar excecao
     * ou retornar uma mensagem de erro, nao "ok".
     */
    @Test
    @DisplayName("addProduto: falha no salvar deve propagar erro [DEFEITO: excecao silenciada, retorna ok]")
    void addProduto_salvarComExcecao_devePropagarErro() {
        when(vendas.verificaSituacao(1L)).thenReturn(VendaSituacao.ABERTA.toString());
        doThrow(new RuntimeException("erro ao salvar"))
                .when(vendaProdutos).salvar(any());

        // Comportamento CORRETO: deveria lancar excecao ou retornar mensagem de erro.
        // Comportamento ATUAL (DEFEITO): retorna "ok" silenciosamente.
        // Este assertThrows vai FALHAR, evidenciando o defeito.
        assertThrows(RuntimeException.class,
                () -> service.addProduto(1L, 10L, 0.0));
    }

    // ------------------------------------------------------------------
    // removeProduto
    // ------------------------------------------------------------------

    @Test
    @DisplayName("removeProduto: venda aberta deve remover o produto e retornar ok")
    void removeProduto_vendaAberta_deveRemoverProduto() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        String resultado = service.removeProduto(5L, 1L);

        verify(vendaProdutos).removeProduto(5L);
        assertEquals("ok", resultado);
    }

    /**
     * DEFEITO documentado: quando a venda esta fechada, o codigo executa
     * {@code return "Venda fechada"} dentro do bloco try — mas esse return
     * nao encerra o metodo porque o fluxo cai no finally implicito do try
     * e segue para o {@code return "ok"} fora do try.
     *
     * Na pratica, remover produto de uma venda fechada retorna "ok" em vez
     * de "Venda fechada". O chamador (controller) interpreta isso como sucesso.
     *
     * Este teste vai FALHAR ao asserir "Venda fechada", expondo o defeito.
     */
    @Test
    @DisplayName("removeProduto: venda fechada deve retornar 'Venda fechada' sem remover [DEFEITO: sempre retorna ok]")
    void removeProduto_vendaFechada_deveRetornarMensagemErro() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.FECHADA);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        String resultado = service.removeProduto(5L, 1L);

        // Comportamento CORRETO esperado: "Venda fechada"
        // Comportamento ATUAL (DEFEITO): "ok" — o return interno ao try é ignorado
        assertEquals("Venda fechada", resultado,
                "DEFEITO: venda fechada deve retornar 'Venda fechada', nao 'ok'");
        verify(vendaProdutos, never()).removeProduto(any());
    }

    @Test
    @DisplayName("removeProduto: venda fechada nao deve chamar removeProduto")
    void removeProduto_vendaFechada_naoDeveRemoverProduto() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.FECHADA);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        service.removeProduto(5L, 1L);

        verify(vendaProdutos, never()).removeProduto(any());
    }

    // ------------------------------------------------------------------
    // fechaVenda — guards (validacoes iniciais)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("fechaVenda: venda ja fechada deve lancar RuntimeException 'venda fechada'")
    void fechaVenda_vendaFechada_deveLancarExcecao() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.FECHADA);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                        new String[]{"100.0"}, new String[]{"1"}));

        assertEquals("venda fechada", ex.getMessage());
    }

    @Test
    @DisplayName("fechaVenda: vlprodutos igual a zero deve lancar RuntimeException")
    void fechaVenda_valorProdutosZero_deveLancarExcecao() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.fechaVenda(1L, 1L, 0.0, 0.0, 0.0,
                        new String[]{"0.0"}, new String[]{"1"}));

        assertEquals("Venda sem valor, verifique", ex.getMessage());
    }

    @Test
    @DisplayName("fechaVenda: vlprodutos negativo deve lancar RuntimeException")
    void fechaVenda_valorProdutosNegativo_deveLancarExcecao() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.fechaVenda(1L, 1L, -10.0, 0.0, 0.0,
                        new String[]{"10.0"}, new String[]{"1"}));

        assertEquals("Venda sem valor, verifique", ex.getMessage());
    }

    @Test
    @DisplayName("fechaVenda: caixa fechado em pagamento a vista dinheiro deve lancar RuntimeException")
    void fechaVenda_caixaFechado_aVistaDinheiro_deveLancarExcecao() {
        // Arrange
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão
        when(caixas.caixaIsAberto()).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                        new String[]{"100.0"}, new String[]{"1"}));

        assertEquals("nenhum caixa aberto", ex.getMessage());
    }

    @Test
    @DisplayName("fechaVenda: venda a prazo sem cliente deve lancar RuntimeException")
    void fechaVenda_aPrazoSemCliente_deveLancarExcecao() {
        // Arrange: venda sem pessoa
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        // pessoa == null
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("30"); // a prazo
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                        new String[]{"100.0"}, new String[]{"1"}));

        assertEquals("Venda sem cliente, verifique", ex.getMessage());
    }

    // ------------------------------------------------------------------
    // fechaVenda — caminho feliz: a vista em dinheiro
    // ------------------------------------------------------------------

    @Test
    @DisplayName("fechaVenda: a vista em dinheiro deve realizar lancamento no caixa")
    void fechaVenda_aVistaDinheiro_deveRealizarLancamentoNoCaixa() {
        dadoUsuarioAutenticado();

        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão
        when(caixas.caixaIsAberto()).thenReturn(true);

        Caixa caixa = new Caixa();
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        verify(lancamentos).lancamento(any(CaixaLancamento.class));
    }

    @Test
    @DisplayName("fechaVenda: a vista em dinheiro deve retornar mensagem de sucesso")
    void fechaVenda_aVistaDinheiro_deveRetornarMensagemSucesso() {
        dadoUsuarioAutenticado();

        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão
        when(caixas.caixaIsAberto()).thenReturn(true);
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

        String resultado = service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        assertEquals("Venda finalizada com sucesso", resultado);
    }

    @Test
    @DisplayName("fechaVenda: a vista em dinheiro deve chamar movimentaEstoque com SAIDA")
    void fechaVenda_aVistaDinheiro_deveChamarMovimentaEstoque() {
        dadoUsuarioAutenticado();

        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão
        when(caixas.caixaIsAberto()).thenReturn(true);
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

        service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        verify(produtos).movimentaEstoque(1L, EntradaSaida.SAIDA);
    }

    /**
     * DEFEITO documentado: em fechaVenda, a chamada a vendas.fechaVenda() esta
     * DENTRO do loop for(formaPagar). Com pagamento unico o efeito e o mesmo,
     * mas com multiplas formas de pagamento a venda sera fechada N vezes.
     * Alem disso, confirma que a venda e de fato marcada como FECHADA.
     */
    @Test
    @DisplayName("fechaVenda: a vista em dinheiro deve chamar vendas.fechaVenda com situacao FECHADA")
    void fechaVenda_aVistaDinheiro_deveChamarFechaVendaComSituacaoFechada() {
        dadoUsuarioAutenticado();

        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão
        when(caixas.caixaIsAberto()).thenReturn(true);
        when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

        service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        verify(vendas).fechaVenda(
                eq(1L),
                eq(VendaSituacao.FECHADA),
                any(), any(), any(), any(), any());
    }

    // ------------------------------------------------------------------
    // fechaVenda — caminho feliz: cartao debito e credito
    // ------------------------------------------------------------------

    @Test
    @DisplayName("fechaVenda: pagamento com cartao debito deve chamar cartaoLancamento.lancamento")
    void fechaVenda_cartaoDebito_deveChamarCartaoLancamento() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("CARTDEB");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão

        service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        verify(cartaoLancamento).lancamento(
                eq(100.0),
                eq(Optional.of(titulo)));
    }

    @Test
    @DisplayName("fechaVenda: pagamento com cartao credito deve chamar cartaoLancamento.lancamento")
    void fechaVenda_cartaoCredito_deveChamarCartaoLancamento() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("00");
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("CARTCRED");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão

        service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        verify(cartaoLancamento).lancamento(
                eq(100.0),
                eq(Optional.of(titulo)));
    }

    // ------------------------------------------------------------------
    // fechaVenda — caminho feliz: a prazo
    // ------------------------------------------------------------------

    @Test
    @DisplayName("fechaVenda: a prazo deve chamar parcelas.gerarParcela com sequencia iniciando em 1")
    void fechaVenda_aPrazo_deveGerarParcela() {
        Venda venda = new Venda();
        venda.setSituacao(VendaSituacao.ABERTA);
        venda.setPessoa(new Pessoa());
        when(vendas.findByCodigoEquals(1L)).thenReturn(venda);

        PagamentoTipo formaPagamento = new PagamentoTipo();
        formaPagamento.setFormaPagamento("30"); // 30 dias
        when(formaPagamentos.busca(1L)).thenReturn(formaPagamento);

        TituloTipo tipoTitulo = new TituloTipo();
        tipoTitulo.setSigla("DIN");
        Titulo titulo = new Titulo();
        titulo.setTipo(tipoTitulo);
        when(tituloService.busca(1L)).thenReturn(Optional.of(titulo));

        // receberServ.cadastrar é void — mock não lança exceção por padrão

        service.fechaVenda(1L, 1L, 100.0, 0.0, 0.0,
                new String[]{"100.0"}, new String[]{"1"});

        verify(parcelas).gerarParcela(
                any(), any(), any(), any(), any(), any(),
                anyInt(),
                eq(1), // sequencia inicial = 1
                any(), any());
    }

    // ------------------------------------------------------------------
    // qtdAbertos
    // ------------------------------------------------------------------

    @Test
    @DisplayName("qtdAbertos: deve delegar para vendas.qtdVendasEmAberto e retornar o valor")
    void qtdAbertos_deveDelegarParaRepositorioERetornarValor() {
        when(vendas.qtdVendasEmAberto()).thenReturn(7);

        int resultado = service.qtdAbertos();

        assertEquals(7, resultado,
                "O valor retornado deve ser o mesmo do repositorio");
        verify(vendas).qtdVendasEmAberto();
    }
}
