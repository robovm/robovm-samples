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

import java.math.BigDecimal;
import java.util.ArrayList;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.service.ClientManager;

/**
 * Test {@link ClientManager} implementation.
 */
public class TestClientManager implements ClientManager {
    public ArrayList<TestClient> clients = new ArrayList<>();

    @Override
    public Client create() {
        return new TestClient(null, BigDecimal.ZERO);
    }

    @Override
    public int count() {
        return clients.size();
    }

    @Override
    public Client get(int index) {
        return clients.get(index);
    }

    @Override
    public int indexOf(Client client) {
        return clients.indexOf(client);
    }
    
    @Override
    public void save(Client client) {
        if (!clients.contains(client)) {
            clients.add((TestClient) client);
        }
    }

    @Override
    public boolean delete(Client client) {
        return clients.remove(client);
    }

}
