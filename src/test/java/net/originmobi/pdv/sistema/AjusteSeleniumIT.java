package net.originmobi.pdv.sistema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Testes de SISTEMA (Selenium WebDriver) do ajuste de estoque.
 *
 * <p>O Chrome usa o sistema real, como uma pessoa faria: tela, JavaScript
 * ({@code ajuste.js}, seleção de produto, janelas de quantidade e de
 * confirmação), {@code AjusteController}, {@code AjusteService} e MySQL,
 * incluindo o trigger de estoque. A digitação é feita com eventos reais de
 * teclado ({@code sendKeys}) e as janelas do navegador (prompt, confirm e
 * alert) são respondidas de verdade, sem os contornos com {@code execute script}
 * que o Selenium IDE exigia no Firefox.
 *
 * <p>Não há rollback: os ajustes criados ficam no banco. Por isso os testes de
 * saldo comparam o saldo antes e depois, em vez de depender de um saldo fixo.
 *
 * <p>Pré-requisitos: sistema no ar em {@code http://localhost:8080}, Chrome
 * instalado e o produto 1 (Picolé, que controla estoque) cadastrado.
 * Execução: {@code mvn -Dtest=AjusteSeleniumIT test} (opcional:
 * {@code -Dheadless=true}, {@code -Dpdv.url=...}, {@code -Dpdv.produto=...}).
 */
class AjusteSeleniumIT {

    private static final String URL = System.getProperty("pdv.url", "http://localhost:8080");
    private static final String PRODUTO = System.getProperty("pdv.produto", "1");
    private static final String USUARIO = "gerente";
    private static final String SENHA = "123";

    private static final Pattern SALDO = Pattern.compile("Total Estoque:\\s*(-?\\d+)");

    private WebDriver driver;
    private WebDriverWait espera;

    @BeforeEach
    void abreNavegadorEFazLogin() {
        ChromeOptions opcoes = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {
            opcoes.addArguments("--headless=new", "--window-size=1366,900");
        }
        driver = new ChromeDriver(opcoes);
        espera = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(URL + "/login");
        driver.findElement(By.id("user")).sendKeys(USUARIO);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.id("btn-login")).click();
        espera.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    @AfterEach
    void fechaNavegador() {
        driver.quit();
    }

    // ------------------------------------------------------------------
    // Ações na tela
    // ------------------------------------------------------------------

    /** Clica em "Novo" na lista de ajustes e devolve o código do ajuste criado. */
    private String novoAjuste() {
        driver.get(URL + "/ajustes");
        driver.findElement(By.cssSelector(".btn-novo-ajuste")).click();
        espera.until(ExpectedConditions.urlMatches(".*/ajustes/\\d+$"));
        return driver.findElement(By.id("codajuste")).getAttribute("value");
    }

    /**
     * Escolhe o produto na caixa de seleção, clica em "Inserir", digita a
     * quantidade na janela do navegador e espera o produto aparecer na tabela.
     */
    private void insereProduto(int quantidade) {
        // a caixa de produtos é um bootstrap-select: o <select> original fica escondido
        driver.findElement(By.cssSelector("button[data-id='codigoProduto']")).click();
        espera.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//button[@data-id='codigoProduto']/ancestor::div[contains(@class,'bootstrap-select')][1]"
                        + "//li/a[contains(normalize-space(.),'COD: " + PRODUTO + " -')]")))
                .click();
        assertEquals(PRODUTO, new Select(driver.findElement(By.id("codigoProduto")))
                .getFirstSelectedOption().getAttribute("value"), "o produto deveria estar selecionado");

        driver.findElement(By.cssSelector(".btn-addajuste-produto")).click();
        Alert prompt = espera.until(ExpectedConditions.alertIsPresent());
        assertEquals("Informe a quantidade que deseja alterar", prompt.getText());
        prompt.sendKeys(String.valueOf(quantidade));
        prompt.accept();

        // espera a tabela recarregar com o produto (ou um alerta de erro do sistema)
        try {
            espera.until(d -> alertaPresente() != null || tabelaTemProduto());
        } catch (TimeoutException e) {
            fail("O produto não apareceu na tabela do ajuste");
        }
        Alert erro = alertaPresente();
        if (erro != null) {
            String texto = erro.getText();
            erro.accept();
            fail("O sistema não inseriu o produto: " + texto);
        }
    }

    /** Digita a observação, clica em "Processar", confirma e devolve a mensagem final do sistema. */
    private String processa(String observacao) {
        driver.findElement(By.id("obs")).sendKeys(observacao);
        driver.findElement(By.cssSelector(".btn-ajuste-processar")).click();

        Alert confirmacao = espera.until(ExpectedConditions.alertIsPresent());
        assertEquals("Tem certeza que deseja processar este ajuste?", confirmacao.getText());
        confirmacao.accept();

        Alert resultado = espera.until(ExpectedConditions.alertIsPresent());
        String texto = resultado.getText();
        resultado.accept();
        return texto;
    }

    /** Clica em "Cancelar" no ajuste aberto e responde à janela de confirmação. */
    private void clicaCancelar(boolean confirma) {
        driver.findElement(By.cssSelector(".btn-ajuste-cancelar")).click();
        Alert confirmacao = espera.until(ExpectedConditions.alertIsPresent());
        assertEquals("Tem certeza que deseja cancelar este ajuste?", confirmacao.getText());
        if (confirma) {
            confirmacao.accept();
        } else {
            confirmacao.dismiss();
        }
    }

    /** Saldo do produto, lido na caixa de seleção do ajuste ("COD: 1 - Picolé - Total Estoque: N"). */
    private int saldoDoProduto(String codigoAjuste) {
        driver.get(URL + "/ajustes/" + codigoAjuste);
        String texto = driver.findElement(By.cssSelector("#codigoProduto option[value='" + PRODUTO + "']"))
                .getAttribute("textContent");
        Matcher m = SALDO.matcher(texto);
        if (!m.find()) {
            fail("Não foi possível ler o saldo do produto em: " + texto);
        }
        return Integer.parseInt(m.group(1));
    }

    private boolean tabelaTemProduto() {
        return driver.findElements(By.cssSelector(".tabela-dados-ajuste tbody td:first-child")).stream()
                .anyMatch(td -> td.getText().trim().equals(PRODUTO));
    }

    /** A lista de ajustes, filtrada pelo código, mostra o ajuste? */
    private boolean ajusteExiste(String codigo) {
        driver.get(URL + "/ajustes?codigo=" + codigo);
        return driver.findElements(By.cssSelector("table.table tbody td:first-child")).stream()
                .anyMatch(td -> td.getText().trim().equals(codigo));
    }

    private String statusNaTela(String codigo) {
        driver.get(URL + "/ajustes/" + codigo);
        return driver.findElement(By.id("status")).getAttribute("value");
    }

    private Alert alertaPresente() {
        try {
            return driver.switchTo().alert();
        } catch (NoAlertPresentException e) {
            return null;
        }
    }

    /** Lê o alerta que o sistema mostrar (ou falha se nenhum aparecer) e o fecha. */
    private String leAlerta() {
        Alert alerta = espera.until(ExpectedConditions.alertIsPresent());
        String texto = alerta.getText();
        alerta.accept();
        return texto;
    }

    // ------------------------------------------------------------------
    // Cenários
    // ------------------------------------------------------------------

    @Test
    @DisplayName("1. CT-AJU-01: processar ajuste de entrada mostra sucesso e o ajuste fica Processado")
    void processarEntrada_mostraSucessoEStatusProcessado() {
        String codigo = novoAjuste();
        insereProduto(5);

        assertEquals("Ajuste realizado com sucesso", processa("Teste Selenium WebDriver entrada"));

        assertEquals("Processado", statusNaTela(codigo));
        assertFalse(driver.findElement(By.id("obs")).isEnabled(), "a observação deveria ficar bloqueada");
    }

    /** DEFEITO #14: a entrada não aumenta o saldo (o trigger sempre subtrai). */
    @Test
    @Tag("defeito")
    @DisplayName("2. CT-AJU-01: ajuste de entrada (+5) aumenta o saldo do produto em 5")
    void processarEntrada_aumentaSaldo() {
        String codigo = novoAjuste();
        int antes = saldoDoProduto(codigo);
        insereProduto(5);
        processa("Teste Selenium WebDriver saldo entrada");

        assertEquals(antes + 5, saldoDoProduto(codigo), "a entrada de +5 deveria aumentar o saldo (#14)");
    }

    /** DEFEITO #27: a saída aumenta o saldo (o trigger faz saldo - (-3)). */
    @Test
    @Tag("defeito")
    @DisplayName("3. CT-AJU-02: ajuste de saída (-3) diminui o saldo do produto em 3")
    void processarSaida_diminuiSaldo() {
        String codigo = novoAjuste();
        int antes = saldoDoProduto(codigo);
        insereProduto(-3);
        processa("Teste Selenium WebDriver saldo saida");

        assertEquals(antes - 3, saldoDoProduto(codigo), "a saída de -3 deveria diminuir o saldo (#27)");
    }

    @Test
    @DisplayName("4. CT-AJU-03: cancelar ajuste já processado mostra o bloqueio e o ajuste continua")
    void cancelarProcessado_eBloqueado() {
        String codigo = novoAjuste();
        insereProduto(1);
        processa("Teste Selenium WebDriver cancelar processado");
        driver.get(URL + "/ajustes/" + codigo);

        clicaCancelar(true);

        assertEquals("O ajuste já esta processado", leAlerta());
        assertTrue(ajusteExiste(codigo), "o ajuste processado não pode ser removido");
    }

    @Test
    @DisplayName("5. CT-AJU-04: cancelar ajuste pendente volta à lista sem o ajuste")
    void cancelarPendente_removeAjuste() {
        String codigo = novoAjuste();

        clicaCancelar(true);

        espera.until(ExpectedConditions.urlMatches(".*/ajustes/?$"));
        assertFalse(ajusteExiste(codigo), "o ajuste pendente deveria ter sido removido");
    }

    @Test
    @DisplayName("6. Responder \"Cancelar\" na janela de confirmação não remove o ajuste")
    void desistirDoCancelamento_mantemAjuste() {
        String codigo = novoAjuste();

        clicaCancelar(false);

        assertTrue(driver.getCurrentUrl().endsWith("/ajustes/" + codigo), "deveria continuar na tela do ajuste");
        assertEquals("A Processar", statusNaTela(codigo));
        assertTrue(ajusteExiste(codigo));
    }
}
