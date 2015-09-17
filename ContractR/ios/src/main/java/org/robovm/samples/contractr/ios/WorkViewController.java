/*
 * Copyright (C) 2014 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.contractr.ios;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.robovm.apple.coreanimation.CALayer;
import org.robovm.apple.dispatch.DispatchQueue;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIButtonType;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIEdgeInsets;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UINavigationController;
import org.robovm.apple.uikit.UIView;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

public class WorkViewController extends UIViewController {

    private static final UIColor START_COLOR = ContractRApp.HIGHLIGHT_COLOR;
    private static final UIColor STOP_COLOR =
            UIColor.fromRGBA(1.0, 0, 0, 1.0);

    private final TaskModel taskModel;
    private SelectTaskViewController selectTaskViewController;
    private final UINavigationController selectTaskNavigationController;

    private UIButton startStopButton;
    private UILabel currentTaskLabel;
    private UILabel earnedLabel;
    private UILabel timerLabel;
    private boolean showing = true;

    public WorkViewController(ClientModel clientModel, TaskModel taskModel) {
        this.taskModel = taskModel;
        this.selectTaskViewController = new SelectTaskViewController(clientModel, taskModel,
                () -> start(this.selectTaskViewController.getSelectedTask()));
        this.selectTaskNavigationController = new UINavigationController(this.selectTaskViewController);
        this.selectTaskViewController.getNavigationItem().setTitle("Task to work on");
    }

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        UIView rootView = getView();
        rootView.setBackgroundColor(UIColor.white());

        startStopButton = new UIButton(UIButtonType.RoundedRect);
        startStopButton.setContentEdgeInsets(new UIEdgeInsets(12, 30, 12, 30));
        startStopButton.getTitleLabel().setFont(UIFont.getSystemFont(25));
        startStopButton.addOnTouchUpInsideListener((c, e) -> startStopClicked());
        startStopButton.setTranslatesAutoresizingMaskIntoConstraints(false);

        UILabel currentTaskTitleLabel = new UILabel();
        currentTaskTitleLabel.setText("Current Task");
        currentTaskTitleLabel.setTranslatesAutoresizingMaskIntoConstraints(false);

        currentTaskLabel = new UILabel();
        currentTaskLabel.setText("None");
        currentTaskLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        currentTaskLabel.setFont(UIFont.getBoldSystemFont(25));

        UILabel timerTitleLabel = new UILabel();
        timerTitleLabel.setText("Time Elapsed");
        timerTitleLabel.setTranslatesAutoresizingMaskIntoConstraints(false);

        timerLabel = new UILabel();
        timerLabel.setText("00:00:00");
        timerLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        timerLabel.setFont(UIFont.getBoldSystemFont(25));

        UILabel earnedTitleLabel = new UILabel();
        earnedTitleLabel.setText("Amount Earned");
        earnedTitleLabel.setTranslatesAutoresizingMaskIntoConstraints(false);

        earnedLabel = new UILabel();
        earnedLabel.setText("0");
        earnedLabel.setTranslatesAutoresizingMaskIntoConstraints(false);
        earnedLabel.setFont(UIFont.getBoldSystemFont(25));

        rootView.addSubview(currentTaskTitleLabel);
        rootView.addSubview(currentTaskLabel);
        rootView.addSubview(timerTitleLabel);
        rootView.addSubview(timerLabel);
        rootView.addSubview(earnedTitleLabel);
        rootView.addSubview(earnedLabel);
        rootView.addSubview(timerLabel);
        rootView.addSubview(startStopButton);

        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(currentTaskTitleLabel, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(currentTaskTitleLabel, rootView, 0.4, -20));
        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(currentTaskLabel, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(currentTaskLabel, rootView, 0.4, 10));
        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(timerTitleLabel, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(timerTitleLabel, rootView, 0.8, -20));
        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(timerLabel, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(timerLabel, rootView, 0.8, 10));
        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(earnedTitleLabel, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(earnedTitleLabel, rootView, 1.2, -20));
        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(earnedLabel, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(earnedLabel, rootView, 1.2, 10));
        rootView.addConstraint(NSLayoutConstraintUtil.centerHorizontally(startStopButton, rootView, 1.0, 0));
        rootView.addConstraint(NSLayoutConstraintUtil.centerVertically(startStopButton, rootView, 1.6, -10));
    }

    @Override
    public void viewWillAppear(boolean animated) {
        super.viewWillAppear(animated);
        showing = true;
        updateUIComponents();
        tick();
    }

    @Override
    public void viewWillDisappear(boolean animated) {
        showing = false;
        super.viewWillDisappear(animated);
    }

    private void startStopClicked() {
        Task workingTask = taskModel.getWorkingTask();
        if (workingTask == null) {
            presentViewController(selectTaskNavigationController, true, null);
        } else {
            stop();
        }
    }

    private void updateUIComponents() {
        Task task = taskModel.getWorkingTask();
        UIColor startStopColor = null;
        String startStopTitle = null;
        String currentTaskText = null;
        if (task == null) {
            startStopTitle = "Start Work";
            startStopColor = START_COLOR;
            currentTaskText = "None";
        } else {
            startStopTitle = "Stop Work";
            startStopColor = STOP_COLOR;
            currentTaskText = task.getClient().getName() + " - " + task.getTitle();
        }
        startStopButton.setTitle(startStopTitle, UIControlState.Normal);
        startStopButton.setTitleColor(startStopColor, UIControlState.Normal);
        CALayer layer = startStopButton.getLayer();
        layer.setBorderWidth(1.0);
        layer.setBorderColor(startStopColor.getCGColor());
        currentTaskLabel.setText(currentTaskText);
    }

    private void start(Task task) {
        taskModel.startWork(task);
        updateUIComponents();
    }

    private void stop() {
        taskModel.stopWork();
        updateUIComponents();
        tick(); // Resets timer to 00:00:00
    }

    private void tick() {
        if (!showing) {
            return;
        }
        Task task = taskModel.getWorkingTask();
        if (task != null) {
            timerLabel.setText(task.getTimeElapsed());
            earnedLabel.setText(task.getAmountEarned(Locale.US));
            DispatchQueue.getMainQueue().after(1, TimeUnit.SECONDS, this::tick);
        } else {
            timerLabel.setText("00:00:00");
            earnedLabel.setText(NumberFormat.getCurrencyInstance(Locale.US).format(0));
        }
    }
}
