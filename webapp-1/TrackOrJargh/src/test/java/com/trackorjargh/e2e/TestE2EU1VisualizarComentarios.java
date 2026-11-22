package com.trackorjargh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Automacao do caso de teste manual U1 - Visualizar comentarios de uma serie.
 * Os numeros dos passos seguem a tabela do caso manual da Entrega 1.
 */
public class TestE2EU1VisualizarComentarios extends TestE2EBase {

    @Test
    public void visualizaComentariosDaSerieNarcos() {
        goToPage("");

        // Passo 1: clicar na aba "Busqueda" no topo superior direito
        driver.findElement(By.cssSelector("ul.nav-menu a[href='/busqueda']")).click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='inputSearch']")),
                "Pagina de busca nao exibida", 5);
        assertThat(driver.getCurrentUrl()).endsWith("/busqueda");

        // Passo 2: digitar "narcos" no campo de busca
        WebElement campo = driver.findElement(By.cssSelector("input[name='inputSearch']"));
        campo.sendKeys("narcos");
        assertThat(campo.getAttribute("value")).isEqualTo("narcos");

        // Passo 3: clicar no botao "Series" -> card da serie "Narcos" nos resultados
        driver.findElement(By.id("linkShow")).click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#elements a[name='nameFilm']")),
                "Resultado da busca nao exibido", 5);
        List<WebElement> titulos = driver.findElements(By.cssSelector("#elements a[name='nameFilm']"));
        assertThat(titulos).hasSize(1);
        assertThat(textOf(titulos.get(0))).isEqualTo("Narcos");

        // Passo 4: clicar no titulo "Narcos" -> pagina da serie
        clickJs(titulos.get(0));
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("contact")),
                "Pagina da serie Narcos nao carregada", 5);
        assertThat(driver.getCurrentUrl()).contains("/serie/Narcos");

        // Passo 5: rolar ate a secao de Comentarios
        WebElement secao = driver.findElement(By.id("contact"));
        scrollTo(secao); // rola ate a secao
        assertThat(textOf(secao.findElement(By.cssSelector(".section-header h3")))).contains("Comentarios");

        // Passo 6: comentarios dos usuarios relacionados a serie sao exibidos
        List<WebElement> comentarios = driver.findElements(By.cssSelector("#contact .comment p.description"));
        assertThat(comentarios).isNotEmpty();
        assertThat(textOf(comentarios.get(0))).isNotEmpty();
        assertThat(driver.findElements(By.xpath("//*[@id='contact']//p[contains(.,'aún no tiene comentarios')]")))
                .isEmpty();
    }
}
