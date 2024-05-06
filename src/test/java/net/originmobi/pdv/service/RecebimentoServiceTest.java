package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.repository.RecebimentoRepository;


@SpringBootTest
@ActiveProfiles("test")
public class RecebimentoServiceTest {

    @Mock
    private RecebimentoRepository recebimentos;

    @Mock
    private PessoaService pessoas;

    @Mock
    private ParcelaService parcelas;

    @Mock
    private TituloService titulos;

    @InjectMocks
    private RecebimentoService recebimentoService;

    RuntimeException exception;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
public void testAbrirRecebimento_jaquitado() {
    
    Long codpes1 = 1L;
    String[] arrayParcelas1 = {"2","3"};
    Parcela parcelaMock = mock(Parcela.class);
    when(parcelas.busca(anyLong())).thenReturn(parcelaMock);
    when(parcelaMock.getQuitado()).thenReturn(1);
    when(parcelaMock.getCodigo()).thenReturn(1L);

//Teste se a parcela ja ta paga
    exception = assertThrows(RuntimeException.class, () -> {
    recebimentoService.abrirRecebimento(codpes1, arrayParcelas1);
    });
    assertEquals("Parcela "+codpes1+" já esta quitada, verifique.", exception.getMessage());


}
@Test
public void testAbrirRecebimento_naoPertenceAoUsuario() {
    Long codpes = 1L;
    Long codpes2 = 2L;
    Long parcelaCodigo=3L;
    String[] arrayParcelas = {"1", "2"};
    Parcela parcelaMock = mock(Parcela.class);
    Receber receberMock = mock(Receber.class);
    Pessoa pessoaMock = mock(Pessoa.class);
    when(parcelas.busca(anyLong())).thenReturn(parcelaMock);
    when(parcelaMock.getQuitado()).thenReturn(0);
    when(parcelaMock.getCodigo()).thenReturn(parcelaCodigo);
    when(parcelaMock.getReceber()).thenReturn(receberMock);
    when(receberMock.getPessoa()).thenReturn(pessoaMock);
    when(pessoaMock.getCodigo()).thenReturn(codpes2);
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        recebimentoService.abrirRecebimento(codpes, arrayParcelas);
    });
    assertEquals("A parcela "+parcelaCodigo+" não pertence ao cliente selecionado", exception.getMessage());


}

@Test
public void testAbrirRecebimento_ClienteNãoEncontrado() {
    Long codpes = 1L;
    Long parcelaCodigo=3L;
    String[] arrayParcelas = {"1", "2"};
    Parcela parcelaMock = mock(Parcela.class);
    Receber receberMock = mock(Receber.class);
    Pessoa pessoaMock = mock(Pessoa.class);
    pessoas = mock(PessoaService.class);
    when(parcelas.busca(anyLong())).thenReturn(parcelaMock);
    when(parcelaMock.getQuitado()).thenReturn(0);
    when(parcelaMock.getCodigo()).thenReturn(parcelaCodigo);
    when(parcelaMock.getReceber()).thenReturn(receberMock);
    when(receberMock.getPessoa()).thenReturn(pessoaMock);
    when(pessoaMock.getCodigo()).thenReturn(codpes);
    when(parcelaMock.getValor_restante()).thenReturn(10D);
    when(pessoas.buscaPessoa(anyLong())).thenReturn(null);
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        recebimentoService.abrirRecebimento(codpes, arrayParcelas);
    });
    assertEquals("Cliente não encontrado", exception.getMessage());


}
    // Add more test cases as needed for other methods in RecebimentoService

}
