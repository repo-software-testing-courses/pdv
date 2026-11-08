package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
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
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.UsuarioService;
import net.originmobi.pdv.singleton.Aplicacao;

/**
 * Testes unitários de {@link CaixaService}.
 */
@ExtendWith(MockitoExtension.class)
class CaixaServiceTest {

	private static final String USUARIO_LOGADO = "gerente";
	private static final String SENHA_CORRETA = "123";
	private static final String HASH_SENHA = "$2a$10$hashFicticioDaSenhaDoGerente";

	@Mock
	private CaixaRepository caixas;

	@Mock
	private UsuarioService usuarios;

	@Mock
	private CaixaLancamentoService lancamentos;

	@InjectMocks
	private CaixaService service;

	@Captor
	private ArgumentCaptor<Caixa> caixaCaptor;

	@Captor
	private ArgumentCaptor<CaixaLancamento> lancamentoCaptor;

	private Usuario usuario;

	private MockedStatic<Aplicacao> aplicacaoEstatica;

	private MockedConstruction<BCryptPasswordEncoder> encoderConstruido;

	@AfterEach
	void fechaMocksEstaticos() {
		if (aplicacaoEstatica != null)
			aplicacaoEstatica.close();
		if (encoderConstruido != null)
			encoderConstruido.close();
	}

	/**
	 * Stub do usuário devolvido pelo UsuarioService.
	 *
	 * Fica aqui, e não num {@code @BeforeEach}, porque só os fluxos que passam
	 * pelo UsuarioService precisam dele — nos demais o modo estrito do Mockito
	 * reprovaria o stub não utilizado. A senha é um hash fictício: quem decide
	 * se ela confere é o encoder mockado em {@link #dadoQueASenhaConfere}.
	 */
	private void dadoUsuarioCadastrado() {
		usuario = new Usuario();
		usuario.setCodigo(1L);
		usuario.setUser(USUARIO_LOGADO);
		usuario.setSenha(HASH_SENHA);

		when(usuarios.buscaUsuario(anyString())).thenReturn(usuario);
	}

	/**
	 * O fechaCaixa instancia o próprio BCryptPasswordEncoder (não é injetado),
	 * então o mock de construção é a forma de isolar a verificação de senha
	 * sem executar o BCrypt real.
	 */
	private void dadoQueASenhaConfere(boolean confere) {
		encoderConstruido = mockConstruction(BCryptPasswordEncoder.class,
				(encoder, contexto) -> when(encoder.matches(anyString(), anyString())).thenReturn(confere));
	}

	private BCryptPasswordEncoder encoderUsado() {
		assertEquals(1, encoderConstruido.constructed().size());
		return encoderConstruido.constructed().get(0);
	}

	/**
	 * Arranjo dos fluxos que descobrem o usuário logado pelo singleton
	 * {@link Aplicacao}. O singleton guarda o usuário na primeira chamada e
	 * nunca mais o relê, então o mock estático é o que garante que cada teste
	 * enxergue o usuário que ele mesmo definiu.
	 */
	private void dadoUsuarioAutenticado() {
		dadoUsuarioCadastrado();

		Aplicacao aplicacao = mock(Aplicacao.class);
		when(aplicacao.getUsuarioAtual()).thenReturn(USUARIO_LOGADO);

		aplicacaoEstatica = mockStatic(Aplicacao.class);
		aplicacaoEstatica.when(Aplicacao::getInstancia).thenReturn(aplicacao);
	}

	private Caixa novoCaixa(CaixaTipo tipo, String descricao, Double valorAbertura) {
		Caixa caixa = new Caixa();
		caixa.setTipo(tipo);
		caixa.setDescricao(descricao);
		caixa.setValor_abertura(valorAbertura);
		return caixa;
	}

