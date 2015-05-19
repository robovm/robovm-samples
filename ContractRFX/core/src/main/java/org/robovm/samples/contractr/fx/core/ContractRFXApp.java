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
package org.robovm.samples.contractr.fx.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.TaskModel;

/**
 * 
 */
public abstract class ContractRFXApp extends Application {

    protected ClientModel clientModel;
    protected TaskModel taskModel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        BorderPane rootPane = new BorderPane();
        rootPane.setId("root");
        
        TabPane rootTabPane = new TabPane();
        rootTabPane.setSide(Side.BOTTOM);
        rootTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        rootPane.setCenter(rootTabPane);
        
        Controller[] controllers = new Controller[4];
        
        rootTabPane.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
            if (oldVal.intValue() >= 0 && controllers[oldVal.intValue()] != null) {
                controllers[oldVal.intValue()].afterHide();
            }
            if (controllers[newVal.intValue()] != null) {
                controllers[newVal.intValue()].beforeShow();
            }
        });
        
        FXMLLoader loader = null;

        loader = new FXMLLoader();
        loader.setLocation(ContractRFXApp.class.getResource("SelectTask.fxml"));
        loader.load();
        SelectTaskController selectTaskController = loader.getController();
        selectTaskController.init(clientModel, taskModel);

        loader = new FXMLLoader();
        loader.setLocation(ContractRFXApp.class.getResource("Work.fxml"));
        Parent workPane = (Parent) loader.load();
        WorkController workController = loader.getController();
        workController.init(taskModel, selectTaskController);
        controllers[0] = workController;
        
        loader = new FXMLLoader();
        loader.setLocation(ContractRFXApp.class.getResource("Clients.fxml"));
        Parent clientsPane = (Parent) loader.load();
        ClientsController clientsController = loader.getController();
        clientsController.init(clientModel);
        controllers[2] = clientsController;

        loader = new FXMLLoader();
        loader.setLocation(ContractRFXApp.class.getResource("Tasks.fxml"));
        Parent tasksPane = (Parent) loader.load();
        TasksController tasksController = loader.getController();
        tasksController.init(clientModel, taskModel);
        controllers[3] = tasksController;

        Font iconFont = Font.loadFont(ContractRFXApp.class.getResource("ionicons.ttf").toExternalForm(), 10);

        Tab workTab = createIconTab(iconFont, '\uf1e1', "Work");
        rootTabPane.getTabs().add(workTab);
        workTab.setContent(workPane);
        
        Tab reportsTab = createIconTab(iconFont, '\uf2b5', "Reports");
        rootTabPane.getTabs().add(reportsTab);

        Tab clientsTab = createIconTab(iconFont, '\uf1bf', "Clients");
        rootTabPane.getTabs().add(clientsTab);
        clientsTab.setContent(clientsPane);

        Tab tasksTab = createIconTab(iconFont, '\uf16c', "Tasks");
        rootTabPane.getTabs().add(tasksTab);
        tasksTab.setContent(tasksPane);
        
        Scene scene = new Scene(rootPane);
        rootTabPane.tabMinWidthProperty().bind(scene.widthProperty().divide(4.0).subtract(5));
        rootTabPane.tabMaxWidthProperty().bind(rootTabPane.tabMinWidthProperty());
        primaryStage.setTitle("ContractR");
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }

    private Tab createIconTab(Font iconFont, char icon, String text) {
        Tab tab = new Tab();
        Label iconLabel = new Label(String.valueOf(icon));
        iconLabel.setFont(iconFont);
        iconLabel.getStyleClass().add("icon-tab-icon");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("icon-tab-label");
        VBox content = new VBox(iconLabel, textLabel);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("icon-tab-container");
        tab.setGraphic(content);
        tab.getStyleClass().add("icon-tab");
        return tab;
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }

}
