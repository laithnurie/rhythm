package com.laithlab.rhythm.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatDelegate;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.utils.DialogHelper;

public class RhythmPrefs extends PreferenceActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final ListPreference themePref = (ListPreference) findPreference("theme_list");
            if (themePref.getEntry() != null) {
                themePref.setSummary(themePref.getEntry());
            }
            themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = themePref.findIndexOfValue(String.valueOf(newValue));
                    String theme = (String) themePref.getEntries()[index];
                    themePref.setSummary(theme);
                    themePref.setValue(newValue.toString());

                    return true;
                }
            });
            Preference resetStats = findPreference("reset_stats");

            resetStats.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogHelper.resetMusicDataAlert(getActivity());
                    return true;
                }
            });

            Preference disclaimerStats = findPreference("disclaimer");

            disclaimerStats.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), DisclaimerActivity.class));
                    return true;
                }
            });

            Preference about = findPreference("about");

            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogHelper.aboutDialog(getActivity());
                    return true;
                }
            });
        }
    }
}