package com.nikolaychernov.samaratransport;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBar;

/**
 * A {@link ProperSettingsActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ProperSettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setupActionBar();
        ListPreference splashList = (ListPreference) findPreference("searchRadius");
        splashList.setSummary(splashList.getEntry());

        splashList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String nv = (String) newValue;

                if (preference.getKey().equals("searchRadius")) {
                    ListPreference splashList = (ListPreference) preference;
                    splashList.setSummary(splashList.getEntries()[splashList.findIndexOfValue(nv)]);
                    DataController.getInstance().setRadius(nv);
                }
                return true;
            }

        });
        CheckBoxPreference background = (CheckBoxPreference) findPreference("backgroundFlag");
        background.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                DataController.getInstance().setBackgroundUpdatesActivated((Boolean) newValue);
                return true;
            }
        });

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
