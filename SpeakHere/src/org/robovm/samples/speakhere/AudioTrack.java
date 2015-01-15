package org.robovm.samples.speakhere;

import java.lang.reflect.Method;
import java.util.Vector;

import org.robovm.apple.audiotoolbox.AudioQueue;
import org.robovm.apple.audiotoolbox.AudioQueueBuffer;
import org.robovm.apple.audiotoolbox.AudioQueue.AudioQueuePtr;
import org.robovm.apple.audiotoolbox.AudioQueueBuffer.AudioQueueBufferPtr;
import org.robovm.apple.audiotoolbox.AudioQueueError;
import org.robovm.apple.audiotoolbox.AudioQueueParam;
import org.robovm.apple.coreaudio.AudioFormat;
import org.robovm.apple.coreaudio.AudioStreamBasicDescription;
import org.robovm.apple.coreaudio.CoreAudio;
import org.robovm.rt.bro.Bro;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.Callback;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.ptr.BytePtr;
import org.robovm.rt.bro.ptr.FunctionPtr;
import org.robovm.rt.bro.ptr.VoidPtr;


public class AudioTrack {

    public static final int MODE_STREAM = -1;

    private int kNumberBuffers = 3;
    private Vector<byte[]> mData = new Vector<>();
    private int mStateID = -1;
    private boolean mRunning = false;

    protected double mSampleRate;
    protected AudioFormat mFormatID;
    protected int mFormatFlags;
    protected int mBytesPerPacket;
    protected int mFramesPerPacket;
    protected int mBytesPerFrame;
    protected int mChannelsPerFrame;
    protected int mBitsPerChannel;  

    protected AudioQueue mQueue = null;

    public AudioTrack() 
    {
        mSampleRate = 44100;
        mFormatID = AudioFormat.LinearPCM;
        mFormatFlags = CoreAudio.AudioFormatFlagIsPacked | CoreAudio.AudioFormatFlagIsSignedInteger;
        mBytesPerPacket = 2;
        mFramesPerPacket = 1;
        mBytesPerFrame = 2;
        mChannelsPerFrame = 1;
        mBitsPerChannel = 16;    
    }

    public static int getMinBufferSize(int sampleRate, int channelConfigurationMono, int encodingPcm16bit) 
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int deriveBufferSize(AudioStreamBasicDescription ASBDescription, int maxPacketSize, double seconds)
    {
        int maxBufferSize = 0x50000;
        int minBufferSize = 0x4000;

        double numPacketsForTime = ASBDescription.getMSampleRate() / ASBDescription.getMFramesPerPacket() * seconds;
        int outBufferSize = (int)(numPacketsForTime * maxPacketSize);
        if (outBufferSize > maxBufferSize) return maxBufferSize;
        if (outBufferSize < minBufferSize) return minBufferSize;
        return outBufferSize;
    }

