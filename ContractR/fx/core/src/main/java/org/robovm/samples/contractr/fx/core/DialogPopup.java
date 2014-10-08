/*
 * Copyright (C) 2014 Trillian Mobile AB
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
package org.robovm.samples.contractr.fx.core;

import javafx.event.EventDispatcher;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * 
 */
public class DialogPopup extends Popup {

    private EventDispatcher oldEventDispatcher;
    
    public void show(Window window) {
        installDismissEventHandler(window);
        super.show(window);
    }

    public void hide() {
        unistallDismissEventHandler(getOwnerWindow());
        super.hide();
    }
    
    private void installDismissEventHandler(Window window) {
        oldEventDispatcher = window.getEventDispatcher();
        window.setEventDispatcher((event, tail) -> {
            EventType<?> eventType = event.getEventType();
            if (eventType == MouseEvent.MOUSE_PRESSED 
                    || eventType == TouchEvent.TOUCH_PRESSED) {
                // Dismiss
                hide();
            } else {
                // Event in the popup window.
                tail.dispatchEvent(event);
            }
            return null;
        });
    }
    
    private void unistallDismissEventHandler(Window window) {
        window.setEventDispatcher(oldEventDispatcher);
    }
}
