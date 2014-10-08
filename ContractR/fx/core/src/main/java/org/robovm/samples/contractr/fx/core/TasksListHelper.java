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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

/**
 * 
 */
public class TasksListHelper {
    private final ClientModel clientModel;
    private final TaskModel taskModel;
    private final TreeView<String> tasksTreeView;

    public TasksListHelper(ClientModel clientModel, TaskModel taskModel, TreeView<String> tasksTreeView) {
        this.clientModel = Objects.requireNonNull(clientModel, "clientModel");
        this.taskModel = Objects.requireNonNull(taskModel, "taskModel");
        this.tasksTreeView = Objects.requireNonNull(tasksTreeView, "tasksTreeView");
    }

    public void reload() {
        tasksTreeView.setRoot(buildTree(clientModel, taskModel));
    }
    
    protected TreeItem<String> buildTree(ClientModel clientModel, TaskModel taskModel) {
        TreeItem<String> n = new TreeItem<String>("Root");
        for (int i = 0; i < clientModel.count(); i++) {
            n.getChildren().add(buildTree(clientModel.get(i), taskModel));
        }
        return n;
    }
    
    protected TreeItem<String> buildTree(Client client, TaskModel taskModel) {
        TreeItem<String> n = new ClientTreeItem(client);
        n.setExpanded(true);
        for (Task task : taskModel.getForClient(client, false)) {
            n.getChildren().add(new TaskTreeItem(task));
        }
        return n;
    }
    
    public TreeItem<String> findTaskTreeItem(TreeItem<String> root, Task task) {
        for (TreeItem<String> child : root.getChildren()) {
            if (child instanceof TaskTreeItem) {
                if (((TaskTreeItem) child).getTask().equals(task)) {
                    return child;
                }
            } else {
                TreeItem<String> n = findTaskTreeItem(child, task);
                if (n != null) {
                    return n;
                }
            }
        }
        return null;
    }
    
    public static class ClientTreeItem extends TreeItem<String> {
        public ClientTreeItem(Client client) {
            super(client.getName());
        }
    }
    
    public static class TaskTreeItem extends TreeItem<String> {
        private final Task task;

        public TaskTreeItem(Task task) {
            super(task.getTitle());
            this.task = task;
        }
        
        public Task getTask() {
            return task;
        }
    }

}
