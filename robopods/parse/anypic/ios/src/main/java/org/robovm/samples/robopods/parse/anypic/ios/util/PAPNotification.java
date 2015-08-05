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
