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

import com.google.gson.JsonPrimitive;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringJoiner;

public class Wrapper {
    private static final Logger logger = LoggerFactory.getLogger(Wrapper.class);
    @SafeVarargs
    public final <T> String evaluationString(String fun, T... args) {
        String _args = "";
        StringJoiner stringJoiner = new StringJoiner(", ");
        // currently only works for args of the same type and for boolean and String :-/
        if (args.length > 0) {
            for (T arg : args) {
                if (arg.getClass() == Boolean.class) {
                    stringJoiner.add(new JsonPrimitive((Boolean) arg).toString());
                } else if (arg.getClass() == String.class) {
                    stringJoiner.add(new JsonPrimitive((String) arg).toString());
                }
            }
            _args = stringJoiner.toString();
        }
        logger.debug("(" + fun + ")(" + _args + ")");
        return "(" + fun + ")(" + _args + ")";
    }

    @SafeVarargs
    public final <T> void evaluateOnNewDocument(ChromeDriver webDriver, String pageFunction, T... args) {
        String jsCode = evaluationString(pageFunction, args);
        webDriver.executeCdpCommand(
                "Page.addScriptToEvaluateOnNewDocument",
                Map.of("source", jsCode));
    }
}
