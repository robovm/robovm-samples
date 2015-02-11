
package org.robovm.samples.speakhere;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Method;

import org.robovm.apple.audiotoolbox.AudioQueue;
import org.robovm.apple.audiotoolbox.AudioQueue.AudioQueuePtr;
import org.robovm.apple.audiotoolbox.AudioQueueBuffer;
import org.robovm.apple.audiotoolbox.AudioQueueBuffer.AudioQueueBufferPtr;
import org.robovm.apple.audiotoolbox.AudioQueueError;
import org.robovm.apple.coreaudio.AudioFormat;
import org.robovm.apple.coreaudio.AudioStreamBasicDescription;
import org.robovm.apple.coreaudio.AudioStreamPacketDescription.AudioStreamPacketDescriptionPtr;
import org.robovm.apple.coreaudio.AudioTimeStamp.AudioTimeStampPtr;
import org.robovm.apple.coreaudio.CoreAudio;
import org.robovm.apple.corefoundation.CFRunLoopMode;
import org.robovm.rt.VM;
import org.robovm.rt.bro.Bro;
import org.robovm.rt.bro.Struct;
import org.robovm.rt.bro.annotation.Callback;
import org.robovm.rt.bro.annotation.Library;
import org.robovm.rt.bro.annotation.Pointer;
import org.robovm.rt.bro.ptr.FunctionPtr;
import org.robovm.rt.bro.ptr.VoidPtr;

/*<annotations>*/@Library("AudioToolbox")
/* </annotations> */
public class AudioRecord {
    protected double mSampleRate;
    protected AudioFormat mFormatID;
    protected int mFormatFlags;
    protected int mBytesPerPacket;
    protected int mFramesPerPacket;
    protected int mBytesPerFrame;
    protected int mChannelsPerFrame;
    protected int mBitsPerChannel;

    protected AudioQueue mQueue = null;

    private final int kNumberBuffers = 3;
    private final PipedInputStream mPIS;
    private final PipedOutputStream mPOS;
    private int mStateID = -1;

    private boolean mRunning = false;

    public AudioRecord () throws IOException {
        mSampleRate = 44100;
        mFormatID = AudioFormat.LinearPCM;
        mFormatFlags = CoreAudio.AudioFormatFlagIsPacked | CoreAudio.AudioFormatFlagIsSignedInteger;
        mBytesPerPacket = 2;
        mFramesPerPacket = 1;
        mBytesPerFrame = 2;
        mChannelsPerFrame = 1;
        mBitsPerChannel = 16;

        mPOS = new PipedOutputStream();
        mPIS = new PipedInputStream(mPOS);
    }

    public static int getMinBufferSize (int sampleRate, int channelConfig, int audioFormat) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int deriveBufferSize (AudioQueue audioQueue, AudioStreamBasicDescription ASBDescription, double seconds) {
        int maxBufferSize = 0x50000;
        int maxPacketSize = ASBDescription.getMBytesPerPacket();
        double numBytesForTime = ASBDescription.getMSampleRate() * maxPacketSize * seconds;
        return (int)(numBytesForTime < maxBufferSize ? numBytesForTime : maxBufferSize);
    }

    public void release () {
        System.out.println("RECORD QUEUE STOPPING...");
        mRunning = false;
        mQueue.stop(true);
// mQueue.dispose(true);
        System.out.println("RECORD QUEUE STOPPED");
        try {
            mPOS.close();
            mPIS.close();
            AQRecorderState.drop(mStateID);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public int read (byte[] abData, int i, int length) throws IOException {
        return mPIS.read(abData, i, length);
    }

    /* <bind> */static {
        Bro.bind(AudioRecord.class);
    }/* </bind> */

    /* <constants> *//* </constants> */
    /* <constructors> *//* </constructors> */
    /* <properties> *//* </properties> */
    /* <members> *//* </members> */
    @Callback
    public static void callbackMethod (@Pointer long refcon, AudioQueue inAQ, AudioQueueBuffer inBuffer,
        AudioTimeStampPtr inStartTime, int inNumPackets, AudioStreamPacketDescriptionPtr inPacketDesc) {
        try {
            AQRecorderState.AQRecorderStatePtr ptr = new AQRecorderState.AQRecorderStatePtr();
            ptr.set(refcon);
            AQRecorderState aqrs = ptr.get();
            byte[] ba = VM.newByteArray(inBuffer.getMAudioData().getHandle(), inBuffer.getMAudioDataByteSize());
            aqrs.getRecord().receive(ba);
        } catch (Exception x) {
            x.printStackTrace();
        }

        inAQ.enqueueBuffer(inBuffer, 0, null);
    }

    private void receive (byte[] ba) {
        if (mRunning) try {
            mPOS.write(ba);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void startRecording () throws Exception {
        AudioStreamBasicDescription asbd = new AudioStreamBasicDescription(mSampleRate, mFormatID, mFormatFlags, mBytesPerPacket,
            mFramesPerPacket, mBytesPerFrame, mChannelsPerFrame, mBitsPerChannel, 0);
        AudioQueuePtr mQueuePtr = new AudioQueuePtr();
        AudioQueueBufferPtr mBuffers = Struct.allocate(AudioQueueBufferPtr.class, kNumberBuffers);
        AQRecorderState aqData = new AQRecorderState(this);
        mStateID = aqData.mID();
        Method callbackMethod = null;
        Method[] methods = this.getClass().getMethods();
        int i = methods.length;
        while (i-- > 0)
            if (methods[i].getName().equals("callbackMethod")) {
                callbackMethod = methods[i];
                break;
            }
        FunctionPtr fp = new FunctionPtr(callbackMethod);
        VoidPtr vp = aqData.as(VoidPtr.class);

        AudioQueueError aqe = AudioQueue.newInput(asbd, fp, vp, null, null, 0, mQueuePtr);
        System.out.println(CFRunLoopMode.Common.value());
        System.out.println(aqe.name());
        mQueue = mQueuePtr.get();
        int bufferByteSize = deriveBufferSize(mQueue, asbd, 0.5);
        System.out.println("BUFFER SIZE: " + bufferByteSize);

        AudioQueueBufferPtr[] buffers = mBuffers.toArray(kNumberBuffers);
        for (i = 0; i < kNumberBuffers; ++i) {
            mQueue.allocateBuffer(bufferByteSize, buffers[i]);
            mQueue.enqueueBuffer(buffers[i].get(), 0, null);
        }

        mRunning = true;
        mQueue.start(null);
    }

}
