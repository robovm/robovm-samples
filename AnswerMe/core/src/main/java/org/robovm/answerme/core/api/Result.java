/*
 * Copyright (C) 2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.robovm.answerme.core.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root of the result returned by the Instant Answer API. See <a
 * href=
 * "http://api.duckduckgo.com/?q=valley+forge+national+park&format=json&pretty=1"
 * >sample</a>.
 */
public class Result {
    @SerializedName("Heading")
    public String heading;
    @SerializedName("RelatedTopics")
    public List<Topic> relatedTopics;

    /**
     * Recursively extracts all {@link Topic}s in this {@link Result}.
     */
    public List<Topic> getTopics() {
        List<Topic> topics = new ArrayList<>();
        for (Topic t : this.relatedTopics) {
            if (t.text != null) {
                topics.add(t);
            }
        }
        for (Topic t : relatedTopics) {
            topics.addAll(t.getTopics());
        }
        return topics;
    }

    @Override
    public String toString() {
        return "Result{" +
                "heading='" + heading + '\'' +
                ", relatedTopics=" + relatedTopics +
                '}';
    }
}
