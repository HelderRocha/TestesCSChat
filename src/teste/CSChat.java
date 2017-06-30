package teste;

import java.io.File;

import static org.junit.Assert.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.Date;

public class CSChat {
	
	private WebDriver chrome1;
	private WebDriver chrome2;
	private String URL;

	public static void main(String[] args) {
        CSChat chat = new CSChat("http://localhost:1976/?templateset=dark");
        chat.Mensagem(1);
	}
	
	public void StartChrome1() {
		if (this.chrome1 != null)
			return;
		this.chrome1 = new ChromeDriver();
		this.chrome1.get(this.URL);
	}
	
	public void StartChrome2() {
		if (this.chrome2 != null)
			return;
		this.chrome2 = new ChromeDriver();
		this.chrome2.get(this.URL);
	}
	
	CSChat(String chatURL) {
		File classpathRoot = new File(System.getProperty("user.dir"));
        File chromedriver = new File(classpathRoot, "driver/chromedriver");
        System.setProperty("webdriver.chrome.driver", chromedriver.getAbsolutePath());

		this.URL = chatURL;
		StartChrome1();
	}
	
	public WebDriver GetDriver(int ind) {
		switch (ind) {
			case 1: 
				StartChrome1();
				return this.chrome1;
			case 2:
				StartChrome2();
				return this.chrome2;
			default:
				StartChrome1();
				return this.chrome1;
		}
	}
	
	public void Login(int ind) {
		String username = "hue";
		String password = "huehue";
		
		WebDriver driver = GetDriver(ind);
		
		
		driver.findElement(By.cssSelector("input[name=\"username\"]")).sendKeys(username);
		driver.findElement(By.cssSelector("input[name=\"password\"]")).sendKeys(password);
		driver.findElement(By.cssSelector("input[value=\"[login]\"]")).click();
	}

	public void Mensagem(int ind) {
		WebDriver driver = GetDriver(ind);
		Login(ind);
		Login(ind);
	}
	
	public String CriarSala(int ind) {
		String uniqueRoomName = "Room" + new Date().toString();
		String createRoomCommand = "/j " + uniqueRoomName;
		WebDriver driver = GetDriver(ind);

		Login(ind);
		sendCommandViaChat(driver, createRoomCommand);

		return uniqueRoomName;
	}
	
	public String EntrarSala() {
		Login(1);
		Login(2);
		String roomName = CriarSala(1);
		String enterExistingRoomCommand = "/j " + roomName;
		WebDriver driver = GetDriver(2);

		sendCommandViaChat(driver, enterExistingRoomCommand);

		return roomName;
	}

	public void CriaSalaTest() {
		int i = 1;
		WebDriver driver = GetDriver(i);
		String roomName = CriarSala(i);

		switchDriverToFrameSource(driver, "messages");
		assertTrue(driver.getPageSource().indexOf("You just created " + roomName + ".") > 0);
		switchDriverBackToDefault(driver);
		closeDrivers();
	}

	public void EntrarSalaTest() {
		WebDriver driver1 = GetDriver(1), driver = GetDriver(2);
		String roomName = EntrarSala();

		switchDriverToFrameSource(driver, "messages");
		assertTrue(driver.getPageSource().indexOf("You have just joined " + roomName + ".") > 0);
		switchDriverBackToDefault(driver);
		closeDrivers();
	}

	public void sendCommandViaChat(WebDriver driver, String command) {
		driver.findElement(By.cssSelector("input[name=\"message\"]")).sendKeys(command);
		driver.findElement(By.cssSelector("input[name=\"message\"]")).sendKeys(Keys.ENTER);
	}

	private void switchDriverToFrameSource(WebDriver driver, String frameName) {
		WebElement fr = driver.findElement(By.name(frameName));
		driver.switchTo().frame(fr);
	}

	private void switchDriverBackToDefault(WebDriver driver) {
		driver.switchTo().defaultContent();
	}

	private void closeDrivers(){
		if(this.chrome1 != null){
			this.chrome1.close();
		}
		
		if(this.chrome2 != null) {
			this.chrome2.close();
		}
	}
}
