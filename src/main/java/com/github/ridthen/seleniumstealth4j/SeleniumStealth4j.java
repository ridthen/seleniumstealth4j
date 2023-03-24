/**
 *
 * Copyright © 2023 Alexander Popov (ridven@yahoo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ridthen.seleniumstealth4j;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SeleniumStealth4j {
    private final Wrapper wrapper;
    private final ChromeDriver webDriver;
    private final String userAgent;
    private final String[] languages;
    private final String vendor;
    private final String platform;
    private final String webglVendor;
    private final String renderer;
    private final boolean fixHairline;
    private final boolean runOnInsecureOrigins;
    private static final Logger logger = LoggerFactory.getLogger(SeleniumStealth4j.class);

    public static class Builder {
        // required parameters
        private final ChromeDriver webDriver;

        // optional parameters - initialized to default values
        private String userAgent    = "";
        private String[] languages  = new String[] {};
        private String vendor       = "";
        private String platform     = "";
        private String webglVendor  = "Intel Inc.";
        private String renderer     = "Intel Iris OpenGL Engine";
        private boolean fixHairline = true;
        private boolean runOnInsecureOrigins = false;

        public Builder(ChromeDriver webDriver) {
            this.webDriver = webDriver;
        }

        public Builder userAgent(String val)
        { userAgent = val; return this; }

        public Builder languages(String[] val)
        { languages = val; return this; }

        public Builder vendor(String val)
        { vendor = val; return this; }

        public Builder platform(String val)
        { platform = val; return this; }

        public Builder webglVendor(String val)
        { webglVendor = val; return this; }

        public Builder renderer(String val)
        { renderer = val; return this; }

        public Builder fixHairline(boolean val)
        { fixHairline = val; return this; }

        public Builder runOnInsecureOrigins(boolean val)
        { runOnInsecureOrigins = val; return this; }

        public SeleniumStealth4j build() {
            return new SeleniumStealth4j(this);
        }

    }

    private SeleniumStealth4j(Builder builder) {
        wrapper = new Wrapper();
        webDriver = builder.webDriver;
        userAgent = builder.userAgent;
        languages = builder.languages;
        vendor = builder.vendor;
        platform = builder.platform;
        webglVendor = builder.webglVendor;
        renderer = builder.renderer;
        fixHairline = builder.fixHairline;
        runOnInsecureOrigins = builder.runOnInsecureOrigins;

        stealth(webDriver, userAgent, languages, vendor, platform, webglVendor, renderer, fixHairline,
                runOnInsecureOrigins);
    }

    private void stealth(ChromeDriver webDriver,
                        String userAgent, // 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.53 Safari/537.36'
                        String[] languages, // ["en-US", "en"]
                        String vendor, // "Google Inc."
                        String platform, // "Win32"
                        String webglVendor, // "Intel Inc."
                        String renderer, // "Intel Iris OpenGL Engine"
                        boolean fixHairline,
                        boolean runOnInsecureOrigins) {

        String uaLanguages = String.join(",", languages);
        logger.debug("uaLanguages: " + uaLanguages);

        jsLoader(webDriver, "js/utils.js");
        jsLoader(webDriver, "js/chrome.app.js");
        jsLoader(webDriver, "js/chrome.runtime.js", runOnInsecureOrigins);
        jsLoader(webDriver, "js/iframe.contentWindow.js");
        jsLoader(webDriver, "js/media.codecs.js");
        jsLoader(webDriver, "js/navigator.languages.js", languages);
        jsLoader(webDriver, "js/navigator.permissions.js");
        jsLoader(webDriver, "js/navigator.plugins.js");
        jsLoader(webDriver, "js/navigator.vendor.js", vendor);
        jsLoader(webDriver, "js/navigator.webdriver.js");
        userAgentOverride(webDriver, userAgent, uaLanguages, platform);
        jsLoader(webDriver, "js/webgl.vendor.js", webglVendor, renderer);
        jsLoader(webDriver, "js/window.outerdimensions.js");
        if (fixHairline) jsLoader(webDriver, "js/hairline.fix.js");

    }

    @SafeVarargs
    private <T> void jsLoader(ChromeDriver webDriver, String jsFile, T... args) {
        var inputStream = getClass().getClassLoader().getResourceAsStream(jsFile);
        String pageFunction = null;
        try {
            if (inputStream != null) {
                pageFunction = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            logger.info("Error occurred while reading " + jsFile);
        }
        wrapper.evaluateOnNewDocument(webDriver, pageFunction, args);
    }

    private void userAgentOverride(ChromeDriver webDriver, String userAgent, String uaLanguages, String platform) {
        String ua;
        if (userAgent.equals("")) {
            ua = webDriver.executeCdpCommand(
                    "Browser.getVersion",
                    Map.of()
            ).get("userAgent").toString();
            logger.debug("userAgent from Browser.getVersion:\n" + ua);
        } else {
            ua = userAgent;
        }
        ua = ua.replaceAll("HeadlessChrome", "Chrome");
        HashMap<String, Object> overrideUserAgent = new HashMap<>();

        overrideUserAgent.put("userAgent", ua);
        if (!uaLanguages.equals("")) overrideUserAgent.put("acceptLanguage", uaLanguages);
        if (!platform.equals("")) overrideUserAgent.put("platform", platform);
        logger.debug("overrideUserAgent:\n" + overrideUserAgent);

        webDriver.executeCdpCommand("Network.setUserAgentOverride", overrideUserAgent);
    }

}
