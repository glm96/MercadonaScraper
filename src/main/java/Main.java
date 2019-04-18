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

    private static String defHandle;
    private static List<String[]> data = new ArrayList<String[]>();

    public static void main(String[] args) {



        //support for chrome
        System.setProperty("webdriver.chrome.driver","C:\\chromedriver\\chromedriver.exe");

        //Initialize the browser and the waiting service
        ChromeDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver,30);
        String[] header = {"name", "barcode", "price"};
        data.add(header);

        WebHelper helper = new WebHelper(driver, wait);

        helper.login();

        driver.get("https://www.telecompra.mercadona.es/ns/principal.php");
       // driver.switchTo().defaultContent();
        waitSeconds(3);
        defHandle = driver.getWindowHandle();
        driver.switchTo().frame("toc");
        driver.switchTo().frame("menu");
        List<WebElement> elementlist = driver.findElements(By.tagName("li"));

       /* //TEST TEST TEST
        elementlist.get(0).findElement(By.tagName("a")).click();
        elementlist.get(0).findElements(By.tagName("li")).get(0).findElement(By.tagName("a")).click();

        helper.checkPageContent(defHandle,data);


        for(String[] strings : data){
            for(String s : strings){
                System.out.print(s);
                System.out.print(", ");
            }
            System.out.println("");
        }
        //TEST TEST TEST
        */

        // Iterate through every category
        for(WebElement element : elementlist) {
            waitSeconds(1);
            List<WebElement> elementlist2, elementlist3;
            element.findElement(By.tagName("a")).click();
            elementlist2 = element.findElements(By.tagName("li"));
            if (elementlist2.size()<1) {
                helper.checkPageContent(defHandle,data);
                nextPage(driver,helper);
            }
            else {
                for (WebElement element2 : elementlist2) {
                    element2.findElement(By.tagName("a")).click();
                    elementlist3 = element2.findElements(By.tagName("li"));
                    if (elementlist3.size()<1) {
                        helper.checkPageContent(defHandle,data);
                        nextPage(driver,helper);
                    } else {
                        for (WebElement element3 : elementlist3) {
                            element3.findElement(By.tagName("a")).click();
                            helper.checkPageContent(defHandle,data);
                            nextPage(driver,helper);
                        }
                    }
                }
            }
        }

        CSVHelper csvHelper = new CSVHelper("C:\\Users\\tyrio\\eclipse-workspace\\mercadonaScraper\\csvtest.csv",data);
        csvHelper.GenerateCSV();


    }

    private static void waitSeconds(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        }catch (Exception e){e.printStackTrace();}
    }

    private static void checkResults(){
        //ToDo Grab data and store it on a list
       // helper.checkPageContent(defHandle,data);
    }

    private static void nextPage(ChromeDriver driver, WebHelper helper){
        System.out.println("in");
        try {
            driver.findElement(By.xpath("/html/body/div[3]")).findElements(By.tagName("a")).get(1).click();
            System.out.println("in2");
            waitSeconds(1);
            helper.checkPageContent(defHandle,data);
        }catch (Exception e){System.out.println("out");}//section finished
    }
}