    /*<bind>*/static { Bro.bind(AudioTrack.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*//*</constructors>*/
    /*<properties>*//*</properties>*/
    /*<members>*//*</members>*/
    @Callback
    public static void callbackMethod(
            @Pointer long                     refcon,
            AudioQueue                        inAQ,
            AudioQueueBuffer                  inBuffer
        )
    {
        System.out.println("In Callback");
        AQPlayerState.AQPlayerStatePtr ptr = new AQPlayerState.AQPlayerStatePtr();
        ptr.set(refcon);
        AQPlayerState aqps = ptr.get();
        AudioTrack me = aqps.getTrack();
        me.nextChunk(inAQ, inBuffer);
    }

    private void nextChunk(AudioQueue inAQ, AudioQueueBuffer inBuffer) 
    {
        byte[] ba = null;
        long when = System.currentTimeMillis() + 30000;
        while (mRunning && System.currentTimeMillis() < when)
        {
            if (mData.size() > 0)
            {
                ba = mData.remove(0);
                break;
            }
            try { Thread.yield(); } catch (Exception x) { x.printStackTrace(); }
        }
        if (ba == null) ba = new byte[0];
        System.out.println("PLAYING BYTES: "+ba.length);

        if (ba.length>0)
        {
            VoidPtr vp = inBuffer.getMAudioData();
            BytePtr bp = vp.as(BytePtr.class); //Struct.allocate(BytePtr.class, ba.length);
            bp.set(ba);
//          inBuffer.setMAudioData(vp);
            inBuffer.setMAudioDataByteSize(ba.length);
        }
        mQueue.enqueueBuffer(inBuffer, 0, null);
    }

    public void play() 
    {
        final AudioTrack me = this;

        Runnable r = new Runnable() 
        {
            public void run() 
            {
                AudioStreamBasicDescription asbd = new AudioStreamBasicDescription(mSampleRate, mFormatID, mFormatFlags, mBytesPerPacket, mFramesPerPacket, mBytesPerFrame, mChannelsPerFrame, mBitsPerChannel, 0);
                AudioQueuePtr mQueuePtr = new AudioQueuePtr();
                Method callbackMethod = null;
                Method[] methods = me.getClass().getMethods();
                int i = methods.length;
                while (i-->0) if (methods[i].getName().equals("callbackMethod")) 
                {
                    callbackMethod = methods[i];
                    break;
                }

                FunctionPtr fp = new FunctionPtr(callbackMethod );

                AQPlayerState aqData = new AQPlayerState(me);
                mStateID = aqData.mID();
                VoidPtr vp = aqData.as(VoidPtr.class);
//              AudioQueueError aqe = AudioQueue.newOutput(asbd, fp, vp, CFRunLoop.getCurrent(), new CFString(CFRunLoopMode.Common.value()), 0, mQueuePtr);
                AudioQueueError aqe = AudioQueue.newOutput(asbd, fp, vp, null, null, 0, mQueuePtr);
                System.out.println(aqe.name());
                mQueue = mQueuePtr.get();

                int bufferByteSize = deriveBufferSize(asbd, 2, 0.5);
                System.out.println("BUFFER SIZE: "+bufferByteSize);

                System.out.println("Volume PARAM:"+(int)AudioQueueParam.Volume.value());
                mQueue.setParameter((int)AudioQueueParam.Volume.value(), 1.0f);

                mRunning = true;

                AudioQueueBufferPtr mBuffers = Struct.allocate(AudioQueueBufferPtr.class, kNumberBuffers);
                AudioQueueBufferPtr[] buffers = mBuffers.toArray(kNumberBuffers);

                for (i = 0; i < kNumberBuffers; ++i) 
                {
                    mQueue.allocateBuffer(bufferByteSize, buffers[i]);
                    nextChunk(mQueue, buffers[i].get());
                }

                System.out.println("STARTING QUEUE");
                mQueue.start(null);
                System.out.println("QUEUE STARTED");
/*              
                System.out.println("RUNNING LOOP");

                do
                {
                    System.out.print(".");

                    CFRunLoop.runInMode(CFRunLoopMode.Default, 0.25, false);

                    System.out.print("#");

                }
                while (mRunning);

                System.out.println("!!!");

                CFRunLoop.runInMode(CFRunLoopMode.Default, 1, false);

                System.out.println("DONE RUNNING LOOP");

                mQueue.stop(true);
                AQPlayerState.drop(mStateID);

                System.out.println("QUEUE STOPPED");
*/
            }
        };

        new Thread(r).start();
    }

    public void write(byte[] ba, int i, int length) 
    {
        while (mData.size() > 10) Thread.yield();

        System.out.println("SOUND IN: "+length+" bytes");
        mData.addElement(ba);
    }

    public void stop() 
    {
        System.out.println("STOPPING AUDIO PLAYER");
        mRunning = false;
        mQueue.stop(true);
        AQPlayerState.drop(mStateID);
    }

    public void release() 
    {
        // TODO Auto-generated method stub

    }

}
