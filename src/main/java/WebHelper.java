import org.openqa.selenium.*;
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
       // driver.navigate().to(loginURL);
        driver.get(loginURL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitbutton)));
        driver.findElementById("username").sendKeys(Credentials.USERNAME);
        driver.findElementById("password").sendKeys(Credentials.PASSWORD);
        waitSeconds(2);
        driver.findElement(By.xpath(submitbutton)).click();
    }

    public boolean checkPageContent(String defHandle, List<String[]> data){
            //Switch to list frame
            waitSeconds(1);
            boolean res = checkBan();
            driver.switchTo().defaultContent();
            driver.switchTo().frame("mainFrame");
            if(res) {
                //Get list of all tr elements in this page
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"TaulaLlista\"]/tbody")));
                }catch(TimeoutException e){ //In case the web doesn't load, return false so progress is saved
                    switchToMenu(driver);
                    return false;
                }
                List<WebElement> testelements = driver.findElement(By.xpath("//*[@id=\"TaulaLlista\"]/tbody")).findElements(By.tagName("tr"));
                //Save data for every item on this page
                for (WebElement el : testelements) {
                    try {
                        boolean flag = true;
                        List<WebElement> tdelements = el.findElements(By.tagName("td"));
                        try {
                            tdelements.get(1).findElement(By.tagName("a"));
                        } catch (Exception e) {
                            flag = false; // No details on this product, skip it.
                        }
                        if (flag) {
                            String name = tdelements.get(0).getText();
                            String price = tdelements.get(2).getText();
                            price = readPrice(price);
                            tdelements.get(1).findElement(By.tagName("a")).click();
                            for (String s : driver.getWindowHandles()) {
                                if (!s.equals(defHandle))
                                    driver.switchTo().window(s);
                            }
                            if(!checkBan()) {//Check if user is banned and needs to reconnect
                                res = false;
                                closeTab(driver, defHandle);
                                break;
                            }
                            price = price.substring(0,price.indexOf(","))+"."+price.substring(price.indexOf(",")+1);
                            String EAN = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/dl/dd[4]")).getText();
                            String[] entity = {name, EAN, price};
                            data.add(entity);
                            closeTab(driver, defHandle);
                        }
                    } catch (Exception e) {
                        closeTab(driver, defHandle);
                    }//Different structure details page, skip it
                }
            }
            driver.switchTo().window(defHandle);
            switchToMenu(driver);
            return res;
    }

    private boolean checkBan (){

        boolean res = true;
        //System.out.println(driver.findElement(By.xpath("/html/body")).getText().trim());
        if(driver.getCurrentUrl().trim().equals("https://www.telecompra.mercadona.es/ns/principal.php")){ //Check for ban on main screen
            ((JavascriptExecutor)driver).executeScript("window.key = \"blabla\";");
            driver.switchTo().defaultContent();
            driver.switchTo().frame("mainFrame");
            String body = driver.findElementByTagName("body").getText();
            if(body.trim().contains("The requested URL was rejected.")){
                res = false;
            }
            }


        else if(driver.findElement(By.xpath("/html/body")).getText().trim().contains("The requested URL was rejected.")){
            res = false;
        }
        return res;

    }

    public void switchToMenu(ChromeDriver driver){
        driver.switchTo().defaultContent();
        driver.switchTo().frame("toc");
        driver.switchTo().frame("menu");
    }

    public void switchToMain(ChromeDriver driver){
        driver.switchTo().defaultContent();
        driver.switchTo().frame("mainFrame");
    }

    private String readPrice(String price){
        int index;
        try { //Get the price out of every different possible format
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
        return price.substring(0, index);
    }
    private static void closeTab(WebDriver driver, String defHandle){
        driver.close();
        driver.switchTo().window(defHandle);
        driver.switchTo().defaultContent();
        driver.switchTo().frame("mainFrame");
    }
    private static void waitSeconds(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        }catch (Exception e){e.printStackTrace();}
    }
}
