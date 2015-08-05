/*
 * Copyright (C) 2013-2015 RoboVM AB
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
 * 
 * Portions of this code is based on Parse's AnyPic sample
 * which is copyright (C) 2013 Parse.
 */
package org.robovm.samples.robopods.parse.anypic.ios.model;

import org.robovm.pods.parse.PFObject;
import org.robovm.pods.parse.PFUser;
import org.robovm.pods.parse.ParseClassName;

@ParseClassName("Activity")
public class PAPActivity extends PFObject {
    public static final String TYPE_KEY = "type";
    public static final String FROM_USER_KEY = "fromUser";
    public static final String TO_USER_KEY = "toUser";
    public static final String CONTENT_KEY = "content";
    public static final String PHOTO_KEY = "photo";

    public PAPActivityType getType() {
        String activity = getString(TYPE_KEY);
        return PAPActivityType.findByKey(activity);
    }

    public void setType(PAPActivityType type) {
        put(TYPE_KEY, type.getKey());
    }

    public PAPUser getFromUser() {
        return (PAPUser) getParseUser(FROM_USER_KEY);
    }

    public void setFromUser(PFUser user) {
        put(FROM_USER_KEY, user);
    }

    public PAPUser getToUser() {
        return (PAPUser) getParseUser(TO_USER_KEY);
    }

    public void setToUser(PFUser user) {
        put(TO_USER_KEY, user);
    }

    public String getContent() {
        return getString(CONTENT_KEY);
    }

    public void setContent(String content) {
        put(CONTENT_KEY, content);
    }

    public PAPPhoto getPhoto() {
        return (PAPPhoto) get(PHOTO_KEY);
    }

    public void setPhoto(PAPPhoto photo) {
        put(PHOTO_KEY, photo);
    }
}
