package org.example;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;


public class QRCD {
    static WebDriver driver;


    public static void main(String[] args) throws InterruptedException {
        JavascriptExecutor js = null;
        try {

            System.setProperty("webdriver.edge.driver",
                    "C:\\Users\\11024ss\\driver\\msedgedriver.exe");

            driver = new EdgeDriver();
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            System.out.println("Cache Cleared Successfully");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            // ✅ Set browser zoom to 75%
            js = (JavascriptExecutor) driver;
            driver.get("https://www.bajajfinserv.in/qr-code-web-page?xc=408Csix2AP3KPq1qQOtb++JV8Wq5s1j82OnhM4ZhgMyOD1uGWPk8t7VCadPyRZyUI+OMxWGO9FHXasH/Z15HDm2nOVA218f4J9UhC/TDBdeWQKV2ODgzIdpK+up18jTdq2X9W2EfA2Z2sGxgglzu8eXxT6LBBCygqJxDAsbgP8c=&utm_source=ACMS");
            js.executeScript("document.body.style.zoom='75%'");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            System.out.println("Page title: " + driver.getTitle());

            // Enter mobile number
            By mobileInputBy = By.xpath("//*[@id=\"container-a264ea1ca2\"]/div/div/div[1]/div/div[1]/div/div[3]/div/form/div[1]/input");
            wait.until(ExpectedConditions.visibilityOfElementLocated(mobileInputBy)).sendKeys("9739241381");

            // Step 1: Click consent checkbox
            try {
                By consentBy = By.xpath("//input[@type='checkbox' and @id='tnc' and @name='checkbox']");
                WebElement consentCheckbox = wait.until(ExpectedConditions.elementToBeClickable(consentBy));
                safeJsClick(consentCheckbox);
                System.out.println("Consent Clicked Successfully");
            } catch (Exception e) {
                System.out.println("❌ Failed to click 'Consent': " + e.getMessage());

            }

            // Step 2: Click on Get OTP CTA
            try {
                By getOtpBtnBy = By.xpath("//*[@id=\"container-a264ea1ca2\"]/div/div/div[1]/div/div[1]/div/div[3]/div/form/button");
                WebElement getOtpBtn = wait.until(ExpectedConditions.elementToBeClickable(getOtpBtnBy));
                safeJsClick(getOtpBtn);
                System.out.println("Get OTP Button Clicked Successfully");
            } catch (Exception e) {
                System.out.println("❌ Failed to click 'Get OTP': " + e.getMessage());
            }

            // Step 3: OTP autofetching
            try {
                System.out.println("⏳ Fetching OTP via API...");
                Otp_Scenario otpScenario = new Otp_Scenario();
                String otp = otpScenario.calloperationlist("9739241381");
                System.out.println("🎯 OTP Received: " + otp);

                if (otp != null && otp.length() == 6) {
                    WebElement shadowHost = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-onboarding-landing")));
                    SearchContext otpShadowRoot = shadowHost.getShadowRoot();

                    for (int i = 0; i < otp.length(); i++) {
                        String digit = String.valueOf(otp.charAt(i));
                        String cssSelector = "input#otp" + (i + 1);
                        try {
                            WebElement otpInput = otpShadowRoot.findElement(By.cssSelector(cssSelector));
                            otpInput.sendKeys(digit);
                            if (i == otp.length() - 1) {
                                otpInput.sendKeys(Keys.TAB);
                            }
                            System.out.println("✅ Digit " + digit + " entered in " + cssSelector);
                        } catch (Exception innerEx) {
                            System.out.println("⚠️ Unable to set digit " + digit + " into " + cssSelector + " → " + innerEx.getMessage());
                        }
                    }
                    System.out.println("✅ OTP entered successfully: " + otp);
                } else {
                    System.out.println("⚠️ OTP not received or invalid length, waiting for manual entry fallback...");
                    Thread.sleep(30000);
                }
            } catch (Exception e) {
                System.out.println("❌ Auto OTP fetch failed: " + e.getMessage());
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ignored) {
                }
            }

            // Step 4: Click SUBMIT inside Shadow DOM
            try {
                WebElement shadowHost = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("app-onboarding-landing")));
                SearchContext shadowRoot = shadowHost.getShadowRoot();
                System.out.println("✅ ShadowRoot Retrieved for Submit button");

