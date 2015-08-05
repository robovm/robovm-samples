package org.robovm.samples.robopods.parse.anypic.ios.ui.controllers.home;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.NSTextAlignment;
import org.robovm.apple.uikit.UIActivityIndicatorView;
import org.robovm.apple.uikit.UIActivityIndicatorViewStyle;
import org.robovm.apple.uikit.UIBarButtonItem;
import org.robovm.apple.uikit.UIBarButtonItemStyle;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCellSelectionStyle;
import org.robovm.apple.uikit.UITableViewCellStyle;
import org.robovm.apple.uikit.UITableViewStyle;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewContentMode;
import org.robovm.pods.parse.PFCachePolicy;
import org.robovm.pods.parse.PFCountCallback;
import org.robovm.pods.parse.PFError;
import org.robovm.pods.parse.PFErrorCode;
import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFQuery;
import org.robovm.pods.parse.PFSaveCallback;
import org.robovm.pods.parse.ui.PFImageDownloadCallback;
import org.robovm.pods.parse.ui.PFImageView;
import org.robovm.pods.parse.ui.PFTableViewCell;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivity;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPActivityType;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPCache;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPPhoto;
import org.robovm.samples.robopods.parse.anypic.ios.model.PAPUser;
import org.robovm.samples.robopods.parse.anypic.ios.ui.views.tablecells.PAPLoadMoreCell;
import org.robovm.samples.robopods.parse.anypic.ios.util.Log;
import org.robovm.samples.robopods.parse.anypic.ios.util.PAPUtility;
import org.robovm.samples.robopods.parse.anypic.ios.util.UIImageEffects;

public class PAPAccountViewController extends PAPPhotoTimelineViewController {
    private PAPUser user;
    private UIView headerView;

    public PAPAccountViewController(UITableViewStyle style) {
        super(style);
    }

    public PAPAccountViewController(PAPUser user) {
        super(UITableViewStyle.Plain);
        if (user == null) {
            throw new NullPointerException("user");
        }
        this.user = user;
    }

