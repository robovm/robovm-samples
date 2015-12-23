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
import java.util.Collections;
import java.util.List;

/**
 * Represents a topic returned by the Instant Answer API. See <a href=
 * "http://api.duckduckgo.com/?q=valley+forge+national+park&format=json&pretty=1"
 * >sample</a>.
 */
public class Topic {
    @SerializedName("Result")
    public String result;
    @SerializedName("Text")
    public String text;
    @SerializedName("Icon")
    public Icon icon;
    @SerializedName("Topics")
    public List<Topic> topics;

    /**
     * Returns a nice display text from this {@link Topic} by extracting the
     * text of the {@code <a>} tag contained in the {@code Result} JSON value.
     */
    public String getDisplayText() {
        if (result != null) {
            String s = result.replaceAll("<a href[^>]*>", "");
            s = s.replaceAll("</a>", ": ");
            return s;
        }
        return text;
    }

    /**
     * Recursively extracts all {@link Topic}s in this {@link Topic}.
     */
    public List<Topic> getTopics() {
        if (this.topics == null) {
            return Collections.emptyList();
        }
        List<Topic> topics = new ArrayList<>();
        for (Topic t : this.topics) {
            if (t.text != null) {
                topics.add(t);
            }
        }
        for (Topic t : this.topics) {
            topics.addAll(t.getTopics());
        }
        return topics;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "result='" + result + '\'' +
                ", text='" + text + '\'' +
                ", icon=" + icon +
                ", topics=" + topics +
                '}';
    }
}
