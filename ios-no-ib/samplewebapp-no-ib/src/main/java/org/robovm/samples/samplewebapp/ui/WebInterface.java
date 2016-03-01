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

import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UIWebView;
import org.robovm.rt.annotation.StronglyLinked;

/**
 * Contains only methods that should be accessible from JavaScript.
 */
@StronglyLinked
public class WebInterface {
    private UIWebView webView;

    public WebInterface(UIWebView webView) {
        this.webView = webView;
    }

    public void submitAge(Number age) {
        showAlert("Hello!", "You are " + age.intValue() + " years old!");
    }

    public void showAlert(String title, String message) {
        new UIAlertView(title, message, null, "OK").show();
    }
}
