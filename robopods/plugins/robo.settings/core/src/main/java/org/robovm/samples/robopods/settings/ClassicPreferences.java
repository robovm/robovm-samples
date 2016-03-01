package org.robovm.samples.robopods.settings;

import org.robovm.pods.Util;
import org.robovm.pods.settings.Settings;

/**
 * This class demonstrates the classic usage of preferences. <p>
 * Please see {@link AppPreferences} for the recommended way how to save settings.
 */
public class ClassicPreferences {
    private static ClassicPreferences instance;

    private static final String SOUND_VOLUME_KEY = "sound_volume";
    private static final String MUSIC_VOLUME_KEY = "music_volume";
    private static final String LANGUAGE_KEY = "language";
    private static final String BUTTON_CLICK_COUNT_KEY = "button_click_count";

    private Settings settings;

    private ClassicPreferences() {
        settings = new Settings("org.robovm.pods.settings.classic");
    }

    public static ClassicPreferences getInstance() {
        return instance;
    }

    public ClassicPreferences setSoundVolume(double volume) {
        settings.put(SOUND_VOLUME_KEY, volume);
        return this;
    }

    public ClassicPreferences setMusicVolume(double volume) {
        settings.put(MUSIC_VOLUME_KEY, volume);
        return this;
    }

    public ClassicPreferences setLanguage(Language language) {
        Util.requireNonNull(language, "language");
        settings.put(LANGUAGE_KEY, language.name());
        return this;
    }

    public ClassicPreferences increaseButtonClickCount(int clicks) {
        settings.put(BUTTON_CLICK_COUNT_KEY, getButtonClickCount() + clicks);
        return this;
    }

    public double getSoundVolume() {
        return settings.getDouble(SOUND_VOLUME_KEY, 1);
    }

    public double getMusicVolume() {
        return settings.getDouble(MUSIC_VOLUME_KEY, 0.5);
    }

    public Language getLanguage() {
        return Language.valueOf(settings.getString(LANGUAGE_KEY, "EN"));
    }

    public int getButtonClickCount() {
        return settings.getInt(BUTTON_CLICK_COUNT_KEY);
    }

    public void save() {
        settings.flush();
    }
}
