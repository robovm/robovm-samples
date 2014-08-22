 /*
 * Copyright (C) 2014 Trillian Mobile AB
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
 * Portions of this code is based on Apple Inc's UICatalog sample (v2.11)
 * which is copyright (C) 2008-2013 Apple Inc.
 */

package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIKeyboardType;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UITextAutocapitalizationType;
import org.robovm.apple.uikit.UITextAutocorrectionType;
import org.robovm.apple.uikit.UITextBorderStyle;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UITextFieldDelegateAdapter;
import org.robovm.apple.uikit.UITextFieldViewMode;
import org.robovm.apple.uikit.UIViewAutoresizing;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWebView;
import org.robovm.apple.uikit.UIWebViewDelegateAdapter;
import org.robovm.objc.annotation.Method;
import org.robovm.samples.uicatalog.Constants;

/**
 * The view controller for hosting the UIWebView feature of this sample. 
 */
public class WebViewController extends  UIViewController{
    
    private UIWebView myWebView;

    /**
     * show url bar and load webview 
     */
    @Method
    public void viewDidLoad() {
        super.viewDidLoad();
        this.setTitle("");
        
        CGRect textFieldFrame = new CGRect(Constants.LEFT_MARGIN, Constants.TWEEN_MARGIN+70, getView().getBounds().getWidth()-Constants.LEFT_MARGIN*2.0, Constants.TEXT_FIELD_HEIGHT);
        
        UITextField urlField = new UITextField(textFieldFrame);
        urlField.setBorderStyle(UITextBorderStyle.Bezel);
        urlField.setTextColor(UIColor.colorBlack());
        
        urlField.setDelegate(new UITextFieldDelegateAdapter() {

            @Override
            public boolean shouldReturn(UITextField textField) {
                textField.resignFirstResponder();
                myWebView.loadRequest(new NSURLRequest(new NSURL(textField.getText())));
                return true;
            }
            
        });
        
        urlField.setPlaceholder("<enter a full URL>");
        urlField.setText("http://www.apple.com");
        urlField.setBackgroundColor(UIColor.colorWhite());
        urlField.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth);
        //urlField.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleBottomMargin;
        urlField.setReturnKeyType(UIReturnKeyType.Go);
        urlField.setKeyboardType(UIKeyboardType.URL);
        urlField.setAutocapitalizationType(UITextAutocapitalizationType.None);
        urlField.setAutocorrectionType(UITextAutocorrectionType.No);
        urlField.setClearButtonMode(UITextFieldViewMode.Always);
        
        getView().addSubview(urlField);
        
        // create the UIWebView
        CGRect webFrame = this.getView().getFrame();
        webFrame.origin().y(webFrame.origin().y() + (Constants.TWEEN_MARGIN * 2.0) + Constants.TEXT_FIELD_HEIGHT+70);       // leave room for the URL input field
        webFrame.size().height(webFrame.size().height() - 40.0);
        
        myWebView = new UIWebView(webFrame);
        myWebView.setBackgroundColor(UIColor.colorWhite());
        myWebView.setScalesPageToFit(true);
        myWebView.setAutoresizingMask(UIViewAutoresizing.FlexibleWidth);
        //(UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleBottomMargin);
        myWebView.setDelegate(new WebViewDelegate());    
        getView().addSubview(myWebView);
        
        myWebView.loadRequest(new NSURLRequest(new NSURL("http://www.apple.com/")));
    }
    
    private class WebViewDelegate extends UIWebViewDelegateAdapter {
        @Override
        public void didStartLoad(UIWebView webView) {
            UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(true);
        }

        @Override
        public void didFinishLoad(UIWebView webView) {
            UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);
        }
    }
    
    public void viewWillAppear(boolean animated){
        super.viewWillAppear(animated);
        
        myWebView.setDelegate(new WebViewDelegate());     // setup the delegate as the web view is shown
    }

    public void viewWillDisappear(boolean animated) {
        super.viewWillDisappear(animated);
        
        myWebView.stopLoading();       // in case the web view is still loading its content
        myWebView.setDelegate(null);  // disconnect the delegate as the webview is hidden
        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(false);
    }
    
}