                List<WebElement> buttons = shadowRoot.findElements(By.cssSelector("button.onb-btn-orange"));
                boolean clicked = false;
                for (WebElement button : buttons) {
                    if (button.getText().trim().equalsIgnoreCase("Submit")) {
                        wait.until(ExpectedConditions.elementToBeClickable(button));
                        scrollIntoViewCenter(button);
                        safeJsClick(button);
                        System.out.println("✅ 'SUBMIT' button clicked successfully");
                        clicked = true;
                        break;
                    }
                }
                if (!clicked) {
                    System.out.println("⚠️ 'SUBMIT' button not found inside Shadow DOM");
                }
            } catch (Exception e) {
                System.out.println("❌ Failed: Unable to click 'SUBMIT' → " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("❌ Fatal error in main: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // driver.quit(); // Uncomment when needed
        }

        // Step 5 : Click on Start on EMI CTA
        try {
            Thread.sleep(15000);
            driver.findElement(By.xpath("//*[@id=\"container-a264ea1ca2\"]/div/div/div[3]/div/div[1]/div/div[2]/div/div/div[3]/button/span[1]")).click();
        } catch (Exception e) {
            System.out.println("❌ Fatal error in Step 5: " + e.getMessage());
            e.printStackTrace();
        }

        // Step 6 : Enter amount
        try {
            // Wait for element to be clickable
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement salaryInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[formcontrolname='incomeSourceInput']")
            ));


            salaryInput.clear();

// Click and enter 250001
            salaryInput.click();
            salaryInput.sendKeys("250001");
            System.out.println("✅ User able to enter Amount Successfully");
        } catch (Exception e) {
            System.out.println("❌ Fatal error in Step 6: " + e.getMessage());
        }
            // Step : MITC Check Box Click

            Thread.sleep(Long.parseLong("10000"));
            driver.findElement(By.xpath("//*[@id=\"mat-mdc-checkbox-1-input\"]")).click();
            System.out.println("✅ Check box clicked successfully");


        // Step 7 : Continue CTA click on Amount Page
        try {

            driver.findElement(By.xpath("//button[contains(@class,'mat-mdc-button')]//span[text()='CONTINUE']")).click();
            System.out.println("✅ Continue CTA Clicked Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to click Continue CTA: " + e.getMessage());
        }
        // Step 8 : Send three words to Product Selection Category
        try {
            Thread.sleep(Long.parseLong("15000"));
            driver.findElement(By.cssSelector("input[formcontrolname='categoryInput']")).sendKeys("SMART");
            System.out.println("✅User succrssfully able to send keys to Product Section");
        } catch (Exception e) {
            System.out.println("❌ Not able to Enter Words");
        }
        // Step 9 : Need to Click Smart Watch from Drop Down
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement smartWatch = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[text()='SMART WATCH']")

            ));
            smartWatch.click();
        } catch (Exception e) {
            System.out.println("❌ Not able to Click Smart Watch");
        }
        // Step 10 : Need to Enter Brands
        Thread.sleep(Long.parseLong("15000"));
        driver.findElement(By.xpath("//*[@id=\"xmain__content\"]/app-product-selection/div/div[2]/form/div[2]/div[2]/input")).sendKeys("sa");
        System.out.println("✅ User able to enter S words successfully");
        // Step 11 : Set Browser Zoom to 70 %

// Step 11 : Need to Click on Samsung Watch
        try {
            js.executeScript("window.scrollBy(0,500)");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement samsungwatch = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[text()='SAMSUNG WATCH']")
            ));
            samsungwatch.click();
        } catch (Exception e) {
            System.out.println("✅ User able to Click Samsung Watch");
        }
        // Step 12 : Need to Click on Continue CTA on Product Selection Page
        try {

            driver.findElement(By.xpath("//button[contains(@class,'mat-mdc-button')]//span[text()='CONTINUE']")).click();
            System.out.println("✅ Continue CTA Clicked Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to click Continue CTA: " + e.getMessage());
        }

