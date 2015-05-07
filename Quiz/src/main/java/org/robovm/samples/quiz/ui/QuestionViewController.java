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

import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.foundation.NSIndexPath;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.NSStringDrawingOptions;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIStoryboardSegue;
import org.robovm.apple.uikit.UITableView;
import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.samples.quiz.model.Quiz;

@CustomClass("QuestionViewController")
public class QuestionViewController extends UITableViewController {
    /**
     * The Quiz to source the question from.
     */
    private Quiz currentQuiz;

    /**
     * Index of the Question in the Quiz to display.
     */
    private int questionIndex;
    private boolean isLastQuestion;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Prevent the user from returning to the previous question.
        getNavigationItem().setHidesBackButton(true);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        setTitle(String.format("Question %d", questionIndex + 1));
        isLastQuestion = questionIndex == currentQuiz.getTotalQuestions() - 1;
    }

    /**
     * Unwinds from the ResultsViewController back to the first
     * QuestionViewController when the user taps the 'Start Over' button.
     * 
     * This is an unwind action. Note that the sender parameter is a
     * 'UIStoryboardSegue'. Like all unwind actions, this method is invoked
     * early in the unwind process, before the visual transition. Note that the
     * receiver of this method is the destinationViewController of the segue.
     * Your view controller should use this callback to update its UI before it
     * is redisplayed.
     * 
     * @param sender
     */
    @IBAction
    private void exitToQuizStart(UIStoryboardSegue sender) {
        // The user has restarted the quiz.
        currentQuiz.resetQuiz();
    }

    /**
     * We must disambiguate which QuestionViewController should handle the
     * exitToQuizStart: action as there will be several QuestionViewController
     * instances on the navigation stack when the unwind segue is triggered. By
     * default, a UINavigationController (or QuizContainerViewController)
     * searches its viewControllers array in reverse. Without overriding this
     * method, the QuestionViewController directly preceding the results screen
     * would be selected as the destination of the unwind segue.
     */
    @Override
    public boolean canPerformUnwind(Selector action, UIViewController fromViewController, NSObject sender) {
        // Always check if the view controller implements the unwind action by
        // calling the super's implementation.
        if (super.canPerformUnwind(action, fromViewController, sender)) {
            // The first QuestionViewController in the navigation stack should
            // handle the unwind action.
            return this == ((UINavigationController) getParentViewController()).getViewControllers().get(0);
        }

        return false;
    }

    /**
     * Overriding this method allows a view controller to block the execution of
     * a triggered segue.
     */
    @Override
    public boolean shouldPerformSegue(String identifier, NSObject sender) {
        if (identifier.equals("NextQuestion")) {
            return !isLastQuestion;
        }

        return super.shouldPerformSegue(identifier, sender);
    }

    @Override
    public void prepareForSegue(UIStoryboardSegue segue, NSObject sender) {
        if (segue.getIdentifier().equals("NextQuestion")) {
            currentQuiz.answerQuestion(questionIndex, (int)
                    getTableView().getIndexPathForSelectedRow().getRow() + 1);
            QuestionViewController nextQuestionVC = (QuestionViewController) segue.getDestinationViewController();
            nextQuestionVC.setQuiz(currentQuiz);
            nextQuestionVC.setQuestionIndex(questionIndex + 1);
        } else if (segue.getIdentifier().equals("ResultScreen")) {
            ResultsViewController resultVC = (ResultsViewController) segue.getDestinationViewController();
            resultVC.setQuiz(currentQuiz);
        }
    }

    @Override
    public long getNumberOfSections(UITableView tableView) {
        return 2;
    }

    @Override
    public long getNumberOfRowsInSection(UITableView tableView, long section) {
        if (section == 0) {
            return 1;
        } else {
            return currentQuiz.getQuestion(questionIndex).getTotalAnswers();
        }
    }

    /**
     * This delegate method is implemented because the height of the cell
     * displaying the question will need to change depending on the height
     * required to display the question text. As the device rotates this will
     * change.
     */
    @Override
    public double getHeightForRow(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.getSection() == 0) {
            UITableViewCell questionCell = tableView.dequeueReusableCell("QuestionCell");
            UILabel questionLabel = questionCell.getTextLabel();

            // The width must be constrained to the width of the table view
            // minus the
            // left and right margin of a grouped style cell.
            // Unfortunately, there is no way to lookup exactly what that margin
            // is, so
            // it must be hardcoded.
            // The height is left unconstrained.
            CGSize constrainingSize = new CGSize(tableView.getBounds().getSize().getWidth() - 40 * 2, Float.MAX_VALUE);

            NSAttributedString string = new NSAttributedString(questionLabel.getText(),
                    new NSAttributedStringAttributes().setFont(questionLabel.getFont()));
            return string.getBoundingRect(constrainingSize, NSStringDrawingOptions.UsesLineFragmentOrigin, null)
                    .getHeight() + 22;
        }
        return tableView.getRowHeight();
    }

    @Override
    public UITableViewCell getCellForRow(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.getSection() == 0) {
            UITableViewCell cell = tableView.dequeueReusableCell("QuestionCell");
            cell.getTextLabel().setText(currentQuiz.getQuestion(questionIndex).getQuestionText());
            return cell;
        } else {
            UITableViewCell cell = tableView.dequeueReusableCell("AnswerCell");
            cell.getTextLabel().setText(currentQuiz.getQuestion(questionIndex).getAnswer((int) indexPath.getRow()));
            return cell;
        }
    }

    @Override
    public void didSelectRow(UITableView tableView, NSIndexPath indexPath) {
        tableView.deselectRow(indexPath, false);

        // This method is called regardless of the value returned by
        // shouldPerformSegue. Therefore, check if this view
        // controller is displaying the final question and advance to the
        // results
        // screen only if it is.
        if (indexPath.getSection() == 1 && isLastQuestion) {
            currentQuiz.answerQuestion(questionIndex, (int) indexPath.getRow() + 1);

            // Trigger the segue leading to the results screen.
            performSegue("ResultScreen", this);
        }
    }

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }
}
