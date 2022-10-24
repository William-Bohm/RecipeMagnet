package Recipe;// control addition of recipe/nutrition/creation of nutrition to the apps databases.

import Recipe.Util;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

public class RecipeAutomation {
    public static WebDriver driver;

    public static void main(String[] args) throws IOException, InterruptedException {
        String recpieDataset = "Kaggle: Food Ingredients and Recipes Dataset with Images";
        String moreDataset = "Food.com Recipes and Interactions";
        String moremoreData = "Kaggle: Epicurious - Recipes with Rating and Nutrition";
        String superDataset = "recipe1m+ dataset";
        String ultraDataset = "Hugging Face: RecipeNLG";

        String url = "https://www.allrecipes.com/recipe/23600/worlds-best-lasagna/";
        String tikka = "https://cafedelites.com/chicken-tikka-masala/#recipe";
        String rice = "https://www.gimmesomeoven.com/fried-rice-recipe/";
        String meatball = "https://www.culinaryhill.com/turkey-meatballs/";
        String yummlyTest = "https://www.lordbyronskitchen.com/orange-sesame-cauliflower/?utm_campaign=yummly&utm_medium=yummly&utm_source=yummly";
        String eggSalad = "https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/";
        RecipeGrabber.main(eggSalad);
    }

}
