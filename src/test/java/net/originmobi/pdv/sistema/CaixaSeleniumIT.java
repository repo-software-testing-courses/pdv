package net.originmobi.pdv.sistema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Testes de SISTEMA (Selenium) do módulo de caixa.
 *
 * <p>O Chrome usa o sistema real, como uma pessoa faria: tela, JavaScript
 * ({@code caixa.js}, máscara de valor), {@code CaixaController},
 * {@code CaixaService} e MySQL. Não há rollback: cada teste fecha pela tela o
 * caixa que abriu, para não travar a próxima execução.
 *
 * <p>Pré-requisitos: sistema no ar ({@code docker compose up -d}), Chrome
 * instalado e nenhum caixa diário aberto. Execução:
 * {@code mvn -Dtest=CaixaSeleniumIT test} (opcional: {@code -Dheadless=true},
 * {@code -Dpdv.url=...}).
 */
class CaixaSeleniumIT {

    private static final String URL = System.getProperty("pdv.url", "http://localhost:8080");
    private static final String USUARIO = "gerente";
    private static final String SENHA = "123";

    private WebDriver driver;
    private WebDriverWait espera;

    /** Caixas abertos pelo teste, fechados no {@link #fechaCaixasAbertos()}. */
    private final List<String> abertos = new ArrayList<>();

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
    void fechaCaixasAbertos() {
        try {
            for (String codigo : abertos) {
                driver.get(URL + "/caixa/gerenciar/" + codigo);
                if (driver.findElement(By.id("btnfechacaixa")).isEnabled()) {
                    fechaCaixaPelaTela(SENHA);
                }
            }
        } finally {
            driver.quit();
        }
    }

    // ------------------------------------------------------------------
    // Ações na tela
    // ------------------------------------------------------------------

    /** Preenche o formulário de abertura e clica em "Abrir". Devolve o alerta, se o sistema mostrar um. */
    private Alert preencheEAbre(String tipo, String valorDigitado) {
        return preencheEAbre(tipo, valorDigitado, null, null);
    }

