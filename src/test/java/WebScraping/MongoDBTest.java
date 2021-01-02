package WebScraping;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Mongo client: Connect to mongoDB by "mongo" command and check "connecting to:..."
 */
public class MongoDBTest
{
    WebDriver driver;
    MongoCollection<Document> WebDocument;

    @BeforeSuite
    public void ConnectToMongoDb()
    {
        Logger MongodbLogger = Logger.getLogger("org.mongodb.driver");


        //To create MongoDB client
        MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017");

        //Create a database from terminal
        MongoDatabase Mongo_Database = mongoClient.getDatabase("AutomationDemo");

        //Create collection
        WebDocument = Mongo_Database.getCollection("WebData");
    }


    @BeforeTest
    public void Setup()
    {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chrome_options = new ChromeOptions();
        chrome_options.addArguments("--headless");

        driver = new ChromeDriver(chrome_options);
    }


    @DataProvider
    public Object[][] Data_Provider()
    {
        return new Object[][]
                {
                        {"https://www.amazon.com/"},{"https://www.walmart.com/"}
                };
    }



    @Test(dataProvider = "Data_Provider")
    public void WebScrapeTest(String AppUrl)
    {
        driver.get(AppUrl);

        String Url = driver.getCurrentUrl();
        String Title = driver.getTitle();

        int LinksCount = driver.findElements(By.tagName("a")).size();

        List<WebElement> LinksList = driver.findElements(By.tagName("a"));
        List<String> HrefList = new ArrayList<>();



        //We can only add data in mongoDB in form of document
        Document document = new Document();
        document.append("Url", Url);
        document.append("Title", Title);
        document.append("Links Count", LinksCount);


        for(WebElement it: LinksList)
        {
            String HrefValue = it.getAttribute("href");
            if(HrefValue != null )
            {
                HrefList.add(HrefValue);
            }
        }
        document.append("Href Value", HrefList);


        List<Document> DocsList = new ArrayList<Document>();
        DocsList.add(document);


        WebDocument.insertMany(DocsList);

    }



    @AfterTest
    public void TearDown()
    {
        driver.quit();
    }



}
