package Recipe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class allRecipesScraper {

    public static WebDriver driver;

    public static void main(String[] args) throws IOException, InterruptedException {
        String url = "https://www.allrecipes.com/recipes/728/world-cuisine/latin-american/mexican/";

        Elements categories = getCuisineCategories(url);
        int recipeCount = 0;
        int city = 0;
        // for every catagory
        for (Element catagory : categories) {
            String categorieURL = catagory.attr("href");
            Elements links = getLinks(categorieURL);
            // for every recipe link in catagory
            for (Element link : links) {
                // change vpn every 15 recipes
                if (recipeCount % 15 == 0) {
                    city = Util.changeVPN(city);
                }
                recipeCount++;
                RecipeGrabber.getIngredientsDirections(link.attr("href"));
                Thread.sleep(2000);
            }
        }
    }


    public static Elements getLinks(String url) throws IOException {
        InputStream inStream = new URL(url).openStream();
        Document html = Jsoup.parse(inStream, "UTF-8", url);

        Elements allLinks = html.select("a[href]");

        Elements links = new Elements();
        for (Element link : allLinks)
        {

            String linkurl = link.attr("href");

            if (linkurl.contains("https://www.allrecipes.com/recipe/")) {
                links.add(link);
            }
        }
        return links;
    }

    public static Elements getCuisineCategories(String url) throws IOException {
        InputStream inStream = new URL(url).openStream();
        Document html = Jsoup.parse(inStream, "UTF-8", url);

        return html.select("ul[id=taxonomy-nodes__list_1-0] > li > a");
    }

}
