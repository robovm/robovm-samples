/*
 * Copyright (C) 2014 Trillian Mobile AB
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

package org.robovm.samples.iap;

import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;

public class Product {
    // Products are organized by category
    private final String category;
    // Title of the product
    private final String title;
    // iTunes identifier of the product
    private final String productID;

    public Product (String category, String title, String productID) {
        this.category = category;
        this.title = title;
        this.productID = productID;
    }

    public Product (NSDictionary<NSString, NSObject> data) {
        this.category = data.get(new NSString("category")).toString();
        this.title = data.get(new NSString("title")).toString();
        this.productID = data.get(new NSString("identifier")).toString();
    }

    public String getCategory () {
        return category;
    }

    public String getTitle () {
        return title;
    }

    public String getProductID () {
        return productID;
    }
}
