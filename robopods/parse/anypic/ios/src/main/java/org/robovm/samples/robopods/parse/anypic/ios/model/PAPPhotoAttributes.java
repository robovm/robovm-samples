package org.robovm.samples.robopods.parse.anypic.ios.model;

import java.util.List;

import org.robovm.samples.robopods.parse.anypic.ios.util.DefaultMap;

public class PAPPhotoAttributes {
    private static final String IS_LIKED_BY_CURRENT_USER_KEY = "isLikedByCurrentUser";
    private static final String LIKE_COUNT_KEY = "likeCount";
    private static final String LIKERS_KEY = "likers";
    private static final String COMMENT_COUNT_KEY = "commentCount";
    private static final String COMMENTERS_KEY = "commenters";

    private final DefaultMap data;

    public PAPPhotoAttributes() {
        data = new DefaultMap();
    }

    public PAPPhotoAttributes(List<PAPUser> likers, List<PAPUser> commenters, boolean likedByCurrentUser) {
        data = new DefaultMap();
        setLikers(likers).setLikeCount(likers.size()).setCommenters(commenters).setCommentCount(commenters.size())
                .setIsLikedByCurrentUser(likedByCurrentUser);
    }

    public boolean isLikedByCurrentUser() {
        return data.getBoolean(IS_LIKED_BY_CURRENT_USER_KEY, false);
    }

    public PAPPhotoAttributes setIsLikedByCurrentUser(boolean likedByCurrentUser) {
        data.put(IS_LIKED_BY_CURRENT_USER_KEY, likedByCurrentUser);
        return this;
    }

    public int getLikeCount() {
        return data.getInt(LIKE_COUNT_KEY, 0);
    }

    public PAPPhotoAttributes setLikeCount(int likeCount) {
        data.put(LIKE_COUNT_KEY, likeCount);
        return this;
    }

    @SuppressWarnings("unchecked")
    public List<PAPUser> getLikers() {
        return (List<PAPUser>) data.get(LIKERS_KEY);
    }

    public PAPPhotoAttributes setLikers(List<PAPUser> likers) {
        data.put(LIKERS_KEY, likers);
        return this;
    }

    public int getCommentCount() {
        return data.getInt(COMMENT_COUNT_KEY, 0);
    }

    public PAPPhotoAttributes setCommentCount(int commentCount) {
        data.put(COMMENT_COUNT_KEY, commentCount);
        return this;
    }

    @SuppressWarnings("unchecked")
    public List<PAPUser> getCommenters() {
        return (List<PAPUser>) data.get(COMMENTERS_KEY);
    }

    public PAPPhotoAttributes setCommenters(List<PAPUser> commenters) {
        data.put(COMMENTERS_KEY, commenters);
        return this;
    }
}
