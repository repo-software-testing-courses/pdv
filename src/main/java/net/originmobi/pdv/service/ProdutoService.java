package net.originmobi.pdv.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import net.originmobi.pdv.dtos.ProdutoDTO;
import net.originmobi.pdv.exceptions.InsufficientStockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.filter.ProdutoFilter;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;

@Service
public class ProdutoService {


	private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

	private final ProdutoRepository produtos;
	private final VendaProdutoService vendaProdutos;
	private LocalDate dataAtual = LocalDate.now();

	public ProdutoService(ProdutoRepository produtos, VendaProdutoService vendaProdutos) {
		this.produtos = produtos;
		this.vendaProdutos = vendaProdutos;
	}

	public List<Produto> listar() {
		return produtos.findAll();
	}
	
	public List<Produto> listaProdutosVendaveis() {
		return produtos.produtosVendaveis();
	}

	public Produto busca(Long codigoProduto) {
		return produtos.findByCodigoIn(codigoProduto);
	}

	public Optional<Produto> buscaProduto(Long codigo) {
		return produtos.findById(codigo);
	}

	public Page<Produto> filter(ProdutoFilter filter, Pageable pageable) {
		String descricao = filter.getDescricao() == null ? "%" : filter.getDescricao();
		return produtos.findByDescricaoContaining(descricao, pageable);
	}

	/**
	 * Método para inserir ou atualizar um produto
	 * @param produtoDTO
	 * @return
	 */
	public String merger(ProdutoDTO produtoDTO) {

		// Vars
		Long codprod = produtoDTO.getCodprod();
		Long codforne = produtoDTO.getCodforne();
		Long codcategoria = produtoDTO.getCodcategoria();
		Long codgrupo = produtoDTO.getCodgrupo();
		int balanca = produtoDTO.getBalanca();
		String descricao = produtoDTO.getDescricao();
		Double valorCusto = produtoDTO.getValorCusto();
		Double valorVenda = produtoDTO.getValorVenda();
		java.util.Date dataValidade = produtoDTO.getDataValidade();
		String controleEstoque = produtoDTO.getControleEstoque();
		String situacao = produtoDTO.getSituacao();
		String unitario = produtoDTO.getUnitario();
		ProdutoSubstTributaria subtribu = produtoDTO.getSubtribu();
		String ncm = produtoDTO.getNcm();
		String cest = produtoDTO.getCest();
		Long tributacao = produtoDTO.getTributacao();
		Long modbc = produtoDTO.getModbc();
		String vendavel = produtoDTO.getVendavel();

		// Verifica se o produto já existe
		if (codprod == 0) {
			try {
				produtos.insere(codforne, codcategoria, codgrupo, balanca, descricao, valorCusto, valorVenda,
						dataValidade, controleEstoque, situacao, unitario, subtribu.ordinal(), Date.valueOf(dataAtual),
						ncm, cest, tributacao, modbc, vendavel);
			} catch (Exception e) {
				logger.info(e.getMessage());
				return "Erro a cadastrar produto, chame o suporte";
			}
		} else {

			try {
				produtos.atualiza(codprod, codforne, codcategoria, codgrupo, balanca, descricao, valorCusto, valorVenda,
						dataValidade, controleEstoque, situacao, unitario, subtribu.ordinal(), ncm, cest, tributacao,
						modbc, vendavel);

				return "Produto atualizado com sucesso";
			} catch (Exception e) {
				logger.info(e.getMessage());
				return "Erro a atualizar produto, chame o suporte";
			}

		}

		return "Produdo cadastrado com sucesso";
	}

	@SuppressWarnings("static-access")
	public void movimentaEstoque(Long codvenda, EntradaSaida tipo) {
		List<Object[]> resultado = vendaProdutos.buscaQtdProduto(codvenda);

		for (int i = 0; i < resultado.size(); i++) {
			Long codprod = Long.decode(resultado.get(i)[0].toString());
			int qtd = Integer.parseInt(resultado.get(i)[1].toString());

			Produto produto = produtos.findByCodigoIn(codprod);

			if (produto.getControla_estoque().equals(ProdutoControleEstoque.SIM)) {

				// estoque atual do produto
				int qtEstoque = produtos.saldoEstoque(codprod);
				String origemOp = "Venda " + codvenda.toString();

				if (qtd <= qtEstoque) {
					produtos.movimentaEstoque(codprod, tipo.SAIDA.toString(), qtd, origemOp,
							Date.valueOf(dataAtual));
				} else {
					throw new InsufficientStockException(
							"O produto de código " + codprod + " não tem estoque suficiente, verifique");
				}
			} else {
				logger.info("Produto não controla estoque");
			}
		}

	}

	public void ajusteEstoque(Long codprod, int qtd, EntradaSaida tipo, String origemOp, Date dataMovimentacao) {
		Produto produto = produtos.findByCodigoIn(codprod);

		if (produto.getControla_estoque().equals(ProdutoControleEstoque.NAO))
			throw new InsufficientStockException("O produto de código " + codprod + " não controla estoque, verifique");

		produtos.movimentaEstoque(codprod, tipo.toString(), qtd, origemOp, dataMovimentacao);
	}

}
