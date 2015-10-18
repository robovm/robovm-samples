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
 * Portions of this code is based on Apple Inc's UnwindSegue sample (v1.0)
 * which is copyright (C) 2013 Apple Inc.
 */
package org.robovm.samples.quiz.ui;

import java.util.List;

import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSStringDrawingOptions;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewCellEditingStyle;
import org.robovm.apple.uikit.UITableViewDataSource;
import org.robovm.apple.uikit.UITableViewDelegateAdapter;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.quiz.model.Quiz;

@CustomClass("MainMenuViewController")
public class MainMenuViewController extends UIViewController implements UITableViewDataSource {
    private float highScore;
    @IBOutlet
    private UITableView tableView;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // No highscrore when the view first loads.
        highScore = -1;

        tableView.setDelegate(new UITableViewDelegateAdapter() {
            /**
             * This delegate method is implemented because the height of the
             * instructions cell will need to change depending on the height
             * required to display the instruction text. As the device rotates
             * this will change.
             */
            @Override
            public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
                if (indexPath.getSection() == 0) {
                    // Instructions cell
                    UITableViewCell instructionsCell = tableView.dequeueReusableCell("Instructions");
                    UILabel instructionsLabel = instructionsCell.getTextLabel();

                    // The width must be constrained to the width of the table
                    // view
                    // minus the
                    // left and right margin of a grouped style cell.
                    // Unfortunately, there is no way to lookup exactly what
                    // that margin
                    // is, so
                    // it must be hardcoded.
                    // The height is left unconstrained.
                    CGSize constrainingSize = new CGSize(tableView.getBounds().getSize().getWidth() - 40 * 2,
                            Float.MAX_VALUE);

                    NSAttributedString string = new NSAttributedString(instructionsLabel.getText(),
                            new NSAttributedStringAttributes().setFont(instructionsLabel.getFont()));
                    return string
                            .getBoundingRect(constrainingSize, NSStringDrawingOptions.UsesLineFragmentOrigin, null)
                            .getHeight();
                }
                return tableView.getRowHeight();
            }
        });
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        tableView.reloadData();
    }

    /**
     * This method will be called when the 'Begin' button is tapped.
     */
    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        // Create a new Quiz from the questions.xml file in the bundle
        // resources.
        String questionsPath = NSBundle.getMainBundle().findResourcePath("questions", "xml");
        Quiz newQuiz = new Quiz(questionsPath);

        // Set the newQuiz as the currentQuiz of the destination view
        // controller.
        QuestionViewController firstQuestionVC = (QuestionViewController) ((UINavigationController) segue
                .getDestinationViewController())
                .getViewControllers().get(0);
        firstQuestionVC.setQuiz(newQuiz);
    }

    /**
     * Unwinds from the ResultsViewController back to the MainMenuViewController
     * when the user taps the 'Return to the Home Screen' button.
     * 
     * This is an unwind action. Note that the sender parameter is a
     * 'UIStoryboardSegue*' instead of the usual 'id'. Like all unwind actions,
     * this method is invoked early in the unwind process, before the visual
     * transition. Note that the receiver of this method is the
     * destinationViewController of the segue. Your view controller should use
     * this callback to retrieve information from the sourceViewController. Used
     * properly, this method can replace existing delegation techniques for
     * passing information from a detail view controller to a previous view
     * controller in the navigation hierarchy.
     * 
     * @param unwindSegue
     */
    @IBAction
    public void exitToHomeScreen(UIStoryboardSegue unwindSegue) {
        // Retrieve the score from the ResultsViewController and update the high
        // score.
        ResultsViewController resultVC = (ResultsViewController) unwindSegue.getSourceViewController();
        this.highScore = Math.max(resultVC.getQuiz().getPercentageScore(), highScore);
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        // If there is a highscore to display, there are three sections. Else,
        // there are two sections.
        return highScore > -1 ? 3 : 2;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        // There is only one row per section.
        return 1;
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        UITableViewCell cell;

        if (indexPath.getSection() == 0) {
            cell = tableView.dequeueReusableCell("Instructions");
        } else if (indexPath.getSection() == 1 && tableView.getNumberOfSections() == 3) {
            // If there are three setions being displayed, the second row shows
            // the
            // highscore.
            cell = tableView.dequeueReusableCell("Score");
            cell.getTextLabel().setText(String.format("Your best recorded score is %.0f%%", highScore * 100));
        } else {
            // Last section is always the Begin button.
            cell = tableView.dequeueReusableCell("Begin");
        }
        return cell;
    }

    @Override
    public String getTitleForHeader(UITableView tableView, long section) {
        return null;
    }

    @Override
    public String getTitleForFooter(UITableView tableView, long section) {
        return null;
    }

    @Override
    public boolean canEditRow(UITableView tableView, NSIndexPath indexPath) {
        return false;
    }

    @Override
    public boolean canMoveRow(UITableView tableView, NSIndexPath indexPath) {
        return false;
    }

    @Override
    public List<String> getSectionIndexTitles(UITableView tableView) {
        return null;
    }

    @Override
    public long getSectionForSectionIndexTitle(UITableView tableView, String title, long index) {
        return 0;
    }

    @Override
    public void commitEditingStyleForRow(UITableView tableView, UITableViewCellEditingStyle editingStyle,
            NSIndexPath indexPath) {}

    @Override
    public void moveRow(UITableView tableView, NSIndexPath sourceIndexPath, NSIndexPath destinationIndexPath) {}
}
