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

import java.util.Date;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.service.TaskImpl;

/**
 * Test {@link Task} implementation.
 */
public class TestTask extends TaskImpl {

    public TestTask(Client client, String title, String notes, boolean finished, 
            int secondsWorked, Date workStartTime) {

        this.client = client;
        this.title = title;
        this.notes = notes;
        this.finished = finished;
        this.secondsWorked = secondsWorked;
        this.workStartTime = workStartTime;
    }

}
