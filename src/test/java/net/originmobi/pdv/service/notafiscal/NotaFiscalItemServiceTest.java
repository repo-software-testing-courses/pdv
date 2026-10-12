package net.originmobi.pdv.service.notafiscal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.notafiscal.NotaFiscalTipo;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.model.CFOP;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.CstCsosn;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Estado;
import net.originmobi.pdv.model.ModBcIcms;
import net.originmobi.pdv.model.NotaFiscal;
import net.originmobi.pdv.model.NotaFiscalItem;
import net.originmobi.pdv.model.NotaFiscalItemImposto;
import net.originmobi.pdv.model.NotaFiscalTotais;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.model.Tributacao;
import net.originmobi.pdv.model.TributacaoRegra;
import net.originmobi.pdv.repository.notafiscal.NotaFiscalItemRepository;
import net.originmobi.pdv.service.ProdutoService;

@RunWith(MockitoJUnitRunner.class)
public class NotaFiscalItemServiceTest {

	private static final Long COD_PRODUTO = 1L;
	private static final Long COD_NOTA = 10L;
	private static final double VALOR_UNITARIO = 25.0;
	private static final double DELTA = 0.000001;

	@Mock
	private NotaFiscalItemRepository itemServer;

	@Mock
	private NotaFiscalItemImpostoService impostos;

	@Mock
	private NotaFiscalTotaisServer totais;

	@Mock
	private ProdutoService produtos;

	@Mock
	private NotaFiscalService notas;

	@InjectMocks
	private NotaFiscalItemService service;

	private Produto produto;
	private NotaFiscal nota;
	private TributacaoRegra regraSaida;
	private NotaFiscalItemImposto imposto;

	@Before
	public void setUp() {
		// Dados novos antes de cada teste.
		regraSaida = regra(EntradaSaida.SAIDA, "RJ");
		produto = produtoValido(regraSaida);
		nota = notaValida(NotaFiscalTipo.SAIDA, "RJ");
		imposto = new NotaFiscalItemImposto();
	}

	// -------------------------------------------------- verificaRegraDeTributacao Testes -------------------------------------------------- 
	// (feitos com insere, já que o método é privado)

	// Rejeições

