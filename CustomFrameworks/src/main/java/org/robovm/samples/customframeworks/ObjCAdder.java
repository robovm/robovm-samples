package org.robovm.samples.customframeworks;

import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.Library;

@NativeClass("ObjCAdder")
@Library(Library.INTERNAL)
public class ObjCAdder extends NSObject {
	@Method(selector="add::")
	public native int add(int a, int b);
}
