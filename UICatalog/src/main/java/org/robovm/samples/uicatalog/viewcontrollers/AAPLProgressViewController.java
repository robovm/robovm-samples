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
 * Portions of this code is based on Apple Inc's UICatalog sample (v11.3)
 * which is copyright (C) 2008-2015 Apple Inc.
 */
package org.robovm.samples.uicatalog.viewcontrollers;

import org.robovm.apple.foundation.NSOperationQueue;
import org.robovm.apple.uikit.UIProgressView;
import org.robovm.apple.uikit.UIProgressViewStyle;
import org.robovm.apple.uikit.UITableViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.uicatalog.Colors;

@CustomClass("AAPLProgressViewController")
public class AAPLProgressViewController extends UITableViewController {
    private static final int MAX_PROGRESS = 100;

    private UIProgressView defaultStyleProgressView;
    private UIProgressView barStyleProgressView;
    private UIProgressView tintedProgressView;

    private NSOperationQueue operationQueue;
    private int completedProgress = -1;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Set the initial progress for all progress views.
        updateProgress(0);

        configureDefaultStyleProgressView();
        configureBarStyleProgressView();
        configureTintedProgressView();

        // As progress is received from another subsystem (i.e. NSProgress,
        // NSURLSessionTaskDelegate, etc.), update the progressView's progress.
        simulateProgress();
    }

    private void updateProgress(int completedProgress) {
        if (this.completedProgress != completedProgress) {
            float fractionalProgress = (float) completedProgress / MAX_PROGRESS;

            boolean animated = this.completedProgress != 0;

            defaultStyleProgressView.setProgress(fractionalProgress, animated);
            barStyleProgressView.setProgress(fractionalProgress, animated);
            tintedProgressView.setProgress(fractionalProgress, animated);

            this.completedProgress = completedProgress;
        }
    }

    private void configureDefaultStyleProgressView() {
        defaultStyleProgressView.setProgressViewStyle(UIProgressViewStyle.Default);
    }

    private void configureBarStyleProgressView() {
        barStyleProgressView.setProgressViewStyle(UIProgressViewStyle.Bar);
    }

    private void configureTintedProgressView() {
        tintedProgressView.setProgressViewStyle(UIProgressViewStyle.Default);

        tintedProgressView.setTrackTintColor(Colors.BLUE);
        tintedProgressView.setProgressTintColor(Colors.PURPLE);
    }

    private void simulateProgress() {
        // In this example we will simulate progress with a "sleep operation".
        operationQueue = new NSOperationQueue();

        for (int i = 0; i < MAX_PROGRESS; i++) {
            operationQueue.addOperation(new Runnable() {
                @Override
                public void run() {
                    // Delay the system for a random number of seconds.
                    // This code is not intended for production purposes. The
                    // "sleep" call is meant to simulate work done in another
                    // subsystem.
                    try {
                        Thread.sleep((long) Math.floor(Math.random() * 10) * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    NSOperationQueue.getMainQueue().addOperation(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress(completedProgress + 1);
                        }
                    });
                }
            });
        }
    }

    @IBOutlet
    private void setDefaultStyleProgressView(UIProgressView defaultStyleProgressView) {
        this.defaultStyleProgressView = defaultStyleProgressView;
    }

    @IBOutlet
    private void setBarStyleProgressView(UIProgressView barStyleProgressView) {
        this.barStyleProgressView = barStyleProgressView;
    }

    @IBOutlet
    private void setTintedProgressView(UIProgressView tintedProgressView) {
        this.tintedProgressView = tintedProgressView;
    }
}
