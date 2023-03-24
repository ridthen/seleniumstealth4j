# SeleniumStealth4j

A Java library seleniumstealth4j helps to prevent webdriver detection.
The library totally inspired by
[https://github.com/diprajpatra/selenium-stealth](https://github.com/diprajpatra/selenium-stealth)

It can be seen as a re-implementation of JavaScript [puppeteer-extra-plugin-stealth](
https://github.com/berstend/puppeteer-extra/tree/master/packages/puppeteer-extra-plugin-stealth)

## Usage
Update your app's pom.xml dependencies:
```xml
<dependencies>
  <dependency>
    <groupId>com.github.ridthen</groupId>
    <artifactId>seleniumstealth4j</artifactId>
    <version>0.1</version>
  </dependency>
</dependencies>
```
Then try to wrap ChromeDriver with SeleniumStealth4j.Builder and open a bot checker.
```java
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.github.ridthen.seleniumstealth4j;

import java.util.Collections;

public class Example {
    public static void main(String[] args) {
        var options = new ChromeOptions();
        options.addArguments("start-maximized");

//        options.addArguments("--headless");
        options.addArguments("--no-sandbox"); // to run from docker

        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", Collections.singletonList("false"));
        System.setProperty("webdriver.chrome.driver", "chromedriver");

        var webDriver = new ChromeDriver(options);

        new SeleniumStealth4j.Builder(webDriver)
// optional parameters:
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/83.0.4103.53 Safari/537.36")
                .languages(new String[] {"en-US", "en"})
                .vendor("Google Inc.")
                .platform("Win32")
// optional parameters with default values:
//                .webglVendor("Intel Inc.")
//                .renderer("Intel Iris OpenGL Engine")
//                .fixHairline(true)
//                .runOnInsecureOrigins(false)
                .build();

        webDriver.get("https://bot.sannysoft.com/");
    }
}
```