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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.AbstractList;
import java.util.Locale;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import jfxtras.labs.scene.control.BigDecimalField;
import net.engio.mbassy.listener.Handler;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.ClientModel.SelectedClientChangedEvent;

/**
 * 
 */
public class ClientsController extends AbstractController {
    private ClientModel clientModel;
    
    @FXML
    private ListView<String> clientsList;
    @FXML
    private Button addButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private HBox hourlyRateContainer;
    @FXML
    private Parent addEditPane;
    private BigDecimalField hourlyRateField;

    @Override
    public void beforeShow() {
        clientsList.setItems(FXCollections.observableList(new AbstractList<String>() {
            public String get(int index) {
                return clientModel.get(index).getName();
            }

            public int size() {
                return clientModel.count();
            }
        }));
        clientModel.selectClient(null);
        updateSaveButtonEnabled();
    }
    
    @Handler
    public void selectedClientChanged(SelectedClientChangedEvent event) {
        Client client = event.getNewClient();
        addEditPane.setVisible(client != null);
        nameTextField.setText(client == null ? "" : client.getName());
        hourlyRateField.setNumber(client == null ? BigDecimal.ZERO : client.getHourlyRate());
        updateSaveButtonEnabled();
    }
    
    public void init(ClientModel clientModel) {
        this.clientModel = Objects.requireNonNull(clientModel, "clientModel");

        clientModel.subscribe(this);
        
        clientsList.getSelectionModel().selectedIndexProperty().addListener((o, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index == -1) {
                clientModel.selectClient(null);
            } else {
                Client client = clientModel.get(newVal.intValue());
                clientModel.selectClient(client);
            }
        });
        
        addButton.setOnAction(e -> {
            clientsList.getSelectionModel().clearSelection();
            addEditPane.setVisible(true);
        });
        
        nameTextField.textProperty().addListener((o, oldVal, newVal) -> {
            updateSaveButtonEnabled();
        });
        
        hourlyRateField = new BigDecimalField(BigDecimal.ZERO);
        hourlyRateField.setFormat(NumberFormat.getCurrencyInstance(Locale.US));
        hourlyRateField.setStepwidth(BigDecimal.TEN);
        hourlyRateContainer.getChildren().add(hourlyRateField);
        HBox.setHgrow(hourlyRateField, Priority.ALWAYS);
        
        hourlyRateField.numberProperty().addListener((o, oldVal, newVal) -> {
            if (newVal == null) {
                hourlyRateField.setNumber(BigDecimal.ZERO);
            }
        });
        
        saveButton.setOnAction(e -> {
            Client client = clientModel.getSelectedClient();
            if (client == null) {
                client = clientModel.create();
            }
            client.setName(nameTextField.getText());
            client.setHourlyRate(hourlyRateField.getNumber());
            clientModel.save(client);
            ObservableList<String> l = clientsList.getItems();
            clientsList.setItems(null);
            clientsList.setItems(l);
            clientsList.getSelectionModel().select(clientModel.indexOf(client));
        });
        
        addEditPane.setVisible(false);
    }

    protected void updateSaveButtonEnabled() {
        saveButton.setDisable(!canSave());
    }

    private boolean canSave() {
        String name = nameTextField.getText();
        name = name == null ? "" : name.trim();
        return !name.isEmpty() && hourlyRateField.getNumber() != null;
    }

}
