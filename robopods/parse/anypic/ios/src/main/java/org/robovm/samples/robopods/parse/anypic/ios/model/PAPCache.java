package org.robovm.samples.robopods.parse.anypic.ios.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robovm.apple.foundation.NSDate;
import org.robovm.apple.foundation.NSUserDefaults;
import org.robovm.pods.parse.PFUser;

public class PAPCache {
    private static final String USER_DEFAULTS_ACTIVITY_FEED_VIEW_CONTROLLER_LAST_REFRESH_KEY = "com.parse.Anypic.userDefaults.activityFeedViewController.lastRefresh";
    private static final String USER_DEFAULTS_CACHE_FACEBOOK_FRIENDS_KEY = "com.parse.Anypic.userDefaults.cache.facebookFriends";

    private static PAPCache instance;
    private final Map<String, Object> cache;

    private PAPCache() {
        cache = new HashMap<>();
    }

    public static PAPCache getSharedCache() {
        if (instance == null) {
            instance = new PAPCache();
        }
        return instance;
    }

    public void clear() {
        cache.clear();

        // clear NSUserDefaults
        NSUserDefaults defaults = NSUserDefaults.getStandardUserDefaults();
        defaults.remove(USER_DEFAULTS_CACHE_FACEBOOK_FRIENDS_KEY);
        defaults.remove(USER_DEFAULTS_ACTIVITY_FEED_VIEW_CONTROLLER_LAST_REFRESH_KEY);
        defaults.synchronize();
    }

    public void setPhotoAttributes(PAPPhoto photo, PAPPhotoAttributes attributes) {
        String key = getKey(photo);
        cache.put(key, attributes);
    }

    public PAPPhotoAttributes getPhotoAttributes(PAPPhoto photo) {
        return getPhotoAttributes(photo, false);
    }

    private PAPPhotoAttributes getPhotoAttributes(PAPPhoto photo, boolean create) {
        String key = getKey(photo);
        PAPPhotoAttributes attributes = (PAPPhotoAttributes) cache.get(key);
        if (create && attributes == null) {
            attributes = new PAPPhotoAttributes();
        }
        return attributes;
    }

    public int getPhotoLikeCount(PAPPhoto photo) {
        PAPPhotoAttributes attributes = getPhotoAttributes(photo);
        return attributes.getLikeCount();
    }

    public int getPhotoCommentCount(PAPPhoto photo) {
        PAPPhotoAttributes attributes = getPhotoAttributes(photo);
        return attributes.getCommentCount();
    }

    public List<PAPUser> getPhotoLikers(PAPPhoto photo) {
        PAPPhotoAttributes attributes = getPhotoAttributes(photo);
        return attributes.getLikers();
    }

    public List<PAPUser> getPhotoCommenters(PAPPhoto photo) {
        PAPPhotoAttributes attributes = getPhotoAttributes(photo);
        return attributes.getCommenters();
    }

    public void setPhotoIsLikedByCurrentUser(PAPPhoto photo, boolean liked) {
        PAPPhotoAttributes attributes = getPhotoAttributes(photo, true);
        attributes.setIsLikedByCurrentUser(liked);
        setPhotoAttributes(photo, attributes);
    }

    public boolean isPhotoLikedByCurrentUser(PAPPhoto photo) {
        PAPPhotoAttributes attributes = getPhotoAttributes(photo);
        return attributes.isLikedByCurrentUser();
    }

    public void incrementPhotoLikerCount(PAPPhoto photo) {
        int likeCount = getPhotoLikeCount(photo) + 1;
        PAPPhotoAttributes attributes = getPhotoAttributes(photo, true);
        attributes.setLikeCount(likeCount);
        setPhotoAttributes(photo, attributes);
    }

