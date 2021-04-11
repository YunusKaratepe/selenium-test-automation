import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderedTests {

    static WebDriver driver;
    static String driverPath = "src/test/java/chromedriver";
    private static HomePage homePage;

    @BeforeClass
    public static void beforeClass() {
        MyLogger logger = new MyLogger();
        homePage = new HomePage();
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get("http://gittigidiyor.com");
        logger.info("Chrome Opened succesfully.");
    }

    @AfterClass
    public static void afterClass() {
        driver.close();
    }

    @Test
    public void a_openHomePage() {
        Assert.assertEquals("GittiGidiyor - Türkiye'nin Öncü Alışveriş Sitesi", homePage.getPageTitle());
        if(homePage.getPageTitle().equals("GittiGidiyor - Türkiye'nin Öncü Alışveriş Sitesi"))
            new MyLogger().info("Gitti gidiyor opened successfully.");
    }

    @Test
    public void b_login() {
        LoginPage loginPage = homePage.openLoginPage();
        boolean loginControl = loginPage.login("yyunuskaratepe", "yns0122436");
        Assert.assertTrue(loginControl);
    }

    @Test
    public void c_searchSomething() throws InterruptedException {
        // search the text: "bilgisayar", open the nth result: 2
        // not working for 'bilgisayar' at nthElement = 1 but works for other values.
        // you can also try other searchTexts, such as 'oyuncak', 'klavye' (i didn't have much time to test all other texts)
        String hrefOfSelectedElement = homePage.searchSomething("bilgisayar", 2);
        System.out.println("URL: " + formattedUrl(driver.getCurrentUrl()));
        Assert.assertEquals(formattedUrl(driver.getCurrentUrl()), formattedUrl(hrefOfSelectedElement));
        if(formattedUrl(driver.getCurrentUrl()).equals(formattedUrl(hrefOfSelectedElement)))
            new MyLogger().info("Search successful.");
        else
            new MyLogger().fatal("Search has failed.");
    }

    @Test
    public void d_selectRandomProductAndAddToBasket() {
         homePage.selectRandomProductAndAddToBasket();
    }

    @Test
    public void e_compareProductPrices() {
        boolean isEqual = homePage.compareProductPrices();
        Assert.assertTrue(isEqual);
    }

    @Test
    public void f_increaseAndCheckProductAmount() throws InterruptedException {
        int amountOfProduct = homePage.increaseAndCheckProductAmount();
        Assert.assertEquals(2, amountOfProduct);
    }

    @Test
    public void g_removeProductFromBasket() throws InterruptedException {
        boolean isBasketEmpty = homePage.removeProductFromBasket();
        Assert.assertTrue(isBasketEmpty);
    }

    public String formattedUrl(String url) {
        return url.replaceAll("/", "");
    }

}
