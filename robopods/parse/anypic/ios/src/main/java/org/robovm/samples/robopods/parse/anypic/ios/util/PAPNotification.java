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
package org.robovm.samples.robopods.parse.anypic.ios.util;

public enum PAPNotification {
    DID_RECEIVE_REMOTE_NOTIFICATION("com.parse.Anypic.appDelegate.applicationDidReceiveRemoteNotification"),
    USER_FOLLOWING_CHANGED("com.parse.Anypic.utility.userFollowingChanged"),
    USER_LIKING_PHOTO_CALLBACK_FINISHED("com.parse.Anypic.utility.userLikedUnlikedPhotoCallbackFinished"),
    DID_FINISH_PROCESSING_PROFILE_PICTURE("com.parse.Anypic.utility.didFinishProcessingProfilePictureNotification"),
    DID_FINISH_EDITING_PHOTO("com.parse.Anypic.tabBarController.didFinishEditingPhoto"),
    DID_FINISH_IMAGE_FILE_UPLOAD("com.parse.Anypic.tabBarController.didFinishImageFileUploadNotification"),
    USER_DELETED_PHOTO("com.parse.Anypic.photoDetailsViewController.userDeletedPhoto"),
    USER_LIKES_PHOTO("com.parse.Anypic.photoDetailsViewController.userLikedUnlikedPhotoInDetailsViewNotification"),
    USER_COMMENTED_ON_PHOTO("com.parse.Anypic.photoDetailsViewController.userCommentedOnPhotoInDetailsViewNotification");

    private String name;

    PAPNotification(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
