package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.model.Empresa;
import net.originmobi.pdv.model.EmpresaParametro;
import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalRepository;
import net.originmobi.pdv.service.notafiscal.NotaFiscalService;
import net.originmobi.pdv.service.notafiscal.NotaFiscalTotaisServer;
import net.originmobi.pdv.xml.nfe.GeraXmlNfe;

@ExtendWith(MockitoExtension.class)
class NotaFiscalServiceTest {

    @Mock
    private NotaFiscalRepository notasFiscais;
    @Mock
    private EmpresaService empresas;
    @Mock
    private NotaFiscalTotaisServer notaTotais;
    @Mock
    private PessoaService pessoas;
    @Mock
    private GeraXmlNfe geraXmlNfe;

    @InjectMocks
    private NotaFiscalService service;

    @Test
    void deveLancarErroQuandoEmpresaNaoExiste() {
        when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA));
    }

    @Test
    void deveLancarErroQuandoPessoaNaoExiste() {
        Empresa empresa = empresaValida(1, 1);
        when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
        when(pessoas.buscaPessoa(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA));
    }

    @Test
    void deveCadastrarNotaFiscalComSucesso() {
        Empresa empresa = empresaValida(1, 1);
        Pessoa pessoa = new Pessoa();

        when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
        when(pessoas.buscaPessoa(anyLong())).thenReturn(Optional.of(pessoa));
        when(notasFiscais.buscaUltimaNota(anyInt())).thenReturn(10L);

        when(notasFiscais.save(any())).thenAnswer(inv -> {
            NotaFiscal nf = inv.getArgument(0);
            nf.setCodigo(123L);
            return nf;
        });

        String codigo = service.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA);

        assertEquals("123", codigo);
    }

    @Test
    void deveGerarDVCorreto() {
        Integer dv = service.geraDV("12345678");
        assertEquals(9, dv);
    }

    @Test
    void deveGerarDVParaEntradaComZeros() {
        assertNotNull(service.geraDV("00000000"));
    }

    @TempDir
    Path temp;

    @Test
    void deveSalvarXmlNoDiretorio() throws Exception {
        ReflectionTestUtils.setField(service, "CAMINHO_XML", temp.toString());
        service.salvaXML("<xml>", "1234");
        assertTrue(Files.exists(temp.resolve("1234.xml")));
    }

    @Test
    void deveTratarErroAoSalvarXml() {
        ReflectionTestUtils.setField(service, "CAMINHO_XML", "/caminho/que/nao/existe");
        assertDoesNotThrow(() -> service.salvaXML("<xml>", "x"));
    }

    @Test
    void deveLancarErroQuandoSerieNfeForZero() {
        Empresa empresa = empresaValida(0, 1);
        when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
        assertThrows(RuntimeException.class,
                () -> service.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA));
    }

    @Test
    void deveLancarErroQuandoFalhaAoCadastrarTotais() {
        Empresa empresa = empresaValida(1, 1);
        Pessoa pessoa = new Pessoa();

        when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
        when(pessoas.buscaPessoa(anyLong())).thenReturn(Optional.of(pessoa));
        when(notaTotais.cadastro(any())).thenThrow(new RuntimeException("Erro"));

        assertThrows(RuntimeException.class,
                () -> service.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA));
    }

    @Test
    void deveLancarErroQuandoFalhaAoSalvarNota() {
        Empresa empresa = empresaValida(1, 1);
        Pessoa pessoa = new Pessoa();

        when(empresas.verificaEmpresaCadastrada()).thenReturn(Optional.of(empresa));
        when(pessoas.buscaPessoa(anyLong())).thenReturn(Optional.of(pessoa));
        when(notasFiscais.buscaUltimaNota(anyInt())).thenReturn(10L);
        when(notasFiscais.save(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> service.cadastrar(1L, "Venda", NotaFiscalTipo.SAIDA));
    }

    @Test
    void deveRemoverXmlQuandoExiste(@TempDir Path temp) throws Exception {
        ReflectionTestUtils.setField(service, "CAMINHO_XML", temp.toString());

        Path file = temp.resolve("teste.xml");
        Files.write(file, "<xml/>".getBytes());
        service.removeXml("teste");

        assertFalse(Files.exists(file));
    }

    @Test
    void naoDeveFalharQuandoXmlNaoExiste(@TempDir Path temp) {
        ReflectionTestUtils.setField(service, "CAMINHO_XML", temp.toString());
        assertDoesNotThrow(() -> service.removeXml("inexistente"));
    }

    @Test
    void deveEmitirNotaFiscal() {
        NotaFiscal nf = new NotaFiscal();
        when(geraXmlNfe.gerarXML(any())).thenReturn("CHAVE123");

        service.emitir(nf);

        assertEquals("CHAVE123", nf.getChave_acesso());
    }

    @Test
    void deveLancarErroQuandoFalhaAoEmitir() {
        NotaFiscal nf = new NotaFiscal();
        when(geraXmlNfe.gerarXML(any())).thenThrow(new RuntimeException("Erro ao emitir"));

        assertThrows(RuntimeException.class, () -> service.emitir(nf));
    }

    // ---- Metodos auxiliares

    private Empresa empresaValida(int serie, int ambiente) {
        Empresa empresa = new Empresa();
        EmpresaParametro p = new EmpresaParametro();
        p.setSerie_nfe(serie);
        p.setAmbiente(ambiente);
        empresa.setParametro(p);
        return empresa;
    }

}
