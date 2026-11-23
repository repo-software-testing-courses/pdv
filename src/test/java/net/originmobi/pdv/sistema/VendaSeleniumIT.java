package net.originmobi.pdv.sistema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Testes de SISTEMA (Selenium WebDriver) do fechamento de venda.
 *
 * <p>O Chrome usa o sistema real, como uma pessoa faria: telas de pedido,
 * pagamento e caixa, JavaScript ({@code venda.js}, janela de pagamento, AJAX das
 * parcelas e dos títulos), {@code VendaController}, {@code VendaService} e MySQL,
 * incluindo os triggers. A digitação usa eventos reais de teclado
 * ({@code sendKeys}, inclusive nos campos com máscara de dinheiro) e os alertas
 * do navegador são lidos de verdade.
 *
 * <p>Massa de dados: os dois produtos de teste são criados direto no banco (JDBC),
 * porque o cadastro de produto pela tela exige fornecedor, categoria, grupo e
 * validações que não fazem parte do requisito sob teste. Títulos de pagamento e
 * caixa são criados pela própria interface. Não há rollback: pedidos, títulos e
 * produtos criados ficam no banco; por isso os testes comparam valores do próprio
 * pedido e do caixa que abriram, e não totais globais.
 *
 * <p>Pré-requisitos: sistema no ar em {@code http://localhost:8080}, banco
 * acessível e o cliente 1 (João Rafael Mendes Nogueira) cadastrado. Execução:
 * {@code mvn -Dtest=VendaSeleniumIT test} (opcional: {@code -Dheadless=true},
 * {@code -Dpdv.url=...}, {@code -Dpdv.db.url=...},
 * {@code -Dselenium.remote=http://host:4444} para usar um Chrome remoto, como o
 * da imagem {@code selenium/standalone-chrome}).
 */
class VendaSeleniumIT {

    private static final String URL = System.getProperty("pdv.url", "http://localhost:8080");
    private static final String USUARIO = "gerente";
    private static final String SENHA = "123";
    private static final String CLIENTE = "João Rafael Mendes Nogueira";

    private static final String DB_URL = System.getProperty("pdv.db.url",
            "jdbc:mysql://localhost:3306/pdv?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String DB_USUARIO = System.getProperty("pdv.db.usuario", "root");
    private static final String DB_SENHA = System.getProperty("pdv.db.senha", "123456");

    private static final String TITULO_DINHEIRO = "Dinheiro Selenium";
    private static final String TITULO_DEBITO = "Débito Selenium";
    private static final String PRODUTO_LIVRE = "Produto Selenium sem controle de estoque";
    private static final String PRODUTO_CONTROLADO = "Produto Selenium com controle de estoque";
    private static final double VALOR_PRODUTO = 3.5;

    /** Produto que não controla estoque (as vendas de sucesso dependem disso, ver #14). */
    private static String codigoProdutoLivre;
    /** Produto que controla estoque e nasce com saldo 0. */
    private static String codigoProdutoControlado;

    private WebDriver driver;
    private WebDriverWait espera;

    /** Caixas abertos pelo teste, fechados no {@link #fechaNavegador()}. */
    private final List<String> abertos = new ArrayList<>();

    // ------------------------------------------------------------------
    // Massa de dados e navegador
    // ------------------------------------------------------------------

    @BeforeAll
    static void criaProdutosDeTeste() throws SQLException {
        codigoProdutoLivre = garanteProduto(PRODUTO_LIVRE, "NAO");
        codigoProdutoControlado = garanteProduto(PRODUTO_CONTROLADO, "SIM");
    }

    /** Devolve o código do produto com essa descrição, criando-o se ainda não existir. */
    private static String garanteProduto(String descricao, String controlaEstoque) throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USUARIO, DB_SENHA)) {
            try (PreparedStatement busca = c.prepareStatement("select codigo from produto where descricao = ?")) {
                busca.setString(1, descricao);
                try (ResultSet rs = busca.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
            String sql = "insert into produto (descricao, valor_custo, valor_venda, balanca, ativo, "
                    + "fornecedor_codigo, grupo_codigo, unidade, subtributaria, categoria_codigo, vendavel, "
                    + "controla_estoque, data_cadastro) values (?, 1, ?, 0, 'ATIVO', 1, 1, 'UN', 0, 1, 'SIM', ?, curdate())";
            try (PreparedStatement insere = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                insere.setString(1, descricao);
                insere.setDouble(2, VALOR_PRODUTO);
                insere.setString(3, controlaEstoque);
                insere.executeUpdate();
                try (ResultSet chaves = insere.getGeneratedKeys()) {
                    chaves.next();
                    return chaves.getString(1);
                }
            }
        }
    }

    private static WebDriver criaNavegador() {
        ChromeOptions opcoes = new ChromeOptions();
        String remoto = System.getProperty("selenium.remote");
        if (Boolean.getBoolean("headless")) {
            opcoes.addArguments("--headless=new");
        }
        opcoes.addArguments("--window-size=1366,900");
        if (remoto == null) {
            return new ChromeDriver(opcoes);
        }
        try {
            return new RemoteWebDriver(new URL(remoto), opcoes);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("selenium.remote inválido: " + remoto, e);
        }
    }

    @BeforeEach
    void abreNavegadorEFazLogin() {
        driver = criaNavegador();
        espera = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(URL + "/login");
        driver.findElement(By.id("user")).sendKeys(USUARIO);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.id("btn-login")).click();
        espera.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    @AfterEach
    void fechaNavegador() {
        try {
            Alert pendente = alertaPresente();
            if (pendente != null) {
                pendente.dismiss();
            }
            for (String codigo : abertos) {
                driver.get(URL + "/caixa/gerenciar/" + codigo);
                if (driver.findElement(By.id("btnfechacaixa")).isEnabled()) {
                    fechaCaixaPelaTela();
                }
            }
        } finally {
            driver.quit();
        }
    }

    // ------------------------------------------------------------------
    // Ações na tela: títulos e caixa
    // ------------------------------------------------------------------

    /** Cadastra o título pela tela, se a lista ainda não o tiver. */
    private void garanteTitulo(String descricao, String tipoVisivel) {
        driver.get(URL + "/titulos");
        boolean existe = driver.findElements(By.cssSelector("#body-titulos table tbody td")).stream()
                .anyMatch(td -> td.getText().trim().equals(descricao));
        if (existe) {
            return;
        }
        driver.get(URL + "/titulos/form");
        driver.findElement(By.id("descricao")).sendKeys(descricao);
        new Select(driver.findElement(By.id("titulo_tipo"))).selectByVisibleText(tipoVisivel);
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        espera.until(ExpectedConditions.urlMatches(".*/titulos$"));
    }

    /** Já existe algum caixa aberto no sistema? (só caixa aberto tem o link de gerenciar) */
    private boolean existeCaixaAberto() {
        driver.get(URL + "/caixa");
        return !driver.findElements(By.cssSelector("a[href*='/caixa/gerenciar/']")).isEmpty();
    }

    /** Abre um caixa de R$ 100,00, se não houver nenhum aberto, e devolve o código do caixa em uso. */
    private String garanteCaixaAberto() {
        if (existeCaixaAberto()) {
            String href = driver.findElement(By.cssSelector("a[href*='/caixa/gerenciar/']")).getAttribute("href");
            return href.substring(href.lastIndexOf('/') + 1);
        }
        driver.get(URL + "/caixa/form");
        new Select(driver.findElement(By.id("caixatipo"))).selectByValue("CAIXA");
        WebElement valor = driver.findElement(By.id("valorAbertura"));
        valor.clear();
        valor.sendKeys("10000"); // a máscara transforma em R$ 100,00
        driver.findElement(By.cssSelector(".btn-abrir-caixa")).click();
        try {
            espera.until(d -> alertaPresente() != null || d.getCurrentUrl().contains("/caixa/gerenciar/"));
        } catch (TimeoutException e) {
            fail("A tela não respondeu à abertura do caixa");
        }
        Alert alerta = alertaPresente();
        if (alerta != null) {
            String texto = alerta.getText();
            alerta.accept();
            fail("Não foi possível abrir o caixa: " + texto);
        }
        String codigo = driver.findElement(By.id("codigoCaixa")).getAttribute("value");
        abertos.add(codigo);
        return codigo;
    }

    private void fechaCaixaPelaTela() {
        driver.findElement(By.id("btnfechacaixa")).click();
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("admsenha"))).sendKeys(SENHA);
        driver.findElement(By.cssSelector(".btn-fechar-caixa")).click();
        espera.until(ExpectedConditions.alertIsPresent()).accept();
    }

    // ------------------------------------------------------------------
    // Ações na tela: pedido e pagamento
    // ------------------------------------------------------------------

    /** Salva um pedido novo (com ou sem cliente) e devolve o código mostrado na tela. */
    private String novaVenda(String cliente) {
        driver.get(URL + "/venda/form");
        if (cliente != null) {
            new Select(driver.findElement(By.id("cliente"))).selectByVisibleText(cliente);
        }
        driver.findElement(By.id("btn-salva")).click();
        espera.until(ExpectedConditions.urlMatches(".*/venda/\\d+$"));
        return driver.findElement(By.id("codigoVenda")).getAttribute("value");
    }

    /** Escolhe o produto na caixa de seleção (bootstrap-select), clica em "Inserir" e espera a tabela. */
    private void insereProduto(String codigoProduto) {
        driver.findElement(By.cssSelector("button[data-id='codigoProduto']")).click();
        espera.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//button[@data-id='codigoProduto']/ancestor::div[contains(@class,'bootstrap-select')][1]"
                        + "//li/a[contains(normalize-space(.),'COD: " + codigoProduto + " -')]")))
                .click();
        driver.findElement(By.cssSelector(".js-addvenda-produto")).click();
        try {
            espera.until(d -> d.findElements(By.cssSelector("#tabdados tbody td:first-child")).stream()
                    .anyMatch(td -> td.getText().trim().equals(codigoProduto)));
        } catch (TimeoutException e) {
            fail("O produto " + codigoProduto + " não apareceu na tabela do pedido");
        }
    }

    /** Abre a janela de pagamento, escolhe a forma e o título e espera o AJAX preencher as parcelas. */
    private void preparaPagamento(String forma, String tituloDescricao) {
        driver.findElement(By.id("btn-venda")).click();
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagamento")));
        new Select(driver.findElement(By.id("pagamento"))).selectByVisibleText(forma);
        // as linhas de parcela e as opções de título chegam por AJAX
        espera.until(d -> !d.findElements(By.cssSelector(".selectnovo option")).isEmpty());
        new Select(driver.findElement(By.cssSelector(".selectnovo"))).selectByVisibleText(tituloDescricao);
    }

    private void digita(String campo, String digitos) {
        driver.findElement(By.id(campo)).sendKeys(digitos);
    }

    /** Clica em "Pagar" e devolve a mensagem que o sistema mostra no alerta. */
    private String pagar() {
        driver.findElement(By.cssSelector("a.btn-pagamento")).click();
        Alert alerta = espera.until(ExpectedConditions.alertIsPresent());
        String texto = alerta.getText();
        alerta.accept();
        return texto;
    }

    private String situacaoDoPedido(String codigo) {
        driver.get(URL + "/venda/" + codigo);
        return driver.findElement(By.id("statusPedido")).getAttribute("value");
    }

    /** Valor da linha de lançamento do caixa cuja descrição contém o texto, ou null se não houver. */
    private String valorDoLancamento(String codigoCaixa, String descricao) {
        driver.get(URL + "/caixa/gerenciar/" + codigoCaixa);
        for (WebElement linha : driver.findElements(By.cssSelector("#tabLancamentos tbody tr"))) {
            List<WebElement> colunas = linha.findElements(By.tagName("td"));
            if (colunas.size() >= 3 && colunas.get(1).getText().contains(descricao)) {
                return colunas.get(2).getText().trim();
            }
        }
        return null;
    }

    private Alert alertaPresente() {
        try {
            return driver.switchTo().alert();
        } catch (NoAlertPresentException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Cenários
    // ------------------------------------------------------------------

    @Test
    @DisplayName("1. CT-VENDA-01: venda à vista em dinheiro com caixa aberto fecha e lança a entrada no caixa")
    void aVistaEmDinheiro_fechaELancaNoCaixa() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        String caixa = garanteCaixaAberto();
        String venda = novaVenda(CLIENTE);
        insereProduto(codigoProdutoLivre);

        preparaPagamento("À Vista", TITULO_DINHEIRO);
        assertEquals("Venda finalizada com sucesso", pagar());

        assertEquals("FECHADA", situacaoDoPedido(venda));
        assertFalse(driver.findElement(By.id("btn-venda")).isEnabled(),
                "o botão Gerar Venda deveria ficar bloqueado na venda fechada");
        assertFalse(driver.findElement(By.id("btn-salva")).isEnabled(),
                "o botão Salvar deveria ficar bloqueado na venda fechada");
        assertEquals("R$ 3,50", valorDoLancamento(caixa, "Recebimento de venda"),
                "a entrada no caixa deveria ser o valor da venda");
    }

    /**
     * DEFEITO (DEF-VENDA-004): o {@code insert into parcela} não informa {@code data_alteracao},
     * coluna {@code TIMESTAMP NOT NULL} sem valor padrão na migração; no MySQL 8 o banco recusa o
     * insert e nenhuma venda a prazo fecha ("No message available" na tela).
     */
    @Test
    @Tag("defeito")
    @DisplayName("2. CT-VENDA-04: venda a prazo com cliente fecha [DEFEITO]")
    void aPrazoComCliente_fecha() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        String venda = novaVenda(CLIENTE);
        insereProduto(codigoProdutoLivre);

        preparaPagamento("A Prazo", TITULO_DINHEIRO);

        assertEquals("Venda finalizada com sucesso", pagar());
        assertEquals("FECHADA", situacaoDoPedido(venda));
    }

    @Test
    @DisplayName("3. CT-VENDA-05: venda a prazo sem cliente é bloqueada e o pedido continua aberto")
    void aPrazoSemCliente_ficaBloqueada() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        String venda = novaVenda(null);
        insereProduto(codigoProdutoLivre);

        preparaPagamento("A Prazo", TITULO_DINHEIRO);

        assertEquals("Venda sem cliente, verifique", pagar());
        assertEquals("ABERTA", situacaoDoPedido(venda));
    }

    @Test
    @DisplayName("4. CT-VENDA-07: venda à vista em dinheiro sem caixa aberto é bloqueada e o pedido continua aberto")
    void aVistaEmDinheiro_semCaixaAberto_ficaBloqueada() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        assumeTrue(!existeCaixaAberto(), "há um caixa aberto no sistema; o cenário exige nenhum");
        String venda = novaVenda(CLIENTE);
        insereProduto(codigoProdutoLivre);

        preparaPagamento("À Vista", TITULO_DINHEIRO);

        assertEquals("nenhum caixa aberto", pagar());
        assertEquals("ABERTA", situacaoDoPedido(venda));
    }

    @Test
    @DisplayName("5. CT-VENDA-08: venda sem produtos (valor zero) é bloqueada")
    void vendaSemValor_ficaBloqueada() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        String venda = novaVenda(CLIENTE);

        preparaPagamento("À Vista", TITULO_DINHEIRO);

        assertEquals("Venda sem valor, verifique", pagar());
        assertEquals("ABERTA", situacaoDoPedido(venda));
    }

    @Test
    @DisplayName("6. CT-VENDA-10: produto que controla estoque sem saldo bloqueia o fechamento e o pedido continua aberto")
    void estoqueInsuficiente_ficaBloqueada() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        garanteCaixaAberto();
        String venda = novaVenda(CLIENTE);
        insereProduto(codigoProdutoControlado);

        preparaPagamento("À Vista", TITULO_DINHEIRO);

        assertEquals("O produto de código " + codigoProdutoControlado
                + " não tem estoque suficiente, verifique", pagar());
        assertEquals("ABERTA", situacaoDoPedido(venda),
                "sem estoque, o fechamento inteiro deveria ser desfeito");
    }

    /** DEFEITO #12: título de cartão sem máquina associada derruba o fechamento (NullPointerException). */
    @Test
    @Tag("defeito")
    @DisplayName("7. CT-VENDA-03: venda à vista no cartão de débito fecha [DEFEITO #12]")
    void aVistaNoCartaoDeDebito_fecha() {
        garanteTitulo(TITULO_DEBITO, "Cartão Debito");
        String venda = novaVenda(CLIENTE);
        insereProduto(codigoProdutoLivre);

        preparaPagamento("À Vista", TITULO_DEBITO);

        assertEquals("Venda finalizada com sucesso", pagar(),
                "o sistema deveria fechar a venda no cartão de débito");
        assertEquals("FECHADA", situacaoDoPedido(venda));
    }

    /** DEFEITO #13: desconto e acréscimo são aplicados trocados na parcela lançada no caixa. */
    @Test
    @Tag("defeito")
    @DisplayName("8. CT-VENDA-06: desconto de 1,00 e acréscimo de 0,95 lançam R$ 3,45 no caixa [DEFEITO #13]")
    void comDescontoEAcrescimo_lancaOValorCorreto() {
        garanteTitulo(TITULO_DINHEIRO, "Dinheiro");
        String caixa = garanteCaixaAberto();
        String venda = novaVenda(CLIENTE);
        insereProduto(codigoProdutoLivre);

        preparaPagamento("À Vista", TITULO_DINHEIRO);
        digita("desconto", "100"); // a máscara transforma em 1,00
        digita("acrescimo", "95"); // a máscara transforma em 0,95
        assertEquals("Venda finalizada com sucesso", pagar());
        assertEquals("FECHADA", situacaoDoPedido(venda));

        // (3,50 + 0,95) - 1,00 = 3,45
        String lancado = valorDoLancamento(caixa, "Recebimento de venda");
        assertTrue(lancado != null, "a venda deveria ter lançado a entrada no caixa");
        assertEquals("R$ 3,45", lancado, "o valor lançado no caixa deveria ser (produtos + acréscimo) - desconto");
    }
}
