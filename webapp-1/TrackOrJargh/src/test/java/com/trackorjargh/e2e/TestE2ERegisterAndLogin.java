package com.trackorjargh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
// import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Teste de sistema: cadastro de usuario, ativacao da conta e login.
 * Adaptado dos testes E2E do repositorio de apoio (ver docs/ai/AI-LOG.md).
 */
public class TestE2ERegisterAndLogin extends TestE2EBase {

    @Test
    public void registraAtivaELogaUsuarioNovo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String username = "e2euser_" + timestamp;
        String email = "e2e_" + timestamp + "@test.com";
        String password = "test1234";

        goToPage("login");
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("loginRegister")),
                "Pagina de login nao carregada", 5);

        // Abre o formulario de registro e preenche
        driver.findElement(By.id("RegsDropdown")).click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#collapseRegt input[name='name']")),
                "Formulario de registro nao exibido", 5);
        driver.findElement(By.cssSelector("#collapseRegt input[name='name']")).sendKeys(username);
        driver.findElement(By.cssSelector("#collapseRegt input[name='email']")).sendKeys(email);
        driver.findElement(By.cssSelector("#collapseRegt input#password")).sendKeys(password);
        driver.findElement(By.cssSelector("#collapseRegt input#confirmPass")).sendKeys(password);
        driver.findElement(By.cssSelector("#collapseRegt input[name='register-submit']")).click();

        // Cadastro concluido, conta ainda por ativar
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h5.text-center")),
                "Mensagem de cadastro nao exibida", 5);
        assertThat(driver.findElement(By.cssSelector("h5.text-center")).getText())
                .contains("Has consegido registrarte correctamente");

        // Ativa a conta (o link normalmente chega por email)
        goToPage("activarusuario/" + username);
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h5.text-center")),
                "Mensagem de ativacao nao exibida", 5);
        assertThat(driver.findElement(By.cssSelector("h5.text-center")).getText())
                .contains("se ha activado correctamente");

        // Login com o usuario novo
        loginUser(username, password);
        logout();
    }
}
