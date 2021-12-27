package me.t3sl4.textfileencoder.Selenium;

import java.io.IOException;

public class SeleniumTest {
    public static void main(String[] args) throws IOException {
        goToURL();
    }

    private static void goToURL() throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\" http://www.yazilimcilardunyasi.com/");
    }

    private void searchInSite() {
        /*
        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.google.com");
        WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("Cheese!\n"); // send also a "\n"
        element.submit();

        // wait until the google page shows the result
        WebElement myDynamicElement = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("resultStats")));

        List<WebElement> findElements = driver.findElements(By.xpath(""));
        driver.findElement(By.xpath("/html/body/div[4]/div[2]/div[2]/div[2]/div[2]/div[2]/div[2]/div/div[4]/div[2]/div/aside/div/div[4]/div[1]/div/form/table/tbody/tr/td[2]/input")).click();
        // this are all the links you like to visit
        for (WebElement webElement : findElements) {

        }
         */
    }
}
