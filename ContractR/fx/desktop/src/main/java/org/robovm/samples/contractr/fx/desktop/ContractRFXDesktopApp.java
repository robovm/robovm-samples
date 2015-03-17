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
package org.robovm.samples.contractr.fx.desktop;

import java.io.File;
import java.util.Arrays;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;
import org.robovm.samples.contractr.fx.core.ContractRFXApp;

/**
 * 
 */
public class ContractRFXDesktopApp extends ContractRFXApp {

    @Override
    public void init() throws Exception {
        Class.forName("org.sqlite.JDBC");
        
        File dbFile = new File(System.getProperty("user.home"), ".ContractR/db.sqlite");
        dbFile.getParentFile().mkdirs();
        System.out.println("Using db in file: " + dbFile.getAbsolutePath());
        SingletonConnectionPool connectionPool = new SingletonConnectionPool(
                "jdbc:sqlite::memory:");
        JdbcClientManager clientManager = new JdbcClientManager(connectionPool);
        JdbcTaskManager taskManager = new JdbcTaskManager(connectionPool);
        clientManager.setTaskManager(taskManager);
        taskManager.setClientManager(clientManager);
        
        super.clientModel = new ClientModel(clientManager);
        super.taskModel = new TaskModel(taskManager);

        for (String name : Arrays.asList("Apple", "Google", "Oracle")) {
            Client client = clientModel.create();
            client.setName(name);
            clientModel.save(client);
            for (String title : Arrays.asList("Task 1", "Task 2", "Task 3")) {
                Task task = taskModel.create(client);
                task.setTitle(title);
                taskModel.save(task);
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
