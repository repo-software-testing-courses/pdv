package net.originmobi.pdv.service;

import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.repository.CaixaLancamentoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaixaLancamentoServiceTest {

    @InjectMocks
    private CaixaLancamentoService caixaLancamentoService;

    @Mock
    private CaixaLancamentoRepository caixaLancamentoRepository;

    @Mock
    private UsuarioService usuarioService;

    private Caixa caixaAberto;
    private CaixaLancamento lancamentoEntrada;
    private CaixaLancamento lancamentoSaida;

    @Before
    public void setUp() {
        caixaAberto = new Caixa();
        caixaAberto.setValor_total(100.0);
        caixaAberto.setData_fechamento(null);

        lancamentoEntrada = new CaixaLancamento();
        lancamentoEntrada.setCaixa(caixaAberto);
        lancamentoEntrada.setValor(50.0);
        lancamentoEntrada.setEstilo(EstiloLancamento.ENTRADA);
        lancamentoEntrada.setTipo(TipoLancamento.SUPRIMENTO);

        lancamentoSaida = new CaixaLancamento();
        lancamentoSaida.setCaixa(caixaAberto);
        lancamentoSaida.setValor(20.0);
        lancamentoSaida.setEstilo(EstiloLancamento.SAIDA);
        lancamentoSaida.setTipo(TipoLancamento.SANGRIA);
    }

    @Test
    public void testLancamentoObservacaoPadraoSangria() {
        lancamentoSaida.setObservacao("");
        caixaLancamentoService.lancamento(lancamentoSaida);
        assertEquals("Sangria de caixa", lancamentoSaida.getObservacao());
    }

    @Test
    public void testLancamentoObservacaoPadraoSuprimento() {
        lancamentoEntrada.setObservacao("");
        caixaLancamentoService.lancamento(lancamentoEntrada);
        assertEquals("Suprimento de caixa", lancamentoEntrada.getObservacao());
    }
    @Test(expected = RuntimeException.class)
    public void testLancamentoCaixaFechado() {
        caixaAberto.setData_fechamento(new Timestamp(System.currentTimeMillis()));
        lancamentoEntrada.setCaixa(caixaAberto);

        caixaLancamentoService.lancamento(lancamentoEntrada);
    }

    @Test
    public void testGetLancamentosDoCaixaComCaixaVazio() {

        when(caixaLancamentoRepository.findByCaixaEquals(caixaAberto)).thenReturn(Collections.emptyList());
        List<CaixaLancamento> resultado = caixaLancamentoService.lancamentosDoCaixa(caixaAberto);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testGetLancamentosDoCaixaComUmLancamento() {
        when(caixaLancamentoRepository.findByCaixaEquals(caixaAberto)).thenReturn(Collections.singletonList(lancamentoEntrada));
        List<CaixaLancamento> resultado = caixaLancamentoService.lancamentosDoCaixa(caixaAberto);
        assertEquals(1, resultado.size());
        assertEquals(lancamentoEntrada, resultado.get(0));
    }


    @Test
    public void testLancamentoComSaldoInsuficiente() {
        lancamentoSaida.setValor(150.0);
        String resultado = caixaLancamentoService.lancamento(lancamentoSaida);
        assertEquals("Saldo insuficiente para realizar esta operação", resultado);
        assertEquals(Double.valueOf(150.0), lancamentoSaida.getValor()); //
    }



}
