import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {



        //support for chrome
        System.setProperty("webdriver.chrome.driver","C:\\chromedriver\\chromedriver.exe");


        //XPaths needed for navigation
        String userXpath = "//*[@id=\"username\"]";
        String pwXpath = "//*[@id=\"password\"]";
        //Initialize the browser and the waiting service
        ChromeDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver,30);
        List<String[]> data = new ArrayList<String[]>();
        String[] header = {"name", "barcode", "price"};

        WebHelper helper = new WebHelper(driver, wait);

        helper.login();

        driver.get("https://www.telecompra.mercadona.es/ns/principal.php");
       // driver.switchTo().defaultContent();
        waitSeconds(3);
        driver.switchTo().frame("toc");
        driver.switchTo().frame("menu");
        List<WebElement> elementlist = driver.findElements(By.tagName("li"));

        //TEST TEST TEST

        //TEST TEST TEST


        /* //
        for(WebElement element : elementlist) {
            List<WebElement> elementlist2, elementlist3;
            element.findElement(By.tagName("a")).click();
            elementlist2 = element.findElements(By.tagName("li"));
            if (elementlist2 == null) {
                checkResults();
                System.out.println(element.getText());
            }
            else {
                for (WebElement element2 : elementlist2) {
                    element2.findElement(By.tagName("a")).click();
                    elementlist3 = element2.findElements(By.tagName("li"));
                    if (elementlist3 == null) {
                        checkResults();
                        System.out.println(element2.getText());
                    } else {
                        for (WebElement element3 : elementlist3) {
                            element3.findElement(By.tagName("a")).click();
                            checkResults();
                            System.out.println(element3.getText());
                        }
                    }
                }
            }
        }
        */

    }

    private static void waitSeconds(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        }catch (Exception e){e.printStackTrace();}
    }

    private static void checkResults(){
        //ToDo Grab data and store it on a list
    }

}
