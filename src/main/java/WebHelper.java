import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebHelper {

    private ChromeDriver driver;
    private WebDriverWait wait;

    public ChromeDriver getDriver() {
        return driver;
    }

    public void setDriver(ChromeDriver driver) {
        this.driver = driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public void setWait(WebDriverWait wait) {
        this.wait = wait;
    }

    public WebHelper (ChromeDriver d, WebDriverWait w){
        this.driver = d;
        this.wait = w;
    }

    public void login (){
        String submitbutton = "//*[@id=\"ImgEntradaAut\"]";
        String loginURL = "https://www.telecompra.mercadona.es/ns/entrada.php?js=1";

        driver.get(loginURL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitbutton)));
        driver.findElementById("username").sendKeys(Credentials.USERNAME);
        driver.findElementById("password").sendKeys(Credentials.PASSWORD);
        driver.findElement(By.xpath(submitbutton)).click();
    }
}
