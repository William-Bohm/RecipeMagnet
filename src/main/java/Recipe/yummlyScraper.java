package Recipe;


import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfWindowsToBe;

public class yummlyScraper {

    public static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        String website = "";
        String searchText = "";
        int numRecipes = 100;

        driver = Util.createChromeDriver();
        driver.get("https://www.yummly.com/recipes");
        searchField("asian noodles");
        Thread.sleep(2000);
        List<WebElement> links = getYummlyContainers();
        getRecipeLinks(10);

    }

    public static void searchField(String searchText) {
        // send keys to searchBar
        WebElement search = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Search recipes']")));
        search.click();
        search.sendKeys(searchText + Keys.ENTER);
        System.out.println("searched: " + searchText);
    }

    public static List<WebElement> getYummlyContainers() {
        // gets every yummly.com link to the recipes on page
        String[] linkTagNames = {"link-overlay", "card-ingredients"};

        // get the div containing all recipes
        WebElement recipeBox = driver.findElement(By.xpath("//div[@class='RecipeContainer']"));
        List<WebElement> recipeContainers = driver.findElements(By.xpath("//div[@class='RecipeContainer']/div"));

        for (WebElement container : recipeContainers) {
            container.click();

        }


//        WebElement test = driver.findElement(By.xpath("//div[@class='RecipeContainer']/div[3]"));
//        WebElement aTag = test.findElement(By.className("link-overlay"));
//        aTag.click();
//        System.out.println(aTag.getAttribute("href"));
//        System.out.println("ahhhhhhhhhhhhhh");

//         get each link to the recipe
//        for (WebElement container : recipeContainers) {
//            WebElement aTag = container.findElement(By.className("card-ingredients"));
//            System.out.println(aTag.getAttribute("href"));
//         }

        return recipeContainers;
    }

    public static void getRecipeLinks(int numRecipes) throws InterruptedException {
        // for each recipe container..
        for (int i = 1; i <numRecipes + 1; i++ ) {
            try {
                // click the recipe container on main screen
                Thread.sleep(5000);
                String aTagString = "link-overlay";
                WebElement recipeContainer = driver.findElement(By.xpath("//div[@class='RecipeContainer']/div" + "[" + i + "]"));
                WebElement aTag = recipeContainer.findElement(By.className(aTagString));
                aTag.click();

                // if recipe is listed on yummly website, get it.
                String html = driver.getPageSource();
                if (RecipeGrabber.headerCheck(html)) {
                    System.out.println("yummly recipe******************");
                     RecipeGrabber.getIngredientsDirections(driver.getPageSource());
                    System.out.println("yummly recipe done*************************");
                }
                // else go to the website it is on
                // have to switch tabs to get the correct html to send over
                else {
                    // get recipe website
                    String originalWindow = driver.getWindowHandle();
                    System.out.println("other recipe-----------------------");
                    WebElement directionsButton = driver.findElement(By.linkText("Read Directions"));
                    directionsButton.click();

                    // handle new window
                    for (String windowHandle : driver.getWindowHandles()) {
                        if(!originalWindow.contentEquals(windowHandle)) {
                            driver.switchTo().window(windowHandle);
                            break;
                        }
                    }

                    // get recipe
                    RecipeGrabber.getIngredientsDirections(driver.getCurrentUrl());

                    // close and switch back to old window
                    driver.close();
                    driver.switchTo().window(originalWindow);

                    System.out.println("unofficial recipe done--------------------");
                }


                Thread.sleep(5000);
                driver.navigate().back();
            } catch (Exception e){
                try {
                    // Thread.sleep(3000);
                    String aTagString = "card-ingredients";
                    WebElement recipeContainer = driver.findElement(By.xpath("//div[@class='RecipeContainer']/div" + "[" + i + "]"));
                    WebElement aTag = recipeContainer.findElement(By.className(aTagString));
                    aTag.click();
                    Thread.sleep(5000);
                    driver.navigate().back();
                } catch (Exception k) {
                    System.out.println("failed to find recipe container " + e.getMessage());
                };
            }
        }
    }
}
