package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.ajuste.AjusteStatus;
import net.originmobi.pdv.filter.AjusteFilter;
import net.originmobi.pdv.model.Ajuste;
import net.originmobi.pdv.model.AjusteProduto;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.AjusteRepository;
import net.originmobi.pdv.service.AjusteService;
import net.originmobi.pdv.service.ProdutoService;
import net.originmobi.pdv.singleton.Aplicacao;

/**
 * Testes unitários de {@link AjusteService}.
 *
 * <p>As dependências ({@link AjusteRepository}, {@link ProdutoService} e o
 * singleton {@link Aplicacao}) são substituídas por dublês do Mockito, de modo
 * que nenhum teste acessa o banco de dados nem depende de usuário logado.
 *
 * <p>Técnicas aplicadas:
 * <ul>
 * <li><b>Funcional</b>: partição de equivalência e valor-limite sobre
 * {@code qtd_alteracao} (negativo, 0, 1, positivo) e sobre o status do ajuste
 * (A PROCESSAR / PROCESSADO);</li>
 * <li><b>Estrutural</b>: cada desvio de {@code lista}, {@code processar} e
 * {@code remover} é exercitado nos dois sentidos (critério todas-arestas);</li>
 * <li><b>Baseada em defeitos</b>: asserções sobre os valores efetivamente
 * gravados (observação, data, origem da movimentação, quantidade de chamadas)
 * para matar os mutantes gerados pelo PIT.</li>
 * </ul>
 *
 * <p>Testes marcados com {@code @Tag("defeito")} asserem o comportamento
 * CORRETO esperado e falham de propósito, evidenciando um defeito do código.
 * Eles são excluídos apenas da medição de mutação (ver {@code pom.xml}).
 */
@ExtendWith(MockitoExtension.class)
class AjusteServiceTest {

    private static final Long COD_AJUSTE = 1L;
    private static final String ORIGEM_ESPERADA = "Referente ao ajuste de estoque 1";
    private static final String MSG_SUCESSO = "Ajuste realizado com sucesso";
    private static final String MSG_ERRO_PROCESSAR = "Erro ao tentar processar o ajuste, chame o suporte";

    @InjectMocks
    private AjusteService ajusteService;

    @Mock
    private AjusteRepository ajusteRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private Pageable pageable;

    @Mock
    private Page<Ajuste> pageAjuste;

    private Ajuste ajuste;

    @BeforeEach
    void setUp() {
        ajuste = new Ajuste();
        ajuste.setCodigo(COD_AJUSTE);
        ajuste.setProdutos(new ArrayList<>());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Adiciona ao ajuste um item com o produto e a quantidade informados. */
    private void adicionaProduto(Long codProduto, int qtdAlteracao) {
        Produto produto = new Produto();
        produto.setCodigo(codProduto);

        AjusteProduto item = new AjusteProduto();
        item.setProduto(produto);
        item.setQtd_alteracao(qtdAlteracao);

        ajuste.getProdutos().add(item);
    }

    /** Ajuste com status A PROCESSAR, encontrado pelo repositório. */
    private void dadoAjusteAProcessar() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        when(ajusteRepository.findById(COD_AJUSTE)).thenReturn(Optional.of(ajuste));
    }

