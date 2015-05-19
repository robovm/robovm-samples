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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.robovm.samples.contractr.core.Client;

/**
 * {@link ClientManager} implementation which stores {@link Client}s in a
 * database using JDBC. This implementation caches all {@link Client}s in the
 * database in memory.
 */
public class JdbcClientManager implements ClientManager {

    // @formatter:off
    private static final String SQL_CREATE_TABLE_CLIENTS =
            "create table if not exists clients ("
          + "  id varchar(255) not null,"
          + "  name varchar(255) not null,"
          + "  hourly_rate text not null"
          + ")";
    private static final String SQL_SELECT_CLIENTS =
            "select * from clients order by name";
    /*
     * NOTE: The update and insert statements must list the columns in the same 
     * order.
     */
    private static final String SQL_INSERT_CLIENT =
            "insert into clients (name, hourly_rate, id) values (?, ?, ?)";
    private static final String SQL_UPDATE_CLIENT =
            "update clients set name = ?, hourly_rate = ? where id = ?";
    private static final String SQL_DELETE_CLIENT =
            "delete from clients where id = ?";
    // @formatter:on

    private final ConnectionPool connectionPool;
    private boolean dirty = true;
    private final ArrayList<JdbcClientImpl> clients = new ArrayList<>();
    private JdbcTaskManager taskManager;

    public JdbcClientManager(ConnectionPool connectionPool) {
        this.connectionPool = Objects.requireNonNull(connectionPool, "connectionPool");
        createSchemaIfNeeded();
    }

    public void setTaskManager(JdbcTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    private Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    private void createSchemaIfNeeded() {
        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(SQL_CREATE_TABLE_CLIENTS);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<JdbcClientImpl> getClients() {
        if (dirty) {
            clients.clear();
            try {
                Connection conn = getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_CLIENTS)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        JdbcClientImpl client = new JdbcClientImpl();
                        client.id = rs.getString("id");
                        client.name = rs.getString("name");
                        client.hourlyRate = new BigDecimal(rs.getString("hourly_rate"));
                        clients.add(client);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            dirty = false;
        }
        return clients;
    }

    @Override
    public Client create() {
        return new JdbcClientImpl();
    }

    @Override
    public int count() {
        return getClients().size();
    }

    @Override
    public Client get(int index) {
        return getClients().get(index);
    }

    @Override
    public int indexOf(Client client) {
        return getClients().indexOf(client);
    }

    public JdbcClientImpl getById(String id) {
        for (JdbcClientImpl client : getClients()) {
            if (id.equals(client.id)) {
                return client;
            }
        }
        throw new NoSuchElementException("Client with id '" + id + "' not found");
    }

    @Override
    public void save(Client client) {
        String id = ((JdbcClientImpl) client).id;
        String sql = null;
        if (id == null) {
            // New client
            id = UUID.randomUUID().toString();
            ((JdbcClientImpl) client).id = id;
            sql = SQL_INSERT_CLIENT;
        } else {
            sql = SQL_UPDATE_CLIENT;
        }
        try {
            Connection conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, client.getName());
                stmt.setString(2, client.getHourlyRate().toString());
                stmt.setString(3, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dirty = true;
        }
    }

    @Override
    public boolean delete(Client client) {
        String id = ((JdbcClientImpl) client).id;
        try {
            Connection conn = getConnection();
            taskManager.deleteForClient(client);
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_CLIENT)) {
                stmt.setString(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dirty = true;
        }
    }

    static class JdbcClientImpl extends ClientImpl {
        String id = null;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            JdbcClientImpl other = (JdbcClientImpl) obj;
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JdbcClientImpl [id=" + id + ", name=" + name
                    + ", hourlyRate=" + hourlyRate + "]";
        }
    }
}