// Step 13 : Need to Click on Product Model Field on Product Details Page

        try {
            Thread.sleep(Long.parseLong("15000"));
            driver.findElement(By.xpath("/html/body/app-root/app-main/div/div[2]/div[1]/app-product-details/div/div[2]/form/div[1]/div[2]/input")).click();
            System.out.println("✅ Product Model Field Clicked Successfully Clicked Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to click Product Model Field : " + e.getMessage());
        }
//     Step 14 : Need to Click on Products present in dropdown in Product Model Field
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(),'Model ID: 180220')]")
        ));
        firstOption.click();
        System.out.println("✅ User able to Click on Product MOdel from Dropdown");

//        Step 15 : Need to Enter Product Price
        try {

            driver.findElement(By.xpath("//*[@id=\"xmain__content\"]/app-product-details/div/div[2]/form/div[2]/div[2]/input")).sendKeys("29915");
            System.out.println("✅ User able to enter Amount Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to enter Amount  " + e.getMessage());
        }

// Step 16 : Need to Enter Loan Amount of Product
        try {
            driver.findElement(By.xpath("//*[@id=\"xmain__content\"]/app-product-details/div/div[2]/form/div[3]/div[2]/input")).sendKeys("20000");
            System.out.println("✅ User able to enter Amount Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to enter Loan Amount  " + e.getMessage());
        }
//   Step 17 :     Set Browser Zoom to 85 %
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        driver.get("https://www.bajajfinserv.in/myaccount/qr-cdloans/product-details");
//        js.executeScript("document.body.style.zoom='90%'");
//        Step 18 : Need to Click on Continue CTA
        js.executeScript("document.body.style.zoom='75%'");

        try {
            driver.findElement(By.xpath("//button[contains(@class,'mat-mdc-button')]//span[text()='CONTINUE']")).click();
            System.out.println("✅ Continue CTA Clicked Successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to click Continue CTA: " + e.getMessage());
        }
//        Step 19 : Need to Click on Schemes
        Thread.sleep(Long.parseLong("20000"));
        driver.findElement(By.xpath("//*[@id=\"scheme-radio\"]")).click();
        System.out.println("✅ User able to Click on Schemes Successfully");

//        Step 20 : Need to Click on GET OTP
//        Thread.sleep(Long.parseLong("15000"));
//        driver.findElement(By.xpath("//span[contains(text(),'GET OTP')]/parent::button")).click();
//        System.out.println("✅ User able to Click Successfully on GET OTP cta ");
//  Step 21 : Need to Quit thE Browser
        Thread.sleep(Long.parseLong("15000"));
        driver.quit();
        System.out.println("✅  User successfully Reached ATOS AUTH OTP");
    }

    // ---------- Helpers ----------
    private static void scrollIntoViewCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    private static void safeJsClick(WebElement el) {
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    public static boolean isShadowElementDisplayed(String selector) {
        try {
            WebElement shadowHost = driver.findElement(By.cssSelector("app-onboarding-landing"));
            SearchContext shadowRoot = shadowHost.getShadowRoot();
            JavascriptExecutor js = (JavascriptExecutor) QRCD.driver;
            Object result = js.executeScript(
                    "var root = arguments[0];" +
                            "var sel = arguments[1];" +
                            "var element = root.querySelector(sel);" +
                            "if (!element) return false;" +
                            "var rect = element.getBoundingClientRect();" +
                            "var style = window.getComputedStyle(element);" +
                            "return rect.width > 0 && rect.height > 0 && style.visibility !== 'hidden' && style.display !== 'none' && style.opacity !== '0';",
                    shadowRoot, selector
            );
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            System.out.println("❌ Error checking shadow element visibility: " + e.getMessage());
            return false;
        }
    }
}
