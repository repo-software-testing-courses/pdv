package net.originmobi.pdv.service;

import net.originmobi.pdv.dtos.ProdutoDTO;
import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.exceptions.InsufficientStockException;
import net.originmobi.pdv.exceptions.ProdutoNotFoundException;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProdutoServiceTest {

    @Mock
    ProdutoRepository produtos;

    @Mock
    VendaProdutoService vendaProdutos;

    @Autowired
    ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        produtoService = new ProdutoService(produtos, vendaProdutos);
    }


    @Test
    void shouldReturnAllProducts() {
        Produto product1 = new Produto();
        Produto product2 = new Produto();
        List<Produto> expectedProducts = Arrays.asList(product1, product2);
        when(produtos.findAll()).thenReturn(expectedProducts);

        List<Produto> actualProducts = produtoService.listar();

        assertEquals(expectedProducts, actualProducts);
        verify(produtos, times(1)).findAll();
    }

    @Test
    void shouldReturnVendableProducts() {
        Produto product1 = new Produto();
        Produto product2 = new Produto();
        List<Produto> expectedProducts = Arrays.asList(product1, product2);
        when(produtos.produtosVendaveis()).thenReturn(expectedProducts);

        List<Produto> actualProducts = produtoService.listaProdutosVendaveis();

        assertEquals(expectedProducts, actualProducts);
        verify(produtos, times(1)).produtosVendaveis();
    }
    @Test
    void shouldReturnProductWhenProductExists_BUSCA_PRODUTO() {
        Long codigoProduto = 1L;
        Produto expectedProduct = new Produto();
        when(produtos.findById(codigoProduto)).thenReturn(Optional.of(expectedProduct));

        Produto actualProduct = produtoService.buscaProduto(codigoProduto);

        assertEquals(expectedProduct, actualProduct);
        verify(produtos, times(1)).findById(codigoProduto);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist_BUSCA_PRODUTO() {
        Long codigoProduto = 1L;
        when(produtos.findById(codigoProduto)).thenReturn(Optional.empty());

        assertThrows(ProdutoNotFoundException.class, () -> produtoService.buscaProduto(codigoProduto));
        verify(produtos, times(1)).findById(codigoProduto);
    }
    @Test
    void shouldReturnProductWhenProductExists() {
        Long codigoProduto = 1L;
        Produto expectedProduct = new Produto();
        when(produtos.findById(codigoProduto)).thenReturn(Optional.of(expectedProduct));

        Produto actualProduct = produtoService.busca(codigoProduto);

        assertEquals(expectedProduct, actualProduct);
        verify(produtos, times(1)).findById(codigoProduto);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        Long codigoProduto = 1L;
        when(produtos.findById(codigoProduto)).thenReturn(Optional.empty());

        assertThrows(ProdutoNotFoundException.class, () -> produtoService.busca(codigoProduto));
        verify(produtos, times(1)).findById(codigoProduto);
    }
    @Test
    void shouldInsertProductSuccessfully() {
        ProdutoDTO produtoDTO = createValidProdutoDTO(0L);

        when(produtos.save(any())).thenReturn(null);
        String result = produtoService.insertOrUpdate(produtoDTO);

        assertEquals("Produto cadastrado com sucesso", result);
    }

    @Test
    void shouldRaiseProductInsertionException() {
        ProdutoDTO produtoDTO = createUncategorizedProdutoDTO(0L);

        when(produtos.save(any())).thenReturn(null);
        String result = produtoService.insertOrUpdate(produtoDTO);

        assertEquals("Erro a cadastrar produto, chame o suporte", result);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        ProdutoDTO produtoDTO = createValidProdutoDTO(1L);

        when(produtos.save(any())).thenReturn(null);
        String result = produtoService.insertOrUpdate(produtoDTO);

        assertEquals("Produto atualizado com sucesso", result);
    }

    @Test
    void shouldRaiseProductUpdateException(){
        ProdutoDTO produtoDTO = createUncategorizedProdutoDTO(1L);

        when(produtos.save(any())).thenReturn(null);
        String result = produtoService.insertOrUpdate(produtoDTO);

        assertEquals("Erro a atualizar produto, chame o suporte", result);
    }
    @Test
    void shouldMoveStockSuccessfully() {
        Long codvenda = 1L;
        EntradaSaida tipo = EntradaSaida.SAIDA;
        Long codprod = 1L;
        int qtd = 5;
        Object[] array = new Object[]{codprod.toString(), qtd};
        List<Object[]> resultado = new ArrayList<>();
        resultado.add(array);

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.SIM);

        when(vendaProdutos.buscaQtdProduto(codvenda)).thenReturn(resultado);
        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);
        when(produtos.saldoEstoque(codprod)).thenReturn(qtd);

        assertDoesNotThrow(() -> produtoService.movimentaEstoque(codvenda, tipo));
    }

    @Test
    void shouldThrowInsufficientStockException() {
        Long codvenda = 1L;
        EntradaSaida tipo = EntradaSaida.SAIDA;
        Long codprod = 1L;
        int qtd = 5;
        Object[] array = new Object[]{codprod.toString(), qtd};
        List<Object[]> resultado = new ArrayList<>();
        resultado.add(array);

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.SIM);

        when(vendaProdutos.buscaQtdProduto(codvenda)).thenReturn(resultado);
        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);
        when(produtos.saldoEstoque(codprod)).thenReturn(qtd - 1);

        assertThrows(InsufficientStockException.class, () -> produtoService.movimentaEstoque(codvenda, tipo));
    }

    @Test
    void shouldAdjustStockSuccessfully() {
        Long codprod = 1L;
        int qtd = 5;
        EntradaSaida tipo = EntradaSaida.ENTRADA;
        String origemOp = "Ajuste de Estoque";
        java.sql.Date dataMovimentacao = java.sql.Date.valueOf(java.time.LocalDate.now());

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.SIM);

        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);

        assertDoesNotThrow(() -> produtoService.ajusteEstoque(codprod, qtd, tipo, origemOp, dataMovimentacao));
    }

    @Test
    void shouldThrowInsufficientStockExceptionOnAdjustment() {
        Long codprod = 1L;
        int qtd = 5;
        EntradaSaida tipo = EntradaSaida.SAIDA;
        String origemOp = "Ajuste de Estoque";
        java.sql.Date dataMovimentacao = java.sql.Date.valueOf(java.time.LocalDate.now());

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.NAO);

        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);
        when(produtos.saldoEstoque(codprod)).thenReturn(qtd - 1);

        assertThrows(InsufficientStockException.class, () -> produtoService.ajusteEstoque(codprod, qtd, tipo, origemOp, dataMovimentacao));
    }

    @Test
    void shouldThrowExceptionWhenAdjustingStockOfNonControllingProduct() {
        Long codprod = 1L;
        int qtd = 5;
        EntradaSaida tipo = EntradaSaida.SAIDA;
        String origemOp = "Ajuste de Estoque";
        java.sql.Date dataMovimentacao = java.sql.Date.valueOf(java.time.LocalDate.now());

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.NAO);

        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);

        assertThrows(InsufficientStockException.class, () -> produtoService.ajusteEstoque(codprod, qtd, tipo, origemOp, dataMovimentacao));
    }

    @Test
    void shouldInsertProductWithBoundaryValues() {
        ProdutoDTO produtoDTO = createValidProdutoDTO(0L);
        produtoDTO.setValorCusto(0.0); // Min boundary value - valor limite
        produtoDTO.setValorVenda(Double.MAX_VALUE); // Max boundary value - valor limite

        when(produtos.save(any())).thenReturn(null);
        String result = produtoService.insertOrUpdate(produtoDTO);

        assertEquals("Produto cadastrado com sucesso", result);
    }

    @Test
    void shouldMoveStockWithBoundaryValues() {
        Long codvenda = 1L;
        EntradaSaida tipo = EntradaSaida.SAIDA;
        Long codprod = 1L;
        int qtd = 1; // Min boundary value - valor limite
        Object[] array = new Object[]{codprod.toString(), qtd};
        List<Object[]> resultado = new ArrayList<>();
        resultado.add(array);

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.SIM);

        when(vendaProdutos.buscaQtdProduto(codvenda)).thenReturn(resultado);
        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);
        when(produtos.saldoEstoque(codprod)).thenReturn(qtd);

        assertDoesNotThrow(() -> produtoService.movimentaEstoque(codvenda, tipo));
    }

    @Test
    void shouldAdjustStockWithBoundaryValues() {
        Long codprod = 1L;
        int qtd = 1; // Min boundary value - valor limite
        EntradaSaida tipo = EntradaSaida.ENTRADA;
        String origemOp = "Ajuste de Estoque";
        java.sql.Date dataMovimentacao = java.sql.Date.valueOf(java.time.LocalDate.now());

        Produto produto = new Produto();
        produto.setControla_estoque(ProdutoControleEstoque.SIM);

        when(produtos.findByCodigoIn(codprod)).thenReturn(produto);

        assertDoesNotThrow(() -> produtoService.ajusteEstoque(codprod, qtd, tipo, origemOp, dataMovimentacao));
    }


    private ProdutoDTO createUncategorizedProdutoDTO(Long codprod) {
        ProdutoDTO produtoDTO = new ProdutoDTO();
        ProdutoSubstTributaria VALID_subtribu = ProdutoSubstTributaria.SIM;
        produtoDTO.setCodcategoria(-1L);
        produtoDTO.setSubtribu(VALID_subtribu);
        produtoDTO.setCodprod(codprod);
        return produtoDTO;
    }

    private ProdutoDTO createValidProdutoDTO(Long codProd) {
        ProdutoDTO produtoDTO = new ProdutoDTO();

        Long VALID_codforne = 1L;
        Long VALID_codcategoria = 1L;
        Long VALID_codgrupo = 1L;
        int VALID_balanca = 1;
        String VALID_descricao = "Product Description";
        double VALID_valorCusto = 100.0;
        double VALID_valorVenda = 150.0;
        Date VALID_dataValidade = new Date();
        String VALID_controleEstoque = "Yes";
        String VALID_situacao = "Active";
        String VALID_unitario = "Unit";
        ProdutoSubstTributaria VALID_subtribu = ProdutoSubstTributaria.SIM;
        String VALID_ncm = "NCM Code";
        String VALID_cest = "CEST Code";
        Long VALID_tributacao = 1L;
        Long VALID_modbc = 1L;
        String VALID_vendavel = "Yes";

        produtoDTO.setCodforne(VALID_codforne);
        produtoDTO.setCodprod(codProd);
        produtoDTO.setCodcategoria(VALID_codcategoria);
        produtoDTO.setCodgrupo(VALID_codgrupo);
        produtoDTO.setBalanca(VALID_balanca);
        produtoDTO.setDescricao(VALID_descricao);
        produtoDTO.setValorCusto(VALID_valorCusto);
        produtoDTO.setValorVenda(VALID_valorVenda);
        produtoDTO.setDataValidade(VALID_dataValidade);
        produtoDTO.setControleEstoque(VALID_controleEstoque);
        produtoDTO.setSituacao(VALID_situacao);
        produtoDTO.setUnitario(VALID_unitario);
        produtoDTO.setSubtribu(VALID_subtribu);
        produtoDTO.setNcm(VALID_ncm);
        produtoDTO.setCest(VALID_cest);
        produtoDTO.setTributacao(VALID_tributacao);
        produtoDTO.setModbc(VALID_modbc);
        produtoDTO.setVendavel(VALID_vendavel);

        return produtoDTO;
    }
}
