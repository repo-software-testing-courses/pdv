package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import net.originmobi.pdv.model.Parcela;


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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
public void testAbrirRecebimento_jaquitado() {
    Long codpes = 1L;
    String[] arrayParcelas = {"1", "2"};
    Parcela parcelaMock = mock(Parcela.class);
    when(parcelas.busca(anyLong())).thenReturn(parcelaMock);
    when(parcelaMock.getQuitado()).thenReturn(1);
    when(parcelaMock.getCodigo()).thenReturn(1L);
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        recebimentoService.abrirRecebimento(codpes, arrayParcelas);
    });
    assertEquals("Parcela 1 já esta quitada, verifique.", exception.getMessage());


}

    // Add more test cases as needed for other methods in RecebimentoService

}
