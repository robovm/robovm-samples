package org.robovm.samples.robopods.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class DialogAppActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final Button alertButton = (Button) findViewById(R.id.alertButton);
        final Button inputButton = (Button) findViewById(R.id.inputButton);
        final Button progressButton = (Button) findViewById(R.id.progressButton);

        alertButton.setOnClickListener(v -> {
            DialogHandler.getInstance().showAlertDialogSample();
        });
        inputButton.setOnClickListener(v -> {
            DialogHandler.getInstance().showInputDialogSample();
        });
        progressButton.setOnClickListener(v -> {
            DialogHandler.getInstance().showProgressDialogSample();
        });
    }
}
