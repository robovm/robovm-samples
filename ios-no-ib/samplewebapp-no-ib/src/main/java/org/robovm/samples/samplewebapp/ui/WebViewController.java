/*
 * Copyright (C) 2016 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.samplewebapp.ui;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSStringEncoding;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWebView;
import org.robovm.apple.uikit.UIWebViewDelegate;
import org.robovm.apple.uikit.UIWebViewNavigationType;

import java.lang.reflect.InvocationTargetException;

/**
 * This class demonstrates how to call into JavaScript and how to call Java from JavaScript.<p>
 * To call into Javascript, we use {@link UIWebView#evaluateJavaScript}.<p>
 * To call Java from JavaScript we have to use a trick: In JS we change the {@code window.location} to a custom url scheme.
 * We override {@link UIWebViewDelegate#didFinishLoad(UIWebView)} to intercept the url loading. We parse the incoming url and handle it as we like.
 * We return {@code false} so the web view does not try to load our custom url.
 */
public class WebViewController extends UIViewController implements UIWebViewDelegate {
    private static final String CUSTOM_URL_PREFIX = "ios:";
    private static final String CUSTOM_PAGE =
            "<!DOCTYPE html>"
                    + "<html>"
                    + "<head>"
                    + "<script type='text/javascript'>"

                    + "function callJava(functionName) {"
                    + "   if (arguments.length == 1) { window.location = '" + CUSTOM_URL_PREFIX
                    + "' + functionName + '()'; }"
                    + "   else {"
                    + "      var args = Array.prototype.slice.call(arguments, 1);"
                    + "      for (var i = 0; i < args.length; i++) {"
                    + "         var type = typeof args[i];"
                    + "         if (type !== 'boolean' && type !== 'number') { args[i] = '\"' + args[i] + '\"'; }"
                    + "      }"
                    + "      window.location = '" + CUSTOM_URL_PREFIX + "' + functionName + '(' + args.join(',') + ')';"
                    + "   }"
                    + "}"

                    + "function getAge() { return document.forms['ageForm'].userAge.value; }"

                    + "function submitAge() {"
                    + "   var age = getAge();"
                    + "   if (age == null || age == '') { callJava('showAlert', 'Error', 'Enter a valid age!'); }"
                    + "   else { callJava('submitAge', parseInt(age)); }"
                    + "}"

                    + "</script>"
                    + "</head>"
                    + "<body align='center'>"
                    + "<h1>My Mobile App</h1>"
                    + "<p>Please enter your age:</p>"
                    + "<form name='ageForm'>"
                    + "   <p><input type='number' name='userAge'/></p>"
                    + "   <p><input type='button' onclick='submitAge()' value='OK'/></p>"
                    + "</form>"
                    + "</body>"
                    + "</html>";

    private WebInterface webInterface;
    private UIWebView webView;

    public WebViewController() {
        webView = new UIWebView(getView().getFrame());
        webView.setDelegate(this);
        getView().addSubview(webView);

        webInterface = new WebInterface(webView);

        webView.loadHTML(CUSTOM_PAGE, null);
    }

    /**
     * This demonstrates how to call a Javascript function.
     */
    public void callJavaScriptFunctionGetAge() {
        System.out.println(webView.evaluateJavaScript("getAge()"));
    }

    /**
     * Override the delegate method to intercept url loading.
     */
    @Override
    public boolean shouldStartLoad(UIWebView webView, NSURLRequest request,
            UIWebViewNavigationType navigationType) {

        // Check for our app specific url prefix.
        if (request.getURL().getAbsoluteString().startsWith(CUSTOM_URL_PREFIX)) {
            String url = NSString.replacePercentEscapes(request.getURL().getAbsoluteString(), NSStringEncoding.UTF8);

            // Easiest way is to check the string and invoke methods manually:
            //            if (url.contains("submitAge")) {
            //                doStuff();
            //            }

            // Flexible but complex way is the create a custom url scheme that you can parse to invoke Java methods via reflection:
            String methodName = url.substring(CUSTOM_URL_PREFIX.length(), url.indexOf('('));
            String[] params = url.substring(url.indexOf('(') + 1, url.length() - 1)
                    .split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            Class[] methodParamTypes = new Class[params.length];
            Object[] methodArgs = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                String param = params[i];
                // Primitive way to check types:
                if (param.startsWith("\"")) {
                    methodParamTypes[i] = String.class;
                    methodArgs[i] = param.substring(1, param.length() - 1);
                } else if ("true".equalsIgnoreCase(param) || "false".equalsIgnoreCase(param)) {
                    methodParamTypes[i] = Boolean.class;
                    methodArgs[i] = Boolean.parseBoolean(param);
                } else {
                    methodParamTypes[i] = Number.class;
                    methodArgs[i] = Double.parseDouble(param);
                }
            }

            try {
                webInterface.getClass().getMethod(methodName, methodParamTypes).invoke(webInterface, methodArgs);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            // Cancel location change.
            return false;
        }
        return true;
    }

    @Override
    public void didStartLoad(UIWebView webView) {}

    @Override
    public void didFinishLoad(UIWebView webView) {}

    @Override
    public void didFailLoad(UIWebView webView, NSError error) {}
}
