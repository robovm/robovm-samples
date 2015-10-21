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
 * Portions of this code is based on Apple Inc's TableSearch sample (v1.2)
 * which is copyright (C) 2015 Apple Inc.
 */

package org.robovm.samples.tablesearch.ui;

import org.robovm.apple.foundation.NSCoder;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSNumberFormatterStyle;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.tablesearch.APLProduct;

@CustomClass("APLDetailViewController")
public class APLDetailViewController extends UIViewController {
    private static final String VIEW_CONTROLLER_PRODUCT_KEY = "ViewControllerProductKey";

    private APLProduct product;
    @IBOutlet
    private UILabel yearLabel;
    @IBOutlet
    private UILabel priceLabel;

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        setTitle(product.getTitle());

        yearLabel.setText(String.valueOf(product.getYearIntroduced()));

        NSNumberFormatter numFormatter = new NSNumberFormatter();
        numFormatter.setNumberStyle(NSNumberFormatterStyle.Currency);
        String priceString = numFormatter.format(NSNumber.valueOf(product.getIntroPrice()));
        priceLabel.setText(priceString);
    }

    public void setProduct(APLProduct product) {
        this.product = product;
    }

    @Override
    public void encodeRestorableState(NSCoder coder) {
        super.encodeRestorableState(coder);

        // encode the product
        coder.encodeObject(VIEW_CONTROLLER_PRODUCT_KEY, product);
    }

    @Override
    public void decodeRestorableState(NSCoder coder) {
        super.decodeRestorableState(coder);

        // restore the product
        product = coder.decodeObject(VIEW_CONTROLLER_PRODUCT_KEY, APLProduct.class);
    }
}
