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

import java.math.BigDecimal;

/**
 * Represents a contractor's client.
 */
public interface Client {

    /**
     * Returns the client's name.
     */
    String getName();

    /**
     * Sets the client's name.
     */
    void setName(String name);

    /**
     * Returns the hourly rate for this client.
     */
    BigDecimal getHourlyRate();

    /**
     * Sets the hourly rate for this client.
     */
    void setHourlyRate(BigDecimal hourlyRate);
}
