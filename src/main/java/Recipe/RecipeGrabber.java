package Recipe;// scrape a recipe from a given URL
import com.google.common.base.Ascii;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.jsoup.nodes.Entities.EscapeMode.extended;

public class RecipeGrabber {

    // to grab step: find an <ol> with text containing "step"

    // to grab ingridients find a <ul> after "ingridients" that contains names of food.

    public static WebDriver driver;

    public static void main(String url) throws IOException, InterruptedException {
        // basic jsoup element finding
        // then check desceneindents, if ul not find go to parents check descendants reapeat.
        System.out.println("Fetching %... " + url);

        //Document doc = Jsoup.connect(url).get();  // connect to html
        try {

            //get input stream from the URL
            InputStream inStream = new URL(url).openStream();

            //parse document using input stream and specify the charset
            Document doc = Jsoup.parse(inStream, "UTF-8", url);
            System.out.println(doc.charset());

            // get page title
            String title = doc.title();
            System.out.println("Title: " + title);

            // get headers
            Element[] headers = getHeaders(doc);  // [0] == ingredients [1] == directions

            if (headers[0] == null || headers[1] == null) {
                System.out.println("headers failed");
                return;
            }
            System.out.println("headers found!");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean headerCheck(String html) {
        // checks if both headers are found
        // basic jsoup element finding
        // then check desceneindents, if ul not find go to parents check descendants reapeat.
        System.out.println("Fetching %... ");

        //Document doc = Jsoup.connect(url).get();  // connect to html
        //parse document using input stream and specify the charset
        Document doc = Jsoup.parse(html);
        System.out.println(doc.charset());

        // get page title
        String title = doc.title();
        System.out.println("Title: " + title);

        // get headers
        Element[] headers = getHeaders(doc);  // [0] == ingredients [1] == directions

        return headers[0] != null && headers[1] != null;
    }

    public static void getIngredientsDirections(String url) {
        // basic jsoup element finding
        // then check desceneindents, if ul not find go to parents check descendants reapeat.
        System.out.println("Fetching %... " + url);


        //Document doc = Jsoup.connect(url).get();  // connect to html
        try {

            //get input stream from the URL
            InputStream inStream = new URL(url).openStream();

            //parse document using input stream and specify the charset
            Document doc = Jsoup.parse(inStream, "UTF-8", url);
            doc.outputSettings().charset("ISO-8859-1");



            // get headers
            Element[] headers = getHeaders(doc);  // [0] == ingredients [1] == directions

            // get ingredients and directions
            getIngredients(doc, headers[0], headers[1]);
            getDirection(doc, headers[1]);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // offlimits websites foodnetwork.com

    public static void getIngredients() {
        String[] ingredientHeaders = {"h2", "h3", "h4"};
        String[] directionHeaders = {"Directions", "Method", "Instructions"};
        String[] directionMiniHeaders = {"h4", ""};

        String htmlLong = driver.getPageSource();
        Document doc = (Document) Jsoup.parse(htmlLong);


        try {
            WebElement ingredientsElement = driver.findElement(By.xpath("//h2[contains(text(),'Ingredients')]"));
            WebElement directionsElement = driver.findElement(By.xpath("//h2[contains(text(),'Directions')]"));
        } catch (Exception e){
            System.out.println("could not find any ingridients" + e.getMessage());
        }
    }

    public static Elements getImages(Document html) {
        Elements images = html.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
        for (Element image : images) {
            System.out.println("src : " + image.attr("src"));
            System.out.println("height : " + image.attr("height"));
            System.out.println("width : " + image.attr("width"));
            System.out.println("alt : " + image.attr("alt"));
        }
        return images;
    }

    public static Elements getLinks(Document html) {
        Elements links = html.select("a[href]");
        for (Element link : links)
        {
            String linkurl = link.attr("href");
            if (linkurl.contains("https://www.allrecipes.com/recipe/:")) {

            }
        }
        return links;
    }

    public static Element[] getHeaders(Document html) {
        // Ingredients
        Element ingredientHeader = null;
        try {
            // search for each tag type to find ingredient headers
            String[] headerTypes = {"h2", "h3"};
            for (String type : headerTypes) {
                // get ingredient header
                try {
                    ingredientHeader = html.select(type + ":contains(Ingredients)").get(0);
                } catch (Exception e) {
                    continue;
                }

                // check if it is actually the ingredients header
                if (ingredientHeader.text().contains("Ingredients")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("could not find the ingredient Header: " + e.getMessage());
        }

        Element directionHeader = null;
        try {
            String[] headerTypes = {"h2", "h3"};
            String[] directionNames = {"Directions", "Instructions", "Method"};

            // loop through all possible names and tag types for directions header.
            outerLoop:
            for (String name : directionNames) {
                for (String type : headerTypes) {

                    // try to get the header
                    directionHeader = html.select(type + ":contains(" + name + ")").first();

                    // check if it is the correct header, if so break loop
                    if (directionHeader != null) {
                        for (String word : directionNames) {
                            if (directionHeader.text().equals(word)) {
                                break outerLoop;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("could not find the direction element: " + e.getMessage());
        }

        return new Element[]{ingredientHeader, directionHeader};
    }

    public static void getDirection(Document html, Element directionHeader) throws InterruptedException {
        // get description
        int searchLimit = 5;
        Element parent = directionHeader;
        for (int i = 0; i < searchLimit; i ++) {
            // get/save parent webelement
            final Element perm = parent;
            parent = perm.parent();

            // if we see the directions stop searching and save ingredients.
            assert parent != null;
            Elements checkForDirections = parent.getAllElements();
            Elements ol = parent.select("ol > li");
            Elements ul = parent.select("ul > li");

            // check if we found directions
            int directionCount = 0;
            for (Element li : ul) directionCount++;
            for (Element li : ol) directionCount++;

            // if we found directions
            if (directionCount != 0) {
                System.out.println("\nDIRECTIONS------");
                for (Element li : ul) System.out.println(li.text());
                for (Element li : ol) System.out.println(li.text());
                System.out.println("number of directions: " + Integer.toString(directionCount));
                break;
            }
        }
    }

    public static void getIngredients(Document html, Element ingredientHeader, Element directionHeader) throws InterruptedException {
        // get the parent element and see the direction header is a descendent, if so
        // save the ingredients (we do this to ensure we found all the lists of ingredients
        int searchLimit = 5;
        Element parent = ingredientHeader;
        Element prev = null;
        for (int i = 0; i < searchLimit; i ++) {
            // save the parent element for next loop (can only find parent if element is final
            prev = parent;
            parent = prev.parent();

            // if we see the directions stop searching and save ingredients.
            assert parent != null;
            Elements checkForDirections = parent.getAllElements();
            if (checkForDirections.contains(directionHeader)) break;

        }
        System.out.println("\nINGREDIENTS---------");
        Elements ul = prev.select("ul > li");
        // ***** check and make sure none of the elements are in the directions.
        for (Element li : ul) {
            System.out.println(li.text());
        }
    }
}
