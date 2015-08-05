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
package org.robovm.samples.robopods.parse.anypic.ios.ui.views.timeline;

import org.robovm.apple.uikit.UIButton;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;

public interface PAPPhotoHeaderViewDelegate {
    /**
     * Sent to the delegate when the user button is tapped
     * 
     * @param user the PFUser associated with this button
     */
    void didTapUserButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPUser user);

    /**
     * Sent to the delegate when the like photo button is tapped
     * 
     * @param photo the PFObject for the photo that is being liked or disliked
     */
    void didTapLikePhotoButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPPhoto photo);

    /**
     * Sent to the delegate when the comment on photo button is tapped
     * 
     * @param photo the PFObject for the photo that will be commented on
     */
    void didTapCommentOnPhotoButton(PAPPhotoHeaderView photoHeaderView, UIButton button, PAPPhoto photo);
}
