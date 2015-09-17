package org.robovm.samples.customframeworks;

import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.uikit.UILabel;
import org.robovm.apple.uikit.UIStoryboard;
import org.robovm.apple.uikit.UITextField;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("MyViewController")
public class MyViewController extends UIViewController {
    private UILabel result;
    private UITextField val1;
    private UITextField val2;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        // this just demonstrates how to load a resources
        // from a framework. The storyboard is not really used.
        NSBundle bundle = NSBundle.getBundle("org.robovm.MySwiftFramework");
        UIStoryboard storyboard = new UIStoryboard("Storyboard", bundle);
    }

    @IBOutlet
    public void setResult(UILabel result) {
        this.result = result;
        this.result.setText("Enter 2 numbers");
    }

    @IBOutlet
    public void setVal1(UITextField val1) {
        this.val1 = val1;
    }

    @IBOutlet
    public void setVal2(UITextField val2) {
        this.val2 = val2;
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
