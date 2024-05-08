package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

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
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.repository.RecebimentoRepository;


@SpringBootTest
@ActiveProfiles("test")
public class RecebimentoServiceTest {

    @Mock
    private Optional<Pessoa> pessoaOptionalMock;
    @Mock
    private Pessoa pessoaMock;
    @Mock
    private Recebimento recebimentoMock;
    @Mock
    private RecebimentoRepository recebimentosMock;

    @Mock
    private PessoaService pessoasMock;

    @Mock
    private ParcelaService parcelasMock;

    @Mock
    private Parcela parcelaMock;

    @Mock
    private TituloService titulosMock;

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
    parcelaMock = mock(Parcela.class);
    when(parcelasMock.busca(anyLong())).thenReturn(parcelaMock);
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
    pessoaMock = mock(Pessoa.class);
    when(parcelasMock.busca(anyLong())).thenReturn(parcelaMock);
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
    pessoasMock = mock(PessoaService.class);
    when(parcelasMock.busca(anyLong())).thenReturn(parcelaMock);
    when(parcelaMock.getQuitado()).thenReturn(0);
    when(parcelaMock.getCodigo()).thenReturn(parcelaCodigo);
    when(parcelaMock.getReceber()).thenReturn(receberMock);
    when(receberMock.getPessoa()).thenReturn(pessoaMock);
    when(pessoaMock.getCodigo()).thenReturn(codpes);
    when(parcelaMock.getValor_restante()).thenReturn(10D);
    when(pessoasMock.buscaPessoa(anyLong())).thenReturn(null);
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        recebimentoService.abrirRecebimento(codpes, arrayParcelas);
    });
    assertEquals("Cliente não encontrado", exception.getMessage());
}
    // Add more test cases as needed for other methods in RecebimentoService


@Test
public void testAbrirRecebimento_() {
    Long codpes = 1L;
    Long parcelaCodigo=3L;
    String[] arrayParcelas = {"1", "2"};
    Parcela parcelaMock = mock(Parcela.class);
    Receber receberMock = mock(Receber.class);
    pessoaMock = mock(Pessoa.class);
    pessoaMock.setCodigo(1L);
    pessoasMock = mock(PessoaService.class);
    recebimentosMock = mock(RecebimentoRepository.class);
    recebimentoMock = mock(Recebimento.class);
    when(recebimentoMock.getCodigo()).thenReturn(1L);
    when(parcelasMock.busca(anyLong())).thenReturn(parcelaMock);
    when(parcelaMock.getQuitado()).thenReturn(0);
    when(parcelaMock.getCodigo()).thenReturn(parcelaCodigo);
    when(parcelaMock.getReceber()).thenReturn(receberMock);
    when(receberMock.getPessoa()).thenReturn(pessoaMock);
    when(pessoaMock.getCodigo()).thenReturn(codpes);
    when(parcelaMock.getValor_restante()).thenReturn(1D);
    when(pessoasMock.buscaPessoa(anyLong())).thenReturn(Optional.of(pessoaMock));
    when(recebimentosMock.save(recebimentoMock)).thenReturn(recebimentoMock);
    assertEquals(codpes.toString(), recebimentoService.abrirRecebimento(codpes, arrayParcelas));
}
    // Add more test cases as needed for other methods in RecebimentoService

}
