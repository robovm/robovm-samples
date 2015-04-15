package org.robovm.samples.quiz.ui;

import org.robovm.apple.uikit.UITableViewCell;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.quiz.model.Quiz;

@CustomClass("ResultsViewController")
public class ResultsViewController extends UITableViewController {
    private Quiz currentQuiz;
    /**
     * Table view cell that displays the user's percentage quiz score.
     */
    private UITableViewCell resultsCell;

    @IBOutlet
    private void setResultsCell(UITableViewCell resultsCell) {
        this.resultsCell = resultsCell;
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        getNavigationItem().setHidesBackButton(true);
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);

        resultsCell.getTextLabel().setText(
                String.format("You answered %d of %d questions correctly for a score of %.0f%%!",
                        currentQuiz.getCorrectlyAnsweredQuestions(),
                        currentQuiz.getTotalQuestions(), currentQuiz.getPercentageScore() * 100));
    }

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    public Quiz getQuiz() {
        return currentQuiz;
    }
}
