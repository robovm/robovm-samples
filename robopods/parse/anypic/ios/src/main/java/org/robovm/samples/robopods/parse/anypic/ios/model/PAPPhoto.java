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

import org.robovm.pods.parse.PFFile;
import org.robovm.pods.parse.PFObject;
import org.robovm.pods.parse.PFUser;
import org.robovm.pods.parse.ParseClassName;

@ParseClassName("Photo")
public class PAPPhoto extends PFObject {
    public static final String PICTURE_KEY = "image";
    public static final String THUMBNAIL_KEY = "thumbnail";
    public static final String USER_KEY = "user";
    public static final String OPEN_GRAPH_ID_KEY = "fbOpenGraphID";

    public PFFile getPicture() {
        return (PFFile) get(PICTURE_KEY);
    }

    public void setPicture(PFFile picture) {
        put(PICTURE_KEY, picture);
    }

    public PFFile getThumbnail() {
        return (PFFile) get(THUMBNAIL_KEY);
    }

    public void setThumbnail(PFFile thumbnail) {
        put(THUMBNAIL_KEY, thumbnail);
    }

    public PAPUser getUser() {
        return (PAPUser) get(USER_KEY);
    }

    public void setUser(PFUser user) {
        put(USER_KEY, user);
    }
}
