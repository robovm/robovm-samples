package org.robovm.samples.customframeworks;

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.Library;

@NativeClass("MySwiftFramework.SwiftAdder")
@Library(Library.INTERNAL)
public class SwiftAdder extends NSObject {
	@Method(selector="add:b:")
	public native int add(int a, int b);
}
