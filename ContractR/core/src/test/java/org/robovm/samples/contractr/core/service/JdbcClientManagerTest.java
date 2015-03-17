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

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.service.ConnectionPool;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;

/**
 * Tests {@link JdbcClientManager}.
 */
public class JdbcClientManagerTest {

    ConnectionPool connectionPool;
    JdbcTaskManager taskManager;

    @Before
    public void setup() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connectionPool = new SingletonConnectionPool("jdbc:sqlite::memory:");
        taskManager = new JdbcTaskManager(connectionPool);
    }

    @Test
    public void testEmpty() {
        JdbcClientManager man = new JdbcClientManager(connectionPool);
        man.setTaskManager(taskManager);

        assertEquals(0, man.count());
    }

    @Test
    public void testSaveGetDelete() {
        JdbcClientManager man = new JdbcClientManager(connectionPool);
        man.setTaskManager(taskManager);

        assertEquals(0, man.count());
        Client client1 = man.create();
        client1.setName("Google");
        client1.setHourlyRate(BigDecimal.valueOf(10000, 2));
        man.save(client1);
        Client client2 = man.create();
        client2.setName("Apple");
        client2.setHourlyRate(BigDecimal.valueOf(20000, 2));
        man.save(client2);
        assertEquals(2, man.count());

        // Clients should be ordered by name so Apple comes first.
        assertNotSame(client2, man.get(0));
        assertEquals(client2, man.get(0));
        assertNotSame(client1, man.get(1));
        assertEquals(client1, man.get(1));

        assertTrue(man.delete(client2));
        assertEquals(1, man.count());
        assertEquals(client1, man.get(0));

        assertFalse(man.delete(client2));
        assertEquals(1, man.count());
        assertEquals(client1, man.get(0));

        assertTrue(man.delete(client1));
        assertEquals(0, man.count());
    }

    @Test
    public void testDeleteDeletesTasks() {
        JdbcClientManager clientManager = new JdbcClientManager(connectionPool);
        JdbcTaskManager taskManager = new JdbcTaskManager(connectionPool);
        clientManager.setTaskManager(taskManager);
        taskManager.setClientManager(clientManager);

        assertEquals(0, clientManager.count());
        Client client1 = clientManager.create();
        client1.setName("Google");
        clientManager.save(client1);
        Client client2 = clientManager.create();
        client2.setName("Apple");
        clientManager.save(client2);
        assertEquals(2, clientManager.count());

        Task task1 = taskManager.create(client1);
        task1.setTitle("Fix foo");
        taskManager.save(task1);
        Task task2 = taskManager.create(client2);
        task2.setTitle("Create bar");
        taskManager.save(task2);
        assertEquals(2, taskManager.count());

        assertTrue(clientManager.delete(client1));
        assertEquals(1, clientManager.count());
        assertEquals(client2, clientManager.get(0));
        assertEquals(1, taskManager.count());
        assertEquals(task2, taskManager.get(0));
    }
}
