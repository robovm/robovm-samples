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
package org.robovm.samples.robopods.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import org.robovm.pods.android.AndroidConfig;

public class DialogAppActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        AndroidConfig.setLaunchActivity(this);

        final Button alertButton = (Button) findViewById(R.id.alertButton);
        final Button inputButton = (Button) findViewById(R.id.inputButton);
        final Button progressButton = (Button) findViewById(R.id.progressButton);

        alertButton.setOnClickListener(v -> DialogHandler.getInstance().showAlertDialogSample());
        inputButton.setOnClickListener(v -> DialogHandler.getInstance().showInputDialogSample());
        progressButton.setOnClickListener(v -> DialogHandler.getInstance().showProgressDialogSample());
    }
}
