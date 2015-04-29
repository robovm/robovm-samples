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
package org.robovm.samples.uicatalog.toolbar;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIBarButtonSystemItem;
import org.robovm.apple.uikit.UIToolbar;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("AAPLDefaultToolbarViewController")
public class AAPLDefaultToolbarViewController extends UIViewController implements UIBarButtonItem.OnClickListener {
    private UIToolbar toolbar;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        configureToolbar();
    }

    private void configureToolbar() {
        NSArray<UIBarButtonItem> toolbarButtonItems = new NSArray<UIBarButtonItem>(getTrashBarButtonItem(),
                getFlexibleSpaceBarButtonItem(), getCustomTitleBarButtonItem());
        toolbar.setItems(toolbarButtonItems, true);
    }

    private UIBarButtonItem getTrashBarButtonItem() {
        return new UIBarButtonItem(UIBarButtonSystemItem.Trash, this);
    }

    private UIBarButtonItem getFlexibleSpaceBarButtonItem() {
        return new UIBarButtonItem(UIBarButtonSystemItem.FlexibleSpace, null);
    }

    private UIBarButtonItem getCustomTitleBarButtonItem() {
        String customTitle = "Action";

        return new UIBarButtonItem(customTitle, UIBarButtonItemStyle.Plain, this);
    }

    @Override
    public void onClick(UIBarButtonItem barButtonItem) {
        System.out.println(String.format("A bar button item on the default toolbar was clicked: %s.", barButtonItem));
    }

    @IBOutlet
    private void setToolbar(UIToolbar toolbar) {
        this.toolbar = toolbar;
    }
}
