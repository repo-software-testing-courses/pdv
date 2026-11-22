package com.trackorjargh.e2e;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import io.github.bonigarcia.seljup.SeleniumExtension;

@ExtendWith(SeleniumExtension.class)
public class TestE2EFront extends ElastestBaseTest {

	final static Logger log = getLogger(lookup().lookupClass());

	@Test
	public void checkCreateList() {
		// Login
		this.goToPage("login");
		this.loginUser("oscar", "1234");

		// Go to profile
		this.goToPage("perfil");

		// Select menu my lists
		WebElement menuProfileBar = driver.findElement(By.id("v-pills-tab"));
		List<WebElement> elementsProfileBar = menuProfileBar.findElements(By.tagName("a"));
		WebElement myLists = elementsProfileBar.get(1);

		// Move to my lists
		myLists.click();

		// Add list
		WebElement buttonAdd = driver.findElement(By.cssSelector("[data-target='#showAddList']"));
		buttonAdd.click();

		// Show add lists
		waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("showAddList")), "add list not shown", 1);

		// Create new list
		WebElement listName = this.driver.findElement(By.name("listName"));
		listName.sendKeys("nueva-lista");
		WebElement createList = driver.findElement(By.name("list-submit"));
		createList.click();

		// Check correct create
		waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("CreatedLists")), "Failed creating list",
				1);
		List<WebElement> createLists = driver.findElements(By.id("CreatedLists"));
		String nameListCreated = createLists.get(createLists.size() - 1).getText();
		assertThat(nameListCreated).isEqualToIgnoringCase("nueva-lista");
		log.info("List created correctly");

		// Remove list
		waitUntil(ExpectedConditions.visibilityOfElementLocated(By.name("deleteList")), "Failed removing list",
				1);
		List<WebElement> deleteListsButtons = driver.findElements(By.name("deleteList"));
		deleteListsButtons.get(deleteListsButtons.size() - 1).click();
		log.info("List removed correctly");

		// Logout
		this.logout();
	}

	public void goToPage() {
		String url = sutUrl;

		this.driver.get(url);
	}

	public void goToPage(String page) {
		String url = sutUrl;

		this.driver.get(url + page);
	}

	public void waitUntil(ExpectedCondition<WebElement> expectedCondition, String errorMessage, int seconds) {
		WebDriverWait waiter = new WebDriverWait(driver, seconds);

		try {
			waiter.until(expectedCondition);
		} catch (org.openqa.selenium.TimeoutException timeout) {
			log.error(errorMessage);
			throw new org.openqa.selenium.TimeoutException(
					"\"" + errorMessage + "\" (checked with condition) > " + timeout.getMessage());
		}
	}

	public void loginUser(String name, String pass) {
		// Wait show form login
		waitUntil(ExpectedConditions.visibilityOfElementLocated(By.name("user")), "No login page", 1);

		// Load form
		WebElement userField = driver.findElement(By.name("user"));
		WebElement passField = driver.findElement(By.name("password"));

		// Write credentials
		userField.sendKeys(name);
		passField.sendKeys(pass);

		driver.findElement(By.name("password")).sendKeys(Keys.ENTER);

		// Check login
		waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("carousel-content")), "Login failed", 2);

		WebElement menuBar = driver.findElement(By.id("menuList"));
		List<WebElement> elementsBar = menuBar.findElements(By.tagName("a"));
		assertThat(elementsBar.get(5).getText()).isEqualToIgnoringCase(name);

		log.info("Loggin successful, user {}", name);
	}

	public void logout() {
		String name = "";

		// Click logout
		WebElement menuBar = driver.findElement(By.id("menuList"));
		List<WebElement> elementsBar = menuBar.findElements(By.tagName("a"));
		WebElement loginUser = elementsBar.get(elementsBar.size() - 2);
		WebElement logoutButton = elementsBar.get(elementsBar.size() - 1);

		// Check user is login
		assertThat(logoutButton.getText()).isEqualToIgnoringCase("logout");
		name = loginUser.getText();

		// Click logout button
		logoutButton.click();

		// Check logout
		menuBar = driver.findElement(By.id("menuList"));
		elementsBar = menuBar.findElements(By.tagName("a"));
		logoutButton = elementsBar.get(elementsBar.size() - 1);

		assertThat(logoutButton.getText()).isEqualToIgnoringCase("login");

		log.info("Logout successful, user {}", name);
	}
}
