
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main {

    private static String defHandle;
    private static List<String[]> data = new ArrayList<String[]>();
    private static WebHelper helper;

    public static void main(String[] args) {

        boolean flag, flag2=true, running = true;

        //support for chrome
        System.setProperty("webdriver.chrome.driver","C:\\chromedriver\\chromedriver.exe");

        //Initialize the browser and the waiting service
        ChromeDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver,30);


        helper = new WebHelper(driver, wait);

        helper.login();

        driver.get("https://www.telecompra.mercadona.es/ns/principal.php");
       // driver.switchTo().defaultContent();
        waitSeconds(1);
        defHandle = driver.getWindowHandle();
        helper.switchToMenu(driver);
        List<WebElement> elementlist = driver.findElements(By.tagName("li"));

        //Fools the website into believing a human is navigating it
        ((JavascriptExecutor)driver).executeScript("window.key = \"foobar\";");

        String cat = getCategory();
        if(!cat.equals("")) {//There was a ban last run
            data = readLastCSV();
            elementlist = runAfterBan(elementlist, driver, getCategory()); //Specify category from last run
        }
        else{
            String[] header = {"name", "barcode", "price"};
            data.add(header);
        }
        // Iterate through every category
        List<String> banCat = new ArrayList<String>();
        for(WebElement element : elementlist) {


            if(flag2) {//Checks whether last page showed a ban
                List<WebElement> elementlist2, elementlist3, elementlist4;

                element.findElement(By.tagName("a")).click();
                elementlist2 = element.findElements(By.tagName("li"));

                if (elementlist2.size() < 1) {//Reached a category
                    flag = helper.checkPageContent(defHandle, data);
                    if(!flag) { // if banned, print category and save it on a file for future use
                        System.out.println(element.getText());
                        banCat.add(element.getText());
                        flag2 = false;
                    }
                    nextPage(driver);
                }
                else {
                    for (WebElement element2 : elementlist2) {
                        if (flag2) {
                            element2.findElement(By.tagName("a")).click();
                            elementlist3 = element2.findElements(By.tagName("li"));
                            if (elementlist3.size() < 1) {//Reached a category
                                flag = helper.checkPageContent(defHandle, data);
                                if (!flag) { // if banned, print category and save it on a file for future use
                                    System.out.println(element2.getText());
                                    String s = element.getText();
                                    banCat.add(s.substring(0,s.indexOf('\n')));
                                    banCat.add(element2.getText());
                                    flag2 = false;
                                }
                                nextPage(driver);
                            } else {
                                for (WebElement element3 : elementlist3) {//Reached a category
                                    if(flag2) {
                                        elementlist4 = element2.findElements(By.tagName("li"));
                                        if(elementlist4.size() < 1) {
                                            element3.findElement(By.tagName("a")).click();
                                            flag = helper.checkPageContent(defHandle, data);
                                            if (!flag) { // if banned, print category and save it on a file for future use
                                                String s = element.getText();
                                                banCat.add(s.substring(0, s.indexOf('\n')));
                                                s = element2.getText();
                                                banCat.add(s.substring(0, s.indexOf('\n')));
                                                banCat.add(element3.getText());
                                                flag2 = false;
                                            }
                                            nextPage(driver);
                                        }
                                        else {
                                            for(WebElement element4 : elementlist4) {
                                                if(flag2){
                                                    element4.findElement(By.tagName("a")).click();
                                                    flag = helper.checkPageContent(defHandle, data);
                                                    if (!flag) { // if banned, print category and save it on a file for future use
                                                        String s = element.getText();
                                                        banCat.add(s.substring(0, s.indexOf('\n')));
                                                        s = element2.getText();
                                                        banCat.add(s.substring(0, s.indexOf('\n')));
                                                        s = element3.getText();
                                                        s = s.contains("\n") ? s.substring(0,s.indexOf('\n')) : s;
                                                        banCat.add(s);
                                                        banCat.add(element4.getText());
                                                        flag2 = false;
                                                    }
                                                    nextPage(driver);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if(running){ //Banned, save current data
                createCSV(data);
                createBanFile(banCat);
                running = false;
            }
        }

        createCSV(data);
        driver.close();

    }

    private static List<WebElement> runAfterBan(List<WebElement> list, ChromeDriver driver, String cat){
        boolean flag = false, banned = false;
        List<WebElement> res = new ArrayList<WebElement>();
        List<WebElement> l2, l3, l4;
        String c1, c2, c3;
        c1 = !cat.contains("|") ? cat : cat.substring(0,cat.indexOf("|"))  ;
        cat = cat.substring(cat.indexOf("|")+1);
        c2 = cat.contains("|") ? cat.substring(0, cat.indexOf("|")) : ""; //if there is no level 2 subcategory, c2 = ""
        c3 = cat.contains("|") ? cat.substring(cat.lastIndexOf("|") + 1) : ""; // same as above with lvl 3 subcat
        c3 = cat.contains("\n") ? cat.substring(0,cat.indexOf("\n")) : c3;


        List<String> banlist = new ArrayList<String>();
        for(WebElement element : list){
            if(!banned){
                if(flag) //Add next categories to a list
                    res.add(element);
                else if(element.getText().equals(c1)){//level 1 category specified
                    element.findElement(By.tagName("a")).click();
                    if(c2.equals("")){
                        banned = !helper.checkPageContent(defHandle,data);
                        if(banned){
                            banlist.add(element.getText());
                            break;
                        }
                        nextPage(driver);
                        flag = true; //got the data
                    }
                    else{//level 2+ category specified
                        l2 = element.findElements(By.tagName("li"));
                        for (WebElement element2 : l2){
                            if(!banned){
                                if(element2.getText().equals(c2) || flag) {
                                    element2.findElement(By.tagName("a")).click();
                                    l3 = element2.findElements(By.tagName("li"));
                                    if (c3.equals("") || l3.size()<1) {
                                        banned = !helper.checkPageContent(defHandle, data);
                                        if (banned) {
                                            String s = element.getText();
                                            banlist.add(s.substring(0, s.indexOf("\n")));
                                            banlist.add(element2.getText());
                                        }
                                        nextPage(driver);
                                        flag = true; //got the data
                                    } else {//level 3 category specified
                                        for (WebElement element3 : l3) {
                                            if (element3.getText().equals(c3) || flag) {
                                                element3.findElement(By.tagName("a")).click();
                                                l4 = element3.findElements(By.tagName("li"));
                                                if(l4.size()<1) {
                                                    banned = !helper.checkPageContent(defHandle, data);
                                                    if (banned) {
                                                        String s = element.getText();
                                                        banlist.add(s.substring(0, s.indexOf("\n")));
                                                        s = element2.getText();
                                                        banlist.add(s.substring(0, s.indexOf("\n")));
                                                        banlist.add(element3.getText());
                                                        break;
                                                    }
                                                    nextPage(driver);
                                                    flag = true; //got the data
                                                }
                                                else{
                                                    for(WebElement element4 : l4){
                                                        element4.findElement(By.tagName("a")).click();
                                                        banned = !helper.checkPageContent(defHandle,data);
                                                        if (banned) {
                                                            String s = element.getText();
                                                            banlist.add(s.substring(0, s.indexOf("\n")));
                                                            s = element2.getText();
                                                            banlist.add(s.substring(0, s.indexOf("\n")));
                                                            banlist.add(element3.getText());
                                                            break;
                                                        }
                                                        nextPage(driver);
                                                        flag = true; //got the data
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else
                                break;
                        }

                    }
                }
            }
            else{
                createBanFile(banlist);
                return new ArrayList<WebElement>();
            }
        }
        return res;
    }
    private static String  getCategory(){
        String fileName = "C:\\Users\\tyrio\\eclipse-workspace\\mercadonaScraper\\banCategory.txt";
        try{
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String s = br.readLine();
            if(s==null)
                return "";
            return s;

        }catch(Exception e){return "";}
    }
    private static void createBanFile(List<String> strings){
        try {
            PrintWriter writer = new PrintWriter("banCategory.txt", "UTF-8");
            StringBuilder stringBuilder = new StringBuilder();
            for(String s : strings){
                stringBuilder.append(s);
                stringBuilder.append("|");
            }
            String s = stringBuilder.toString().substring(0,stringBuilder.length()-1);
            writer.print(s);
            writer.close();
        }catch(Exception e){e.printStackTrace();}
    }
    private static void waitSeconds(int t){
        try {
            TimeUnit.SECONDS.sleep(t);
        }catch (Exception e){e.printStackTrace();}
    }
    private static void createCSV(List<String[]> data){
        CSVHelper csvHelper;
             csvHelper = new CSVHelper("C:\\Users\\tyrio\\eclipse-workspace\\mercadonaScraper\\EANdata.csv", data);
        csvHelper.GenerateCSV();
    }
    private static void nextPage(ChromeDriver driver){
        helper.switchToMain(driver);

        try {
            List<WebElement> list  = driver.findElement(By.xpath("/html/body/div[3]")).findElements(By.tagName("a"));
            if(list.size()==2)
                list.get(1).click();
            else
                list.get(2).click();
            waitSeconds(1);
            helper.checkPageContent(defHandle,data);
            nextPage(driver);
        }catch (Exception e){}//section finished
        helper.switchToMenu(driver);
    }
    private static List<String[]> readLastCSV(){
        List<String[]> records = new ArrayList<String[]>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("EANdata.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(values);
            }
        }catch(Exception e){e.printStackTrace();}
        return records;
    }

}
