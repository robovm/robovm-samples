package org.robovm.samples.robopods.parse.anypic.ios.model;

import org.robovm.samples.robopods.parse.anypic.ios.util.DefaultMap;

public class PAPUserAttributes {
    private static final String PHOTO_COUNT_KEY = "photoCount";
    private static final String IS_FOLLOWED_BY_CURRENT_USER_KEY = "isFollowedByCurrentUser";

    private final DefaultMap data;

    public PAPUserAttributes() {
        data = new DefaultMap();
    }

    public PAPUserAttributes(int photoCount, boolean isFollowedByCurrentUser) {
        data = new DefaultMap();
        setPhotoCount(photoCount).setIsFollowedByCurrentUser(isFollowedByCurrentUser);
    }

    public int getPhotoCount() {
        return data.getInt(PHOTO_COUNT_KEY, 0);
    }

    public PAPUserAttributes setPhotoCount(int photoCount) {
        data.put(PHOTO_COUNT_KEY, photoCount);
        return this;
    }

    public boolean isFollowedByCurrentUser() {
        return data.getBoolean(IS_FOLLOWED_BY_CURRENT_USER_KEY, false);
    }

    public PAPUserAttributes setIsFollowedByCurrentUser(boolean isFollowedByCurrentUser) {
        data.put(IS_FOLLOWED_BY_CURRENT_USER_KEY, isFollowedByCurrentUser);
        return this;
    }
}
