package com.trackorjargh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Automacao do caso de teste manual U3 - Pesquisar serie por genero e ver o
 * retorno. Passos conforme a tabela da Entrega 1.
 */
public class TestE2EU3PesquisarSeriePorGenero extends TestE2EBase {

    @Test
    public void pesquisaSeriesDoGeneroComedia() {
        goToPage("");

        // Passo 1: aba "Busqueda"
        driver.findElement(By.cssSelector("ul.nav-menu a[href='/busqueda']")).click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='inputSearch']")),
                "Pagina de busca nao exibida", 5);

        // Passo 2: selecionar busca por "Genero"
        Select criterio = new Select(driver.findElement(By.cssSelector("select[name='optionSearch']")));
        criterio.selectByValue("genero");
        assertThat(criterio.getFirstSelectedOption().getAttribute("value")).isEqualTo("genero");

        // Passo 3: digitar um genero (Comedia)
        WebElement campo = driver.findElement(By.cssSelector("input[name='inputSearch']"));
        campo.sendKeys("Comedia");
        assertThat(campo.getAttribute("value")).isEqualTo("Comedia");

        // Passo 4: selecionar o tipo "Series"
        driver.findElement(By.id("linkShow")).click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#elements a[name='nameFilm']")),
                "Resultados nao exibidos", 5);
        assertThat(driver.getCurrentUrl()).contains("/busqueda/genero/serie/Comedia");
        assertThat(driver.findElement(By.cssSelector("#portfolio-flters .filter-active")).getText())
                .isEqualToIgnoringCase("Series");

        // Passo 5: listagem de resultados compativeis com o genero
        List<String> nomes = new ArrayList<>();
        for (WebElement titulo : driver.findElements(By.cssSelector("#elements a[name='nameFilm']"))) {
            nomes.add(textOf(titulo));
        }
        assertThat(nomes).isNotEmpty();
        // "The Big Bang Theory" esta cadastrada no genero Comedia (DatabaseInformationLoader)
        assertThat(nomes).contains("The Big Bang Theory");
        // "Narcos" nao e comedia
        assertThat(nomes).doesNotContain("Narcos");
    }
}
