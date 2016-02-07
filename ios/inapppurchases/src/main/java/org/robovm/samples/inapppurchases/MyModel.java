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
 * Portions of this code is based on Apple Inc's StoreKitSuite sample (v1.0)
 * which is copyright (C) 2014 Apple Inc.
 */

package org.robovm.samples.inapppurchases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyModel {
    // Products/Purchases are organized by category
    private final String name;

    // List of products/purchases
    private final List<Object> elements = new ArrayList<>();

    public MyModel(String name, Object... elements) {
        this.name = name;
        if (elements != null) {
            Collections.addAll(this.elements, elements);
        }
    }

    // Check whether there are products/purchases
    public boolean isEmpty() {
        return elements.size() == 0;
    }

    public String getName() {
        return name;
    }

    public List<?> getElements() {
        return elements;
    }

    public void setElements(List<?> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
    }
}
