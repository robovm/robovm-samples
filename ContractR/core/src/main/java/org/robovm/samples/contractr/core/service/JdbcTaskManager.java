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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.WorkUnit;
import org.robovm.samples.contractr.core.service.JdbcClientManager.JdbcClientImpl;

/**
 * {@link TaskManager} implementation which stores {@link Tasks}s in a database
 * using JDBC.
 */
public class JdbcTaskManager implements TaskManager {

    // @formatter:off
    private static final String SQL_CREATE_TABLE_TASKS =
            "create table if not exists tasks ("
          + "  id varchar(255) not null,"
          + "  client_id varchar(255) not null,"
          + "  title varchar(255) not null,"
          + "  notes varchar(255),"
          + "  finished bit not null,"
          + "  work_start_time bigint,"
          + "  seconds_worked int"
          + ")";
    private static final String SQL_SELECT_TASKS =
            "select * from tasks order by title";
    private static final String SQL_CREATE_TABLE_WORK_UNITS =
            "create table if not exists work_units ("
          + "  task_id varchar(255) not null,"
          + "  id varchar(255) not null,"
          + "  start_time bigint not null,"
          + "  end_time bigint not null"
          + ")";
    private static final String SQL_SELECT_WORK_UNITS =
            "select * from work_units order by task_id, start_time";
    /*
     * NOTE: The update and insert statements must list the columns in the same 
     * order.
     */
    private static final String SQL_INSERT_TASK =
            "insert into tasks "
          + "  (client_id, title, notes, finished, work_start_time, seconds_worked, id)"
          + "  values (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_TASK =
            "update tasks set client_id = ?, title = ?, notes = ?,"
          + "  finished = ?, work_start_time = ?, seconds_worked = ?"
          + "  where id = ?";
    private static final String SQL_DELETE_TASK =
            "delete from tasks where id = ?";
    private static final String SQL_DELETE_TASKS_FOR_CLIENT =
            "delete from tasks where client_id = ?";
    private static final String SQL_INSERT_WORK_UNIT =
            "insert into work_units "
          + "  (task_id, start_time, end_time, id)"
          + "  values (?, ?, ?, ?)";
    private static final String SQL_DELETE_DANGLING_WORK_UNITS =
            "delete from work_units where task_id not in (select id from tasks)";
    // @formatter:on

    private final ConnectionPool connectionPool;
    private boolean dirty = true;
    private final ArrayList<JdbcTaskImpl> tasks = new ArrayList<>();
    private JdbcClientManager clientManager;

    public JdbcTaskManager(ConnectionPool connectionPool) {
        this.connectionPool = Objects.requireNonNull(connectionPool, "connectionPool");
        createSchemaIfNeeded();
    }

    public void setClientManager(JdbcClientManager clientManager) {
        this.clientManager = Objects.requireNonNull(clientManager, "clientManager");
    }

