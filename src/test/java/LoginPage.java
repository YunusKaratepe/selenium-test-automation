import org.openqa.selenium.By;

public class LoginPage {

    By usernameLoc = By.name("kullanici");
    By passwordLoc = By.name("sifre");
    By loginButton = By.id("gg-login-enter");


    public boolean login(String username, String password) {
        MyLogger logger = new MyLogger();
        OrderedTests.driver.findElement(usernameLoc).sendKeys(username);
        OrderedTests.driver.findElement(passwordLoc).sendKeys(password);
        OrderedTests.driver.findElement(loginButton).click();

        if (OrderedTests.driver.getTitle().equals("GittiGidiyor - Türkiye'nin Öncü Alışveriş Sitesi")) {
            logger.info("Login successful.");
            return true;
        }
        else {
            logger.fatal("Login is not successful.");
            return false;
        }
    }

    public String getPageTitle() {
        return OrderedTests.driver.getTitle();
    }
}
