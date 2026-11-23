package com.trackorjargh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Teste de sistema de seguranca: mensagens de falha de autenticacao e controle
 * de acesso por papel (ROLE_USER x ROLE_ADMIN).
 * Adaptado dos testes E2E do repositorio de apoio (ver docs/ai/AI-LOG.md).
 */
public class TestE2ESecurityRoles extends TestE2EBase {

    private void tentarLogin(String usuario, String senha) {
        goToPage("login");
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#login input[name='username']")),
                "Pagina de login nao carregada", 5);
        WebElement user = driver.findElement(By.cssSelector("#login input[name='username']"));
        WebElement pass = driver.findElement(By.cssSelector("#login input[name='password']"));
        user.clear();
        user.sendKeys(usuario);
        pass.clear();
        pass.sendKeys(senha);
        pass.sendKeys(Keys.ENTER);
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h5.text-center")),
                "Mensagem de erro nao exibida para " + usuario, 5);
    }

    @Test
    public void loginInvalidoExibeMensagemDeErro() {
        // Usuario inexistente
        tentarLogin("nao_existe", "qualquer");
        assertThat(driver.findElement(By.cssSelector("h5.text-center")).getText())
                .contains("El usuario introducido no existe");

        // Senha errada para usuario existente
        tentarLogin("oscar", "senha_errada");
        assertThat(driver.findElement(By.cssSelector("h5.text-center")).getText()).contains("Contraseña");

        // Nenhuma das tentativas autenticou o usuario
        assertThat(driver.findElements(By.cssSelector("ul.nav-menu a[href='/logout']"))).isEmpty();
    }

    @Test
    public void usuarioComumNaoAcessaAdministracaoEAdminAcessa() {
        // ROLE_USER (oscar) nao pode acessar /administracion
        goToPage("login");
        loginUser("oscar", "1234");
        goToPage("administracion");
        waitUntil(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "ERROR"),
                "Pagina de erro nao exibida para ROLE_USER", 5);
        assertThat(driver.findElements(By.id("adminTab"))).isEmpty();

        // A sessao continua ativa depois do acesso negado
        goToPage("");
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("carousel-content")),
                "Home nao carregada", 5);
        List<WebElement> itens = driver.findElements(By.cssSelector("ul.nav-menu a"));
        assertThat(itens.get(itens.size() - 1).getText()).isEqualToIgnoringCase("logout");
        logout();

        // ROLE_ADMIN (jesus) acessa /administracion com as 4 abas
        goToPage("login");
        loginUser("jesus", "1234");
        goToPage("administracion");
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("report")),
                "Pagina de administracao nao carregada para ADMIN", 5);
        assertThat(driver.findElements(By.cssSelector("#adminTab a.nav-link"))).hasSize(4);
        logout();
    }
}
