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
 * Portions of this code is based on Google Inc's Google Play Games 'Type a Number' sample
 * which is copyright (C) 2015 Google Inc.
 */
package org.robovm.samples.robopods.google.games.typenumber.ios;

public class Log {

    public static void d(String message, Object... data) {
        System.out.println(String.format(message, data));
    }

    public static void e(String message, Object... data) {
        System.err.println(String.format(message, data));
    }

}
