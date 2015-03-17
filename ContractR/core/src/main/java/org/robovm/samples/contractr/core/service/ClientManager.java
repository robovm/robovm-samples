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

import org.robovm.samples.contractr.core.Client;

/**
 * Manages {@link Client}s. Provides CRUD operations.
 */
public interface ClientManager {

    /**
     * Creates a new {@link Client}.
     */
    Client create();

    /**
     * Returns the total number of available {@link Client}s.
     */
    int count();

    /**
     * Returns the {@link Client} at the specified index.
     */
    Client get(int index);

    /**
     * Returns the index of the specified {@link Client} or -1 if it is unknown.
     */
    int indexOf(Client client);
    
    /**
     * Saves the specified {@link Client}.
     */
    void save(Client client);

    /**
     * Deletes the specified {@link Client}. Returns {@code false} if the
     * {@link Client} doesn't exist in this {@link ClientManager}.
     */
    boolean delete(Client client);

}
