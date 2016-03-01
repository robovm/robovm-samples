package org.robovm.samples.robopods.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import org.robovm.pods.Platform;
import org.robovm.pods.Platform.AndroidPlatform;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // *NOTE* We have to set the launch activity atm in order to use android settings in onCreate.
        ((AndroidPlatform) Platform.getPlatform()).setLaunchActivity(this);

        AppPreferences prefs = AppPreferences.getInstance();

        final SeekBar soundVolume = (SeekBar) findViewById(R.id.soundVolume);
        final SeekBar musicVolume = (SeekBar) findViewById(R.id.musicVolume);
        final Spinner language = (Spinner) findViewById(R.id.language);
        final Button button = (Button) findViewById(R.id.button);

        // Sound Volume
        soundVolume.setProgress((int) (AppPreferences.getInstance().soundVolume * 100));
        soundVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.soundVolume = progress / 100f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.save();
            }
        });

        // Music Volume
        musicVolume.setProgress((int) (AppPreferences.getInstance().musicVolume * 100));
        musicVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.musicVolume = progress / 100f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.save();
            }
        });

        // Language
        Language[] langValues = Language.values();
        String[] languageArray = new String[langValues.length];
        for (int i = 0; i < langValues.length; i++) {
            languageArray[i] = langValues[i].name();
        }
        language.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageArray));
        language.setSelection(prefs.language.ordinal());
        language.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.language = Language.values()[position];
                prefs.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Button
        if (prefs.buttonClickCount > 0) {
            button.setText(String.format("%d Clicks!", prefs.buttonClickCount));
        }
        button.setOnClickListener((view) -> {
            prefs.buttonClickCount++;
            prefs.save();

            button.setText(String.format("%d Clicks!", prefs.buttonClickCount));
        });
    }
}
