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
import java.util.Objects;

import org.robovm.samples.contractr.core.Client;

/**
 * Default implementation of {@link Client}.
 */
class ClientImpl implements Client {
    protected String name;
    protected BigDecimal hourlyRate = BigDecimal.ZERO;

    public ClientImpl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = Objects.requireNonNull(name, "name").trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Empty name");
        }
        this.name = name;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((hourlyRate == null) ? 0 : hourlyRate.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        ClientImpl other = (ClientImpl) obj;
        if (hourlyRate == null) {
            if (other.hourlyRate != null) {
                return false;
            }
        } else if (!hourlyRate.equals(other.hourlyRate)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ClientImpl [name=" + name + ", hourlyRate=" + hourlyRate + "]";
    }
}
