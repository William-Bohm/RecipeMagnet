package Recipe;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Hashtable;

public class Util {
    public static WebDriver createChromeDriver() {
        WebDriver driver;
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        return driver;
    }

    public static int changeVPN (int city) throws IOException, InterruptedException {
        Hashtable<String, String> states = new Hashtable<String, String>();
        states.put("uyk", "NJ");
        states.put("sea", "WA");
        states.put("sjc", "CA");
        states.put("slc", "UT");
        states.put("rag", "NC");
        states.put("phx", "AZ");
        states.put("nyc", "NY");
        states.put("mia", "FL");
        states.put("lax", "CA");
        states.put("hou", "TX");
        states.put("den", "CO");
        states.put("dal", "TX");
        states.put("chi", "IL");
        states.put("atl", "GA");
        states.put("qas", "VA");


        String[] locations = {
                "uyk",
                "sea",
                "sjc",
                "slc",
                "rag",
                "phx",
                "nyc",
                "mia",
                "lax",
                "hou",
                "den",
                "dal",
                "chi",
                "atl",
                "qas"
        };

        String newLocation = locations[city];
        String newState = states.get(newLocation);

        // define variables
        Process statusProcess;
        String status;
        boolean changedCity = false;

        // execute vpn change
        Process changeLocation = Runtime.getRuntime().exec("mullvad relay set location us " + newLocation);

        while (!changedCity) {
            System.out.println("Changing vpn city...");
            Thread.sleep(5000);

            // get vpn status
            statusProcess = Runtime.getRuntime().exec("mullvad status");
            status = clOutput(statusProcess);

            // check if vpn has changes, if so loop will break
            if (status.contains(newState) && status.contains("Connected")) {
                changedCity = status.contains(newState);
            }
        }

        Process confirmation = Runtime.getRuntime().exec("mullvad status");
        System.out.println(clOutput(confirmation));

        // setup cycling of vpn cities list for future calls
        city++;
        if (city > 14) {
            city = 0;
        }
        return city;
    }

    public static String clOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        line = reader.readLine();
        return line;
    }


}