    public void setUser(PAPUser user) {
        this.user = user;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        if (user == null) {
            user = PAPUser.getCurrentUser();
            try {
                PAPUser.getCurrentUser().fetchIfNeeded();
            } catch (NSErrorException e) {
                e.printStackTrace();
            }
        }

        getNavigationItem().setTitleView(new UIImageView(UIImage.create("LogoNavigationBar")));

        if (getNavigationController().getViewControllers().first() == this) {
            UIBarButtonItem dismissLeftBarButtonItem = new UIBarButtonItem("Back", UIBarButtonItemStyle.Plain,
                    new UIBarButtonItem.OnClickListener() {
                        @Override
                        public void onClick(UIBarButtonItem barButtonItem) {
                            getNavigationController().dismissViewController(true, null);
                        }
                    });
            getNavigationItem().setLeftBarButtonItem(dismissLeftBarButtonItem);
        } else {
            getNavigationItem().setLeftBarButtonItem(null);
        }

        headerView = new UIView(new CGRect(0, 0, getTableView().getBounds().getSize().getWidth(), 222));
        // should be clear, this will be the container for our avatar, photo
        // count, follower count, following count, and so on
        headerView.setBackgroundColor(UIColor.clear());

        UIView texturedBackgroundView = new UIView(getView().getBounds());
        texturedBackgroundView.setBackgroundColor(UIColor.black());
        getTableView().setBackgroundView(texturedBackgroundView);

        final UIView profilePictureBackgroundView = new UIView(new CGRect(94, 38, 132, 132));
        profilePictureBackgroundView.setBackgroundColor(UIColor.darkGray());
        profilePictureBackgroundView.setAlpha(0);
        CALayer layer = profilePictureBackgroundView.getLayer();
        layer.setCornerRadius(66);
        layer.setMasksToBounds(true);
        headerView.addSubview(profilePictureBackgroundView);

        final PFImageView profilePictureImageView = new PFImageView(new CGRect(94, 38, 132, 132));
        headerView.addSubview(profilePictureImageView);
        profilePictureImageView.setContentMode(UIViewContentMode.ScaleAspectFill);
        layer = profilePictureImageView.getLayer();
        layer.setCornerRadius(66);
        layer.setMasksToBounds(true);
        profilePictureImageView.setAlpha(0);

        if (PAPUtility.userHasProfilePictures(user)) {
            PFFile imageFile = user.getProfilePicMedium();
            profilePictureImageView.setFile(imageFile);
            profilePictureImageView.loadInBackground(new PFImageDownloadCallback() {
                @Override
                public void done(UIImage image, NSError error) {
                    if (error == null) {
                        UIView.animate(0.2, new Runnable() {
                            @Override
                            public void run() {
                                profilePictureBackgroundView.setAlpha(1);
                                profilePictureImageView.setAlpha(1);
                            }
                        });

                        final UIImageView backgroundImageView = new UIImageView(UIImageEffects.applyDarkEffect(image));
                        backgroundImageView.setFrame(getTableView().getBackgroundView().getBounds());
                        backgroundImageView.setAlpha(0);
                        getTableView().getBackgroundView().addSubview(backgroundImageView);

                        UIView.animate(0.2, new Runnable() {
                            @Override
                            public void run() {
                                backgroundImageView.setAlpha(1);
                            }
                        });
                    }
                }
            });
        } else {
            profilePictureImageView.setImage(PAPUtility.getDefaultProfilePicture());
            UIView.animate(0.2, new Runnable() {
                @Override
                public void run() {
                    profilePictureBackgroundView.setAlpha(1);
                    profilePictureImageView.setAlpha(1);
                }
            });

            final UIImageView backgroundImageView = new UIImageView(UIImageEffects.applyDarkEffect(PAPUtility
                    .getDefaultProfilePicture()));
            backgroundImageView.setFrame(getTableView().getBackgroundView().getBounds());
            backgroundImageView.setAlpha(0);
            getTableView().getBackgroundView().addSubview(backgroundImageView);

            UIView.animate(0.2, new Runnable() {
                @Override
                public void run() {
                    backgroundImageView.setAlpha(1);
                }
            });
        }

        UIImageView photoCountIconImageView = new UIImageView(UIImage.create("IconPics"));
        photoCountIconImageView.setFrame(new CGRect(26, 50, 45, 37));
        headerView.addSubview(photoCountIconImageView);

        final UILabel photoCountLabel = new UILabel(new CGRect(0, 94, 92, 22));
        photoCountLabel.setTextAlignment(NSTextAlignment.Center);
        photoCountLabel.setBackgroundColor(UIColor.clear());
        photoCountLabel.setTextColor(UIColor.white());
        photoCountLabel.setShadowColor(UIColor.fromWhiteAlpha(0, 0.3));
        photoCountLabel.setShadowOffset(new CGSize(0, -1));
        photoCountLabel.setFont(UIFont.getBoldSystemFont(14));
        headerView.addSubview(photoCountLabel);

        UIImageView followersIconImageView = new UIImageView(UIImage.create("IconFollowers"));
        followersIconImageView.setFrame(new CGRect(247, 50, 52, 37));
        headerView.addSubview(followersIconImageView);

        final UILabel followerCountLabel = new UILabel(new CGRect(226, 94,
                headerView.getBounds().getSize().getWidth() - 226, 16));
        followerCountLabel.setTextAlignment(NSTextAlignment.Center);
        followerCountLabel.setBackgroundColor(UIColor.clear());
        followerCountLabel.setTextColor(UIColor.white());
        followerCountLabel.setShadowColor(UIColor.fromWhiteAlpha(0, 0.3));
        followerCountLabel.setShadowOffset(new CGSize(0, -1));
        followerCountLabel.setFont(UIFont.getBoldSystemFont(12));
        headerView.addSubview(followerCountLabel);

        final UILabel followingCountLabel = new UILabel(new CGRect(226, 110,
                headerView.getBounds().getSize().getWidth() - 226, 16));
        followingCountLabel.setTextAlignment(NSTextAlignment.Center);
        followingCountLabel.setBackgroundColor(UIColor.clear());
        followingCountLabel.setTextColor(UIColor.white());
        followingCountLabel.setShadowColor(UIColor.fromWhiteAlpha(0, 0.3));
        followingCountLabel.setShadowOffset(new CGSize(0, -1));
        followingCountLabel.setFont(UIFont.getBoldSystemFont(12));
        headerView.addSubview(followingCountLabel);

        UILabel userDisplayNameLabel = new UILabel(new CGRect(0, 176, headerView.getBounds().getSize().getWidth(), 22));
        userDisplayNameLabel.setTextAlignment(NSTextAlignment.Center);
        userDisplayNameLabel.setBackgroundColor(UIColor.clear());
        userDisplayNameLabel.setTextColor(UIColor.white());
        userDisplayNameLabel.setShadowColor(UIColor.fromWhiteAlpha(0, 0.3));
        userDisplayNameLabel.setShadowOffset(new CGSize(0, -1));
        userDisplayNameLabel.setText(user.getDisplayName());
        userDisplayNameLabel.setFont(UIFont.getBoldSystemFont(18));
        headerView.addSubview(userDisplayNameLabel);

        photoCountLabel.setText("0 photos");

        PFQuery<PAPPhoto> queryPhotoCount = PFQuery.getQuery(PAPPhoto.class);
        queryPhotoCount.whereEqualTo(PAPPhoto.USER_KEY, user);
        queryPhotoCount.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        queryPhotoCount.countInBackground(new PFCountCallback() {
            @Override
            public void done(int count, NSError error) {
                if (error == null) {
                    photoCountLabel.setText(String.format("%d photo%s", count, count == 1 ? "" : "s"));
                    PAPCache.getSharedCache().setUserPhotoCount(user, count);
                }
            }
        });

        followerCountLabel.setText("0 followers");

        PFQuery<PAPActivity> queryFollowerCount = PFQuery.getQuery(PAPActivity.class);
        queryFollowerCount.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
        queryFollowerCount.whereEqualTo(PAPActivity.TO_USER_KEY, user);
        queryFollowerCount.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        queryFollowerCount.countInBackground(new PFCountCallback() {
            @Override
            public void done(int count, NSError error) {
                if (error == null) {
                    followerCountLabel.setText(String.format("%d follower%s", count, count == 1 ? "" : "s"));
                }
            }
        });

        followingCountLabel.setText("0 following");

        PFQuery<PAPActivity> queryFollowingCount = PFQuery.getQuery(PAPActivity.class);
        queryFollowingCount.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
        queryFollowingCount.whereEqualTo(PAPActivity.FROM_USER_KEY, user);
        queryFollowingCount.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        queryFollowingCount.countInBackground(new PFCountCallback() {
            @Override
            public void done(int count, NSError error) {
                if (error == null) {
                    followingCountLabel.setText(String.format("%d following", count));
                }
            }
        });

        if (!user.getObjectId().equals(PAPUser.getCurrentUser().getObjectId())) {
            UIActivityIndicatorView loadingActivityIndicatorView = new UIActivityIndicatorView(
                    UIActivityIndicatorViewStyle.White);
            loadingActivityIndicatorView.startAnimating();
            getNavigationItem().setRightBarButtonItem(new UIBarButtonItem(loadingActivityIndicatorView));

            // check if the currentUser is following this user
            PFQuery<PAPActivity> queryIsFollowing = PFQuery.getQuery(PAPActivity.class);
            queryIsFollowing.whereEqualTo(PAPActivity.TYPE_KEY, PAPActivityType.FOLLOW.getKey());
            queryIsFollowing.whereEqualTo(PAPActivity.TO_USER_KEY, user);
            queryIsFollowing.whereEqualTo(PAPActivity.FROM_USER_KEY, PAPUser.getCurrentUser());
            queryIsFollowing.setCachePolicy(PFCachePolicy.CacheThenNetwork);
            queryIsFollowing.countInBackground(new PFCountCallback() {
                @Override
                public void done(int count, NSError error) {
                    if (error != null && error instanceof PFError
                            && ((PFError) error).getErrorCode() == PFErrorCode.CacheMiss) {
                        Log.e("Couldn't determine follow relationship: %s", error);
                        getNavigationItem().setRightBarButtonItem(null);
                    } else {
                        if (count == 0) {
                            configureFollowButton();
                        } else {
                            configureUnfollowButton();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void didLoadObjects(NSError error) {
        super.didLoadObjects(error);

        getTableView().setTableHeaderView(headerView);
    }

    @Override
    public PFQuery<PAPPhoto> getQuery() {
        if (user == null) {
            PFQuery<PAPPhoto> query = PFQuery.getQuery(PAPPhoto.class);
            query.setLimit(0);
            return query;
        }

        PFQuery<PAPPhoto> query = PFQuery.getQuery(PAPPhoto.class);
        query.setCachePolicy(PFCachePolicy.NetworkOnly);
        if (getObjects().size() == 0) {
            query.setCachePolicy(PFCachePolicy.CacheThenNetwork);
        }

        query.whereEqualTo(PAPPhoto.USER_KEY, user);
        query.orderByDescending("createdAt");
        query.include(PAPPhoto.USER_KEY);

        return query;
    }

    @Override
    public PFTableViewCell getCellForNextPage(UITableView tableView, NSIndexPath indexPath) {
        final String loadMoreCellIdentifier = "LoadMoreCell";

        PAPLoadMoreCell cell = (PAPLoadMoreCell) tableView.dequeueReusableCell(loadMoreCellIdentifier);
        if (cell == null) {
            cell = new PAPLoadMoreCell(UITableViewCellStyle.Default, loadMoreCellIdentifier);
            cell.setSelectionStyle(UITableViewCellSelectionStyle.None);
            cell.getSeparatorImageTop().setImage(UIImage.create("SeparatorTimelineDark"));
            cell.setHideSeparatorBottom(true);
            cell.getMainView().setBackgroundColor(UIColor.clear());
        }
        return cell;
    }

    private final UIBarButtonItem.OnClickListener followButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
            UIActivityIndicatorView loadingActivityIndicatorView = new UIActivityIndicatorView(
                    UIActivityIndicatorViewStyle.White);
            loadingActivityIndicatorView.startAnimating();
            getNavigationItem().setRightBarButtonItem(new UIBarButtonItem(loadingActivityIndicatorView));

            configureUnfollowButton();

            PAPUtility.followUserEventually(user, new PFSaveCallback() {
                @Override
                public void done(boolean success, NSError error) {
                    if (error != null) {
                        configureFollowButton();
                    }
                }
            });
        }
    };

    private final UIBarButtonItem.OnClickListener unfollowButtonAction = new UIBarButtonItem.OnClickListener() {
        @Override
        public void onClick(UIBarButtonItem barButtonItem) {
            UIActivityIndicatorView loadingActivityIndicatorView = new UIActivityIndicatorView(
                    UIActivityIndicatorViewStyle.White);
            loadingActivityIndicatorView.startAnimating();
            getNavigationItem().setRightBarButtonItem(new UIBarButtonItem(loadingActivityIndicatorView));

            configureFollowButton();

            PAPUtility.unfollowUserEventually(user);
        }
    };

    private void configureFollowButton() {
        getNavigationItem().setRightBarButtonItem(
                new UIBarButtonItem("Follow", UIBarButtonItemStyle.Plain, followButtonAction));
        PAPCache.getSharedCache().setUserFollowStatus(user, false);
    }

    private void configureUnfollowButton() {
        getNavigationItem().setRightBarButtonItem(
                new UIBarButtonItem("Unfollow", UIBarButtonItemStyle.Plain, unfollowButtonAction));
        PAPCache.getSharedCache().setUserFollowStatus(user, true);
    }
}
