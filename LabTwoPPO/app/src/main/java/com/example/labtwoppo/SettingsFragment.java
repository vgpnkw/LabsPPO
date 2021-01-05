package com.example.labtwoppo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Locale locale;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean language = sharedPreferences.getBoolean("language", true);

        if (language) {
            locale = new Locale("ru", "RU");
        }
        else {
            locale = new Locale("en","US");
        }

        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, null);


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
                    ((ActivityManager)requireActivity().getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
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

        SeekBarPreference sbp = findPreference("fontSize");
        sbp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RecyclerView recyclerView = getListView();
                List<View> viewList = new LinkedList<>();
                int value = Integer.parseInt(newValue.toString()), i = 0;
                while (i < recyclerView.getChildCount()) {
                    viewList.add(recyclerView.getChildAt(i++));
                }
                for (View view : viewList) {
                    FontChangeSize.changeFontSizeInViews(view, value);
                }
                return true;
            }
        });

        SwitchPreferenceCompat spcLanguage = findPreference("language");
        spcLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Locale locale;

                if (newValue.toString().equals("true")) {
                    locale = new Locale("ru", "RU");
                }
                else {
                    locale = new Locale("en","US");
                }

                Locale.setDefault(locale);
                Configuration configuration = new Configuration();
                configuration.locale = locale;
                getResources().updateConfiguration(configuration, null);

                return true;
            }
        });
    }

}