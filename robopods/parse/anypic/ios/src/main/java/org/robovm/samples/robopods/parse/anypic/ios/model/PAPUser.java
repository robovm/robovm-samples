package org.robovm.samples.robopods.parse.anypic.ios.model;

import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFUser;
import org.robovm.pods.parse.ParseClassName;

@ParseClassName("_User")
public class PAPUser extends PFUser implements Comparable<PAPUser> {
    public static final String DISPLAY_NAME_KEY = "displayName";
    public static final String FACEBOOK_ID_KEY = "facebookId";
    public static final String PHOTO_ID_KEY = "photoId";
    public static final String PROFILE_PIC_SMALL_KEY = "profilePictureSmall";
    public static final String PROFILE_PIC_MEDIUM_KEY = "profilePictureMedium";
    public static final String FACEBOOK_FRIENDS_KEY = "facebookFriends";
    public static final String ALREADY_AUTO_FOLLOWED_FACEBOOK_FRIENDS = "userAlreadyAutoFollowedFacebookFriends";
    public static final String EMAIL_KEY = "email";
    public static final String AUTO_FOLLOW_KEY = "autoFollow";

    public static PAPUser getCurrentUser() {
        return (PAPUser) PFUser.getCurrentUser();
    }

    public String getDisplayName() {
        return getString(DISPLAY_NAME_KEY);
    }

    public void setDisplayName(String displayName) {
        put(DISPLAY_NAME_KEY, displayName);
    }

    public String getFacebookId() {
        return getString(FACEBOOK_ID_KEY);
    }

    public void setFacebookId(String facebookId) {
        put(FACEBOOK_ID_KEY, facebookId);
    }

    public String getPhotoId() {
        return getString(PHOTO_ID_KEY);
    }

    public void setPhotoID(String photoId) {
        put(PHOTO_ID_KEY, photoId);
    }

    public PFFile getProfilePicSmall() {
        return (PFFile) get(PROFILE_PIC_SMALL_KEY);
    }

    public void setProfilePicSmall(PFFile pic) {
        put(PROFILE_PIC_SMALL_KEY, pic);
    }

    public PFFile getProfilePicMedium() {
        return (PFFile) get(PROFILE_PIC_MEDIUM_KEY);
    }

    public void setProfilePicMedium(PFFile pic) {
        put(PROFILE_PIC_MEDIUM_KEY, pic);
    }

    public void removeFacebookFriends() {
        if (get(FACEBOOK_FRIENDS_KEY) != null) {
            remove(FACEBOOK_FRIENDS_KEY);
        }
    }

    public boolean hasAlreadyAutoFollowedFacebookFriends() {
        return get(ALREADY_AUTO_FOLLOWED_FACEBOOK_FRIENDS) != null;
    }

    @Override
    public int compareTo(PAPUser another) {
        return getDisplayName().compareTo(another.getDisplayName());
    }
}
