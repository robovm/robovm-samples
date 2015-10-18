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

import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControl;
import org.robovm.apple.uikit.UIPageControl;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLPageControlViewController")
public class AAPLPageControlViewController extends UIViewController implements UIControl.OnValueChangedListener {
    @IBOutlet
    private UIPageControl pageControl;
    @IBOutlet
    private UIView colorView;
    private UIColor[] colors;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Set a list of colors that correspond to the selected page.
        colors = new UIColor[] { UIColor.black(), UIColor.gray(), UIColor.red(), UIColor.green(), UIColor.blue(),
            UIColor.cyan(), UIColor.yellow(), UIColor.magenta(), UIColor.orange(), UIColor.purple() };

        configurePageControl();
        onValueChanged(null);
    }

    private void configurePageControl() {
        // The total number of pages that are available is based on how many
        // available colors we have.
        pageControl.setNumberOfPages(colors.length);
        pageControl.setCurrentPage(2);

        pageControl.setTintColor(Colors.BLUE);
        pageControl.setPageIndicatorTintColor(Colors.GREEN);
        pageControl.setCurrentPageIndicatorTintColor(Colors.PURPLE);

        pageControl.addOnValueChangedListener(this);
    }

    @Override
    public void onValueChanged(UIControl control) {
        System.out.println(String.format("The page control changed its current page to %d.",
                pageControl.getCurrentPage()));

        colorView.setBackgroundColor(colors[(int) pageControl.getCurrentPage()]);
    }
}
