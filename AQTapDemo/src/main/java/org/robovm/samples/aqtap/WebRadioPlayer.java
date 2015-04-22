/*
 * Copyright (C) 2013-2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.samples.aqtap;

import org.robovm.apple.audiotoolbox.AUGraph;
import org.robovm.apple.audiotoolbox.AudioFileStream;
import org.robovm.apple.audiotoolbox.AudioFileStreamMutablePropertyFlags;
import org.robovm.apple.audiotoolbox.AudioFileStreamParseFlags;
import org.robovm.apple.audiotoolbox.AudioFileStreamProperty;
import org.robovm.apple.audiotoolbox.AudioFileType;
import org.robovm.apple.audiotoolbox.AudioQueue;
import org.robovm.apple.audiotoolbox.AudioQueueBuffer;
import org.robovm.apple.audiotoolbox.AudioQueueProcessingTap;
import org.robovm.apple.audiotoolbox.AudioQueueProcessingTap.ProcessingTapCallback;
import org.robovm.apple.audiotoolbox.AudioQueueProcessingTapFlags;
import org.robovm.apple.audiotoolbox.AudioQueueProcessingTapMutableFlags;
import org.robovm.apple.audiounit.AUMutableRenderActionFlags;
import org.robovm.apple.audiounit.AUParameterNewTimePitch;
import org.robovm.apple.audiounit.AURenderActionFlags;
import org.robovm.apple.audiounit.AURenderCallback;
import org.robovm.apple.audiounit.AUScope;
import org.robovm.apple.audiounit.AUTypeConverter;
import org.robovm.apple.audiounit.AUTypeOutput;
import org.robovm.apple.audiounit.AudioComponentDescription;
import org.robovm.apple.audiounit.AudioUnit;
import org.robovm.apple.coreaudio.AudioBufferList;
import org.robovm.apple.coreaudio.AudioStreamBasicDescription;
import org.robovm.apple.coreaudio.AudioStreamPacketDescription;
import org.robovm.apple.coreaudio.AudioTimeStamp;
import org.robovm.apple.coreaudio.AudioTimeStampFlags;
import org.robovm.apple.corefoundation.OSStatusException;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.foundation.NSURLConnection;
import org.robovm.apple.foundation.NSURLConnectionDataDelegateAdapter;
import org.robovm.apple.foundation.NSURLRequest;
import org.robovm.rt.bro.annotation.Pointer;

public class WebRadioPlayer implements AudioQueue.OutputCallback, ProcessingTapCallback, AURenderCallback,
        AudioFileStream.ParseListener {
    private final NSURL stationURL;
    private AudioFileStream audioFileStream;
    private AudioStreamBasicDescription dataFormat;
    private AudioQueue audioQueue;
    private AudioQueueProcessingTap aqTap;
    private AudioUnit genericOutputUnit;
    private AudioUnit effectUnit;
    private AudioUnit convertToEffectUnit;
    private AudioUnit convertFromEffectUnit;
    private AUGraph auGraph;
    private final AudioTimeStamp renderTimeStamp = new AudioTimeStamp();
    /**
     * Handles to our audio buffers.
     */
    private final long[] preRenderData = new long[20];

    private int totalPacketsReceived;

    private class ConnectionDelegate extends NSURLConnectionDataDelegateAdapter {
        private final AudioFileStream audioFileStream;

        public ConnectionDelegate(AudioFileStream stream) {
            this.audioFileStream = stream;
        }

        @Override
        public void didReceiveData(NSURLConnection connection, NSData data) {
            try {
                audioFileStream.parseBytes(data.getBytes(), AudioFileStreamParseFlags.None);
            } catch (OSStatusException e) {
                throw new Error(e);
            }
        }
    }

    public WebRadioPlayer(NSURL radioStationURL) {
        this.stationURL = radioStationURL;
    }

    public void start() {
        try {
            audioFileStream = AudioFileStream.open(this, AudioFileType.MP3);
        } catch (OSStatusException e) {
            throw new Error(e);
        }

        NSURLRequest request = new NSURLRequest(stationURL);
        new NSURLConnection(request, new ConnectionDelegate(audioFileStream));
    }

    public void setPitch(float pitch) {
        if (effectUnit == null)
            return;

        try {
            effectUnit.setParameter(AUParameterNewTimePitch.Pitch, pitch, AUScope.Global);
        } catch (OSStatusException e) {
            throw new Error(e);
        }
    }

    @Override
    public void onOutput(AudioQueue queue, long buffer) {
        try {
            queue.freeBuffer(buffer);
        } catch (OSStatusException e) {
            throw new Error(e);
        }
    }

    @Override
    public int process(AudioQueueProcessingTap aqTap, int numberFrames, AudioTimeStamp timeStamp,
            AudioQueueProcessingTapMutableFlags flags, AudioBufferList data) {
        int sourceFrames = 0;
        try {
            sourceFrames = aqTap.getSourceAudio(numberFrames, timeStamp, flags, data);

            for (int channel = 0; channel < data.getBufferCount(); channel++) {
                preRenderData[channel] = data.getBuffer(channel).getDataPointer();
                data.setBuffer(channel, 0);
            }

            renderTimeStamp.setFlags(AudioTimeStampFlags.SampleTimeValid);

            genericOutputUnit.render(AURenderActionFlags.None, renderTimeStamp, 0, numberFrames, data);
        } catch (OSStatusException e) {
            throw new Error(e);
        }

        return sourceFrames;
    }

    @Override
    public void onRender(AUMutableRenderActionFlags actionFlags, AudioTimeStamp timeStamp, int busNumber,
            int numberFrames, AudioBufferList data) {
        renderTimeStamp.setSampleTime(renderTimeStamp.getSampleTime() + numberFrames);

        for (int channel = 0; channel < data.getBufferCount(); channel++) {
            data.setBuffer(channel, preRenderData[channel]);
        }
    }

    @Override
    public void onPropertyParsed(AudioFileStream audioFileStream, AudioFileStreamProperty property,
            AudioFileStreamMutablePropertyFlags flags) {
        try {
            if (property == AudioFileStreamProperty.DataFormat) {
                dataFormat = audioFileStream.getDataFormat();
                return;
            }

            if (property != AudioFileStreamProperty.ReadyToProducePackets) {
                return;
            }

            if (audioQueue != null) {
                audioQueue.dispose(true);
            }

            audioQueue = AudioQueue.createOutput(dataFormat, this);

            aqTap = audioQueue.createProcessingTap(this, AudioQueueProcessingTapFlags.PreEffects);

            // Create an AUGraph to process to tap.
            auGraph = AUGraph.create();
            auGraph.open();

            int effectNode = auGraph.addNode(AudioComponentDescription.createConverter(AUTypeConverter.NewTimePitch));
            effectUnit = auGraph.getNodeAudioUnit(effectNode);

            int convertToEffectNode = auGraph.addNode(AudioComponentDescription
                    .createConverter(AUTypeConverter.AUConverter));
            convertToEffectUnit = auGraph.getNodeAudioUnit(convertToEffectNode);

            int convertFromEffectNode = auGraph.addNode(AudioComponentDescription
                    .createConverter(AUTypeConverter.AUConverter));
            convertFromEffectUnit = auGraph.getNodeAudioUnit(convertFromEffectNode);

            int genericOutputNode = auGraph.addNode(AudioComponentDescription.createOutput(AUTypeOutput.GenericOutput));
            genericOutputUnit = auGraph.getNodeAudioUnit(genericOutputNode);

            // Set the format conversions throughout the AUGraph.
            AudioStreamBasicDescription effectFormat = effectUnit.getStreamFormat(AUScope.Output);

            AudioStreamBasicDescription tapFormat = aqTap.getProcessingFormat();

            convertToEffectUnit.setStreamFormat(tapFormat, AUScope.Input);
            convertToEffectUnit.setStreamFormat(effectFormat, AUScope.Output);

            convertFromEffectUnit.setStreamFormat(effectFormat, AUScope.Input);
            convertFromEffectUnit.setStreamFormat(tapFormat, AUScope.Output);

            genericOutputUnit.setStreamFormat(tapFormat, AUScope.Input);
            genericOutputUnit.setStreamFormat(tapFormat, AUScope.Output);

            // Set maximum fames per slice higher (4096) so we don't get
            // TooManyFramesToProcess
            final int maxFramesPerSlice = 4096;
            convertToEffectUnit.setMaxFramesPerSlice(maxFramesPerSlice, AUScope.Global);
            effectUnit.setMaxFramesPerSlice(maxFramesPerSlice, AUScope.Global);
            convertFromEffectUnit.setMaxFramesPerSlice(maxFramesPerSlice, AUScope.Global);
            genericOutputUnit.setMaxFramesPerSlice(maxFramesPerSlice, AUScope.Global);

            // Connect nodes.
            auGraph.connectNodeInput(convertToEffectNode, 0, effectNode, 0);
            auGraph.connectNodeInput(effectNode, 0, convertFromEffectNode, 0);
            auGraph.connectNodeInput(convertFromEffectNode, 0, genericOutputNode, 0);

            renderTimeStamp.setSampleTime(0);
            renderTimeStamp.setFlags(AudioTimeStampFlags.SampleTimeValid);

            // Set the callback onto the first convert unit.
            convertToEffectUnit.setRenderCallback(this, AUScope.Global);

            auGraph.initialize();
        } catch (OSStatusException e) {
            throw new Error(e);
        }
    }

    @Override
    public void onPacketsParsed(int numberBytes, @Pointer long inputData,
            AudioStreamPacketDescription[] packetDescriptions) {
        try {
            AudioQueueBuffer buffer = audioQueue.allocateBuffer(numberBytes);
            buffer.setAudioData(inputData, numberBytes);

            // FIXME enqueueBuffer makes audioQueue.start() fail
            audioQueue.enqueueBuffer(buffer, packetDescriptions);

            totalPacketsReceived += packetDescriptions.length;

            if (!audioQueue.isRunning() && totalPacketsReceived > 100) {
                audioQueue.start();
            }
        } catch (OSStatusException e) {
            throw new Error(e);
        }
    }
}
