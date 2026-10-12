package net.originmobi.pdv.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.CaixaRepository;

@RunWith(MockitoJUnitRunner.class)
public class CaixaServiceTest {

	private static final String SENHA = "123";
	private static final String SENHA_CRIPTOGRAFADA = new BCryptPasswordEncoder().encode(SENHA);

	@Mock
	private CaixaRepository caixas;

	@Mock
	private UsuarioService usuarios;

	@Mock
	private CaixaLancamentoService lancamentos;

	@InjectMocks
	private CaixaService service;

	private final Usuario usuario = new Usuario();

	@Before
	public void setUp() {
		// CaixaService pega o usuário logado pelo singleton Aplicacao, que lê o SecurityContextHolder
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("gerente", null));
		usuario.setSenha(SENHA_CRIPTOGRAFADA);
	}

	private Caixa caixa(CaixaTipo tipo, Double valorAbertura, String descricao) {
		Caixa caixa = new Caixa();
		caixa.setTipo(tipo);
		caixa.setValor_abertura(valorAbertura);
		caixa.setDescricao(descricao);
		return caixa;
	}

	private CaixaLancamento lancamentoRealizado() {
		ArgumentCaptor<CaixaLancamento> captor = ArgumentCaptor.forClass(CaixaLancamento.class);
		verify(lancamentos).lancamento(captor.capture());
		return captor.getValue();
	}

	private void assertLancaExcecao(String mensagemEsperada, Runnable acao) {
		try {
			acao.run();
			fail("Deveria ter lançado exceção: " + mensagemEsperada);
		} catch (RuntimeException e) {
			assertEquals(mensagemEsperada, e.getMessage());
		}
	}

	// cadastro: validações

	@Test
	public void cadastroDeCaixaComOutroCaixaAbertoLancaExcecao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 10.0, "");
		when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));

		assertLancaExcecao("Existe caixa de dias anteriores em aberto, favor verifique",
				() -> service.cadastro(caixa));
		verify(caixas, never()).save(any());
	}

	@Test
	public void cadastroDeCofreNaoVerificaCaixaAberto() {
		Caixa cofre = caixa(CaixaTipo.COFRE, 0.0, "");

		service.cadastro(cofre);

		verify(caixas, never()).caixaAberto();
		verify(caixas).save(cofre);
	}

	@Test
	public void cadastroComValorDeAberturaNegativoLancaExcecao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, -0.01, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());

		assertLancaExcecao("Valor informado é inválido", () -> service.cadastro(caixa));
		verify(caixas, never()).save(any());
	}

	// cadastro: valor de abertura

	@Test
	public void cadastroComValorDeAberturaNuloAssumeZeroESemLancamento() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, null, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());

		service.cadastro(caixa);

		assertEquals(0.0, caixa.getValor_abertura(), 0.0);
		assertEquals(0.0, caixa.getValor_total(), 0.0);
		verify(lancamentos, never()).lancamento(any());
	}

	@Test
	public void cadastroComValorDeAberturaZeroNaoFazLancamento() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());

		service.cadastro(caixa);

		assertEquals(0.0, caixa.getValor_total(), 0.0);
		verify(lancamentos, never()).lancamento(any());
	}

	@Test
	public void cadastroDeCaixaComValorPositivoLancaSaldoInicial() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 50.0, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);

		service.cadastro(caixa);

		CaixaLancamento lancamento = lancamentoRealizado();
		assertEquals("Abertura de caixa", lancamento.getObservacao());
		assertEquals(50.0, lancamento.getValor(), 0.0);
		assertEquals(TipoLancamento.SALDOINICIAL, lancamento.getTipo());
		assertEquals(EstiloLancamento.ENTRADA, lancamento.getEstilo());
		assertSame(caixa, lancamento.getCaixa().get());
		assertSame(usuario, lancamento.getUsuario());
	}

	@Test
	public void cadastroDeCofreComValorPositivoLancaAberturaDeCofre() {
		service.cadastro(caixa(CaixaTipo.COFRE, 50.0, ""));

		assertEquals("Abertura de cofre", lancamentoRealizado().getObservacao());
	}

	@Test
	public void cadastroDeBancoComValorPositivoLancaAberturaDeBanco() {
		Caixa banco = caixa(CaixaTipo.BANCO, 50.0, "");
		banco.setAgencia("1");
		banco.setConta("2");

		service.cadastro(banco);

		assertEquals("Abertura de banco", lancamentoRealizado().getObservacao());
	}

	// cadastro: descrição

	@Test
	public void cadastroDeCaixaSemDescricaoUsaPadrao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());

		service.cadastro(caixa);

		assertEquals("Caixa diário", caixa.getDescricao());
	}

	@Test
	public void cadastroDeCofreSemDescricaoUsaPadrao() {
		Caixa cofre = caixa(CaixaTipo.COFRE, 0.0, "");

		service.cadastro(cofre);

		assertEquals("Cofre", cofre.getDescricao());
	}

	@Test
	public void cadastroDeBancoSemDescricaoUsaPadrao() {
		Caixa banco = caixa(CaixaTipo.BANCO, 0.0, "");
		banco.setAgencia("1");
		banco.setConta("2");

		service.cadastro(banco);

		assertEquals("Banco", banco.getDescricao());
	}

	@Test
	public void cadastroComDescricaoInformadaMantemDescricao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "Caixa da manhã");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());

		service.cadastro(caixa);

		assertEquals("Caixa da manhã", caixa.getDescricao());
	}

	// cadastro: dados preenchidos pelo sistema

	@Test
	public void cadastroDeBancoRemoveMascaraDeAgenciaEConta() {
		Caixa banco = caixa(CaixaTipo.BANCO, 0.0, "");
		banco.setAgencia("1234-5");
		banco.setConta("12.345-6");

		service.cadastro(banco);

		assertEquals("12345", banco.getAgencia());
		assertEquals("123456", banco.getConta());
	}

	@Test
	public void cadastroPreencheUsuarioDataERetornaCodigo() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		caixa.setCodigo(7L);
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);

		Long codigo = service.cadastro(caixa);

		assertEquals(Long.valueOf(7L), codigo);
		assertSame(usuario, caixa.getUsuario());
		assertEquals(Date.valueOf(LocalDate.now()), caixa.getData_cadastro());
	}

	// cadastro: falhas de persistência

	@Test
	public void cadastroComFalhaAoSalvarLancaExcecao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 50.0, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		when(caixas.save(caixa)).thenThrow(new RuntimeException("banco fora"));

		assertLancaExcecao("Erro no processo de abertura, chame o suporte técnico", () -> service.cadastro(caixa));
		verify(lancamentos, never()).lancamento(any());
	}

	@Test
	public void cadastroComFalhaNoLancamentoLancaExcecao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 50.0, "");
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		doThrow(new RuntimeException("falha")).when(lancamentos).lancamento(any());

		assertLancaExcecao("Erro no processo, chame o suporte", () -> service.cadastro(caixa));
	}

	// fechaCaixa

	@Test
	public void fechaCaixaComSenhaVaziaPedeSenha() {
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);

		String mensagem = service.fechaCaixa(1L, "");

		assertEquals("Favor, informe a senha", mensagem);
		verify(caixas, never()).findById(any());
	}

	@Test
	public void fechaCaixaComSenhaIncorretaNaoFecha() {
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);

		String mensagem = service.fechaCaixa(1L, "errada");

		assertEquals("Senha incorreta, favor verifique", mensagem);
		verify(caixas, never()).save(any());
	}

	@Test
	public void fechaCaixaComSenhaCorretaFechaComValorTotal() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		caixa.setValor_total(150.0);
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

		String mensagem = service.fechaCaixa(1L, SENHA);

		assertEquals("Caixa fechado com sucesso", mensagem);
		assertEquals(150.0, caixa.getValor_fechamento(), 0.0);
		assertNotNull(caixa.getData_fechamento());
		verify(caixas).save(caixa);
	}

	@Test
	public void fechaCaixaSemValorTotalFechaComZero() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

		service.fechaCaixa(1L, SENHA);

		assertEquals(0.0, caixa.getValor_fechamento(), 0.0);
	}

	@Test
	public void fechaCaixaJaFechadoLancaExcecao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		caixa.setData_fechamento(new Timestamp(System.currentTimeMillis()));
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

		assertLancaExcecao("Caixa já esta fechado", () -> service.fechaCaixa(1L, SENHA));
		verify(caixas, never()).save(any());
	}

	@Test
	public void fechaCaixaComFalhaAoSalvarLancaExcecao() {
		Caixa caixa = caixa(CaixaTipo.CAIXA, 0.0, "");
		when(usuarios.buscaUsuario("gerente")).thenReturn(usuario);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));
		when(caixas.save(caixa)).thenThrow(new RuntimeException("banco fora"));

		assertLancaExcecao("Ocorreu um erro ao fechar o caixa, chame o suporte", () -> service.fechaCaixa(1L, SENHA));
	}

	// --- listarCaixas ---

	@Test
	public void listarCaixasSemDataListaAbertos() {
		List<Caixa> abertos = Arrays.asList(new Caixa());
		when(caixas.listaCaixasAbertos()).thenReturn(abertos);

		assertSame(abertos, service.listarCaixas(new CaixaFilter()));
	}

	@Test
	public void listarCaixasComDataVaziaListaAbertos() {
		List<Caixa> abertos = Arrays.asList(new Caixa());
		CaixaFilter filtro = new CaixaFilter();
		filtro.setData_cadastro("");
		when(caixas.listaCaixasAbertos()).thenReturn(abertos);

		assertSame(abertos, service.listarCaixas(filtro));
	}

	@Test
	public void listarCaixasComDataBuscaPelaDataDeAbertura() {
		List<Caixa> doDia = Arrays.asList(new Caixa());
		CaixaFilter filtro = new CaixaFilter();
		filtro.setData_cadastro("2026/10/05"); // formato do datepicker da tela de caixas
		when(caixas.buscaCaixasPorDataAbertura(Date.valueOf("2026-10-05"))).thenReturn(doDia);

		assertSame(doDia, service.listarCaixas(filtro));
		verify(caixas, never()).listaCaixasAbertos();
	}

	// --- listaBancosAbertosTipoFilterBanco ---

	@Test
	public void listaBancosSemDataBuscaBancosAbertos() {
		List<Caixa> bancos = Arrays.asList(new Caixa());
		when(caixas.buscaCaixaTipo(CaixaTipo.BANCO)).thenReturn(bancos);

		assertSame(bancos, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, new BancoFilter()));
	}

	@Test
	public void listaBancosComDataVaziaBuscaBancosAbertos() {
		List<Caixa> bancos = Arrays.asList(new Caixa());
		BancoFilter filtro = new BancoFilter();
		filtro.setData_cadastro("");
		when(caixas.buscaCaixaTipo(CaixaTipo.BANCO)).thenReturn(bancos);

		assertSame(bancos, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filtro));
	}

	@Test
	public void listaBancosComDataBuscaPorTipoEData() {
		List<Caixa> doDia = Arrays.asList(new Caixa());
		BancoFilter filtro = new BancoFilter();
		filtro.setData_cadastro("2026/10/05"); // formato do datepicker da tela de bancos
		when(caixas.buscaCaixaTipoData(CaixaTipo.BANCO, Date.valueOf("2026-10-05"))).thenReturn(doDia);

		assertSame(doDia, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filtro));
	}

}
