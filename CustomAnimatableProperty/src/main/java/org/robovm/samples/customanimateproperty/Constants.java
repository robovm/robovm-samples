 /*
 * Copyright (C) 2014 RoboVM AB
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
 * Portions of this code is based on Apple Inc's QuickContacts sample (v1.0)
 * which is copyright (C) 2008-2013 Apple Inc.
 * 
 * The view controller creates a few bulb views which host the custom layer subclass.
 */
package org.robovm.samples.customanimateproperty;

public class Constants {

	public static boolean IMPLICIT = false;
	public static boolean EXPLICIT = true;
	public static boolean BASIC = false; 
	public static boolean KEYFRAME = true;

	// Set Animation Trigger to 1 for explicit animations, 0 for implict animations.
	public static boolean ANIMATION_TRIGGER_EXPLICIT = true;

	// Set Animation Type to 0 for basic animation (basic interpolation), 1 for keyframe animation.  
	public static boolean ANIMATION_TYPE_KEYFRAME = true;
	
}
