package org.robovm.samples.robopods.facebook.ios.ui;

import java.util.Arrays;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIAlertView;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.samples.robopods.facebook.ios.FacebookHandler;

@CustomClass("MainMenuViewController")
public class MainMenuViewController extends UITableViewController {

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.getSection() == 0) {
            loginAction();
        } else {
            switch (indexPath.getRow()) {
            case 2:
                publishFeedAction();
                break;
            default:
                break;
            }
        }
    }

    private void loginAction() {
        if (FacebookHandler.getInstance().isLoggedIn()) {
            FacebookHandler.getInstance().logOut();

            // Update UI.
            getTableView().reloadData();
        } else {
            FacebookHandler.getInstance().logIn(Arrays.asList("email", "user_friends"),
                    new FacebookHandler.LoginListener() {
                        @Override
                        public void onSuccess() {
                            // Update UI.
                            getTableView().reloadData();
                        }

                        @Override
                        public void onError(String message) {
                            FacebookHandler.getInstance().alertError("Error during login!", message);
                        }

                        @Override
                        public void onCancel() {
                            // User cancelled, so do nothing.
                        }
                    });
        }
    }

    private void publishFeedAction() {
        FacebookHandler.getInstance().publishFeed("RoboVM", "RoboPods Facebook iOS",
                "Hello World! This has been sent by RoboVM!!!", "http://www.robovm.com",
                "http://www.robovm.com/wp-content/uploads/2015/03/RoboVM-logo-wide.png",
                new FacebookHandler.RequestListener() {
                    @Override
                    public void onSuccess(NSObject result) {
                        UIAlertView alert = new UIAlertView("Success!", "Your message has been posted!", null, "OK");
                        alert.show();
                    }

                    @Override
                    public void onError(String message) {
                        FacebookHandler.getInstance().alertError("Error during feed!", message);
                    }

                    @Override
                    public void onCancel() {}
                });
    }

    @Override
    public boolean shouldPerformSegue(String identifier, NSObject sender) {
        return FacebookHandler.getInstance().isLoggedIn();
    }

    @Override
    public void viewWillAppear(boolean animated) {
        getTableView().reloadData();
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return FacebookHandler.getInstance().isLoggedIn() ? 2 : 1;
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell = super.getCellForRow(tableView, indexPath);
        if (indexPath.getSection() == 0) {
            cell.getTextLabel().setText(FacebookHandler.getInstance().isLoggedIn() ? "logout" : "login");
        }
        return cell;
    }
}
