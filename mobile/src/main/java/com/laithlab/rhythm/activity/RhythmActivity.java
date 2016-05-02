package com.laithlab.rhythm.activity;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.laithlab.rhythm.RhythmApp;

public abstract class RhythmActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        updateTheme();
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode != ((RhythmApp) getApplication()).getCurrentNighMode()) {
            ((RhythmApp) getApplication()).setCurrentNightMode(currentNightMode);
            recreate();
        }
    }

    private void updateTheme() {
        String theme = ((RhythmApp) getApplication()).getSelectedTheme();
        int nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        if (theme.equals("dark")) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        if (theme.equals("auto")) {
            nightMode = AppCompatDelegate.MODE_NIGHT_AUTO;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);
        getDelegate().applyDayNight();
    }
}
