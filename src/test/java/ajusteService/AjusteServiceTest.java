package ajusteService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

import java.sql.Date;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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

public class AjusteServiceTest {

    private AjusteService ajusteService;
    private AjusteRepository ajusteRepositoryMock;
    private ProdutoService produtoServiceMock;

    @BeforeEach
    void setup() {
		// Instancia a classe AjusteService
        ajusteService = new AjusteService(); 
        ajusteRepositoryMock = mock(AjusteRepository.class);
        produtoServiceMock = mock(ProdutoService.class);

        // Insere os mocks na classe a ser testada
        ReflectionTestUtils.setField(ajusteService, "ajustes", ajusteRepositoryMock);
        ReflectionTestUtils.setField(ajusteService, "produtos", produtoServiceMock);
    }

    @Test
    @DisplayName("Cria um novo ajuste com status 'APROCESSAR'")
    void testeAjusteStatusAPROCESSAR() {
    	// Mock que simula a autenticação de usuario_teste
        Authentication autenticacaoMock = mock(Authentication.class);
        when(autenticacaoMock.getName()).thenReturn("usuario_teste");
        
        SecurityContext contextoMock = mock(SecurityContext.class);
        when(contextoMock.getAuthentication()).thenReturn(autenticacaoMock);
        
        SecurityContextHolder.setContext(contextoMock);

		// Mock do repositório salvando um novo ajuste realizado
        Ajuste novoAjuste = new Ajuste();
        novoAjuste.setCodigo(1L);
        novoAjuste.setStatus(AjusteStatus.APROCESSAR);

        when(ajusteRepositoryMock.save(any(Ajuste.class))).thenReturn(novoAjuste);

        Long codigoCriado = ajusteService.novo();

		// Valida se o código retornado pelo método novo() é o esperado
        assertNotNull(codigoCriado);
        assertEquals(1L, codigoCriado);
    }
    
    @Test
    @DisplayName("Busca um ajuste realizado pelo código")  // Se o método busca() retorna um ajuste existente
    void buscaAjustePorCodigo() {
		// Cria um ajuste mockado
    	Ajuste ajuste = new Ajuste();
    	ajuste.setCodigo(5L);
    	
		// Simula a busca por ID
    	when(ajusteRepositoryMock.findById(5L)).thenReturn(Optional.of(ajuste));
    	
    	Optional<Ajuste> resultado = ajusteService.busca(5L);
    	
		// Valida se o ID retornado é o esperado
    	assertTrue(resultado.isPresent());
    	assertEquals(5L, resultado.get().getCodigo());
    }
    
    @Test
    @DisplayName("Testa se não remove ajuste já processado")  // Garante que não é possível remover um ajuste com o status PROCESSADO
    void testeSeNaoRemoveAjusteProcessado() {
		// Cria um ajuste mockado
    	Ajuste ajuste = new Ajuste();
    	ajuste.setCodigo(10L);
    	ajuste.setStatus(AjusteStatus.PROCESSADO);
    	
		// Espera que o método remover() lance uma exceção
    	RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
    		ajusteService.remover(ajuste);
    	});
    	
    	assertEquals("O ajuste já esta processado", excecao.getMessage());
    }
    
    @Test
    @DisplayName("Testa se remove ajuste já processado")  // Testa se é possível remover um ajuste com status APROCESSAR
    void testeSeRemoveAjusteProcessado() {
		// Cria um ajuste mockado
    	Ajuste ajuste = new Ajuste();
    	ajuste.setCodigo(10L);
    	ajuste.setStatus(AjusteStatus.APROCESSAR);
    	
    	doNothing().when(ajusteRepositoryMock).deleteById(10L);
    	
		// Verifica que não foi lançada nenhuma exceção após a simulação de uma deleção no repositório
    	assertDoesNotThrow(() -> ajusteService.remover(ajuste));
    	verify(ajusteRepositoryMock, times(1)).deleteById(10L);
    }
    
    @Test
    @DisplayName("Processa ajuste com a entrada de um produto")
    void testeProcessaAjusteComEntradaProduto() {
    	Long codigoAjuste = 1L;
    	
    	Produto produto = new Produto();
    	produto.setCodigo(100L);
    	
    	AjusteProduto ajusteProduto = new AjusteProduto();
    	ajusteProduto.setProduto(produto);
    	ajusteProduto.setQtd_alteracao(10); // Se positivo, significa que houve a entrada de um produto
    	
		// Cria um ajuste mockado com um projeto que teve entrada de estoque
    	Ajuste ajuste = new Ajuste();
    	ajuste.setCodigo(codigoAjuste);
    	ajuste.setStatus(AjusteStatus.APROCESSAR);
    	ajuste.setProdutos(Collections.singletonList(ajusteProduto));
    	
		// Mock do comportamento do repositório e do serviço de produto
    	when(ajusteRepositoryMock.findById(codigoAjuste)).thenReturn(Optional.of(ajuste));
    	when(ajusteRepositoryMock.save(any(Ajuste.class))).thenReturn(ajuste);
    	
    	doNothing().when(produtoServiceMock).ajusteEstoque(
    			eq(100L),
    			eq(10),
    			eq(EntradaSaida.ENTRADA),
    			anyString(),
    			any(Date.class)
    	);
    	
		// Processa o ajuste e verifica se o estoque foi ajustado
    	String resultado = ajusteService.processar(codigoAjuste,  "Ajuste simples");
    	
    	assertEquals("Ajuste realizado com sucesso", resultado);
    	verify(produtoServiceMock, times(1)).ajusteEstoque(
    			eq(100L),
    			eq(10),
    			eq(EntradaSaida.ENTRADA),
    			anyString(),
    			any(Date.class)
    	);
    	verify(ajusteRepositoryMock).save(any(Ajuste.class));
    }
    
    @SuppressWarnings("unchecked") // Remove o aviso do compilador que era exibido na linha 175
    @Test
    @DisplayName("Testa a lista com filtro de código")
    void testeListaComFiltroCodigo() {
		// Mocka uma consulta paginada com filtro de código e verifica se o retorno é o esperado
    	AjusteFilter filtro = new AjusteFilter();
    	filtro.setCodigo(123L);
    	Pageable pageable = mock(Pageable.class);
    	
    	Page<Ajuste> paginaMock = mock(Page.class);
    	when(ajusteRepositoryMock.lista(
    			eq(123L), 
    			eq(pageable)
    			)).thenReturn(paginaMock);
    	
    	Page<Ajuste> resultado = ajusteService.lista(pageable, filtro);
    	
    	assertEquals(paginaMock, resultado);
    	verify(ajusteRepositoryMock).lista(eq(123L),eq(pageable));
    }
}