    private Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    private void createSchemaIfNeeded() {
        try {
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(SQL_CREATE_TABLE_TASKS);
                stmt.executeUpdate(SQL_CREATE_TABLE_WORK_UNITS);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<JdbcTaskImpl> getTasks() {
        if (dirty) {
            tasks.clear();
            try {
                Connection conn = getConnection();
                Map<String, List<WorkUnit>> workUnits = new HashMap<>();
                try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_WORK_UNITS)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Date startTime = new Date(rs.getLong("start_time"));
                        Date endTime = new Date(rs.getLong("end_time"));
                        JdbcWorkUnitImpl workUnit = new JdbcWorkUnitImpl(startTime, endTime);
                        workUnit.id = rs.getString("id");
                        String taskId = rs.getString("task_id");
                        List<WorkUnit> l = workUnits.get(taskId);
                        if (l == null) {
                            l = new ArrayList<>();
                            workUnits.put(taskId, l);
                        }
                        l.add(workUnit);
                    }
                }
                try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_TASKS)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        JdbcTaskImpl task = new JdbcTaskImpl();
                        task.client = clientManager.getById(rs.getString("client_id"));
                        task.id = rs.getString("id");
                        task.title = rs.getString("title");
                        task.notes = rs.getString("notes");
                        task.finished = rs.getInt("finished") != 0;
                        task.workStartTime = rs.getObject("work_start_time") == null
                                ? null : new Date(rs.getLong("work_start_time"));
                        task.secondsWorked = rs.getInt("seconds_worked");
                        task.workUnits = workUnits.get(task.id);
                        tasks.add(task);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            dirty = false;
        }
        return tasks;
    }

    @Override
    public Task create(Client client) {
        return new JdbcTaskImpl(client);
    }

    @Override
    public int count() {
        return getTasks().size();
    }

    @Override
    public int countUnfinished() {
        int total = 0;
        for (TaskImpl t : getTasks()) {
            if (!t.isFinished()) {
                total++;
            }
        }
        return total;
    }
    
    @Override
    public Task get(int index) {
        return getTasks().get(index);
    }

    @Override
    public List<Task> getForClient(Client client, boolean unfinishedOnly) {
        List<Task> result = new ArrayList<>();
        for (Task task : getTasks()) {
            if ((!unfinishedOnly || !task.isFinished()) && task.getClient().equals(client)) {
                result.add(task);
            }
        }
        return result;
    }
    
    @Override
    public void save(Task task) {
        String id = ((JdbcTaskImpl) task).id;
        String sql = null;
        if (id == null) {
            // New task
            id = UUID.randomUUID().toString();
            ((JdbcTaskImpl) task).id = id;
            sql = SQL_INSERT_TASK;
        } else {
            sql = SQL_UPDATE_TASK;
        }
        try {
            Connection conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ((JdbcClientImpl) task.getClient()).id);
                stmt.setString(2, task.getTitle());
                stmt.setString(3, task.getNotes());
                stmt.setInt(4, task.isFinished() ? 1 : 0);
                stmt.setObject(5, task.getWorkStartTime() == null 
                        ? null : task.getWorkStartTime().getTime());
                stmt.setInt(6, task.getSecondsWorked());
                stmt.setString(7, id);
                stmt.executeUpdate();
            }
            for (WorkUnit wu : task.getWorkUnits()) {
                JdbcWorkUnitImpl workUnit = (JdbcWorkUnitImpl) wu;
                if (workUnit.id == null) {
                    try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_WORK_UNIT)) {
                        workUnit.id = UUID.randomUUID().toString();
                        stmt.setString(1, ((JdbcTaskImpl) task).id);
                        stmt.setLong(2, workUnit.getStartTime().getTime());
                        stmt.setLong(3, workUnit.getEndTime().getTime());
                        stmt.setString(4, workUnit.id);
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dirty = true;
        }
    }

    @Override
    public boolean delete(Task task) {
        String id = ((JdbcTaskImpl) task).id;
        try {
            Connection conn = getConnection();
            boolean found = false;
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_TASK)) {
                stmt.setString(1, id);
                found = stmt.executeUpdate() > 0;
            }
            if (found) {
                try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_DANGLING_WORK_UNITS)) {
                    stmt.executeUpdate();
                }
            }
            return found;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dirty = true;
        }
    }

    public void deleteForClient(Client client) {
        String id = ((JdbcClientImpl) client).id;
        try {
            Connection conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_TASKS_FOR_CLIENT)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_DANGLING_WORK_UNITS)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dirty = true;
        }
    }

    static class JdbcTaskImpl extends TaskImpl {
        String id = null;

        public JdbcTaskImpl() {}

        public JdbcTaskImpl(Client client) {
            super(client);
        }

        protected WorkUnitImpl createWorkUnit(Date startTime, Date endTime) {
            return new JdbcWorkUnitImpl(startTime, endTime);
        }

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
            JdbcTaskImpl other = (JdbcTaskImpl) obj;
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
            return String
                    .format("JdbcTaskImpl [id=%s, client=%s, title=%s, notes=%s, finished=%s, secondsWorked=%s, workStartTime=%s, workUnits=%s]",
                            id, client, title, notes, finished, secondsWorked, workStartTime, workUnits);
        }
    }

    static class JdbcWorkUnitImpl extends WorkUnitImpl {
        String id = null;

        public JdbcWorkUnitImpl(Date startTime, Date endTime) {
            super(startTime, endTime);
        }

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
            JdbcWorkUnitImpl other = (JdbcWorkUnitImpl) obj;
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
            return String.format("JdbcWorkUnitImpl [id=%s, startTime=%s, endTime=%s]", id, startTime, endTime);
        }
    }
}
