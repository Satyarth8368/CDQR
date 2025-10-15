package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

public class QRCD {
    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // ✅ Set browser zoom to 75%
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get("https://www.bajajfinserv.in/qr-code-web-page?xc=AGJ+SdV3rY7VEffZhnq7RKCqsMDFquRk%2FfGrpiC31Hw%3D");
        js.executeScript("document.body.style.zoom='75%'");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        System.out.println("Page title: " + driver.getTitle());

        driver.findElement(By.xpath("//*[@id=\"container-a264ea1ca2\"]/div/div/div[1]/div/div[1]/div/div[3]/div/form/div[1]/input"))
                .sendKeys("9739241381");

        // Step 1: OTP Pop Up Consent Clicked Successfully
        try {
            Thread.sleep(20000);
            WebElement consentCheckbox = driver.findElement(By.xpath("//input[@type='checkbox' and @id='tnc' and @name='checkbox']"));
            consentCheckbox.click();
            System.out.println("Consent Clicked Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to click 'Consent': " + e.getMessage());
        }

        // Step 2: Click on Get OTP CTA
        try {
            driver.findElement(By.xpath("//*[@id=\"container-a264ea1ca2\"]/div/div/div[1]/div/div[1]/div/div[3]/div/form/button")).click();
            System.out.println("Get OTP Button Clicked Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to click 'Get OTP': " + e.getMessage());
        }
    }
}
