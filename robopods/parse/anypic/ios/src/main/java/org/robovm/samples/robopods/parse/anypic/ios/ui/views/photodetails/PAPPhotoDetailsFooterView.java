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
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
package org.robovm.samples.robopods.parse.anypic.ios.ui.views.photodetails;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlContentVerticalAlignment;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIReturnKeyType;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIView;

public class PAPPhotoDetailsFooterView extends UIView {
    private final UITextField commentField;
    private final UIView mainView;

    public PAPPhotoDetailsFooterView(CGRect frame) {
        super(frame);

        setBackgroundColor(UIColor.clear());

        mainView = new UIView(new CGRect(0, 0, UIScreen.getMainScreen().getBounds().getWidth(), 51));
        mainView.setBackgroundColor(UIColor.white());
        addSubview(mainView);

        UIImageView messageIcon = new UIImageView(UIImage.create("IconAddComment"));
        messageIcon.setFrame(new CGRect(20, 15, 22, 22));
        mainView.addSubview(messageIcon);

        UIImageView commentBox = new UIImageView(UIImage.create("TextFieldComment").createResizable(
                new UIEdgeInsets(10, 10, 10, 10)));
        commentBox.setFrame(new CGRect(55, 8, 237, 34));
        mainView.addSubview(commentBox);

        commentField = new UITextField(new CGRect(66, 8, 217, 34));
        commentField.setFont(UIFont.getSystemFont(14));
        commentField.setPlaceholder("Add a comment");
        commentField.setReturnKeyType(UIReturnKeyType.Send);
        commentField.setTextColor(UIColor.fromRGBA(34f / 255f, 34f / 255f, 34f / 255f, 1));
        commentField.setContentVerticalAlignment(UIControlContentVerticalAlignment.Center);
        commentField.getKeyValueCoder().setValue("_placeholderLabel.textColor",
                UIColor.fromRGBA(114f / 255f, 114f / 255f, 114f / 255f, 1));
        mainView.addSubview(commentBox);
    }

    public static CGRect getRectForView() {
        return new CGRect(0, 0, UIScreen.getMainScreen().getBounds().getSize().getWidth(), 69);
    }

    public UITextField getCommentField() {
        return commentField;
    }
}
