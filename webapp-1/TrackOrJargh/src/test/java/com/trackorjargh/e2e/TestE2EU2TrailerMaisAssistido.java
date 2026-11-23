package com.trackorjargh.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Automacao do caso de teste manual U2 - Assistir trailer do filme mais
 * assistido (mais bem avaliado). Passos conforme a tabela da Entrega 1.
 */
public class TestE2EU2TrailerMaisAssistido extends TestE2EBase {

    @Test
    public void assisteTrailerDoFilmeMelhorAvaliado() {
        goToPage("");

        // Passo 1: aba "Peliculas" no topo
        driver.findElement(By.cssSelector("ul.nav-menu a[href='/peliculas']")).click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("elements")), "Secao de filmes nao exibida", 5);
        assertThat(driver.getCurrentUrl()).endsWith("/peliculas");

        // Passo 2: botao "Mejores Valoradas" filtra os filmes mais bem avaliados
        By filtroMelhores = By.cssSelector("#portfolio-flters a[href='/peliculas/mejorvaloradas']");
        waitUntil(ExpectedConditions.elementToBeClickable(filtroMelhores),
                "Filtro de melhores avaliados nao disponivel", 5);
        driver.findElement(filtroMelhores).click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("elements")), "Filtro nao aplicado", 5);
        assertThat(driver.getCurrentUrl()).endsWith("/peliculas/mejorvaloradas");
        assertThat(driver.findElement(By.cssSelector("#portfolio-flters .filter-active")).getText())
                .containsIgnoringCase("Mejores");

        // Passo 3: titulo do primeiro card -> pagina de detalhes do filme
        List<WebElement> cards = driver.findElements(By.cssSelector("#elements a[name='nameFilm']"));
        assertThat(cards).isNotEmpty();
        String nomeFilme = textOf(cards.get(0));
        clickJs(cards.get(0));
        waitUntil(ExpectedConditions.presenceOfElementLocated(By.id("services")), "Detalhes do filme nao carregados", 5);
        assertThat(driver.getCurrentUrl()).contains("/pelicula/");

        // Passo 4: secao "Informacion" com informacoes e trailer
        WebElement secao = driver.findElement(By.id("services"));
        scrollTo(secao);
        assertThat(textOf(secao.findElement(By.cssSelector(".section-header h3")))).contains("Información");

        // Passo 5: trailer disponivel para reproducao (iframe do YouTube)
        WebElement trailer = secao.findElement(By.cssSelector(".videoYoutube iframe"));
        assertThat(trailer.isDisplayed()).isTrue();
        assertThat(trailer.getAttribute("src")).contains("youtube.com/embed");
        driver.switchTo().frame(trailer); // o frame do player pode ser acessado
        driver.switchTo().defaultContent();
        log.info("Trailer disponivel para o filme: {}", nomeFilme);
    }
}
