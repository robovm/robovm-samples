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
package org.robovm.samples.contractr.core.service;

import java.util.ArrayList;
import java.util.List;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.service.TaskManager;

/**
 * Test {@link TaskManager} implementation.
 */
public class TestTaskManager implements TaskManager {
    public ArrayList<TestTask> tasks = new ArrayList<>();

    @Override
    public Task create(Client client) {
        return new TestTask(client, null, null, false, 0, null);
    }

    @Override
    public int count() {
        return tasks.size();
    }

    @Override
    public Task get(int index) {
        return tasks.get(index);
    }

    @Override
    public List<Task> getForClient(Client client, boolean unfinishedOnly) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if ((!unfinishedOnly || !task.isFinished()) && task.getClient().equals(client)) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public void save(Task task) {
        if (!tasks.contains(task)) {
            tasks.add((TestTask) task);
        }
    }

    @Override
    public boolean delete(Task task) {
        return tasks.remove(task);
    }

    @Override
    public int countUnfinished() {
        int total = 0;
        for (Task t : tasks) {
            if (!t.isFinished()) {
                total++;
            }
        }
        return total;
    }

}
