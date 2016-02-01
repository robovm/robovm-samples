package org.robovm.samples.robopods.billing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import org.robovm.pods.Platform;
import org.robovm.pods.Platform.AndroidPlatform;

public class BillingSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ((AndroidPlatform) Platform.getPlatform()).onActivityResult(requestCode, resultCode, data);
    }
}
