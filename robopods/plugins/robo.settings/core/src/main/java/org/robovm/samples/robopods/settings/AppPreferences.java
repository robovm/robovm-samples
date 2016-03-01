package org.robovm.samples.robopods.settings;

import org.robovm.pods.settings.Settings;

/**
 * This class stores all preferences for the app.
 * We use the singleton approach as we will never need two instances and we want to access it from everywhere in the app.
 */
public class AppPreferences {
    private static AppPreferences instance;

    /**
     * The volume of sound fx in the app.
     */
    public double soundVolume = 1;
    /**
     * The volume of music in the app.
     */
    public double musicVolume = 0.5;
    /**
     * The language of the UI.
     */
    public Language language = Language.EN;
    /**
     * Some click counter of the app.
     */
    public int buttonClickCount;

    /**
     * If you don't want to store a field, add the {@code transient} keyword.<p>
     * This field will not be stored.
     */
    private transient boolean excludedField = true;

    private AppPreferences() {}

    /**
     * We lazily setup the singleton instance.<p>
     * We use the {@link Settings#getSharedSettings(String)} convenience method,
     * so we can reuse the same {@link Settings} instance across our app.<p>
     * {@link Settings#get(Class)} will return an instance of {@link AppPreferences}. It's either a fresh instance or
     * if we had already saved the AppPreferences, the fields will be initialized with the stored values.
     *
     * @return the singleton instance.
     */
    public static AppPreferences getInstance() {
        if (instance == null) {
            // Load the stored preferences.
            instance = Settings.getSharedSettings("org.robovm.pods.settings").get(AppPreferences.class);
        }
        return instance;
    }

    /**
     * Save the app preferences.<p>
     * We use the {@link Settings#getSharedSettings(String)} convenience method,
     * so we can reuse the same {@link Settings} instance across our app.<p>
     * We put this instance into the shared settings and flush it, so it gets persisted.
     */
    public void save() {
        Settings settings = Settings.getSharedSettings("org.robovm.pods.settings");
        settings.put(this).flush();
    }
}
