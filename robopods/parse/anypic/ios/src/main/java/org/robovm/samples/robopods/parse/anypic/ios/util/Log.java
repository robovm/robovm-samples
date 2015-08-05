package org.robovm.samples.robopods.parse.anypic.ios.util;

public class Log {
    public static void d(String message, Object... data) {
        System.out.println(String.format(message, data));
    }

    public static void e(String message, Object... data) {
        System.err.println(String.format(message, data));
    }
}