    /** Data de hoje no mesmo fuso usado pelo AjusteService (America/Sao_Paulo). */
    private Date hoje() {
        return Date.valueOf(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
    }

    // ------------------------------------------------------------------
    // lista
    // ------------------------------------------------------------------

    @Test
    @DisplayName("lista: com código no filtro, deve consultar apenas pelo código")
    void lista_ComFiltroCodigo_DeveRetornarPaginaFiltrada() {
        AjusteFilter filter = new AjusteFilter();
        filter.setCodigo(10L);
        when(ajusteRepository.lista(10L, pageable)).thenReturn(pageAjuste);

        Page<Ajuste> resultado = ajusteService.lista(pageable, filter);

        assertSame(pageAjuste, resultado);
        verify(ajusteRepository, times(1)).lista(10L, pageable);
        verify(ajusteRepository, never()).lista(any(Pageable.class));
    }

    @Test
    @DisplayName("lista: sem código no filtro, deve listar todos os ajustes")
    void lista_SemFiltroCodigo_DeveRetornarPaginaGeral() {
        AjusteFilter filter = new AjusteFilter();
        when(ajusteRepository.lista(pageable)).thenReturn(pageAjuste);

        Page<Ajuste> resultado = ajusteService.lista(pageable, filter);

        assertSame(pageAjuste, resultado);
        verify(ajusteRepository, times(1)).lista(pageable);
        verify(ajusteRepository, never()).lista(anyLong(), any(Pageable.class));
    }

    // ------------------------------------------------------------------
    // busca
    // ------------------------------------------------------------------

    @Test
    @DisplayName("busca: código existente deve retornar o ajuste")
    void busca_CodigoExistente_DeveRetornarAjuste() {
        when(ajusteRepository.findById(COD_AJUSTE)).thenReturn(Optional.of(ajuste));

        Optional<Ajuste> resultado = ajusteService.busca(COD_AJUSTE);

        assertTrue(resultado.isPresent());
        assertSame(ajuste, resultado.get());
    }

    @Test
    @DisplayName("busca: código inexistente deve retornar Optional vazio")
    void busca_CodigoInexistente_DeveRetornarVazio() {
        when(ajusteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Ajuste> resultado = ajusteService.busca(99L);

        assertFalse(resultado.isPresent());
    }

    // ------------------------------------------------------------------
    // novo
    // ------------------------------------------------------------------

    @Test
    @DisplayName("novo: deve gravar ajuste A PROCESSAR com usuário logado e data de hoje, e retornar o código")
    void novo_DeveGravarAjusteAProcessarComUsuarioEData() {
        Aplicacao aplicacao = mock(Aplicacao.class);
        when(aplicacao.getUsuarioAtual()).thenReturn("gerente");
        when(ajusteRepository.save(any(Ajuste.class))).thenReturn(ajuste);

        Long codigoGerado;
        try (MockedStatic<Aplicacao> aplicacaoEstatica = mockStatic(Aplicacao.class)) {
            aplicacaoEstatica.when(Aplicacao::getInstancia).thenReturn(aplicacao);
            codigoGerado = ajusteService.novo();
        }

        assertEquals(COD_AJUSTE, codigoGerado);

        ArgumentCaptor<Ajuste> captor = ArgumentCaptor.forClass(Ajuste.class);
        verify(ajusteRepository, times(1)).save(captor.capture());
        Ajuste gravado = captor.getValue();
        assertEquals(AjusteStatus.APROCESSAR, gravado.getStatus());
        assertEquals("gerente", gravado.getUsuario());
        assertEquals(hoje(), gravado.getData_cadastro());
    }

    // ------------------------------------------------------------------
    // processar — caminhos de sucesso
    // ------------------------------------------------------------------

    @Test
    @DisplayName("processar: quantidade positiva deve gerar ENTRADA de estoque e marcar o ajuste como PROCESSADO")
    void processar_ComQtdPositiva_DeveProcessarEntrada() {
        dadoAjusteAProcessar();
        adicionaProduto(100L, 5);

        String resultado = ajusteService.processar(COD_AJUSTE, "Ajuste de entrada");

        assertEquals(MSG_SUCESSO, resultado);
        verify(produtoService, times(1)).ajusteEstoque(
                eq(100L), eq(5), eq(EntradaSaida.ENTRADA), eq(ORIGEM_ESPERADA), eq(hoje()));
        assertEquals(AjusteStatus.PROCESSADO, ajuste.getStatus());
        assertEquals("Ajuste de entrada", ajuste.getObservacao());
        assertEquals(hoje(), ajuste.getData_processamento());
        verify(ajusteRepository, times(1)).save(ajuste);
    }

    @Test
    @DisplayName("processar: quantidade negativa deve gerar SAIDA de estoque")
    void processar_ComQtdNegativa_DeveProcessarSaida() {
        dadoAjusteAProcessar();
        adicionaProduto(200L, -3);

        String resultado = ajusteService.processar(COD_AJUSTE, "Ajuste de saída");

        assertEquals(MSG_SUCESSO, resultado);
        verify(produtoService, times(1)).ajusteEstoque(
                eq(200L), eq(-3), eq(EntradaSaida.SAIDA), eq(ORIGEM_ESPERADA), eq(hoje()));
        assertEquals(AjusteStatus.PROCESSADO, ajuste.getStatus());
    }

    @Test
    @DisplayName("processar (valor-limite): quantidade 1 é o menor valor tratado como ENTRADA")
    void processar_ComQtdUm_DeveProcessarEntrada() {
        dadoAjusteAProcessar();
        adicionaProduto(100L, 1);

        ajusteService.processar(COD_AJUSTE, "Limite superior");

        verify(produtoService, times(1)).ajusteEstoque(
                eq(100L), eq(1), eq(EntradaSaida.ENTRADA), eq(ORIGEM_ESPERADA), eq(hoje()));
    }

    @Test
    @DisplayName("processar (valor-limite): quantidade 0 é tratada como SAIDA (condição qtd > 0)")
    void processar_ComQtdZero_DeveProcessarComoSaida() {
        dadoAjusteAProcessar();
        adicionaProduto(100L, 0);

        ajusteService.processar(COD_AJUSTE, "Limite zero");

        verify(produtoService, times(1)).ajusteEstoque(
                eq(100L), eq(0), eq(EntradaSaida.SAIDA), eq(ORIGEM_ESPERADA), eq(hoje()));
    }

    @Test
    @DisplayName("processar: com vários produtos, deve ajustar o estoque de cada um, com o tipo correto")
    void processar_ComVariosProdutos_DeveAjustarCadaUm() {
        dadoAjusteAProcessar();
        adicionaProduto(100L, 5);
        adicionaProduto(200L, -2);
        adicionaProduto(300L, 7);

        String resultado = ajusteService.processar(COD_AJUSTE, "Inventário");

        assertEquals(MSG_SUCESSO, resultado);
        verify(produtoService).ajusteEstoque(eq(100L), eq(5), eq(EntradaSaida.ENTRADA), eq(ORIGEM_ESPERADA), eq(hoje()));
        verify(produtoService).ajusteEstoque(eq(200L), eq(-2), eq(EntradaSaida.SAIDA), eq(ORIGEM_ESPERADA), eq(hoje()));
        verify(produtoService).ajusteEstoque(eq(300L), eq(7), eq(EntradaSaida.ENTRADA), eq(ORIGEM_ESPERADA), eq(hoje()));
        verify(produtoService, times(3)).ajusteEstoque(anyLong(), anyInt(), any(), anyString(), any());
        verify(ajusteRepository, times(1)).save(ajuste);
    }

    @Test
    @DisplayName("processar: ajuste sem produtos não movimenta estoque, mas é marcado como PROCESSADO")
    void processar_SemProdutos_DeveApenasMarcarProcessado() {
        dadoAjusteAProcessar();

        String resultado = ajusteService.processar(COD_AJUSTE, "Sem itens");

        assertEquals(MSG_SUCESSO, resultado);
        verifyNoInteractions(produtoService);
        assertEquals(AjusteStatus.PROCESSADO, ajuste.getStatus());
        verify(ajusteRepository, times(1)).save(ajuste);
    }

    // ------------------------------------------------------------------
    // processar — caminhos de erro
    // ------------------------------------------------------------------

    @Test
    @DisplayName("processar: ajuste já PROCESSADO deve ser recusado sem mexer no estoque nem gravar")
    void processar_AjusteJaProcessado_DeveLancarExcecao() {
        ajuste.setStatus(AjusteStatus.PROCESSADO);
        adicionaProduto(100L, 5);
        when(ajusteRepository.findById(COD_AJUSTE)).thenReturn(Optional.of(ajuste));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.processar(COD_AJUSTE, "Processamento indevido"));

        assertEquals("Ajuste já processado", erro.getMessage());
        verifyNoInteractions(produtoService);
        verify(ajusteRepository, never()).save(any(Ajuste.class));
        assertNull(ajuste.getObservacao());
    }

    @Test
    @DisplayName("processar: falha no ProdutoService deve virar mensagem de suporte e não gravar o ajuste")
    void processar_ErroAoAjustarEstoque_DeveLancarExcecaoSuporte() {
        dadoAjusteAProcessar();
        adicionaProduto(100L, 5);
        doThrow(new RuntimeException("Erro BD no Produto"))
                .when(produtoService).ajusteEstoque(anyLong(), anyInt(), any(), anyString(), any());

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.processar(COD_AJUSTE, "Tentativa com falha"));

        assertEquals(MSG_ERRO_PROCESSAR, erro.getMessage());
        assertEquals(AjusteStatus.APROCESSAR, ajuste.getStatus());
        verify(ajusteRepository, never()).save(any(Ajuste.class));
    }

