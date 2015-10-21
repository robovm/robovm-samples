package org.robovm.samples.customframeworks.ui;

import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIStoryboard;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;
import org.robovm.samples.customframeworks.ObjCAdder;
import org.robovm.samples.customframeworks.SwiftAdder;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {
    @IBOutlet
    private UILabel result;
    @IBOutlet
    private UITextField val1;
    @IBOutlet
    private UITextField val2;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        result.setText("Enter 2 numbers");

        // this just demonstrates how to load a resources
        // from a framework. The storyboard is not really used.
        NSBundle bundle = NSBundle.getBundle("org.robovm.MySwiftFramework");
        UIStoryboard storyboard = new UIStoryboard("Storyboard", bundle);
    }

    @IBAction
    public void addViaObjc() {
        try {
            int a = Integer.parseInt(val1.getText());
            int b = Integer.parseInt(val2.getText());
            ObjCAdder adder = new ObjCAdder();
            result.setText("= " + adder.add(a, b));
        } catch (Throwable t) {
            result.setText("Check your numbers");
        }
    }

    @IBAction
    public void addViaSwift() {
        try {
            int a = Integer.parseInt(val1.getText());
            int b = Integer.parseInt(val2.getText());
            SwiftAdder adder = new SwiftAdder();
            result.setText("= " + adder.add(a, b));
        } catch (Throwable t) {
            t.printStackTrace();
            result.setText("Check your numbers");
        }
    }
}
