package org.robovm.samples.speakhere;

import java.util.Vector;

import org.robovm.apple.coregraphics.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;

public class SpeakHere extends UIApplicationDelegateAdapter
{
       private UIWindow window = null;
        private int clickCount = 0;

        @Override
        public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) 
        {

            final UIButton button = UIButton.create(UIButtonType.RoundedRect);
            button.setFrame(new CGRect(15.0f, 121.0f, 291.0f, 37.0f));
            button.setTitle("Click me!", UIControlState.Normal);

            button.addOnTouchUpInsideListener(new UIControl.OnTouchUpInsideListener() 
            {
                @Override
                public void onTouchUpInside(UIControl control, UIEvent event) 
                {
                    if (clickCount == 0)
                    {
                        button.setTitle("Recording for 5 seconds... (SPEAK!)", UIControlState.Normal);

                        Runnable r = new Runnable() 
                        {
                            public void run() 
                            {
                                try
                                {
                                    clickCount = 1;

                                    AudioRecord record = new AudioRecord();
                                    record.startRecording();

                                    long when = System.currentTimeMillis() + 5000;
                                    final Vector<byte[]> v = new Vector();
                                    byte[] ba = new byte[3072];
                                    while (System.currentTimeMillis() < when)
                                    {
                                        int n = 0;
                                        while (n<3072)
                                        {
                                            int i = record.read(ba, n, 3072-n);
                                            if (i==-1 || i == 0) break;
                                            n += i;
                                        }

                                        if (n>0)
                                        {
                                            byte[] ba2 = new byte[n];
                                            System.arraycopy(ba, 0, ba2, 0, n);
                                            v.addElement(ba2);
                                        }
                                    }

                                    System.out.println("DONE RECORDING");
                                    record.release();
                                    System.out.println("RECORDER STOPPED");

                                    System.out.println("Playing back recorded audio...");
                                    button.setTitle("Playing back recorded audio...", UIControlState.Normal);

                                    AudioTrack at = new AudioTrack();
                                    at.play();

                                    while (v.size() > 0) 
                                    {
                                        ba = v.remove(0);
                                        at.write(ba, 0, ba.length);
                                        Thread.yield();
                                    }
                                    at.stop();

                                    button.setTitle("DONE", UIControlState.Normal);
                                    System.out.println("FINISHED PIPING AUDIO");
                                }
                                catch (Exception x)
                                {
                                    x.printStackTrace();
                                    button.setTitle("ERROR: " + x.getMessage(), UIControlState.Normal);
                                }

                                clickCount = 0;
                            }
                        };

                        new Thread(r).start();
                    }
                }
            });

            window = new UIWindow(UIScreen.getMainScreen().getBounds());
            window.setBackgroundColor(UIColor.lightGray());
            window.addSubview(button);
            window.makeKeyAndVisible();

            return true;
        }

        public static void main(String[] args) 
        {
            try (NSAutoreleasePool pool = new NSAutoreleasePool()) 
            {
                UIApplication.main(args, null, SpeakHere.class);
            }
        }

}
