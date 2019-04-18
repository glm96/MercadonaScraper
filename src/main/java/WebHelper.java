import com.sun.org.apache.xpath.internal.operations.Bool;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

        System.out.println("Logging in");
        driver.navigate().to(loginURL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitbutton)));
        driver.findElementById("username").sendKeys(Credentials.USERNAME);
        driver.findElementById("password").sendKeys(Credentials.PASSWORD);
        waitSeconds(2);
        driver.findElement(By.xpath(submitbutton)).click();
    }

    public void checkPageContent(String defHandle, List<String[]> data){
            //Switch to list frame

            driver.switchTo().defaultContent();
            driver.switchTo().frame("mainFrame");
            //Get list of all tr elements in this page
            List<WebElement> testelements = driver.findElement(By.xpath("//*[@id=\"TaulaLlista\"]/tbody")).findElements(By.tagName("tr"));
            //Save data for every item on this page
            for (WebElement el : testelements) {
                try {
                    boolean flag = true;
                    int index = 0;
                    List<WebElement> tdelements = el.findElements(By.tagName("td"));
                    try {
                        tdelements.get(1).findElement(By.tagName("a"));
                    } catch (Exception e) {
                        flag = false; // No details on this product, skip it.
                    }
                    if (flag) {
                        String name = tdelements.get(0).getText();
                        String price = tdelements.get(2).getText();
                        try {
                            index = Math.min(price.indexOf(" "), price.indexOf("\n"));
                            if (index == -1){
                                index = Math.max(price.indexOf(" "), price.indexOf("\n"));
                                if (index == -1) {
                                    index = price.length() - 1;
                                }
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            index = price.length() - 1;
                        }
                        price = price.substring(0, index);
                        tdelements.get(1).findElement(By.tagName("a")).click();
                        for (String s : driver.getWindowHandles()) {
                            if (!s.equals(defHandle))
                                driver.switchTo().window(s);
                        }
                        checkBan();
                       // wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div[2]/div[1]/div/dl/dd[4]")));
                        String EAN = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/dl/dd[4]")).getText();
                        String[] entity = {name, EAN, price};
                        data.add(entity);
                        driver.close();
                        driver.switchTo().window(defHandle);
                        driver.switchTo().defaultContent();
                        driver.switchTo().frame("mainFrame");
                    }
                }catch (Exception e) {
                    driver.close();
                    driver.switchTo().window(defHandle);
                    driver.switchTo().defaultContent();
                    driver.switchTo().frame("mainFrame");
                }//Different structure details page, skip it
            }
            driver.switchTo().window(defHandle);
            driver.switchTo().defaultContent();
            driver.switchTo().frame("toc");
            driver.switchTo().frame("menu");
    }

    private void checkBan (){

        //System.out.println(driver.findElement(By.xpath("/html/body")).getText().trim());
        if(driver.findElement(By.xpath("/html/body")).getText().trim().contains("The requested URL was rejected. Please consult with your administrator.")){
            System.out.println("In");
            String url = driver.getCurrentUrl();
            login();
            driver.get(url);
            waitSeconds(5);
        }
    }
    private static void waitSeconds(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        }catch (Exception e){e.printStackTrace();}
    }
}
