package org.robovm.samples.robopods.parse.anypic.ios.util;

import java.util.ArrayList;
import java.util.List;

import org.robovm.apple.coregraphics.CGContext;
import org.robovm.apple.coregraphics.CGInterpolationQuality;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSMutableDictionary;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.pods.facebook.core.FBSDKProfile;
import org.robovm.pods.parse.PFACL;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFFindCallback;
import org.robovm.pods.parse.PFObject;
import org.robovm.pods.parse.PFQuery;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhotoAttributes;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;

public class PAPUtility {
    public static void likePhotoInBackground(final PAPPhoto photo, final PFSaveCallback completion) {
        PFQuery<PAPActivity> queryExistingLikes = PFQuery.getQuery(PAPActivity.class);
        queryExistingLikes.whereEqualTo(PAPActivity.PHOTO_KEY, photo);
        queryExistingLikes.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.LIKE.getKey());
        queryExistingLikes.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        queryExistingLikes.setCachePolicy(PFCachePolicy.NetworkOnly);
        queryExistingLikes.findInBackground(new PFFindCallback<PAPActivity>() {
            @Override
            public void done(NSArray<PAPActivity> objects, NSError error) {
                if (error == null) {
                    try {
                        for (PAPActivity activity : objects) {
                            activity.delete();
                        }
                    } catch (NSErrorException e) {
                        e.printStackTrace();
                    }
                }

                // proceed to creating new like
                PAPActivity likeActivity = PFObject.create(PAPActivity.class);
                likeActivity.setType(PAPActivityType.LIKE);
                likeActivity.setFromUser(PAPUser.getCurrentUser());
                likeActivity.setToUser(photo.getUser());
                likeActivity.setPhoto(photo);

                PFACL likeACL = new PFACL(PAPUser.getCurrentUser());
                likeACL.setPublicReadAccess(true);
                likeACL.setWriteAccess(photo.getUser(), true);
                likeActivity.setACL(likeACL);

                likeActivity.saveInBackground(new PFSaveCallback() {
                    @Override
                    public void done(final boolean success, NSError error) {
                        if (completion != null) {
                            completion.done(success, error);
                        }

                        // refresh cache
                        PFQuery<PAPActivity> query = queryActivities(photo, PFCachePolicy.NetworkOnly);
                        query.findInBackground(new PFFindCallback<PAPActivity>() {
                            @Override
                            public void done(NSArray<PAPActivity> objects, NSError error) {
                                if (error == null) {
                                    List<PAPUser> likers = new ArrayList<>();
                                    List<PAPUser> commenters = new ArrayList<>();

                                    boolean isLikedByCurrentUser = false;

                                    for (PAPActivity activity : objects) {
                                        if (activity.getFromUser() != null) {
                                            if (activity.getType() == PAPActivityType.LIKE) {
                                                likers.add(activity.getFromUser());
                                            } else if (activity.getType() == PAPActivityType.COMMENT) {
                                                commenters.add(activity.getFromUser());
                                            }

                                            if (activity.getFromUser().getObjectId()
                                                    .equals(PAPUser.getCurrentUser().getObjectId())) {
                                                if (activity.getType() == PAPActivityType.LIKE) {
                                                    isLikedByCurrentUser = true;
                                                }
                                            }
                                        }

                                    }

                                    PAPCache.getSharedCache().setPhotoAttributes(photo,
                                            new PAPPhotoAttributes(likers, commenters, isLikedByCurrentUser));
                                }

                                NSDictionary<?, ?> notificationPayload = new NSMutableDictionary<>();
                                notificationPayload.put("liked", success);
                                PAPNotificationManager.postNotification(
                                        PAPNotification.USER_LIKING_PHOTO_CALLBACK_FINISHED, photo,
                                        notificationPayload);
                            }
                        });
                    }
                });

            }
        });
    }

    public static void unlikePhotoInBackground(final PAPPhoto photo, final PFSaveCallback completion) {
        PFQuery<PAPActivity> queryExistingLikes = PFQuery.getQuery(PAPActivity.class);
        queryExistingLikes.whereEqualTo(PAPActivity.PHOTO_KEY, photo);
        queryExistingLikes.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.LIKE.getKey());
        queryExistingLikes.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        queryExistingLikes.setCachePolicy(PFCachePolicy.NetworkOnly);
        queryExistingLikes.findInBackground(new PFFindCallback<PAPActivity>() {
            @Override
            public void done(NSArray<PAPActivity> objects, NSError error) {
                if (error == null) {
                    try {
                        for (PAPActivity activity : objects) {
                            activity.delete();
                        }
                    } catch (NSErrorException e) {
                        e.printStackTrace();
                    }

                    if (completion != null) {
                        completion.done(true, error);
                    }

                    // refresh cache
                    PFQuery<PAPActivity> query = queryActivities(photo, PFCachePolicy.NetworkOnly);
                    query.findInBackground(new PFFindCallback<PAPActivity>() {
                        @Override
                        public void done(org.robovm.apple.foundation.NSArray<PAPActivity> objects, NSError error) {
                            if (error == null) {
                                List<PAPUser> likers = new ArrayList<>();
                                List<PAPUser> commenters = new ArrayList<>();

                                boolean isLikedByCurrentUser = false;

                                for (PAPActivity activity : objects) {
                                    if (activity.getFromUser() != null) {
                                        if (activity.getType() == PAPActivityType.LIKE) {
                                            likers.add(activity.getFromUser());
                                        } else if (activity.getType() == PAPActivityType.COMMENT) {
                                            commenters.add(activity.getFromUser());
                                        }

                                        if (activity.getFromUser().getObjectId()
                                                .equals(PAPUser.getCurrentUser().getObjectId())) {
                                            if (activity.getType() == PAPActivityType.LIKE) {
                                                isLikedByCurrentUser = true;
                                            }
                                        }
                                    }

                                }

                                PAPCache.getSharedCache().setPhotoAttributes(photo,
                                        new PAPPhotoAttributes(likers, commenters, isLikedByCurrentUser));
                            }

                            NSDictionary<?, ?> notificationPayload = new NSMutableDictionary<>();
                            notificationPayload.put("liked", false);
                            PAPNotificationManager.postNotification(
                                    PAPNotification.USER_LIKING_PHOTO_CALLBACK_FINISHED, photo,
                                    notificationPayload);
                        }
                    });
                } else {
                    if (completion != null) {
                        completion.done(false, error);
                    }
                }
            }
        });
    }

    public static void processFacebookProfilePictureData(NSData newProfilePictureData) {
        Log.d("Processing profile picture of size: %d", newProfilePictureData.getLength());
        if (newProfilePictureData.getLength() == 0) {
            Log.e("Image data empty!");
            return;
        }

        UIImage image = UIImage.create(newProfilePictureData);
        if (image == null) {
            Log.e("Image data corrupt!");
            return;
        }

        UIImage mediumImage = UIImageUtility.createThumbnail(image, 280, 0, 0, CGInterpolationQuality.High);
        UIImage smallRoundedImage = UIImageUtility.createThumbnail(image, 64, 0, 0, CGInterpolationQuality.Low);

        NSData mediumImageData = mediumImage.toJPEGData(0.5); // using JPEG for
                                                              // larger pictures
        NSData smallRoundedImageData = smallRoundedImage.toPNGData();

        if (mediumImageData.getLength() > 0) {
            final PFFile fileMediumImage = new PFFile(mediumImageData);
            fileMediumImage.saveInBackground(new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    if (error == null) {
                        PAPUser.getCurrentUser().setProfilePicMedium(fileMediumImage);
                        PAPUser.getCurrentUser().saveInBackground();
                    }
                }
            });
        }

        if (smallRoundedImageData.getLength() > 0) {
            final PFFile fileSmallRoundedImage = new PFFile(smallRoundedImageData);
            fileSmallRoundedImage.saveInBackground(new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    if (error == null) {
                        PAPUser.getCurrentUser().setProfilePicSmall(fileSmallRoundedImage);
                        PAPUser.getCurrentUser().saveInBackground();
                    }
                }
            });
        }

        Log.d("Processed profile picture");
    }

    public static boolean userHasValidFacebookData(PAPUser user) {
        String facebookId = user.getFacebookId();
        return facebookId != null && facebookId.length() > 0 &&
                facebookId.equals(FBSDKProfile.getCurrentProfile().getUserID());
    }

    public static boolean userHasProfilePictures(PAPUser user) {
        PFFile profilePictureMedium = user.getProfilePicMedium();
        PFFile profilePictureSmall = user.getProfilePicSmall();

        return profilePictureMedium != null && profilePictureSmall != null;
    }

    public static UIImage getDefaultProfilePicture() {
        return UIImage.create("AvatarPlaceholderBig");
    }

    public static String getFirstNameOfDisplayName(String displayName) {
        if (displayName == null || displayName.length() == 0) {
            return "Someone";
        }

        String[] displayNameParts = displayName.split(" ");
        String firstName = displayNameParts[0];
        if (firstName.length() > 100) {
            // truncate to 100 so that it fits in a Push payload
            firstName = firstName.substring(0, 100);
        }
        return firstName;
    }

    public static void followUserInBackground(PAPUser user, PFSaveCallback completion) {
        if (user.getObjectId().equals(PAPUser.getCurrentUser().getObjectId())) {
            return;
        }

        PAPActivity followActivity = PFObject.create(PAPActivity.class);
        followActivity.setFromUser(PAPUser.getCurrentUser());
        followActivity.setToUser(user);
        followActivity.setType(PAPActivityType.FOLLOW);

        PFACL followACL = new PFACL(PAPUser.getCurrentUser());
        followACL.setPublicReadAccess(true);
        followActivity.setACL(followACL);

        followActivity.saveInBackground(completion);
        PAPCache.getSharedCache().setUserFollowStatus(user, true);
    }

    public static void followUserEventually(PAPUser user, PFSaveCallback completion) {
        if (user.getObjectId().equals(PAPUser.getCurrentUser().getObjectId())) {
            return;
        }

        PAPActivity followActivity = PFObject.create(PAPActivity.class);
        followActivity.setFromUser(PAPUser.getCurrentUser());
        followActivity.setToUser(user);
        followActivity.setType(PAPActivityType.FOLLOW);

        PFACL followACL = new PFACL(PAPUser.getCurrentUser());
        followACL.setPublicReadAccess(true);
        followActivity.setACL(followACL);

        followActivity.saveEventually(completion);
        PAPCache.getSharedCache().setUserFollowStatus(user, true);
    }

    public static void followUsersEventually(List<PAPUser> users, PFSaveCallback completion) {
        for (PAPUser user : users) {
            followUserEventually(user, completion);
            PAPCache.getSharedCache().setUserFollowStatus(user, true);
        }
    }

    public static void unfollowUserEventually(PAPUser user) {
        PFQuery<PAPActivity> query = PFQuery.getQuery(PAPActivity.class);
        query.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        query.whereEqualTo(PAPActivity.TO_USER_KEY, user);
        query.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
        query.findInBackground(new PFFindCallback<PAPActivity>() {
            @Override
            public void done(NSArray<PAPActivity> activities, NSError error) {
                // While normally there should only be one follow activity
                // returned, we can't guarantee that.
                if (error == null) {
                    for (PAPActivity activity : activities) {
                        activity.deleteEventually();
                    }
                }
            }
        });
        PAPCache.getSharedCache().setUserFollowStatus(user, false);
    }

    public static void unfollowUsersEventually(List<PAPUser> users) {
        PFQuery<PAPActivity> query = PFQuery.getQuery(PAPActivity.class);
        query.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
        query.whereContainedIn(PAPActivity.TO_USER_KEY, new NSArray<PAPUser>(users));
        query.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
        query.findInBackground(new PFFindCallback<PAPActivity>() {
            @Override
            public void done(NSArray<PAPActivity> activities, NSError error) {
                for (PAPActivity activity : activities) {
                    activity.deleteEventually();
                }
            }
        });
        for (PAPUser user : users) {
            PAPCache.getSharedCache().setUserFollowStatus(user, false);
        }
    }

    public static PFQuery<PAPActivity> queryActivities(PAPPhoto photo, PFCachePolicy cachePolicy) {
        PFQuery<PAPActivity> queryLikes = PFQuery.getQuery(PAPActivity.class);
        queryLikes.whereEqualTo(PAPActivity.PHOTO_KEY, photo);
        queryLikes.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.LIKE.getKey());

        PFQuery<PAPActivity> queryComments = PFQuery.getQuery(PAPActivity.class);
        queryComments.whereEqualTo(PAPActivity.PHOTO_KEY, photo);
        queryComments.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.COMMENT.getKey());

        PFQuery<PAPActivity> query = PFQuery.or(new NSArray<PFQuery<?>>(queryLikes, queryComments));
        query.setCachePolicy(cachePolicy);
        query.include(PAPActivity.FROM_USER_KEY);
        query.include(PAPActivity.PHOTO_KEY);

        return query;
    }

    public static void drawSideAndBottomDropShadow(CGRect rect, CGContext context) {
        // Push the context
        context.saveGState();

        // Set the clipping path to remove the rect drawn by drawing the shadow
        CGRect boundingRect = context.getClipBoundingBox();
        context.addRect(boundingRect);
        context.addRect(rect);
        context.clip();
        // Also clip the top and bottom
        context.clipToRect(new CGRect(rect.getOrigin().getX() - 10, rect.getOrigin().getY(),
                rect.getSize().getWidth() + 20, rect.getSize().getHeight() + 10));

        // Draw shadow
        UIColor.black().setFill();
        context.setShadow(CGSize.Zero(), 7);
        context.fillRect(new CGRect(rect.getOrigin().getX(), rect.getOrigin().getY() - 5, rect.getSize().getWidth(),
                rect.getSize().getHeight() + 5));

        // Save context
        context.restoreGState();
    }

    public static void drawSideAndTopDropShadow(CGRect rect, CGContext context) {
        // Push the context
        context.saveGState();

        // Set the clipping path to remove the rect drawn by drawing the shadow
        CGRect boundingRect = context.getClipBoundingBox();
        context.addRect(boundingRect);
        context.addRect(rect);
        context.clip();
        // Also clip the top and bottom
        context.clipToRect(new CGRect(rect.getOrigin().getX() - 10, rect.getOrigin().getY() - 10, rect.getSize()
                .getWidth() + 20, rect.getSize().getHeight() + 10));

        // Draw shadow
        UIColor.black().setFill();
        context.setShadow(CGSize.Zero(), 7);
        context.fillRect(new CGRect(rect.getOrigin().getX(), rect.getOrigin().getY(), rect.getSize().getWidth(), rect
                .getSize().getHeight() + 10));

        // Save context
        context.restoreGState();
    }

    public static void drawSideDropShadow(CGRect rect, CGContext context) {
        // Push the context
        context.saveGState();

        // Set the clipping path to remove the rect drawn by drawing the shadow
        CGRect boundingRect = context.getClipBoundingBox();
        context.addRect(boundingRect);
        context.addRect(rect);
        context.clip();
        // Also clip the top and bottom
        context.clipToRect(new CGRect(rect.getOrigin().getX() - 10, rect.getOrigin().getY(),
                rect.getSize().getWidth() + 20, rect.getSize().getHeight()));

        // Draw shadow
        UIColor.black().setFill();
        context.setShadow(CGSize.Zero(), 7);
        context.fillRect(new CGRect(rect.getOrigin().getX(), rect.getOrigin().getY() - 5, rect.getSize().getWidth(),
                rect.getSize().getHeight() + 10));

        // Save context
        context.restoreGState();
    }
}