	// ------------------------------------------------------------------
	// cadastro
	// ------------------------------------------------------------------
	@Test
	@DisplayName("sem caixa aberto anteriormente deve salvar e retornar o código gerado")
	void cadastro_caixaSemAberturaAnterior_deveSalvarERetornarCodigo() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		when(caixas.save(any(Caixa.class))).thenAnswer(invocacao -> {
			((Caixa) invocacao.getArgument(0)).setCodigo(10L);
			return invocacao.getArgument(0);
		});
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa da loja", 150.0);

		Long codigo = service.cadastro(caixa);

		assertEquals(Long.valueOf(10L), codigo);
		verify(caixas).save(caixaCaptor.capture());
		Caixa salvo = caixaCaptor.getValue();
		assertEquals("Caixa da loja", salvo.getDescricao());
		assertSame(usuario, salvo.getUsuario());
		assertEquals(Date.valueOf(LocalDate.now()), salvo.getData_cadastro(), "a data de cadastro deve ser hoje");
		assertNull(salvo.getData_fechamento(), "um caixa recém aberto não possui fechamento");
		verify(usuarios).buscaUsuario(USUARIO_LOGADO);
	}

	@Test
	@DisplayName("com outro caixa em aberto deve lançar exceção e não salvar")
	void cadastro_caixaComOutroAberto_deveLancarExcecao() {
		when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()));
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa diário", 0.0);

		RuntimeException erro = assertThrows(RuntimeException.class, () -> service.cadastro(caixa));

		assertEquals("Existe caixa de dias anteriores em aberto, favor verifique", erro.getMessage());
		verify(caixas, never()).save(any(Caixa.class));
		verifyNoInteractions(usuarios, lancamentos);
	}

	@Test
	@DisplayName("valor de abertura nulo deve assumir zero e não gerar lançamento")
	void cadastro_valorAberturaNulo_deveAssumirZeroENaoLancar() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", null);

		service.cadastro(caixa);

		assertEquals(Double.valueOf(0.0), caixa.getValor_abertura());
		assertEquals(Double.valueOf(0.0), caixa.getValor_total());
		verify(caixas).save(caixa);
		verify(lancamentos, never()).lancamento(any(CaixaLancamento.class));
	}

	// valores-limite: o menor negativo representável em centavos e um negativo comum
	@ParameterizedTest(name = "abertura {0} deve ser rejeitada")
	@ValueSource(doubles = { -0.01, -1.0 })
	@DisplayName("valor de abertura negativo deve lançar exceção antes de qualquer persistência")
	void cadastro_valorAberturaNegativo_deveLancarExcecao(double valorAbertura) {
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", valorAbertura);

		RuntimeException erro = assertThrows(RuntimeException.class, () -> service.cadastro(caixa));

		assertEquals("Valor informado é inválido", erro.getMessage());
		verify(caixas, never()).save(any(Caixa.class));
		verifyNoInteractions(usuarios, lancamentos);
	}

	@Test
	@DisplayName("valor de abertura zero (limite) é aceito, mas não gera lançamento")
	void cadastro_valorAberturaZero_deveSalvarSemLancamento() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.0);

		service.cadastro(caixa);

		verify(caixas).save(caixa);
		assertEquals(Double.valueOf(0.0), caixa.getValor_total());
		verify(lancamentos, never()).lancamento(any(CaixaLancamento.class));
	}

	@Test
	@DisplayName("valor de abertura 0,01 (menor positivo) deve gerar lançamento")
	void cadastro_valorAberturaMenorPositivo_deveGerarLancamento() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.01);

		service.cadastro(caixa);

		verify(lancamentos).lancamento(lancamentoCaptor.capture());
		assertEquals(Double.valueOf(0.01), lancamentoCaptor.getValue().getValor());
		assertNull(caixa.getValor_total(), "com lançamento o valor total não é zerado pelo cadastro");
	}

	@ParameterizedTest(name = "{0} com descrição \"{1}\" deve ficar \"{2}\"")
	@CsvSource({
			"CAIXA, '',              Caixa diário",
			"COFRE, '',              Cofre",
			"BANCO, '',              Banco",
			"CAIXA, Caixa da loja,   Caixa da loja",
			"COFRE, Cofre central,   Cofre central",
			"BANCO, Banco do Brasil, Banco do Brasil" })
	@DisplayName("descrição vazia recebe o padrão do tipo; descrição informada é mantida")
	void cadastro_descricaoPorTipo_deveAplicarPadraoSomenteSeVazia(CaixaTipo tipo, String descricao,
			String descricaoEsperada) {
		dadoUsuarioAutenticado();
		Caixa caixa = novoCaixa(tipo, descricao, 0.0);
		caixa.setAgencia("1234");
		caixa.setConta("567890");

		service.cadastro(caixa);

		assertEquals(descricaoEsperada, caixa.getDescricao());
		verify(caixas).save(caixa);
	}

	@ParameterizedTest(name = "{0} não depende de caixa fechado")
	@EnumSource(value = CaixaTipo.class, names = { "COFRE", "BANCO" })
	@DisplayName("COFRE e BANCO podem ser abertos sem consultar se há caixa em aberto")
	void cadastro_cofreOuBanco_naoDeveVerificarCaixaAberto(CaixaTipo tipo) {
		dadoUsuarioAutenticado();
		Caixa caixa = novoCaixa(tipo, "Conta", 0.0);
		caixa.setAgencia("1234");
		caixa.setConta("567890");

		service.cadastro(caixa);

		verify(caixas, never()).caixaAberto();
		verify(caixas).save(caixa);
	}

	@Test
	@DisplayName("tipo BANCO deve remover caracteres não numéricos de agência e conta")
	void cadastro_tipoBanco_deveRemoverCaracteresNaoNumericosDeAgenciaEConta() {
		dadoUsuarioAutenticado();
		Caixa banco = novoCaixa(CaixaTipo.BANCO, "Banco do Brasil", 0.0);
		banco.setAgencia("12-34");
		banco.setConta("56.789-0");

		service.cadastro(banco);

		assertEquals("1234", banco.getAgencia());
		assertEquals("567890", banco.getConta());
		assertEquals("Banco do Brasil", banco.getDescricao());
		// para tipo BANCO não há validação de caixa aberto
		verify(caixas, never()).caixaAberto();
		verify(caixas).save(banco);
	}

	@Test
	@DisplayName("com valor de abertura deve gerar o lançamento de saldo inicial")
	void cadastro_comValorAbertura_deveGerarLancamentoDeSaldoInicial() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 200.0);

		service.cadastro(caixa);

		verify(lancamentos).lancamento(lancamentoCaptor.capture());
		CaixaLancamento lancamento = lancamentoCaptor.getValue();
		assertEquals("Abertura de caixa", lancamento.getObservacao());
		assertEquals(Double.valueOf(200.0), lancamento.getValor());
		assertEquals(TipoLancamento.SALDOINICIAL, lancamento.getTipo());
		assertEquals(EstiloLancamento.ENTRADA, lancamento.getEstilo());
		assertTrue(lancamento.getCaixa().isPresent());
		assertSame(caixa, lancamento.getCaixa().get());
		assertSame(usuario, lancamento.getUsuario());
		assertNull(caixa.getValor_total(), "o valor total é calculado pelo lançamento, não pelo cadastro");
	}

	@ParameterizedTest(name = "{0} deve gerar lançamento \"{1}\"")
	@CsvSource({
			"CAIXA, Abertura de caixa",
			"COFRE, Abertura de cofre",
			"BANCO, Abertura de banco" })
	@DisplayName("com valor de abertura, a observação do lançamento depende do tipo")
	void cadastro_comValorAbertura_deveDescreverLancamentoPorTipo(CaixaTipo tipo, String observacaoEsperada) {
		dadoUsuarioAutenticado();
		Caixa caixa = novoCaixa(tipo, "Conta", 80.0);
		caixa.setAgencia("1234");
		caixa.setConta("567890");

		service.cadastro(caixa);

		verify(lancamentos).lancamento(lancamentoCaptor.capture());
		assertEquals(observacaoEsperada, lancamentoCaptor.getValue().getObservacao());
		assertEquals(Double.valueOf(80.0), lancamentoCaptor.getValue().getValor());
	}

	@Test
	@DisplayName("erro ao salvar deve virar exceção de suporte e não gerar lançamento")
	void cadastro_erroAoSalvar_deveLancarExcecaoDeSuporte() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		when(caixas.save(any(Caixa.class))).thenThrow(new RuntimeException("falha no banco"));
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.0);

		RuntimeException erro = assertThrows(RuntimeException.class, () -> service.cadastro(caixa));

		assertEquals("Erro no processo de abertura, chame o suporte técnico", erro.getMessage());
		verify(lancamentos, never()).lancamento(any(CaixaLancamento.class));
	}

	@Test
	@DisplayName("erro no lançamento deve virar exceção de suporte")
	void cadastro_erroNoLancamento_deveLancarExcecaoDeSuporte() {
		dadoUsuarioAutenticado();
		when(caixas.caixaAberto()).thenReturn(Optional.empty());
		when(lancamentos.lancamento(any(CaixaLancamento.class))).thenThrow(new RuntimeException("falha"));
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 50.0);

		RuntimeException erro = assertThrows(RuntimeException.class, () -> service.cadastro(caixa));

		assertEquals("Erro no processo, chame o suporte", erro.getMessage());
		verify(caixas).save(caixa);
	}

	// ------------------------------------------------------------------
	// fechaCaixa
	// ------------------------------------------------------------------
	@Test
	@DisplayName("senha vazia deve retornar mensagem e não tocar no caixa")
	void fechaCaixa_senhaVazia_deveRetornarMensagem() {
		dadoUsuarioAutenticado();
		encoderConstruido = mockConstruction(BCryptPasswordEncoder.class);

		assertEquals("Favor, informe a senha", service.fechaCaixa(1L, ""));

		verify(encoderUsado(), never()).matches(any(), anyString());
		verify(caixas, never()).findById(anyLong());
		verify(caixas, never()).save(any(Caixa.class));
	}

	@Test
	@DisplayName("senha incorreta deve retornar mensagem e não fechar o caixa")
	void fechaCaixa_senhaIncorreta_deveRetornarMensagem() {
		dadoUsuarioAutenticado();
		dadoQueASenhaConfere(false);

		assertEquals("Senha incorreta, favor verifique", service.fechaCaixa(1L, "senhaErrada"));

		verify(encoderUsado()).matches("senhaErrada", HASH_SENHA);
		verify(caixas, never()).findById(anyLong());
		verify(caixas, never()).save(any(Caixa.class));
	}

	@Test
	@DisplayName("senha correta deve fechar o caixa gravando data e valor de fechamento")
	void fechaCaixa_senhaCorreta_deveFecharEGravarValores() {
		dadoUsuarioAutenticado();
		dadoQueASenhaConfere(true);
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.0);
		caixa.setValor_total(320.0);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

		long antes = System.currentTimeMillis();
		String retorno = service.fechaCaixa(1L, SENHA_CORRETA);
		long depois = System.currentTimeMillis();

		assertEquals("Caixa fechado com sucesso", retorno);
		verify(encoderUsado()).matches(SENHA_CORRETA, HASH_SENHA);
		verify(usuarios).buscaUsuario(USUARIO_LOGADO);
		verify(caixas).save(caixa);
		assertEquals(Double.valueOf(320.0), caixa.getValor_fechamento());
		long fechamento = caixa.getData_fechamento().getTime();
		assertTrue(fechamento >= antes && fechamento <= depois,
				"a data de fechamento deve ser o instante do fechamento");
	}

	@Test
	@DisplayName("caixa sem valor total deve ser fechado com zero")
	void fechaCaixa_semValorTotal_deveFecharComZero() {
		dadoUsuarioAutenticado();
		dadoQueASenhaConfere(true);
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.0);
		caixa.setValor_total(null);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

		assertEquals("Caixa fechado com sucesso", service.fechaCaixa(1L, SENHA_CORRETA));

		assertEquals(Double.valueOf(0.0), caixa.getValor_fechamento());
		verify(caixas).save(caixa);
	}

	@Test
	@DisplayName("caixa já fechado deve lançar exceção")
	void fechaCaixa_caixaJaFechado_deveLancarExcecao() {
		dadoUsuarioAutenticado();
		dadoQueASenhaConfere(true);
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.0);
		Timestamp fechamentoOriginal = new Timestamp(System.currentTimeMillis());
		caixa.setData_fechamento(fechamentoOriginal);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));

		RuntimeException erro = assertThrows(RuntimeException.class,
				() -> service.fechaCaixa(1L, SENHA_CORRETA));

		assertEquals("Caixa já esta fechado", erro.getMessage());
		assertEquals(fechamentoOriginal, caixa.getData_fechamento());
		verify(caixas, never()).save(any(Caixa.class));
	}

	@Test
	@DisplayName("erro ao salvar deve virar exceção de suporte")
	void fechaCaixa_erroAoSalvar_deveLancarExcecaoDeSuporte() {
		dadoUsuarioAutenticado();
		dadoQueASenhaConfere(true);
		Caixa caixa = novoCaixa(CaixaTipo.CAIXA, "Caixa", 0.0);
		caixa.setValor_total(10.0);
		when(caixas.findById(1L)).thenReturn(Optional.of(caixa));
		when(caixas.save(any(Caixa.class))).thenThrow(new RuntimeException("falha"));

		RuntimeException erro = assertThrows(RuntimeException.class,
				() -> service.fechaCaixa(1L, SENHA_CORRETA));

		assertEquals("Ocorreu um erro ao fechar o caixa, chame o suporte", erro.getMessage());
	}

	// ------------------------------------------------------------------
	// consultas
	// ------------------------------------------------------------------
	@Test
	@DisplayName("caixaIsAberto deve refletir o retorno do repositório")
	void caixaIsAberto_deveRefletirRetornoDoRepositorio() {
		when(caixas.caixaAberto()).thenReturn(Optional.of(new Caixa()), Optional.empty());

		assertTrue(service.caixaIsAberto());
		assertFalse(service.caixaIsAberto());
		verify(caixas, times(2)).caixaAberto();
	}

	@Test
	@DisplayName("listaTodos deve delegar para o repositório")
	void listaTodos_deveDelegarParaRepositorio() {
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.findByCodigoOrdenado()).thenReturn(esperado);

		assertSame(esperado, service.listaTodos());
	}

	@Test
	@DisplayName("listarCaixas com data deve buscar por data de abertura")
	void listarCaixas_comDataInformada_deveBuscarPorDataAbertura() {
		CaixaFilter filter = new CaixaFilter();
		filter.setData_cadastro("2026/09/19");
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.buscaCaixasPorDataAbertura(Date.valueOf("2026-09-19"))).thenReturn(esperado);

		assertSame(esperado, service.listarCaixas(filter));
		assertEquals("2026-09-19", filter.getData_cadastro(), "a barra deve ser normalizada para hífen");
		verify(caixas, never()).listaCaixasAbertos();
	}

	@Test
	@DisplayName("listarCaixas sem data (nula ou vazia) deve listar os abertos")
	void listarCaixas_semData_deveListarAbertos() {
		List<Caixa> esperado = Arrays.asList(new Caixa(), new Caixa());
		when(caixas.listaCaixasAbertos()).thenReturn(esperado);

		assertSame(esperado, service.listarCaixas(new CaixaFilter()));

		CaixaFilter filtroVazio = new CaixaFilter();
		filtroVazio.setData_cadastro("");
		assertSame(esperado, service.listarCaixas(filtroVazio));
		verify(caixas, times(2)).listaCaixasAbertos();
		verify(caixas, never()).buscaCaixasPorDataAbertura(any(Date.class));
	}

	@Test
	@DisplayName("caixaAberto deve delegar para o repositório")
	void caixaAberto_deveDelegarParaRepositorio() {
		Optional<Caixa> esperado = Optional.of(new Caixa());
		when(caixas.caixaAberto()).thenReturn(esperado);

		assertSame(esperado, service.caixaAberto());
	}

	@Test
	@DisplayName("caixasAbertos deve delegar para o repositório")
	void caixasAbertos_deveDelegarParaRepositorio() {
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.caixasAbertos()).thenReturn(esperado);

		assertSame(esperado, service.caixasAbertos());
	}

	@Test
	@DisplayName("busca deve delegar para o repositório")
	void busca_deveDelegarParaRepositorio() {
		Optional<Caixa> esperado = Optional.of(new Caixa());
		when(caixas.findById(5L)).thenReturn(esperado);

		assertSame(esperado, service.busca(5L));
	}

	@Test
	@DisplayName("buscaCaixaUsuario com caixa aberto deve retornar Optional preenchido")
	void buscaCaixaUsuario_comCaixaAberto_deveRetornarOptionalPreenchido() {
		dadoUsuarioCadastrado();
		Caixa caixa = new Caixa();
		when(caixas.findByCaixaAbertoUsuario(1L)).thenReturn(caixa);

		Optional<Caixa> retorno = service.buscaCaixaUsuario(USUARIO_LOGADO);

		assertTrue(retorno.isPresent());
		assertSame(caixa, retorno.get());
		verify(usuarios).buscaUsuario(USUARIO_LOGADO);
	}

	@Test
	@DisplayName("buscaCaixaUsuario sem caixa aberto deve retornar Optional vazio")
	void buscaCaixaUsuario_semCaixaAberto_deveRetornarOptionalVazio() {
		dadoUsuarioCadastrado();
		when(caixas.findByCaixaAbertoUsuario(anyLong())).thenReturn(null);

		assertFalse(service.buscaCaixaUsuario(USUARIO_LOGADO).isPresent());
	}

	@Test
	@DisplayName("listaBancos deve buscar pelo tipo BANCO")
	void listaBancos_deveBuscarPorTipoBanco() {
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.buscaBancos(CaixaTipo.BANCO)).thenReturn(esperado);

		assertSame(esperado, service.listaBancos());
	}

	@Test
	@DisplayName("listaCaixasAbertosTipo deve buscar pelo tipo informado")
	void listaCaixasAbertosTipo_deveBuscarPorTipoInformado() {
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.buscaCaixaTipo(CaixaTipo.COFRE)).thenReturn(esperado);

		assertSame(esperado, service.listaCaixasAbertosTipo(CaixaTipo.COFRE));
	}

	@Test
	@DisplayName("listaBancosAbertosTipoFilterBanco com data deve buscar por tipo e data")
	void listaBancosAbertosTipoFilterBanco_comData_deveBuscarPorTipoEData() {
		BancoFilter filter = new BancoFilter();
		filter.setData_cadastro("2026/09/19");
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.buscaCaixaTipoData(CaixaTipo.BANCO, Date.valueOf("2026-09-19"))).thenReturn(esperado);

		assertSame(esperado, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter));
		assertEquals("2026-09-19", filter.getData_cadastro());
		verify(caixas, never()).buscaCaixaTipo(any(CaixaTipo.class));
	}

	@Test
	@DisplayName("listaBancosAbertosTipoFilterBanco sem data (nula ou vazia) deve buscar todos os bancos")
	void listaBancosAbertosTipoFilterBanco_semData_deveBuscarTodosOsBancos() {
		List<Caixa> esperado = Collections.singletonList(new Caixa());
		when(caixas.buscaCaixaTipo(CaixaTipo.BANCO)).thenReturn(esperado);

		assertSame(esperado, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, new BancoFilter()));

		BancoFilter filtroVazio = new BancoFilter();
		filtroVazio.setData_cadastro("");
		assertSame(esperado, service.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filtroVazio));
		verify(caixas, times(2)).buscaCaixaTipo(CaixaTipo.BANCO);
		verify(caixas, never()).buscaCaixaTipoData(any(CaixaTipo.class), any(Date.class));
	}
}
