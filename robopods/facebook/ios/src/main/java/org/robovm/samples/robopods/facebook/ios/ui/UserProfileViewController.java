package org.robovm.samples.robopods.facebook.ios.ui;

import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.pods.facebook.core.FBSDKProfile;
import org.robovm.pods.facebook.core.FBSDKProfileChangeNotification;
import org.robovm.samples.robopods.facebook.ios.FacebookHandler;

@CustomClass("UserProfileViewController")
public class UserProfileViewController extends UITableViewController {

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        FBSDKProfile.Notifications.observeCurrentProfileDidChange(new VoidBlock1<FBSDKProfileChangeNotification>() {
            @Override
            public void invoke(FBSDKProfileChangeNotification notification) {
                if (notification.getNewProfile() != null) {
                    getTableView().reloadData();
                }
            }
        });
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        getTableView().reloadData();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        FBSDKProfile profile = FacebookHandler.getInstance().getCurrentProfile();

        UITableViewCell cell = super.getCellForRow(tableView, indexPath);
        switch (indexPath.getRow()) {
        case 0:
            cell.getDetailTextLabel().setText(profile.getUserID());
            break;
        case 1:
            cell.getDetailTextLabel().setText(profile.getFirstName());
            break;
        case 2:
            cell.getDetailTextLabel().setText(profile.getMiddleName());
            break;
        case 3:
            cell.getDetailTextLabel().setText(profile.getLastName());
            break;
        case 4:
            cell.getDetailTextLabel().setText(profile.getName());
            break;
        case 5:
            cell.getDetailTextLabel().setText(profile.getLinkURL().getAbsoluteString());
            break;
        default:
            break;
        }
        return cell;
    }
}
