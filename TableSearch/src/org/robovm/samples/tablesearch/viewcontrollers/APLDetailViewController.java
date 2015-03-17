/*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's TableSearch sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.tablesearch.viewcontrollers;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSNumberFormatter;
import org.robovm.apple.foundation.NSNumberFormatterStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIRectEdge;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.tablesearch.APLProduct;

public class APLDetailViewController extends UIViewController {
    private APLProduct product;
    private final UILabel yearLabel;
    private final UILabel priceLabel;

    public APLDetailViewController () {
        setEdgesForExtendedLayout(UIRectEdge.None);

        UIView view = getView();
        view.setBackgroundColor(UIColor.groupTableViewBackground());

        UILabel yearTitle = new UILabel(new CGRect(20, 20, 44, 21));
        yearTitle.setText("Year:");
        view.addSubview(yearTitle);

        UILabel priceTitle = new UILabel(new CGRect(20, 49, 44, 21));
        priceTitle.setText("Price:");
        view.addSubview(priceTitle);

        yearLabel = new UILabel(new CGRect(72, 20, 228, 21));
        view.addSubview(yearLabel);

        priceLabel = new UILabel(new CGRect(72, 49, 228, 21));
        view.addSubview(priceLabel);
    }

    @Override
    public void viewWillAppear (boolean animated) {
        super.viewWillAppear(animated);
        setTitle(product.getTitle());

        yearLabel.setText(String.valueOf(product.getYearIntroduced()));

        NSNumberFormatter numFormatter = new NSNumberFormatter();
        numFormatter.setNumberStyle(NSNumberFormatterStyle.Currency);
        String priceString = numFormatter.format(product.getIntroPrice());
        priceLabel.setText(priceString);
    }

    public void setProduct (APLProduct product) {
        this.product = product;
    }
}