    @Test
    @DisplayName("processar: falha no segundo produto deve interromper o laço antes do terceiro")
    void processar_ErroNoSegundoProduto_DeveInterromperProcessamento() {
        dadoAjusteAProcessar();
        adicionaProduto(100L, 5);
        adicionaProduto(200L, 3);
        adicionaProduto(300L, 1);
        doNothing()
                .doThrow(new RuntimeException("Produto não controla estoque"))
                .when(produtoService).ajusteEstoque(anyLong(), anyInt(), any(), anyString(), any());

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.processar(COD_AJUSTE, "Falha no meio"));

        assertEquals(MSG_ERRO_PROCESSAR, erro.getMessage());
        verify(produtoService, times(2)).ajusteEstoque(anyLong(), anyInt(), any(), anyString(), any());
        verify(produtoService, never()).ajusteEstoque(eq(300L), anyInt(), any(), anyString(), any());
        verify(ajusteRepository, never()).save(any(Ajuste.class));
    }

    @Test
    @DisplayName("processar: falha ao gravar o ajuste processado deve virar mensagem de suporte")
    void processar_ErroAoSalvarAjuste_DeveLancarExcecaoSuporte() {
        dadoAjusteAProcessar();
        doThrow(new RuntimeException("Erro no save")).when(ajusteRepository).save(any(Ajuste.class));

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.processar(COD_AJUSTE, "Erro no save final"));

        assertEquals(MSG_ERRO_PROCESSAR, erro.getMessage());
    }

    @Test
    @Tag("defeito")
    @DisplayName("processar: código inexistente deve gerar erro tratado [DEFEITO: NoSuchElementException de Optional.get()]")
    void processar_CodigoInexistente_DeveLancarErroTratado() {
        when(ajusteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.processar(99L, "Código que não existe"));

        // Esperado: exceção de negócio com mensagem para o usuário.
        // Obtido: java.util.NoSuchElementException ("No value present"),
        // porque o código chama Optional.get() sem verificar se o ajuste existe.
        assertNotEquals(NoSuchElementException.class, erro.getClass(),
                "processar deveria tratar ajuste inexistente, mas lançou NoSuchElementException");
        verifyNoInteractions(produtoService);
    }

    // ------------------------------------------------------------------
    // remover
    // ------------------------------------------------------------------

    @Test
    @DisplayName("remover: ajuste já PROCESSADO não pode ser removido")
    void remover_AjusteJaProcessado_DeveLancarExcecao() {
        ajuste.setStatus(AjusteStatus.PROCESSADO);

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.remover(ajuste));

        assertEquals("O ajuste já esta processado", erro.getMessage());
        verifyNoInteractions(ajusteRepository);
    }

    @Test
    @DisplayName("remover: ajuste A PROCESSAR deve ser excluído pelo código")
    void remover_AjusteAProcessar_DeveRemoverComSucesso() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);

        assertDoesNotThrow(() -> ajusteService.remover(ajuste));

        verify(ajusteRepository, times(1)).deleteById(COD_AJUSTE);
    }

    @Test
    @DisplayName("remover: falha do banco ao excluir deve virar mensagem tratada")
    void remover_ErroAoDeletar_DeveLancarExcecao() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        doThrow(new RuntimeException("Erro BD")).when(ajusteRepository).deleteById(COD_AJUSTE);

        RuntimeException erro = assertThrows(RuntimeException.class,
                () -> ajusteService.remover(ajuste));

        assertEquals("Erro ao tentar cancelar o ajuste", erro.getMessage());
    }
}