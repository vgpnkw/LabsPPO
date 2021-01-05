package com.example.labtwoppo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat spc = findPreference("night");
            assert spc != null;
            spc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!spc.isChecked()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                return true;
                }
            });

            Preference preference = findPreference("deleteAll");
            assert preference != null;
            preference.setOnPreferenceClickListener(preference1 -> {
                try {
                    if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                        //((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
                    } else {
                        String packageName = requireActivity().getApplicationContext().getPackageName();
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("pm clear " + packageName);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                    if (getContext() instanceof Activity) {
                        ((Activity) requireContext()).finish();
                    }

                    Runtime.getRuntime().exit(0);
                }
                return true;
            });
        }
    }
}