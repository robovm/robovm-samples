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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.engio.mbassy.listener.Handler;

import org.junit.Before;
import org.junit.Test;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;
import org.robovm.samples.contractr.core.TaskModel.SelectedTaskChangedEvent;
import org.robovm.samples.contractr.core.TaskModel.TaskDeletedEvent;
import org.robovm.samples.contractr.core.TaskModel.TaskSavedEvent;
import org.robovm.samples.contractr.core.service.TestClient;
import org.robovm.samples.contractr.core.service.TestTask;
import org.robovm.samples.contractr.core.service.TestTaskManager;

/**
 * Tests {@link TaskModel}.
 */
public class TaskModelTest {

    TestTaskManager taskManager;
    TaskModel model;
    List<Object> events;
    TestClient client;
    TestTask task1;
    TestTask task2;

    @Handler
    public void selectedTaskChanged(SelectedTaskChangedEvent event) {
        events.add(event);
    }

    @Handler
    public void taskSaved(TaskSavedEvent event) {
        events.add(event);
    }

    @Handler
    public void taskDeleted(TaskDeletedEvent event) {
        events.add(event);
    }

    @Before
    public void setup() {
        client = new TestClient("Client", BigDecimal.ZERO);

        task1 = new TestTask(client, "Task 1", null, false, 0, null);
        task2 = new TestTask(client, "Task 2", null, false, 0, null);
        taskManager = new TestTaskManager();
        taskManager.tasks.add(task1);
        taskManager.tasks.add(task2);
        model = new TaskModel(taskManager);
        model.subscribe(this);
        events = new ArrayList<>();
    }

    @Test
    public void testSelectTask() throws Exception {
        // No task should be selected to begin with
        assertNull(model.getSelectedTask());

        // Select task
        model.selectTask(task1);
        assertEquals(task1, model.getSelectedTask());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SelectedTaskChangedEvent);
        assertNull(((SelectedTaskChangedEvent) events.get(0)).getOldTask());
        assertEquals(task1,
                ((SelectedTaskChangedEvent) events.get(0)).getNewTask());
        events.clear();

        // Select same task triggers no event
        model.selectTask(task1);
        assertEquals(task1, model.getSelectedTask());
        assertEquals(0, events.size());
        events.clear();

        // Select other task
        model.selectTask(task2);
        assertEquals(task2, model.getSelectedTask());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SelectedTaskChangedEvent);
        assertEquals(task1,
                ((SelectedTaskChangedEvent) events.get(0)).getOldTask());
        assertEquals(task2,
                ((SelectedTaskChangedEvent) events.get(0)).getNewTask());
        events.clear();

        // Deselect task
        model.selectTask(null);
        assertNull(model.getSelectedTask());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SelectedTaskChangedEvent);
        assertEquals(task2,
                ((SelectedTaskChangedEvent) events.get(0)).getOldTask());
        assertNull(((SelectedTaskChangedEvent) events.get(0)).getNewTask());
        events.clear();

        // Deselect again triggers no event
        model.selectTask(null);
        assertNull(model.getSelectedTask());
        assertEquals(0, events.size());
        events.clear();
    }

    @Test
    public void testCreate() throws Exception {
        // Creating a new Task should not trigger any event and should not add
        // it to the underlying storage
        assertEquals(2, taskManager.tasks.size());
        Task task = model.create(client);
        assertEquals(client, task.getClient());
        assertEquals(0, events.size());
        assertEquals(2, taskManager.tasks.size());
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(2, model.count());
    }
    
    @Test
    public void testGet() throws Exception {
        assertEquals(task1, model.get(0));
        assertEquals(task2, model.get(1));
    }

    @Test
    public void testSave() throws Exception {
        // Saving task1 should trigger an event
        model.save(task1);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof TaskSavedEvent);
        assertEquals(task1, ((TaskSavedEvent) events.get(0)).getTask());
        events.clear();

        // Creating a new Task and saving it should trigger an event
        Task task3 = model.create(client);
        assertEquals(0, events.size());
        model.save(task3);
        assertTrue(events.get(0) instanceof TaskSavedEvent);
        assertEquals(task3, ((TaskSavedEvent) events.get(0)).getTask());
        events.clear();
    }

    @Test
    public void testDelete() throws Exception {
        // Deleting a task should trigger an event
        model.delete(task1);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof TaskDeletedEvent);
        assertEquals(task1, ((TaskDeletedEvent) events.get(0)).getTask());
        events.clear();

        // Make sure it was actually deleted from the manager
        assertEquals(1, taskManager.tasks.size());
        assertFalse(taskManager.tasks.contains(task1));

        // Deleting it again should not trigger any event
        model.delete(task1);
        assertEquals(0, events.size());
    }

    @Test
    public void testDeleteSelected() throws Exception {
        // Deleting the selected task should deselect it
        model.selectTask(task1);
        events.clear();
        model.delete(task1);
        assertEquals(2, events.size());
        assertTrue(events.get(0) instanceof SelectedTaskChangedEvent);
        assertEquals(task1,
                ((SelectedTaskChangedEvent) events.get(0)).getOldTask());
        assertNull(((SelectedTaskChangedEvent) events.get(0)).getNewTask());
        assertTrue(events.get(1) instanceof TaskDeletedEvent);
        assertEquals(task1, ((TaskDeletedEvent) events.get(1)).getTask());
        events.clear();
    }
    
    @Test
    public void testGetWorkingTask() throws Exception {
        assertNull(model.getWorkingTask());
        model.startWork(task1);
        assertEquals(task1, model.getWorkingTask());
        assertNotNull(model.getWorkingTask());
        assertNotNull(task1.getWorkStartTime());
        long start1 = 30 * 1000;
        long end1 = start1 + 5 * 1000;
        task1.setWorkStartTime(new Date(start1));
        model.stopWork(end1);
        assertNull(model.getWorkingTask());
        assertNull(task1.getWorkStartTime());
        assertEquals(5, task1.getSecondsWorked());

        // There should now be 1 work unit
        assertEquals(1, task1.getWorkUnits().size());
        assertEquals(start1, task1.getWorkUnits().get(0).getStartTime().getTime());
        assertEquals(end1, task1.getWorkUnits().get(0).getEndTime().getTime());

        model.startWork(task1);
        assertEquals(task1, model.getWorkingTask());
        assertNotNull(model.getWorkingTask());
        assertNotNull(task1.getWorkStartTime());
        long start2 = 45 * 1000;
        long end2 = start2 + 10 * 1000;
        task1.setWorkStartTime(new Date(start2));
        model.stopWork(end2);
        assertNull(model.getWorkingTask());
        assertNull(task1.getWorkStartTime());
        assertEquals(15, task1.getSecondsWorked());
        
        // There should now be 2 work units
        assertEquals(2, task1.getWorkUnits().size());
        assertEquals(start1, task1.getWorkUnits().get(0).getStartTime().getTime());
        assertEquals(end1, task1.getWorkUnits().get(0).getEndTime().getTime());
        assertEquals(start2, task1.getWorkUnits().get(1).getStartTime().getTime());
        assertEquals(end2, task1.getWorkUnits().get(1).getEndTime().getTime());
    }
}
