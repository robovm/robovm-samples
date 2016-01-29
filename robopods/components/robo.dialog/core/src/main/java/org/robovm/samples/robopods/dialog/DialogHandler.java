package org.robovm.samples.robopods.dialog;

import org.robovm.pods.dialog.*;

public class DialogHandler {
    private static final DialogHandler instance = new DialogHandler();

    private DialogHandler() {}

    public static DialogHandler getInstance() {
        return instance;
    }

    public void showAlertDialogSample() {
        String title = "Alert!";
        String message = "This is an alert dialog!";
        String cancel = "Cancel";
        String ok = "OK";

        AlertDialog dialog = new AlertDialog.Builder()
                .setTitle(title)
                .setMessage(message)
                .addButton(new DialogButton(cancel, DialogButtonStyle.Cancel, (d, button) -> {
                    System.out.println("Cancel has been tapped!");
                }))
                .addButton(new DialogButton(ok, DialogButtonStyle.Default, (d, button) -> {
                    System.out.println("OK has been tapped!");
                }))
                .build();
        dialog.show();
    }

    public void showInputDialogSample() {
        String title = "Input!";
        String message = "This is an alert dialog!";
        String ok = "OK";

        InputDialog dialog = new InputDialog.Builder()
                .setTitle(title)
                .setMessage(message)
                .addButton(new DialogButton(ok, (d, button) -> {
                    System.out.println("OK has been tapped! Input is: " + d.getTextInput());
                }))
                .setTextInputChangeListener((d, input) -> {
                    System.out.println("Input changed: " + input);
                })
                .build();
        dialog.show();
    }

    public void showProgressDialogSample() {
        String title = "Downloading...";
        String message = "Stand by!";

        ProgressDialog dialog = new ProgressDialog.Builder()
                .setTitle(title)
                .setMessage(message)
                .setProgressStyle(ProgressDialogStyle.Determinate)
                .build();
        dialog.show();

        new Thread(() -> {
            double progress = 0;
            while (progress < 1) {
                // Delay the system for a random number of seconds.
                // This code is not intended for production purposes. The
                // "sleep" call is meant to simulate work done in another
                // subsystem.

                try {
                    Thread.sleep((long) Math.floor(Math.random() * 2) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progress += 0.1;

                dialog.setProgress(progress); // Already dispatched on ui thread.
            }
            dialog.hide();
        }).start();
    }
}
