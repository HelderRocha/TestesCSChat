package teste;
import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

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
	}
	
	public void StartChrome2() {
		if (this.chrome2 != null)
			return;
		this.chrome2 = new ChromeDriver();
	}
	
	CSChat(String chatURL) {
		File classpathRoot = new File(System.getProperty("user.dir"));
        File chromedriver = new File(classpathRoot, "driver/chromedriver");
        System.setProperty("webdriver.chrome.driver", chromedriver.getAbsolutePath());

		this.URL = chatURL;
		this.chrome1 = new ChromeDriver();
		//this.chrome2 = new ChromeDriver();
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
		driver.get(URL);
		Login(ind);
		Login(ind);
	}
	
	public void CriarSala() {
		
	}
	
	public void EntrarSala() {
		
	}
}
