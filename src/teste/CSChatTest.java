package teste;

import java.io.File;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import sun.rmi.runtime.Log;

import java.util.Calendar;

public class CSChatTest {

	private static WebDriver chrome1;
	private static WebDriver chrome2;

	private static String URL = "http://127.0.0.1:1976/";

	private static void StartChrome1() {
		if (chrome1 != null)
			return;
		chrome1 = new ChromeDriver();
		chrome1.get(URL);
	}

	private static void StartChrome2() {
		if (chrome2 != null)
			return;
		chrome2 = new ChromeDriver();
		chrome2.get(URL);
	}

	@BeforeClass
	public static void beforeClass() {
		File classpathRoot = new File(System.getProperty("user.dir"));
        File chromedriver = new File(classpathRoot, "driver/chromedriver");
        System.setProperty("webdriver.chrome.driver", chromedriver.getAbsolutePath());
		StartChrome1();
	}

	private static WebDriver GetDriver(int ind) {
		switch (ind) {
			case 1:
				StartChrome1();
				return chrome1;
			case 2:
				StartChrome2();
				return chrome2;
			default:
				StartChrome1();
				return chrome1;
		}
	}

    private void Login(int ind, String username, String password) {
        Calendar cal = Calendar.getInstance();

        WebDriver driver = GetDriver(ind);

        driver.findElement(By.cssSelector("input[name=\"username\"]")).sendKeys(username);
        driver.findElement(By.cssSelector("input[name=\"password\"]")).sendKeys(password);
        driver.findElement(By.cssSelector("input[value=\"[login]\"]")).click();
    }

	private void Login(int ind) {
	    Login(ind, "usuario", "senha");
    }

	private  void Mensagem(int ind) {
		WebDriver driver = GetDriver(ind);
		Login(ind);
		Login(ind);
	}

	private String CriarSala(int ind) {
	    Calendar cal = Calendar.getInstance();
		String uniqueRoomName = "Room" + cal.getTimeInMillis();
		String createRoomCommand = "/j " + uniqueRoomName;
		WebDriver driver = GetDriver(ind);

        Login(ind);
        driver.navigate().to(URL + "/LOGIN");
		sendCommandViaChat(driver, createRoomCommand);

		return uniqueRoomName;
	}

	private  String EntrarSala() {
		Login(1);
		Login(2);
		String roomName = CriarSala(1);
		String enterExistingRoomCommand = "/j " + roomName;
		WebDriver driver = GetDriver(2);

		sendCommandViaChat(driver, enterExistingRoomCommand);

		return roomName;
	}

	@Test
	public void CriaSalaTest() {
		int i = 1;
		WebDriver driver = GetDriver(i);
		String roomName = CriarSala(i);

		switchDriverToFrameSource(driver, "messages");
		assertTrue(driver.getPageSource().indexOf("You just created " + roomName + ".") > 0);
		switchDriverBackToDefault(driver);
	}

    @Test
    public void LoginTest() {
        int i = 1;
        WebDriver driver = GetDriver(i);
        Login(1);

        switchDriverToFrameSource(driver, "messages");
        assertTrue(driver.getPageSource().indexOf("Please obey the netiquette.") > 0);
        switchDriverBackToDefault(driver);
    }

    @Test
    public void LoginTestFail() {
	    int i = 1;
	    WebDriver driver = GetDriver(i);
        Login(1, "", "password");

        switchDriverToFrameSource(driver, "messages");
        assertTrue(!driver.getPageSource().contains("Please obey the netiquette."));
        switchDriverBackToDefault(driver);
    }

	@Test
	public void EnviarMensagemTest() {
		WebDriver driver = GetDriver(1);
		Login(1);

		sendCommandViaChat(driver, "Oi!");
		switchDriverToFrameSource(driver, "messages");
		assertTrue(driver.getPageSource().indexOf("Oi!") > 0);
		switchDriverBackToDefault(driver);
	}


    @Test
    public void EnviarShoutTest() {
        WebDriver driver = GetDriver(1);
        Login(1);

        sendCommandViaChat(driver, "/s Oi!");
        switchDriverToFrameSource(driver, "messages");
        assertTrue(driver.getPageSource().indexOf("shouts out OI!") > 0);
        switchDriverBackToDefault(driver);
    }


    @Test
    public void EntrarSalaTest() {
        WebDriver driver1 = GetDriver(1), driver = GetDriver(2);
        String roomName = EntrarSala();

        switchDriverToFrameSource(driver, "messages");
        assertTrue(driver.getPageSource().indexOf("You have just joined " + roomName + ".") > 0);
        switchDriverBackToDefault(driver);
    }

    private  void sendCommandViaChat(WebDriver driver, String command) {
        switchDriverToFrameSource(driver, "input");
	    driver.findElement(By.name("message")).sendKeys(command);
		driver.findElement(By.name("message")).sendKeys(Keys.ENTER);
	}

	private void switchDriverToFrameSource(WebDriver driver, String frameName) {
        driver.switchTo().defaultContent();
		WebElement fr = driver.findElement(By.name(frameName));
		driver.switchTo().frame(fr);
	}

	private void switchDriverBackToDefault(WebDriver driver) {
		driver.switchTo().defaultContent();
	}

	@AfterClass
	public static void closeDrivers(){
		if(chrome1 != null){
			chrome1.close();
		}

		if(chrome2 != null) {
			chrome2.close();
		}
	}
}