	@Test
	public void testVerificaRegraDeTributacaoRejeitaProdutoNaoEncontrado() {
		when(produtos.buscaProduto(COD_PRODUTO)).thenReturn(Optional.empty());
		when(notas.busca(COD_NOTA)).thenReturn(Optional.of(nota));

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Nenhum produto encontrado, favor verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaProdutoSemTributacao() {
		produto.setTributacao(null);
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Produto sem tributação, favor verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaProdutoSemNcm() {
		produto.setNcm("");
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Produto sem código NCM, favor verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaSubstituicaoTributariaSemCest() {
		produto.setSubtributaria(ProdutoSubstTributaria.SIM);
		produto.setCest("");
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Produto de substituição tributária sem código CEST, favor verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaProdutoSemUnidade() {
		produto.setUnidade("");
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Produto sem unidade, favor verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaSaidaComListaDeRegrasVazia() {
		produto = produtoValido();
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Tributação sem regra de saída, verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaSaidaComSomenteRegrasDeEntrada() {
		produto = produtoValido(regra(EntradaSaida.ENTRADA, "RJ"), regra(EntradaSaida.ENTRADA, "SP"));
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Tributação sem regra de saída, verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaEntradaComListaDeRegrasVazia() {
		produto = produtoValido();
		nota = notaValida(NotaFiscalTipo.ENTRADA, "RJ");
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.ENTRADA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Tributação sem regra de entrada, verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testVerificaRegraDeTributacaoRejeitaEntradaComSomenteRegrasDeSaida() {
		produto = produtoValido(regraSaida, regra(EntradaSaida.SAIDA, "SP"));
		nota = notaValida(NotaFiscalTipo.ENTRADA, "RJ");
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.ENTRADA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Tributação sem regra de entrada, verifique", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	// Casos válidos

	@Test
	public void testVerificaRegraDeTributacaoAceitaSaidaSemSubstituicaoTributaria() {
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verify(itemServer).save(any(NotaFiscalItem.class));
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testVerificaRegraDeTributacaoAceitaEntrada() {
		TributacaoRegra regraEntrada = regra(EntradaSaida.ENTRADA, "RJ");
		produto = produtoValido(regraEntrada);
		nota = notaValida(NotaFiscalTipo.ENTRADA, "RJ");
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraEntrada, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.ENTRADA);

		assertEquals("ok", resultado);
		verify(impostos).calcula(null, 50.0, regraEntrada, '0', 3);
		verify(itemServer).save(any(NotaFiscalItem.class));
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testVerificaRegraDeTributacaoAceitaSubstituicaoTributariaComCest() {
		produto.setSubtributaria(ProdutoSubstTributaria.SIM);
		produto.setCest("1234567");
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verify(itemServer).save(any(NotaFiscalItem.class));
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testVerificaRegraDeTributacaoAceitaSaidaComRegrasMistas() {
		// Regras de entrada e saída misturadas.
		produto = produtoValido(regra(EntradaSaida.ENTRADA, "RJ"), regra(EntradaSaida.SAIDA, "SP"),
				regraSaida, regra(EntradaSaida.ENTRADA, "SP"));
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verify(itemServer).save(any(NotaFiscalItem.class));
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testVerificaRegraDeTributacaoAceitaEntradaComRegrasMistas() {
		TributacaoRegra regraEntrada = regra(EntradaSaida.ENTRADA, "RJ");
		// Regras de entrada e saída misturadas.
		produto = produtoValido(regraSaida, regra(EntradaSaida.ENTRADA, "SP"), regraEntrada,
				regra(EntradaSaida.SAIDA, "SP"));
		nota = notaValida(NotaFiscalTipo.ENTRADA, "RJ");
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraEntrada, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.ENTRADA);

		assertEquals("ok", resultado);
		verify(impostos).calcula(null, 50.0, regraEntrada, '0', 3);
		verify(itemServer).save(any(NotaFiscalItem.class));
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	// -------------------------------------------------- insere Testes -------------------------------------------------- 
	// Casos com produto válido.

	// Regra de tributação: rejeições

	@Test
	public void testInsereRejeitaSaidaSemRegraParaUfDoDestinatario() {
		produto = produtoValido(regra(EntradaSaida.SAIDA, "SP"));
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Nenhuma regra de tributação cadastrada para a UF do destinatário", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	@Test
	public void testInsereRejeitaQuandoUfETipoCoincidemEmRegrasDiferentes() {
		produto = produtoValido(regra(EntradaSaida.ENTRADA, "RJ"), regra(EntradaSaida.SAIDA, "SP"));
		configuraBuscas();

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter rejeitado a inserção");
		} catch (RuntimeException e) {
			assertEquals("Nenhuma regra de tributação cadastrada para a UF do destinatário", e.getMessage());
		}
		verify(produtos).buscaProduto(COD_PRODUTO);
		verify(notas).busca(COD_NOTA);
		verifyZeroInteractions(impostos, itemServer, totais);
	}

	// Regra de tributação: casos válidos

	@Test
	public void testInsereSelecionaRegraComUfETipoCorretosEntreOutrasRegras() {
		produto = produtoValido(regra(EntradaSaida.ENTRADA, "RJ"), regra(EntradaSaida.SAIDA, "SP"),
				regraSaida, regra(EntradaSaida.ENTRADA, "SP"));
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
		verify(itemServer).save(captor.capture());
		assertEquals("5102", captor.getValue().getCfop());
		assertSame(imposto, captor.getValue().getImpostos());
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testInsereUsaUltimaRegraQuandoHaMaisDeUmaComMesmaUfETipo() {
		// O código atual usa a última regra compatível.
		TributacaoRegra ultimaRegra = regra(EntradaSaida.SAIDA, "RJ");
		ultimaRegra.getCfop().setCfop("5103");
		produto = produtoValido(regraSaida, ultimaRegra);
		configuraBuscas();
		when(impostos.calcula(null, 50.0, ultimaRegra, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
		verify(itemServer).save(captor.capture());
		assertEquals("5103", captor.getValue().getCfop());
		assertSame(imposto, captor.getValue().getImpostos());
		verify(impostos).calcula(null, 50.0, ultimaRegra, '0', 3);
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	// Item novo e atualização de quantidade

	@Test
	public void testInsereProdutoNovoQuandoNotaContemSomenteOutroProduto() {
		nota.getItens().add(itemExistente(2L, 30L, 7, 40L));
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
		verify(itemServer).save(captor.capture());
		NotaFiscalItem salvo = captor.getValue();
		assertNull(salvo.getCodigo());
		assertEquals(COD_PRODUTO, salvo.getCodProd());
		assertEquals(2, salvo.getQtd());
		assertEquals(2, salvo.getQtd_tribu());
		assertEquals(50.0, salvo.getVlTotal(), DELTA);
		assertEquals(25.0, salvo.getV_uniTribu(), DELTA);
		assertEquals("UN", salvo.getUnidade_tribu());
		assertEquals("5102", salvo.getCfop());
		assertSame(nota, salvo.getNotaFiscal());
		assertSame(imposto, salvo.getImpostos());
		assertEquals(7, nota.getItens().get(0).getQtd());
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testInsereEncontraItemExistenteESomaQuantidadeEntreOutrosProdutos() {
		nota.getItens().add(itemExistente(2L, 31L, 8, 41L));
		nota.getItens().add(itemExistente(COD_PRODUTO, 30L, 3, 40L));
		nota.getItens().add(itemExistente(3L, 32L, 9, 42L));
		configuraBuscas();
		when(impostos.calcula(40L, 125.0, regraSaida, '0', 3)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
		verify(itemServer).save(captor.capture());
		NotaFiscalItem salvo = captor.getValue();
		assertEquals(Long.valueOf(30L), salvo.getCodigo());
		assertEquals(COD_PRODUTO, salvo.getCodProd());
		assertEquals(5, salvo.getQtd());
		assertEquals(5, salvo.getQtd_tribu());
		assertEquals(125.0, salvo.getVlTotal(), DELTA);
		assertEquals(25.0, salvo.getV_uniTribu(), DELTA);
		assertEquals("UN", salvo.getUnidade_tribu());
		assertEquals("5102", salvo.getCfop());
		assertSame(nota, salvo.getNotaFiscal());
		assertSame(imposto, salvo.getImpostos());
		assertEquals(8, nota.getItens().get(0).getQtd());
		assertEquals(9, nota.getItens().get(2).getQtd());
		verify(impostos).calcula(40L, 125.0, regraSaida, '0', 3);
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	@Test
	public void testInsereEncaminhaValorOrigemEModalidadeIcmsDoProduto() {
		produto.setValor_venda(12.5);
		produto.setUnidade("KG");
		produto.getModBcIcms().setTipo(2);
		regraSaida.getCst_csosn().setCst_csosn("70");
		configuraBuscas();
		when(impostos.calcula(null, 37.5, regraSaida, '7', 2)).thenReturn(imposto);

		String resultado = service.insere(COD_PRODUTO, COD_NOTA, 3, NotaFiscalTipo.SAIDA);

		assertEquals("ok", resultado);
		ArgumentCaptor<NotaFiscalItem> captor = ArgumentCaptor.forClass(NotaFiscalItem.class);
		verify(itemServer).save(captor.capture());
		NotaFiscalItem salvo = captor.getValue();
		assertEquals(COD_PRODUTO, salvo.getCodProd());
		assertEquals("5102", salvo.getCfop());
		assertSame(nota, salvo.getNotaFiscal());
		assertNull(salvo.getCodigo());
		assertEquals(3, salvo.getQtd());
		assertEquals(3, salvo.getQtd_tribu());
		assertEquals(37.5, salvo.getVlTotal(), DELTA);
		assertEquals(12.5, salvo.getV_uniTribu(), DELTA);
		assertEquals("KG", salvo.getUnidade_tribu());
		assertSame(imposto, salvo.getImpostos());
		verify(impostos).calcula(null, 37.5, regraSaida, '7', 2);
		verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}

	// Falhas

	@Test
	public void testInsereTrataFalhaAoSalvarESuspendeAtualizacaoDeTotais() {
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);
		doThrow(new RuntimeException("Falha no banco")).when(itemServer).save(any(NotaFiscalItem.class));

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter informado a falha ao salvar");
		} catch (RuntimeException e) {
			assertEquals("Erro ao salvar item na nota, chame o suporte", e.getMessage());
		}
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verify(itemServer).save(any(NotaFiscalItem.class));
		verifyZeroInteractions(totais);
	}

	@Test
	public void testInserePropagaFalhaDoCalculoDeImpostosSemSalvarItem() {
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3))
				.thenThrow(new RuntimeException("Falha no cálculo de impostos"));

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter propagado a falha nos impostos");
		} catch (RuntimeException e) {
			assertEquals("Falha no cálculo de impostos", e.getMessage());
		}
		verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		verifyZeroInteractions(itemServer, totais);
	}

	@Test
	public void testInserePropagaFalhaDosTotaisDepoisDeSalvarItem() {
		configuraBuscas();
		when(impostos.calcula(null, 50.0, regraSaida, '0', 3)).thenReturn(imposto);
		doThrow(new RuntimeException("Falha nos totais")).when(totais).atualiza(COD_NOTA, nota.getTotais());

		try {
			service.insere(COD_PRODUTO, COD_NOTA, 2, NotaFiscalTipo.SAIDA);
			fail("Deveria ter propagado a falha nos totais");
		} catch (RuntimeException e) {
			assertEquals("Falha nos totais", e.getMessage());
		}
		// Confere a ordem: impostos, item e totais.
		InOrder ordem = inOrder(impostos, itemServer, totais);
		ordem.verify(impostos).calcula(null, 50.0, regraSaida, '0', 3);
		ordem.verify(itemServer).save(any(NotaFiscalItem.class));
		ordem.verify(totais).atualiza(COD_NOTA, nota.getTotais());
	}





	
	private NotaFiscalItem itemExistente(Long codigoProduto, Long codigoItem, int quantidade, Long codigoImposto) {
		NotaFiscalItemImposto impostoExistente = new NotaFiscalItemImposto();
		impostoExistente.setCodigo(codigoImposto);

		NotaFiscalItem item = new NotaFiscalItem();
		item.setCodProd(codigoProduto);
		item.setCodigo(codigoItem);
		item.setQtd(quantidade);
		item.setImpostos(impostoExistente);
		return item;
	}

	private Produto produtoValido(TributacaoRegra... regras) {
		Tributacao tributacao = new Tributacao();
		tributacao.setRegra(new ArrayList<>(Arrays.asList(regras)));
		for (TributacaoRegra regra : regras) {
			regra.setTributacao(tributacao);
		}

		ModBcIcms modBcIcms = new ModBcIcms();
		modBcIcms.setTipo(3);

		// Dados de teste.
		Produto produto = new Produto();
		produto.setCodigo(COD_PRODUTO);
		produto.setTributacao(tributacao);
		produto.setNcm("12345678");
		produto.setSubtributaria(ProdutoSubstTributaria.NAO);
		produto.setCest("");
		produto.setUnidade("UN");
		produto.setValor_venda(VALOR_UNITARIO);
		produto.setModBcIcms(modBcIcms);
		return produto;
	}

	private TributacaoRegra regra(EntradaSaida tipo, String siglaUf) {
		CstCsosn cst = new CstCsosn();
		cst.setCst_csosn("00");

		CFOP cfop = new CFOP();
		cfop.setCfop(tipo == EntradaSaida.SAIDA ? "5102" : "1102");

		TributacaoRegra regra = new TributacaoRegra();
		regra.setTipo(tipo);
		regra.setUf(estado(siglaUf));
		regra.setCst_csosn(cst);
		regra.setCfop(cfop);
		return regra;
	}

	private NotaFiscal notaValida(NotaFiscalTipo tipo, String siglaUf) {
		Cidade cidade = new Cidade();
		cidade.setEstado(estado(siglaUf));

		Endereco endereco = new Endereco();
		endereco.setCidade(cidade);

		Pessoa destinatario = new Pessoa();
		destinatario.setEndereco(endereco);

		NotaFiscal nota = new NotaFiscal();
		nota.setCodigo(COD_NOTA);
		nota.setTipo(tipo);
		nota.setDestinatario(destinatario);
		nota.setItens(new ArrayList<NotaFiscalItem>());
		nota.setTotais(new NotaFiscalTotais());
		return nota;
	}

	private Estado estado(String sigla) {
		Estado estado = new Estado();
		estado.setSigla(sigla);
		return estado;
	}

	// Faz as buscas retornarem o produto e a nota do teste.
	private void configuraBuscas() {
		when(produtos.buscaProduto(COD_PRODUTO)).thenReturn(Optional.of(produto));
		when(notas.busca(COD_NOTA)).thenReturn(Optional.of(nota));
	}
}
