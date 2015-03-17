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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.service.ConnectionPool;
import org.robovm.samples.contractr.core.service.JdbcClientManager;
import org.robovm.samples.contractr.core.service.JdbcTaskManager;
import org.robovm.samples.contractr.core.service.SingletonConnectionPool;
import org.robovm.samples.contractr.core.service.JdbcTaskManager.JdbcTaskImpl;

/**
 * Tests {@link JdbcClientManager}.
 */
public class JdbcTaskManagerTest {

    ConnectionPool connectionPool;
    JdbcClientManager clientManager;
    Client client1;
    Client client2;

    @Before
    public void setup() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connectionPool = new SingletonConnectionPool("jdbc:sqlite::memory:");
        clientManager = new JdbcClientManager(connectionPool);
        client1 = clientManager.create();
        client1.setName("Google");
        client1.setHourlyRate(BigDecimal.valueOf(10000, 2));
        clientManager.save(client1);
        client2 = clientManager.create();
        client2.setName("Apple");
        client2.setHourlyRate(BigDecimal.valueOf(20000, 2));
        clientManager.save(client2);
    }

    @Test
    public void testEmpty() {
        JdbcTaskManager man = new JdbcTaskManager(connectionPool);
        man.setClientManager(clientManager);
        assertEquals(0, man.count());
    }

    @Test
    public void testSaveGetDelete() {
        JdbcTaskManager man = new JdbcTaskManager(connectionPool);
        man.setClientManager(clientManager);
        assertEquals(0, man.count());
        Task task1 = man.create(client1);
        task1.setTitle("Fix foo");
        task1.setNotes("Some notes");
        task1.setFinished(true);
        man.save(task1);
        Task task2 = man.create(client2);
        task2.setTitle("Create bar");
        man.save(task2);
        assertEquals(2, man.count());

        // Tasks should be ordered by title.
        assertNotSame(task2, man.get(0));
        assertEquals(task2, man.get(0));
        assertNotSame(task1, man.get(1));
        assertEquals(task1, man.get(1));

        assertTrue(man.delete(task2));
        assertEquals(1, man.count());
        assertEquals(task1, man.get(0));

        assertFalse(man.delete(task2));
        assertEquals(1, man.count());
        assertEquals(task1, man.get(0));

        assertTrue(man.delete(task1));
        assertEquals(0, man.count());
    }

    @Test
    public void testWorkUnits() throws Exception {
        JdbcTaskManager man = new JdbcTaskManager(connectionPool);
        man.setClientManager(clientManager);
        assertEquals(0, man.count());
        Task task1 = man.create(client1);
        task1.setTitle("Task 1");
        man.save(task1);
        Task task2 = man.create(client1);
        task2.setTitle("Task 2");
        man.save(task2);
        Task task3 = man.create(client2);
        task3.setTitle("Task 3");
        man.save(task3);
        assertEquals(3, man.count());

        // Add some work units to all three tasks
        task1.addWorkUnit(new Date(10 * 1000), new Date(11 * 1000));
        task1.addWorkUnit(new Date(14 * 1000), new Date(16 * 1000));
        man.save(task1);
        task2.addWorkUnit(new Date(20 * 1000), new Date(21 * 1000));
        task2.addWorkUnit(new Date(24 * 1000), new Date(26 * 1000));
        man.save(task2);
        task3.addWorkUnit(new Date(30 * 1000), new Date(31 * 1000));
        task3.addWorkUnit(new Date(34 * 1000), new Date(36 * 1000));
        man.save(task3);

        assertNotSame(task1, man.get(0));
        assertEquals(task1, man.get(0));
        assertNotSame(task2, man.get(1));
        assertEquals(task2, man.get(1));
        assertNotSame(task3, man.get(2));
        assertEquals(task3, man.get(2));

        assertEquals(2, task1.getWorkUnits().size());
        assertEquals(10 * 1000, task1.getWorkUnits().get(0).getStartTime().getTime());
        assertEquals(11 * 1000, task1.getWorkUnits().get(0).getEndTime().getTime());
        assertEquals(14 * 1000, task1.getWorkUnits().get(1).getStartTime().getTime());
        assertEquals(16 * 1000, task1.getWorkUnits().get(1).getEndTime().getTime());
        assertEquals(2, task2.getWorkUnits().size());
        assertEquals(20 * 1000, task2.getWorkUnits().get(0).getStartTime().getTime());
        assertEquals(21 * 1000, task2.getWorkUnits().get(0).getEndTime().getTime());
        assertEquals(24 * 1000, task2.getWorkUnits().get(1).getStartTime().getTime());
        assertEquals(26 * 1000, task2.getWorkUnits().get(1).getEndTime().getTime());
        assertEquals(2, task3.getWorkUnits().size());
        assertEquals(30 * 1000, task3.getWorkUnits().get(0).getStartTime().getTime());
        assertEquals(31 * 1000, task3.getWorkUnits().get(0).getEndTime().getTime());
        assertEquals(34 * 1000, task3.getWorkUnits().get(1).getStartTime().getTime());
        assertEquals(36 * 1000, task3.getWorkUnits().get(1).getEndTime().getTime());
        
        // Deleting a task should delete all its work units
        man.delete(task3);
        assertEquals(2, man.count());
        assertEquals(task1, man.get(0));
        assertEquals(task2, man.get(1));
        
        Connection conn = connectionPool.getConnection();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "select * from work_units where task_id = '" + ((JdbcTaskImpl) task3).id + "'");
            assertFalse(rs.next());
        }
        
        // Deleting all tasks for a client should delete all its work units
        man.deleteForClient(client1);
        assertEquals(0, man.count());
        
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                    "select * from work_units");
            assertFalse(rs.next());
        }
    }
}
