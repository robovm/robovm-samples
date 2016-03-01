/*
 * Copyright (C) 2016 RoboVM AB
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
package org.robovm.samples.robopods.billing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import org.robovm.pods.android.AndroidConfig;

public class BillingSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidConfig.setLaunchActivity(this);

        // Setup the store as early as possible, ideally when the app is launched.
        AppStore.getInstance().setup();

        setContentView(R.layout.activity_my);

        final Button requestProductsButton = (Button) findViewById(R.id.requestProductsButton);
        requestProductsButton.setOnClickListener((view) -> {
            AppStore.getInstance().requestProductData();
        });

        final Button purchaseConsumableButton = (Button) findViewById(R.id.purchaseConsumableButton);
        purchaseConsumableButton.setOnClickListener((view) -> {
            AppStore.getInstance().purchaseProduct(AppStore.CONSUMABLE_PRODUCT1);
        });

        final Button purchaseNonConsumableButton = (Button) findViewById(R.id.purchaseNonConsumableButton);
        purchaseNonConsumableButton.setOnClickListener((view) -> {
            AppStore.getInstance().purchaseProduct(AppStore.NONCONSUMABLE_PRODUCT1_IOS);
        });

        final Button restoreTransactionsButton = (Button) findViewById(R.id.restoreTransactionsButton);
        restoreTransactionsButton.setOnClickListener((view) -> {
            AppStore.getInstance().restoreTransactions();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // This is necessary to make the library work. Will most likely change in future versions to be more configurable.
        AndroidConfig.onActivityResult(requestCode, resultCode, data);
    }
}
