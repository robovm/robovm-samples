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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.engio.mbassy.listener.Handler;

import org.junit.Before;
import org.junit.Test;
import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.ClientModel.ClientDeletedEvent;
import org.robovm.samples.contractr.core.ClientModel.ClientSavedEvent;
import org.robovm.samples.contractr.core.ClientModel.SelectedClientChangedEvent;
import org.robovm.samples.contractr.core.service.TestClient;
import org.robovm.samples.contractr.core.service.TestClientManager;

/**
 * Tests {@link ClientModel}.
 */
public class ClientModelTest {

    TestClientManager clientManager;
    ClientModel model;
    List<Object> events;
    TestClient client1;
    TestClient client2;

    @Handler
    public void selectedClientChanged(SelectedClientChangedEvent event) {
        events.add(event);
    }

    @Handler
    public void clientSaved(ClientSavedEvent event) {
        events.add(event);
    }

    @Handler
    public void clientDeleted(ClientDeletedEvent event) {
        events.add(event);
    }

    @Before
    public void setup() {
        client1 = new TestClient("Client 1", BigDecimal.TEN);
        client2 = new TestClient("Client 2", BigDecimal.TEN);
        clientManager = new TestClientManager();
        clientManager.clients.add(client1);
        clientManager.clients.add(client2);
        model = new ClientModel(clientManager);
        model.subscribe(this);
        events = new ArrayList<>();
    }

    @Test
    public void testSelectClient() throws Exception {
        // No client should be selected to begin with
        assertNull(model.getSelectedClient());

        // Select client
        model.selectClient(client1);
        assertEquals(client1, model.getSelectedClient());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SelectedClientChangedEvent);
        assertNull(((SelectedClientChangedEvent) events.get(0)).getOldClient());
        assertEquals(client1,
                ((SelectedClientChangedEvent) events.get(0)).getNewClient());
        events.clear();

        // Select same client triggers no event
        model.selectClient(client1);
        assertEquals(client1, model.getSelectedClient());
        assertEquals(0, events.size());
        events.clear();

        // Select other client
        model.selectClient(client2);
        assertEquals(client2, model.getSelectedClient());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SelectedClientChangedEvent);
        assertEquals(client1,
                ((SelectedClientChangedEvent) events.get(0)).getOldClient());
        assertEquals(client2,
                ((SelectedClientChangedEvent) events.get(0)).getNewClient());
        events.clear();

        // Deselect client
        model.selectClient(null);
        assertNull(model.getSelectedClient());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SelectedClientChangedEvent);
        assertEquals(client2,
                ((SelectedClientChangedEvent) events.get(0)).getOldClient());
        assertNull(((SelectedClientChangedEvent) events.get(0)).getNewClient());
        events.clear();

        // Deselect again triggers no event
        model.selectClient(null);
        assertNull(model.getSelectedClient());
        assertEquals(0, events.size());
        events.clear();
    }

    @Test
    public void testCreate() throws Exception {
        // Creating a new Client should not trigger any event and should not add
        // it to the underlying storage
        assertEquals(2, clientManager.clients.size());
        model.create();
        assertEquals(0, events.size());
        assertEquals(2, clientManager.clients.size());
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(2, model.count());
    }

    @Test
    public void testGet() throws Exception {
        assertEquals(client1, model.get(0));
        assertEquals(client2, model.get(1));
    }

    @Test
    public void testSave() throws Exception {
        // Saving client1 should trigger an event
        model.save(client1);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ClientSavedEvent);
        assertEquals(client1, ((ClientSavedEvent) events.get(0)).getClient());
        events.clear();

        // Creating a new Client and saving it should trigger an event
        Client client3 = model.create();
        assertEquals(0, events.size());
        model.save(client3);
        assertTrue(events.get(0) instanceof ClientSavedEvent);
        assertEquals(client3, ((ClientSavedEvent) events.get(0)).getClient());
        events.clear();
    }

    @Test
    public void testDelete() throws Exception {
        // Deleting a client should trigger an event
        model.delete(client1);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ClientDeletedEvent);
        assertEquals(client1, ((ClientDeletedEvent) events.get(0)).getClient());
        events.clear();

        // Make sure it was actually deleted from the manager
        assertEquals(1, clientManager.clients.size());
        assertFalse(clientManager.clients.contains(client1));

        // Deleting it again should not trigger any event
        model.delete(client1);
        assertEquals(0, events.size());
    }

    @Test
    public void testDeleteSelected() throws Exception {
        // Deleting the selected client should deselect it
        model.selectClient(client1);
        events.clear();
        model.delete(client1);
        assertEquals(2, events.size());
        assertTrue(events.get(0) instanceof SelectedClientChangedEvent);
        assertEquals(client1,
                ((SelectedClientChangedEvent) events.get(0)).getOldClient());
        assertNull(((SelectedClientChangedEvent) events.get(0)).getNewClient());
        assertTrue(events.get(1) instanceof ClientDeletedEvent);
        assertEquals(client1, ((ClientDeletedEvent) events.get(1)).getClient());
        events.clear();
    }
}