    private Alert preencheEAbre(String tipo, String valorDigitado, String agencia, String conta) {
        driver.get(URL + "/caixa/form");
        new Select(driver.findElement(By.id("caixatipo"))).selectByValue(tipo);
        if (agencia != null) {
            // os campos de agência e conta só são criados pelo JS quando o tipo BANCO é escolhido
            espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("agencia"))).sendKeys(agencia);
            driver.findElement(By.id("conta")).sendKeys(conta);
        }
        WebElement valor = driver.findElement(By.id("valorAbertura"));
        valor.clear();
        valor.sendKeys(valorDigitado);
        driver.findElement(By.cssSelector(".btn-abrir-caixa")).click();

        // espera o redirecionamento ou o alerta de erro (com alerta aberto, ler a URL lança exceção)
        try {
            espera.until(d -> alertaPresente() != null || d.getCurrentUrl().contains("/caixa/gerenciar/"));
        } catch (TimeoutException e) {
            fail("A tela não respondeu à abertura do caixa");
        }
        return alertaPresente();
    }

    /** Abre um caixa diário e devolve o código mostrado na tela de gerenciamento. */
    private String abreCaixa(String valorDigitado) {
        return confirmaAbertura(preencheEAbre("CAIXA", valorDigitado));
    }

    private String confirmaAbertura(Alert alerta) {
        if (alerta != null) {
            String texto = alerta.getText();
            alerta.accept();
            fail("Não foi possível abrir o caixa (há outro caixa diário aberto no sistema?): " + texto);
        }
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("codigoCaixa")));
        String codigo = driver.findElement(By.id("codigoCaixa")).getAttribute("value");
        abertos.add(codigo);
        return codigo;
    }

    /** Abre o modal de fechamento, digita a senha, confirma e devolve a mensagem do alerta. */
    private String fechaCaixaPelaTela(String senha) {
        driver.findElement(By.id("btnfechacaixa")).click();
        WebElement campoSenha = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("admsenha")));
        campoSenha.sendKeys(senha);
        driver.findElement(By.cssSelector(".btn-fechar-caixa")).click();

        Alert alerta = espera.until(ExpectedConditions.alertIsPresent());
        String texto = alerta.getText();
        alerta.accept();
        return texto;
    }

    private Alert alertaPresente() {
        try {
            return driver.switchTo().alert();
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            return null;
        }
    }

    private String saldoTotal() {
        return driver.findElement(By.cssSelector("input#valorTotal")).getAttribute("value");
    }

    private boolean listagemContem(String codigo) {
        return driver.findElements(By.cssSelector("table.table tbody td:first-child")).stream()
                .anyMatch(td -> td.getText().trim().equals(codigo));
    }

    // ------------------------------------------------------------------
    // Cenários
    // ------------------------------------------------------------------

    @Test
    @DisplayName("1. Abrir caixa com R$ 100,00 leva à tela de gerenciamento com o saldo")
    void abrirCaixa_mostraTelaDeGerenciamento() {
        String codigo = abreCaixa("10000"); // a máscara transforma em R$ 100,00

        assertTrue(driver.getCurrentUrl().endsWith("/caixa/gerenciar/" + codigo));
        assertEquals(USUARIO, driver.findElement(By.cssSelector("input#usuario")).getAttribute("value"));
        assertEquals("100,00", saldoTotal());
        assertTrue(driver.findElement(By.id("btnfechacaixa")).isEnabled());
    }

    @Test
    @DisplayName("2. Valor com máscara (1.234,56) chega correto ao sistema")
    void abrirCaixaComMascara_gravaValorCorreto() {
        abreCaixa("123456"); // a máscara transforma em R$ 1.234,56

        assertEquals("1.234,56", saldoTotal());
    }

    @Test
    @DisplayName("3. Fechar com senha errada mostra a mensagem e mantém o caixa aberto")
    void fecharComSenhaErrada_mantemAberto() {
        abreCaixa("1000");

        assertEquals("Senha incorreta, favor verifique", fechaCaixaPelaTela("senha-errada"));
        assertTrue(driver.findElement(By.id("btnfechacaixa")).isEnabled());
    }

    @Test
    @DisplayName("4. Fechar com a senha certa mostra sucesso e desabilita as operações")
    void fecharComSenhaCerta_desabilitaOperacoes() {
        abreCaixa("1000");

        assertEquals("Caixa fechado com sucesso", fechaCaixaPelaTela(SENHA));
        espera.until(ExpectedConditions.presenceOfElementLocated(By.id("statusFechado")));
        assertFalse(driver.findElement(By.id("btnfechacaixa")).isEnabled());
        assertFalse(driver.findElement(By.id("btnSuprimento")).isEnabled());
        assertFalse(driver.findElement(By.id("btnSangria")).isEnabled());
        assertFalse(driver.findElement(By.id("btnTransferencia")).isEnabled());
    }

    @Test
    @DisplayName("5. Abrir um segundo caixa com outro aberto mostra a mensagem de bloqueio")
    void abrirSegundoCaixa_mostraMensagemDeBloqueio() {
        abreCaixa("1000");

        Alert alerta = preencheEAbre("CAIXA", "500");

        assertNotNull(alerta, "o sistema deveria avisar que já existe caixa aberto");
        String texto = alerta.getText();
        alerta.accept();
        assertEquals("Existe caixa de dias anteriores em aberto, favor verifique", texto);
    }

    @Test
    @DisplayName("6. A listagem mostra o caixa recém-aberto")
    void listagem_mostraCaixaAberto() {
        String codigo = abreCaixa("1000");

        driver.get(URL + "/caixa");

        assertTrue(listagemContem(codigo));
    }

    /** DEFEITO #24: o filtro por data sempre volta vazio. */
    @Test
    @Tag("defeito")
    @DisplayName("7. Filtrar a listagem pela data de hoje mostra o caixa aberto hoje")
    void filtroPorData_mostraCaixaDoDia() {
        String codigo = abreCaixa("1000");
        String hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        driver.get(URL + "/caixa");
        WebElement filtro = driver.findElement(By.name("data_cadastro"));
        filtro.sendKeys(hoje);
        filtro.submit();
        espera.until(ExpectedConditions.urlContains("data_cadastro="));

        assertTrue(listagemContem(codigo), "caixa aberto hoje deveria aparecer no filtro por data (#24)");
    }

    @Test
    @DisplayName("9. Abrir BANCO pela tela mostra os campos de agência e conta e o banco aparece na tela Bancos")
    void abrirBanco_apareceNaTelaDeBancos() {
        String codigo = confirmaAbertura(preencheEAbre("BANCO", "5000", "12345", "678901"));

        driver.get(URL + "/banco");

        assertTrue(listagemContem(codigo), "o banco aberto deveria aparecer na tela Bancos");
    }
}
