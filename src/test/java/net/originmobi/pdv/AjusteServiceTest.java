package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

@ExtendWith(MockitoExtension.class)
public class AjusteServiceTest {

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
        ajuste.setCodigo(1L);
    }

    @Test
    @DisplayName("Deve listar ajustes aplicando filtro por código quando informado")
    void lista_ComFiltroCodigo_DeveRetornarPaginaFiltrada() {
        AjusteFilter filter = new AjusteFilter();
        filter.setCodigo(10L);
        when(ajusteRepository.lista(10L, pageable)).thenReturn(pageAjuste);

        Page<Ajuste> resultado = ajusteService.lista(pageable, filter);

        assertNotNull(resultado);
        verify(ajusteRepository, times(1)).lista(10L, pageable);
    }

    @Test
    @DisplayName("Deve listar todos os ajustes quando o filtro de código não for informado")
    void lista_SemFiltroCodigo_DeveRetornarPaginaGeral() {
        AjusteFilter filter = new AjusteFilter();
        when(ajusteRepository.lista(pageable)).thenReturn(pageAjuste);

        Page<Ajuste> resultado = ajusteService.lista(pageable, filter);

        assertNotNull(resultado);
        verify(ajusteRepository, times(1)).lista(pageable);
    }

    @Test
    @DisplayName("Deve retornar o ajuste correspondente ao buscar por um código existente")
    void busca_CodigoExistente_DeveRetornarAjuste() {
        when(ajusteRepository.findById(1L)).thenReturn(Optional.of(ajuste));

        Optional<Ajuste> resultado = ajusteService.busca(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getCodigo());
    }

    @Test
    @DisplayName("Deve registrar um novo ajuste e retornar o código gerado")
    void novo_DeveCriarENovoAjuste() {
        when(ajusteRepository.save(any(Ajuste.class))).thenReturn(ajuste);

        Long codigoGerado = ajusteService.novo();

        assertEquals(1L, codigoGerado);
        verify(ajusteRepository, times(1)).save(any(Ajuste.class));
    }

    @Test
    @DisplayName("Deve processar ajuste com sucesso marcando movimentação como ENTRADA de estoque")
    void processar_ComQtdPositiva_DeveProcessarEntrada() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        
        Produto produto = new Produto();
        produto.setCodigo(100L);
        
        AjusteProduto ajusteProduto = new AjusteProduto();
        ajusteProduto.setProduto(produto);
        ajusteProduto.setQtd_alteracao(5);
        
        List<AjusteProduto> listaProdutos = new ArrayList<>();
        listaProdutos.add(ajusteProduto);
        ajuste.setProdutos(listaProdutos);

        when(ajusteRepository.findById(1L)).thenReturn(Optional.of(ajuste));

        String resultado = ajusteService.processar(1L, "Ajuste de entrada");

        assertEquals("Ajuste realizado com sucesso", resultado);
        assertEquals(AjusteStatus.PROCESSADO, ajuste.getStatus());
        
        verify(produtoService, times(1)).ajusteEstoque(
            eq(100L), eq(5), eq(EntradaSaida.ENTRADA), anyString(), any()
        );
        verify(ajusteRepository, times(1)).save(ajuste);
    }

    @Test
    @DisplayName("Deve processar ajuste com sucesso marcando movimentação como SAIDA de estoque")
    void processar_ComQtdNegativa_DeveProcessarSaida() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        
        Produto produto = new Produto();
        produto.setCodigo(200L);
        
        AjusteProduto ajusteProduto = new AjusteProduto();
        ajusteProduto.setProduto(produto);
        ajusteProduto.setQtd_alteracao(-3);
        
        List<AjusteProduto> listaProdutos = new ArrayList<>();
        listaProdutos.add(ajusteProduto);
        ajuste.setProdutos(listaProdutos);

        when(ajusteRepository.findById(1L)).thenReturn(Optional.of(ajuste));

        String resultado = ajusteService.processar(1L, "Ajuste de saída");

        assertEquals("Ajuste realizado com sucesso", resultado);
        
        verify(produtoService, times(1)).ajusteEstoque(
            eq(200L), eq(-3), eq(EntradaSaida.SAIDA), anyString(), any()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar processar ajuste que já está com status PROCESSADO")
    void processar_AjusteJaProcessado_DeveLancarExcecao() {
        ajuste.setStatus(AjusteStatus.PROCESSADO);
        when(ajusteRepository.findById(1L)).thenReturn(Optional.of(ajuste));

        RuntimeException erro = assertThrows(RuntimeException.class, () -> {
            ajusteService.processar(1L, "Processamento indevido");
        });

        assertEquals("Ajuste já processado", erro.getMessage());
    }

    @Test
    @DisplayName("Deve capturar exceção do produtoService e solicitar suporte ao utilizador")
    void processar_ErroAoAjustarEstoque_DeveLancarExcecaoSuporte() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        
        Produto produto = new Produto();
        produto.setCodigo(100L);
        
        AjusteProduto ajusteProduto = new AjusteProduto();
        ajusteProduto.setProduto(produto);
        ajusteProduto.setQtd_alteracao(5);
        
        List<AjusteProduto> listaProdutos = new ArrayList<>();
        listaProdutos.add(ajusteProduto);
        ajuste.setProdutos(listaProdutos);

        when(ajusteRepository.findById(1L)).thenReturn(Optional.of(ajuste));
        
        doThrow(new RuntimeException("Erro BD no Produto"))
            .when(produtoService).ajusteEstoque(anyLong(), anyInt(), any(), anyString(), any());

        RuntimeException erro = assertThrows(RuntimeException.class, () -> {
            ajusteService.processar(1L, "Tentativa com falha");
        });

        assertEquals("Erro ao tentar processar o ajuste, chame o suporte", erro.getMessage());
    }

    @Test
    @DisplayName("Deve capturar exceção ao tentar salvar o ajuste processado no repositório")
    void processar_ErroAoSalvarAjuste_DeveLancarExcecaoSuporte() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        ajuste.setProdutos(new ArrayList<>()); 

        when(ajusteRepository.findById(1L)).thenReturn(Optional.of(ajuste));
        doThrow(new RuntimeException("Erro no save")).when(ajusteRepository).save(any(Ajuste.class));

        RuntimeException erro = assertThrows(RuntimeException.class, () -> {
            ajusteService.processar(1L, "Erro no save final");
        });

        assertEquals("Erro ao tentar processar o ajuste, chame o suporte", erro.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover um ajuste já processado")
    void remover_AjusteJaProcessado_DeveLancarExcecao() {
        ajuste.setStatus(AjusteStatus.PROCESSADO);

        RuntimeException erro = assertThrows(RuntimeException.class, () -> {
            ajusteService.remover(ajuste);
        });

        assertEquals("O ajuste já esta processado", erro.getMessage());
        verify(ajusteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve remover um ajuste pendente com sucesso")
    void remover_AjusteAProcessar_DeveRemoverComSucesso() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);

        assertDoesNotThrow(() -> {
            ajusteService.remover(ajuste);
        });

        verify(ajusteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve capturar erro de exclusão no banco e lançar exceção tratada")
    void remover_ErroAoDeletar_DeveLancarExcecao() {
        ajuste.setStatus(AjusteStatus.APROCESSAR);
        doThrow(new RuntimeException("Erro BD")).when(ajusteRepository).deleteById(1L);

        RuntimeException erro = assertThrows(RuntimeException.class, () -> {
            ajusteService.remover(ajuste);
        });

        assertEquals("Erro ao tentar cancelar o ajuste", erro.getMessage());
    }
}
