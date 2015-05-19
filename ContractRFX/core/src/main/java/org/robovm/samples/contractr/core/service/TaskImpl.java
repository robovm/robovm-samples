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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.WorkUnit;

/**
 * Default implementation of {@link Task}.
 */
class TaskImpl implements Task {
    protected Client client;
    protected String title;
    protected String notes;
    protected boolean finished;
    protected int secondsWorked;
    protected Date workStartTime;
    protected List<WorkUnit> workUnits;

    protected TaskImpl() {}

    public TaskImpl(Client client) {
        this.client = Objects.requireNonNull(client, "client");
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getSecondsWorked() {
        return secondsWorked;
    }

    public void setSecondsWorked(int secondsWorked) {
        this.secondsWorked = secondsWorked;
    }

    public Date getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(Date workStartTime) {
        this.workStartTime = workStartTime;
    }

    public int getSecondsElapsed() {
        if (workStartTime == null) {
            return secondsWorked;
        }
        long elapsed = System.currentTimeMillis() - workStartTime.getTime();
        return (int) (elapsed / 1000) + getSecondsWorked();
    }

    public String getTimeElapsed() {
        int seconds = getSecondsElapsed();
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    public String getAmountEarned(Locale locale) {
        BigDecimal amount = client.getHourlyRate().multiply(BigDecimal.valueOf(getSecondsElapsed() / 3600.0));
        return NumberFormat.getCurrencyInstance(locale).format(amount);
    }

    public List<WorkUnit> getWorkUnits() {
        if (workUnits == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(workUnits);
    }

    public void addWorkUnit(Date startTime, Date endTime) {
        if (workUnits == null) {
            workUnits = new ArrayList<>();
        }
        workUnits.add(createWorkUnit(startTime, endTime));
    }

    protected WorkUnitImpl createWorkUnit(Date startTime, Date endTime) {
        return new WorkUnitImpl(startTime, endTime);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((client == null) ? 0 : client.hashCode());
        result = prime * result + (finished ? 1231 : 1237);
        result = prime * result + ((notes == null) ? 0 : notes.hashCode());
        result = prime * result + secondsWorked;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((workStartTime == null) ? 0 : workStartTime.hashCode());
        result = prime * result + ((workUnits == null) ? 0 : workUnits.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TaskImpl other = (TaskImpl) obj;
        if (client == null) {
            if (other.client != null) {
                return false;
            }
        } else if (!client.equals(other.client)) {
            return false;
        }
        if (finished != other.finished) {
            return false;
        }
        if (notes == null) {
            if (other.notes != null) {
                return false;
            }
        } else if (!notes.equals(other.notes)) {
            return false;
        }
        if (secondsWorked != other.secondsWorked) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (workStartTime == null) {
            if (other.workStartTime != null) {
                return false;
            }
        } else if (!workStartTime.equals(other.workStartTime)) {
            return false;
        }
        if (workUnits == null) {
            if (other.workUnits != null) {
                return false;
            }
        } else if (!workUnits.equals(other.workUnits)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("TaskImpl [client=%s, title=%s, notes=%s, finished=%s, secondsWorked=%s, workStartTime=%s, workUnits=%s]",
                        client, title, notes, finished, secondsWorked, workStartTime, workUnits);
    }
}
