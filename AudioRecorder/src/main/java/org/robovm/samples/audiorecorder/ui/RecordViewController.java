/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 */
package org.robovm.samples.audiorecorder.ui;

import java.io.File;

import org.robovm.apple.avfoundation.AVAudioPlayer;
import org.robovm.apple.avfoundation.AVAudioPlayerDelegate;
import org.robovm.apple.avfoundation.AVAudioPlayerDelegateAdapter;
import org.robovm.apple.avfoundation.AVAudioRecorder;
import org.robovm.apple.avfoundation.AVAudioRecorderDelegateAdapter;
import org.robovm.apple.avfoundation.AVAudioSession;
import org.robovm.apple.avfoundation.AVAudioSessionCategory;
import org.robovm.apple.avfoundation.AVAudioSettings;
import org.robovm.apple.coreaudio.AudioFormat;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSPathUtilities;
import org.robovm.apple.foundation.NSSearchPathDirectory;
import org.robovm.apple.foundation.NSSearchPathDomainMask;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSURL;
import org.robovm.apple.uikit.UIButton;
import org.robovm.apple.uikit.UIControlState;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.IBAction;
import org.robovm.objc.annotation.IBOutlet;

@CustomClass("RecordViewController")
public class RecordViewController extends UIViewController {
    @IBOutlet
    private UIButton recordButton;
    @IBOutlet
    private UIButton playButton;
    @IBOutlet
    private UIButton stopButton;

    private AVAudioRecorder recorder;
    private AVAudioPlayer player;

    private AVAudioPlayerDelegate audioPlayerDelegate;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();

        // Disable Stop/Play button when application launches
        playButton.setEnabled(false);
        stopButton.setEnabled(false);

        String docsDir = NSPathUtilities.getSearchPathForDirectoriesInDomains(
                NSSearchPathDirectory.DocumentDirectory, NSSearchPathDomainMask.UserDomainMask, true).get(0);

        String soundFilePath = NSString.appendPathComponent(docsDir, "MyAudio.m4a");

        // Set the audio file
        NSURL outputFileURL = new NSURL(new File(soundFilePath));

        try {
            // Setup audio session
            AVAudioSession.getSharedInstance().setCategory(AVAudioSessionCategory.PlayAndRecord);
            AVAudioSession.getSharedInstance().setActive(true);

            // Define the recorder setting
            AVAudioSettings recordSettings = new AVAudioSettings();
            recordSettings.setFormat(AudioFormat.MPEG4AAC);
            recordSettings.setSampleRate(44100);
            recordSettings.setNumberOfChannels(2);

            // Initiate and prepare the recorder
            recorder = new AVAudioRecorder(outputFileURL, recordSettings);
            recorder.setDelegate(new AVAudioRecorderDelegateAdapter() {
                @Override
                public void didFinishRecording(AVAudioRecorder recorder, boolean flag) {
                    try {
                        if (player != null) {
                            // Dispose audio player resources.
                            player.stop();
                        }

                        // Setup a new audio player
                        player = new AVAudioPlayer(recorder.getUrl());
                        player.setDelegate(audioPlayerDelegate);
                        player.prepareToPlay();
                    } catch (NSErrorException e) {
                        e.printStackTrace();
                    }

                    recordButton.setTitle("Record", UIControlState.Normal);
                    playButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }

                @Override
                public void encodeErrorDidOccur(AVAudioRecorder recorder, NSError error) {
                    System.out.println("Encode Error occurred");

                    recordButton.setTitle("Record", UIControlState.Normal);
                    playButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }
            });
            recorder.setMeteringEnabled(true);
            recorder.prepareToRecord();
        } catch (NSErrorException e) {
            e.printStackTrace();
        }

        audioPlayerDelegate = new AVAudioPlayerDelegateAdapter() {
            @Override
            public void didFinishPlaying(AVAudioPlayer player, boolean flag) {
                playButton.setTitle("Play", UIControlState.Normal);
                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
            }

            @Override
            public void decodeErrorDidOccur(AVAudioPlayer player, NSError error) {
                System.out.println("Decode Error occurred");

                playButton.setTitle("Play", UIControlState.Normal);
                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        };
    }

    @Override
    public void viewDidDisappear(boolean animated) {
        super.viewDidDisappear(animated);

        try {
            AVAudioSession.getSharedInstance().setActive(false);
        } catch (NSErrorException e) {
            e.printStackTrace();
        }
    }

    @IBAction
    private void recordAudio() {
        if (!recorder.isRecording()) {
            // Start recording
            recorder.record();

            recordButton.setTitle("Pause", UIControlState.Normal);
            playButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            // Pause recording
            recorder.pause();

            recordButton.setTitle("Record", UIControlState.Normal);
            playButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    @IBAction
    private void playAudio() {
        if (!player.isPlaying()) {
            // Start/Resume playback
            player.play();

            playButton.setTitle("Pause", UIControlState.Normal);
            recordButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else {
            // Pause playback
            player.pause();

            playButton.setTitle("Play", UIControlState.Normal);
            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    @IBAction
    private void stopAudio() {
        if (recorder.isRecording()) {
            // Stop recording
            recorder.stop(); // calls didFinishRecording
        } else if (player.isPlaying()) {
            // Stop playback
            player.pause();
            player.setCurrentTime(0);

            playButton.setTitle("Play", UIControlState.Normal);
            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }
}
