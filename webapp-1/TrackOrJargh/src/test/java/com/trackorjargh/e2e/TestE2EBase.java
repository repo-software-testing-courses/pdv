package com.trackorjargh.e2e;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

/**
 * Base dos testes de sistema (Entrega 2) que automatizam os casos de teste
 * manuais U1 a U4 da Entrega 1. Aponta para a aplicacao rodando localmente
 * (mvn spring-boot:run, porta 8000).
 */
public class TestE2EBase extends ElastestBaseTest {

    final static Logger log = getLogger(lookup().lookupClass());

    @BeforeAll
    public static void setupE2E() {
        String url = System.getenv("SUT_URL");
        sutUrl = (url != null) ? url : "http://localhost:8000/";
        log.info("sutUrl set to: {}", sutUrl);
    }

    public void goToPage(String page) {
        driver.get(sutUrl + page);
    }

    public void waitUntil(ExpectedCondition<?> condition, String errorMessage, int seconds) {
        WebDriverWait waiter = new WebDriverWait(driver, seconds);
        try {
            waiter.until(condition);
        } catch (org.openqa.selenium.TimeoutException timeout) {
            log.error(errorMessage);
            throw new org.openqa.selenium.TimeoutException(
                    "\"" + errorMessage + "\" (checked with condition) > " + timeout.getMessage());
        }
    }

    /**
     * Clique via JavaScript. Os cards do site usam wow.js (fade-in), que
     * deixa o elemento invisivel ate rolar a pagina; o clique via JS evita
     * falsos negativos por animacao.
     */
    protected void clickJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    /** Texto do elemento mesmo que ainda esteja com opacity/visibility da animacao. */
    protected String textOf(WebElement element) {
        Object text = ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", element);
        return text == null ? "" : text.toString().trim();
    }

    public void loginUser(String name, String pass) {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#login input[name='username']")),
                "No login page", 5);

        WebElement userField = driver.findElement(By.cssSelector("#login input[name='username']"));
        WebElement passField = driver.findElement(By.cssSelector("#login input[name='password']"));

        userField.click();
        userField.clear();
        userField.sendKeys(name);
        passField.click();
        passField.clear();
        passField.sendKeys(pass);
        passField.sendKeys(Keys.ENTER);

        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("carousel-content")),
                "Login failed for user: " + name, 5);

        List<WebElement> items = driver.findElements(By.cssSelector("ul.nav-menu a"));
        assertThat(items.get(items.size() - 1).getText()).isEqualToIgnoringCase("logout");
        assertThat(items.get(items.size() - 2).getText()).isEqualToIgnoringCase(name);
        log.info("Login successful, user {}", name);
    }

    public void logout() {
        driver.get(sutUrl + "logout");
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.nav-menu a[href='/login']")),
                "Logout redirect failed", 5);
        log.info("Logout successful");
    }
}
