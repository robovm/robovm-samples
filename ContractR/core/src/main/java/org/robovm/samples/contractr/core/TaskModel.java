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
package org.robovm.samples.contractr.core;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.listener.Handler;

import org.robovm.samples.contractr.core.ClientModel.SelectedClientChangedEvent;
import org.robovm.samples.contractr.core.service.TaskManager;

/**
 * Model for {@link Task} objects. Supports the use cases the controllers in the
 * different GUIs will need. Event handling is based on the {@link MBassador}
 * event bus.
 */
public class TaskModel {
    private final TaskManager taskManager;
    private final MBassador<Object> bus;

    private Task selectedTask;

    /**
     * Creates a new {@link TaskModel} backed by the specified
     * {@link TaskManager}.
     */
    public TaskModel(TaskManager taskManager) {
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.bus = new MBassador<Object>(new BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default()));
    }

    @Handler
    public void selectedClientChanged(SelectedClientChangedEvent event) {
        selectTask(null);
    }

    /**
     * Subscribes to events fired by this {@link TaskModel}. Use MBassador's
     * {@link Handler} annotation to mark methods in the listener as listener
     * methods.
     */
    public void subscribe(Object listener) {
        bus.subscribe(listener);
    }

    /**
     * Returns the currently selected {@link Task} or {@code null} if no
     * {@link Task} has been selected.
     */
    public Task getSelectedTask() {
        return selectedTask;
    }

    /**
     * Selects a new {@link Task}. Pass {@code null} to deselect the currently
     * selected {@link Task}. Fires a {@link SelectedTaskChangedEvent} if the
     * selected {@link Task} has changed.
     */
    public void selectTask(Task newTask) {
        Task oldTask = this.selectedTask;
        this.selectedTask = newTask;
        if (!Objects.equals(oldTask, newTask)) {
            bus.publish(new SelectedTaskChangedEvent(oldTask, newTask));
        }
    }

    /**
     * Creates a new {@link Task} with the specified name. The new {@link Task}
     * will not be added to the underlying storage until {@link #save(Task)} is
     * called.
     */
    public Task create(Client client) {
        return taskManager.create(client);
    }

    /**
     * Returns the total number of available {@link Task}s.
     */
    public int count() {
        return taskManager.count();
    }

    /**
     * Returns the total number of unfinished {@link Task}s.
     */
    public int countUnfinished() {
        return taskManager.countUnfinished();
    }
    
    /**
     * Returns the {@link Task} at the specified index.
     */
    public Task get(int index) {
        return taskManager.get(index);
    }

    /**
     * Returns {@link Task}s for the specified {@link Client}.
     */
    public List<Task> getForClient(Client client, boolean unfinishedOnly) {
        return taskManager.getForClient(client, unfinishedOnly);
    }

    /**
     * Saves the specified {@link Task} in the underlying storage. Fires
     * {@link TaskSavedEvent}.
     */
    public void save(Task task) {
        taskManager.save(task);
        bus.publish(new TaskSavedEvent(task));
    }

    /**
     * Deletes the specified {@link Task} from the underlying storage. Fires
     * {@link TaskDeletedEvent} if the {@link Task} existed in the storage and
     * was deleted. Also fires {@link SelectedTaskChangedEvent} if the selected
     * {@link Task} is deleted.
     */
    public void delete(Task task) {
        if (taskManager.delete(task)) {
            if (task.equals(selectedTask)) {
                selectTask(null);
            }
            bus.publish(new TaskDeletedEvent(task));
        }
    }

    /**
     * Returns the {@link Task} currently being worked on or {@code null} if not
     * working on any {@link Task}.
     */
    public Task getWorkingTask() {
        for (int i = 0; i < count(); i++) {
            Task task = get(i);
            if (task.getWorkStartTime() != null) {
                return task;
            }
        }
        return null;
    }

    /**
     * Starts work on the specified {@link Task}.
     */
    public void startWork(Task task) {
        if (task.getWorkStartTime() != null) {
            // Already working on this task
            return;
        }
        stopWork();
        task.setWorkStartTime(new Date());
        taskManager.save(task);
    }

    /**
     * Stops work on the {@link Task} currently being worked on.
     */
    public void stopWork() {
        stopWork(System.currentTimeMillis());
    }

    protected void stopWork(long now) {
        Task workingTask = getWorkingTask();
        if (workingTask == null) {
            return;
        }
        Date startTime = workingTask.getWorkStartTime();
        Date endTime = new Date(now);
        workingTask.setSecondsWorked((int) (workingTask.getSecondsWorked()
                + (endTime.getTime() - startTime.getTime()) / 1000));
        workingTask.setWorkStartTime(null);
        workingTask.addWorkUnit(startTime, endTime);
        taskManager.save(workingTask);
    }

    /**
     * Event fired when the selected {@link Task} changes.
     */
    public static class SelectedTaskChangedEvent {
        private final Task oldTask;
        private final Task newTask;

        SelectedTaskChangedEvent(Task oldTask, Task newTask) {
            this.oldTask = oldTask;
            this.newTask = newTask;
        }

        public Task getOldTask() {
            return oldTask;
        }

        public Task getNewTask() {
            return newTask;
        }
    }

    /**
     * Event fired when a {@link Task} has been saved.
     */
    public static class TaskSavedEvent {
        private final Task task;

        TaskSavedEvent(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

    /**
     * Event fired when a {@link Task} has been deleted.
     */
    public static class TaskDeletedEvent {
        private final Task task;

        TaskDeletedEvent(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }
}
