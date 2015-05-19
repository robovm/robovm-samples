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

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task.
 */
public interface Task {

    /**
     * Returns the {@link Client} this task belongs to.
     */
    Client getClient();

    /**
     * Returns the {@link Client} this task belongs to.
     */
    void setClient(Client client);

    /**
     * Returns the title of this task.
     */
    String getTitle();

    /**
     * Sets the title of this task.
     */
    void setTitle(String title);

    /**
     * Returns any notes saved for this task.
     */
    String getNotes();

    /**
     * Sets notes for this task.
     */
    void setNotes(String notes);

    /**
     * Returns whether this task is finished.
     */
    boolean isFinished();

    /**
     * Sets whether this {@link Task} is finished.
     */
    void setFinished(boolean b);

    /**
     * Returns the {@link Date} and time when work started on this {@link Task}.
     * Returns {@code null} if this {@link Task} is not being worked on.
     */
    Date getWorkStartTime();

    /**
     * Sets the {@link Date} and time when work started on this {@link Task}.
     * Set to {@code null} if this {@link Task} is not being worked on.
     */
    void setWorkStartTime(Date date);

    /**
     * Returns the seconds worked on this {@link Task}. Does not include work
     * currently being done on the {@link Task}.
     */
    int getSecondsWorked();

    /**
     * Sets the seconds worked on this {@link Task}.
     */
    void setSecondsWorked(int secondsWorked);

    /**
     * Returns the number of seconds elapsed for this {@link Task} taking into
     * account work currently being done.
     */
    int getSecondsElapsed();

    /**
     * Returns a textual representation of the time elapsed for this
     * {@link Task} suitable for display in a UI.
     */
    String getTimeElapsed();

    /**
     * Returns a textual representation of amount of money earned for this
     * {@link Task} suitable for display in a UI.
     */
    String getAmountEarned(Locale locale);

    /**
     * Returns the {@link WorkUnit}s for this {@link Task}.
     */
    List<WorkUnit> getWorkUnits();

    /**
     * Adds a {@link WorkUnit} to this {@link Task}.
     */
    void addWorkUnit(Date startTime, Date endTime);
}
