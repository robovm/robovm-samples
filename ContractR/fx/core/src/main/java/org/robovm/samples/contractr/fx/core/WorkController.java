/*
 * Copyright (C) 2014 Trillian Mobile AB
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
package org.robovm.samples.contractr.fx.core;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Window;

import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

/**
 * 
 */
public class WorkController extends AbstractController {
    private TaskModel taskModel;
    private ScheduledExecutorService executor;

    @FXML
    private Button startStopButton;
    @FXML
    private Label currentTaskLabel;
    @FXML
    private Label timeElapsedLabel;

    private SelectTaskController selectTaskController;
    
    private boolean showing;

    @Override
    public void beforeShow() {
        showing = true;
        updateUIComponents();
        tick();
    }

    @Override
    public void afterHide() {
        showing = false;
    }

    public void init(TaskModel taskModel, SelectTaskController selectTaskController) {
        this.taskModel = Objects.requireNonNull(taskModel, "taskModel");
        this.selectTaskController = Objects.requireNonNull(selectTaskController, "selectTaskController");
        this.executor = Executors.newSingleThreadScheduledExecutor();

        startStopButton.setOnAction(this::startStopClicked);
    }

    private void startStopClicked(ActionEvent e) {
        Task workingTask = taskModel.getWorkingTask();
        if (workingTask == null) {
            Window window = startStopButton.getScene().getWindow();
            selectTaskController.select(window, this::start);
        } else {
            stop();
        }
    }

    private void updateUIComponents() {
        Task task = taskModel.getWorkingTask();
        String startStopTitle = null;
        String currentTaskText = null;
        if (task == null) {
            startStopTitle = "Start Work";
            currentTaskText = "None";
            startStopButton.getStyleClass().clear();
        } else {
            startStopTitle = "Stop Work";
            currentTaskText = task.getClient().getName() + " - " + task.getTitle();
            startStopButton.getStyleClass().add("started");
        }
        startStopButton.setText(startStopTitle);
        currentTaskLabel.setText(currentTaskText);
    }

    private void start(Task task) {
        taskModel.startWork(task);
        updateUIComponents();
        tick();
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
            timeElapsedLabel.setText(task.getTimeElapsed());
            executor.schedule(() -> Platform.runLater(this::tick), 1, TimeUnit.SECONDS);
        } else {
            timeElapsedLabel.setText("00:00:00");
        }
    }
}
