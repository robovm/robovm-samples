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
    @IBOutlet
    private UITableViewCell resultsCell;

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