    public void decrementPhotoLikerCount(PAPPhoto photo) {
        int likeCount = getPhotoLikeCount(photo) - 1;
        if (likeCount < 0) {
            return;
        }
        PAPPhotoAttributes attributes = getPhotoAttributes(photo, true);
        attributes.setLikeCount(likeCount);
        setPhotoAttributes(photo, attributes);
    }

    public void incrementPhotoCommentCount(PAPPhoto photo) {
        int commentCount = getPhotoCommentCount(photo) + 1;
        PAPPhotoAttributes attributes = getPhotoAttributes(photo, true);
        attributes.setCommentCount(commentCount);
        setPhotoAttributes(photo, attributes);
    }

    public void decrementPhotoCommentCount(PAPPhoto photo) {
        int commentCount = getPhotoCommentCount(photo) - 1;
        if (commentCount < 0) {
            return;
        }
        PAPPhotoAttributes attributes = getPhotoAttributes(photo, true);
        attributes.setCommentCount(commentCount);
        setPhotoAttributes(photo, attributes);
    }

    public void setUserAttributes(PFUser user, PAPUserAttributes attributes) {
        String key = getKey(user);
        cache.put(key, attributes);
    }

    public PAPUserAttributes getUserAttributes(PFUser user) {
        return getUserAttributes(user, false);
    }

    private PAPUserAttributes getUserAttributes(PFUser user, boolean create) {
        String key = getKey(user);
        PAPUserAttributes attributes = (PAPUserAttributes) cache.get(key);
        if (create && attributes == null) {
            attributes = new PAPUserAttributes();
        }
        return attributes;
    }

    public int getUserPhotoCount(PFUser user) {
        PAPUserAttributes attributes = getUserAttributes(user);
        return attributes.getPhotoCount();
    }

    public boolean getUserFollowStatus(PFUser user) {
        PAPUserAttributes attributes = getUserAttributes(user);
        return attributes.isFollowedByCurrentUser();
    }

    public void setUserPhotoCount(PFUser user, int count) {
        PAPUserAttributes attributes = getUserAttributes(user, true);
        attributes.setPhotoCount(count);
        setUserAttributes(user, attributes);
    }

    public void setUserFollowStatus(PFUser user, boolean following) {
        PAPUserAttributes attributes = getUserAttributes(user, true);
        attributes.setIsFollowedByCurrentUser(following);
        setUserAttributes(user, attributes);
    }

    public void setLastActivityFeedRefresh(NSDate lastRefresh) {
        NSUserDefaults.getStandardUserDefaults().put(USER_DEFAULTS_ACTIVITY_FEED_VIEW_CONTROLLER_LAST_REFRESH_KEY,
                lastRefresh);
        NSUserDefaults.getStandardUserDefaults().synchronize();
    }

    public NSDate getLastActivityFeedRefresh() {
        return (NSDate) NSUserDefaults.getStandardUserDefaults().get(
                USER_DEFAULTS_ACTIVITY_FEED_VIEW_CONTROLLER_LAST_REFRESH_KEY);
    }

    public void setFacebookFriends(List<String> friends) {
        String key = USER_DEFAULTS_CACHE_FACEBOOK_FRIENDS_KEY;
        cache.put(key, friends);
        NSUserDefaults.getStandardUserDefaults().put(key, friends);
        NSUserDefaults.getStandardUserDefaults().synchronize();
    }

    @SuppressWarnings("unchecked")
    public List<String> getFacebookFriends() {
        String key = USER_DEFAULTS_CACHE_FACEBOOK_FRIENDS_KEY;
        if (cache.containsKey(key)) {
            return (List<String>) cache.get(key);
        }

        List<String> friends = NSUserDefaults.getStandardUserDefaults().getStringArray(key);
        if (friends != null) {
            cache.put(key, friends);
        }

        return friends;
    }

    private String getKey(PAPPhoto photo) {
        return String.format("photo_%s", photo.getObjectId());
    }

    private String getKey(PFUser user) {
        return String.format("user_%s", user.getObjectId());
    }
}
