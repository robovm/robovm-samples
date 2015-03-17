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

import java.util.List;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;

/**
 * Manages {@link Task}s. Provides CRUD operations.
 */
public interface TaskManager {

    /**
     * Creates a new {@link Task} for the specified {@link Client}.
     * 
     * @throws NullPointerException if the client is {@code null}.
     */
    Task create(Client client);

    /**
     * Returns the total number of available {@link Task}s.
     */
    int count();

    /**
     * Returns the total number of unfinished {@link Task}s.
     */
    int countUnfinished();
    
    /**
     * Returns the {@link Task} at the specified index.
     */
    Task get(int index);

    /**
     * Returns the {@link Task}s belonging to the specified {@link Client}.
     */
    List<Task> getForClient(Client client, boolean unfinishedOnly);
    
    /**
     * Saves the specified {@link Task}.
     */
    void save(Task task);

    /**
     * Deletes the specified {@link Task}. Returns {@code false} if the
     * {@link Task} doesn't exist in this {@link TaskManager}.
     */
    boolean delete(Task task);
}
