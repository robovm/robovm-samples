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

import org.robovm.samples.robopods.parse.anypic.ios.util.DefaultMap;

public class PAPUserAttributes {
    private static final String PHOTO_COUNT_KEY = "photoCount";
    private static final String IS_FOLLOWED_BY_CURRENT_USER_KEY = "isFollowedByCurrentUser";

    private final DefaultMap data;

    public PAPUserAttributes() {
        data = new DefaultMap();
    }

    public PAPUserAttributes(int photoCount, boolean isFollowedByCurrentUser) {
        data = new DefaultMap();
        setPhotoCount(photoCount).setIsFollowedByCurrentUser(isFollowedByCurrentUser);
    }

    public int getPhotoCount() {
        return data.getInt(PHOTO_COUNT_KEY, 0);
    }

    public PAPUserAttributes setPhotoCount(int photoCount) {
        data.put(PHOTO_COUNT_KEY, photoCount);
        return this;
    }

    public boolean isFollowedByCurrentUser() {
        return data.getBoolean(IS_FOLLOWED_BY_CURRENT_USER_KEY, false);
    }

    public PAPUserAttributes setIsFollowedByCurrentUser(boolean isFollowedByCurrentUser) {
        data.put(IS_FOLLOWED_BY_CURRENT_USER_KEY, isFollowedByCurrentUser);
        return this;
    }
}
