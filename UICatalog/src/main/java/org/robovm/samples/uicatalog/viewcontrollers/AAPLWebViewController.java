/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSRange;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIDataDetectorTypes;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegate;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWebView;
import org.robovm.apple.uikit.UIWebViewDelegate;
import org.robovm.apple.uikit.UIWebViewNavigationType;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("AAPLWebViewController")
public class AAPLWebViewController extends UIViewController implements UIWebViewDelegate, UITextFieldDelegate {
    private UIWebView webView;
    private UITextField addressTextField;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureWebView();
        loadAddressURL();
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        super.viewWillDisappear(animated);

        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);
    }

    private void loadAddressURL() {
        NSURL requestURL = new NSURL(addressTextField.getText());
        NSURLRequest request = new NSURLRequest(requestURL);
        webView.loadRequest(request);
    }

    private void configureWebView() {
        webView.setBackgroundColor(UIColor.white());
        webView.setScalesPageToFit(true);
        webView.setDataDetectorTypes(UIDataDetectorTypes.All);
    }

    @Override
    public boolean shouldStartLoad(UIWebView webView, NSURLRequest request, UIWebViewNavigationType navigationType) {
        return true;
    }

    @Override
    public void didStartLoad(UIWebView webView) {
        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(true);
    }

    @Override
    public void didFinishLoad(UIWebView webView) {
        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);
    }

    @Override
    public void didFailLoad(UIWebView webView, NSError error) {
        // Report the error inside the web view.
        String errorMessage = "An error occured:";
        String errorFormatString = "<!doctype html><html><body><div style=\"width: 100%%; text-align: center; font-size: 36pt;\">%s%s</div></body></html>";

        String errorHTML = String.format(errorFormatString, errorMessage, error.getLocalizedDescription());
        webView.loadHTML(errorHTML, null);

        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);
    }

    // This helps dismiss the keyboard when the "Done" button is clicked.
    @Override
    public boolean shouldReturn(UITextField textField) {
        textField.resignFirstResponder();

        loadAddressURL();

        return true;
    }

    @Override
    public boolean shouldBeginEditing(UITextField textField) {
        return true;
    }

    @Override
    public void didBeginEditing(UITextField textField) {}

    @Override
    public boolean shouldEndEditing(UITextField textField) {
        return true;
    }

    @Override
    public void didEndEditing(UITextField textField) {}

    @Override
    public boolean shouldChangeCharacters(UITextField textField, NSRange range, String string) {
        return true;
    }

    @Override
    public boolean shouldClear(UITextField textField) {
        return true;
    }

    @IBOutlet
    private void setWebView(UIWebView webView) {
        this.webView = webView;
    }

    @IBOutlet
    private void setAddressTextField(UITextField addressTextField) {
        this.addressTextField = addressTextField;
    }
}
