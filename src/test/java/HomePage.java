import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;

public class HomePage{

    By searchBox = By.xpath("/html/body/div[1]/header/div[3]/div/div/div/div[2]/form/div/div[1]/div[2]/input");
    By allProducts = By.cssSelector("#best-match-right > div.blueWrapper.clearfix > div.clearfix > ul > li");
    By goToBasket = By.xpath("//*[@id=\"header_wrapper\"]/div[4]/div[3]/div/div/div/div[2]/div[4]/div[1]/a");
    By amountOfProduct = By.xpath("//*[@id=\"submit-cart\"]/div/div[2]/div[3]/div/div[1]/div/div[5]/div[1]/div/ul/li[1]/div[1]");

    private double productPriceOnProductPage;
    private String selectedProductId;


    public LoginPage openLoginPage() {
        OrderedTests.driver.get("https://gittigidiyor.com/uye-girisi");
        return new LoginPage();
    }

    // returns true if href of li and url of next page matches
    public String searchSomething(String searchText, int nthElement) throws InterruptedException {
        OrderedTests.driver.findElement(searchBox).click();
        OrderedTests.driver.findElement(searchBox).sendKeys(searchText);
        By nthResult = By.xpath("/html/body/div[1]/header/div[3]/div/div/div/div[2]/form/div/div[3]/ul/li["+nthElement+"]/a");
        String hrefOfElement = OrderedTests.driver.findElement(nthResult).getAttribute("href");

        OrderedTests.driver.findElement(nthResult).click(); // click the nth result (in our scenario it's 1 -> second element)

        return hrefOfElement;
    }

    public String getPageTitle() {
        return OrderedTests.driver.getTitle();
    }

    public void selectRandomProductAndAddToBasket() {

        String priceString;
        double price;

        WebDriverWait wait = new WebDriverWait(OrderedTests.driver, 30);
        wait.until((ExpectedCondition<Boolean>) driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));

        Random r = new Random();
        List<WebElement> productList = OrderedTests.driver.findElements(allProducts);
        // selects random nth element from the productList and clicks on it. Bounds are [0, size_of_list-1]
        WebElement selectedProduct = productList.get(r.nextInt(productList.size()));
        this.selectedProductId = selectedProduct.getAttribute("product-id");
        selectedProduct.click();

        if (!OrderedTests.driver.findElement(By.id("sp-price-lowPrice")).getText().equals(""))
            priceString = OrderedTests.driver.findElement(By.id("sp-price-lowPrice")).getText().
                    substring(0, OrderedTests.driver.findElement(By.id("sp-price-lowPrice")).getText().length() - 3);
        else {
            priceString = OrderedTests.driver.findElement(By.id("sp-price-highPrice")).getText().
                    substring(0, OrderedTests.driver.findElement(By.id("sp-price-highPrice")).getText().length() - 3);
        }

        String formattedString = "";

        for (int i = 0; i < priceString.length(); i++) {
            if(priceString.charAt(i) != '.') {
                if (priceString.charAt(i) == ',') {
                    formattedString += '.';
                }
                else {
                    formattedString += priceString.charAt(i);
                }
            }
        }

        price = Double.parseDouble(formattedString);

        WebElement addToBasketButton =  OrderedTests.driver.findElement(By.id("add-to-basket"));

        JavascriptExecutor jse = (JavascriptExecutor) OrderedTests.driver;
        jse.executeScript("arguments[0].click()", addToBasketButton);

        this.productPriceOnProductPage = price;

        new MyLogger().info("Successfully added product to basket. Price of the product at product page: " +
                this.productPriceOnProductPage);
    }

    public boolean compareProductPrices() {
        double basketPrice;
        String priceString;

        String productPriceXPath = "//*[@data-product=\""+this.selectedProductId+"-0\"]/div[1]/div[5]/div[1]/div[1]/div[2]/strong";
        String productPriceXPath2 = "//*[@data-product=\""+this.selectedProductId+"-0\"]/div[1]/div[5]/div[1]/div[2]/strong";
        String productPriceXPath3 = "//*[@data-product=\""+this.selectedProductId+"-0\"]/div[1]/div[5]/div[1]/div[1]/strong";

        OrderedTests.driver.findElement(goToBasket).click();
        WebElement productPriceElement;

        // It's either xpath or xpath2. Differs from product to product.
        try {
            productPriceElement = OrderedTests.driver.findElement(By.xpath(productPriceXPath));
        }
        catch (NoSuchElementException e) {
            try {
                productPriceElement = OrderedTests.driver.findElement(By.xpath(productPriceXPath2));
            }
            catch (NoSuchElementException e2) {
                productPriceElement = OrderedTests.driver.findElement(By.xpath(productPriceXPath3));
            }
        }

        priceString = productPriceElement.getText().substring(0, OrderedTests.driver.findElement(By.className("total-price")).
                findElement(By.tagName("strong")).getText().length() - 3);

        String formattedString = "";

        for (int i = 0; i < priceString.length(); i++) {
            if(priceString.charAt(i) != '.') {
                if (priceString.charAt(i) == ',') {
                    formattedString += '.';
                }
                else {
                    formattedString += priceString.charAt(i);
                }
            }
        }

        basketPrice = Double.parseDouble(formattedString);
        MyLogger logger = new MyLogger();
        logger.info("Basket price of the products: " + basketPrice);
        if(basketPrice == this.productPriceOnProductPage) {
            logger.info("Price of the product in page and basket are same.");
        }
        else {
            logger.warn("Price of the product in page and basket are NOT same.");
        }
        return basketPrice == this.productPriceOnProductPage;
    }

    public int increaseAndCheckProductAmount() throws InterruptedException {

        String productAmountXPath = "//*[@data-product=\""+this.selectedProductId+"-0\"]/div[1]/div[4]/div/div[2]/select";
        By productId = By.xpath(productAmountXPath);
        WebElement selection = OrderedTests.driver.findElement(productId);
        selection.click();
        try {
            selection.findElements(By.tagName("option")).get(1).click();
        }
        catch (IndexOutOfBoundsException e) {
            new MyLogger().warn("There is only 1 product for this specific product. So the amount could NOT increased.");
            return 1;
        }

        Thread.sleep(3000);

        String productAmountString = OrderedTests.driver.findElement(amountOfProduct).getText();
        String formattedAmountString = "";
        for(int i = 0; i < productAmountString.length(); i++) {
            if(Character.isDigit(productAmountString.charAt(i))) {
                formattedAmountString += productAmountString.charAt(i);
            }
        }
        return Integer.parseInt(formattedAmountString);
    }

    public boolean removeProductFromBasket() throws InterruptedException {
        String removeProductButtonXPath = "//*[@data-id=\""+this.selectedProductId+"\"]";
        OrderedTests.driver.findElement(By.xpath(removeProductButtonXPath)).click();

        Thread.sleep(3000);

        // class "clearfix" means the basket is empty.
        // class "clearfix hidden" means the basket is not empty.
        MyLogger logger = new MyLogger();

        if ((OrderedTests.driver.findElement(By.id("empty-cart-container")).getAttribute("class").equals("clearfix"))) {
            logger.info("Product removed from the basket and the basket is empty.");
        }
        else {
            logger.warn("Product removed from the basket but the basket is NOT empty.");
        }

        return (OrderedTests.driver.findElement(By.id("empty-cart-container")).getAttribute("class").equals("clearfix"));
    }
    public double getProductPriceOnProductPage() {
        return this.productPriceOnProductPage;
    }

}
