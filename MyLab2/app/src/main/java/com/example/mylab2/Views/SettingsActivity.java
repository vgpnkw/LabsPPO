package com.example.mylab2.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.mylab2.DataBase.DbHelper;
import com.example.mylab2.Enums.FontSizeSetting;
import com.example.mylab2.Enums.LanguageSetting;
import com.example.mylab2.R;
import com.example.mylab2.App;
import com.example.mylab2.Enums.Setting;

import java.util.Arrays;
import java.util.Locale;

public class SettingsActivity extends PreferenceActivity {

    SharedPreferences sharedPreferences;
    int font_def;
    int lang_def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(Setting.THEME_SETTINGS.getSetting(), true)) {
            setTheme(R.style.Theme_AppCompat);
        }

        String font = sharedPreferences.getString(Setting.FONTSIZE_SETTINGS.getSetting(), FontSizeSetting.SMALL.getFontSize()[0]);
        String listValue = sharedPreferences.getString(Setting.LANG_SETTINGS.getSetting(), LanguageSetting.EN.getLanguage());
        Locale locale;
        assert listValue != null;


        if (! listValue.equals(LanguageSetting.EN.getLanguage()) {
            font_def = 0;
            locale = new Locale(LanguageSetting.RUS.getLanguage());
        } else {
            font_def = 1;
            locale = new Locale(LanguageSetting.EN.getLanguage());
        }
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;

        assert font != null;
        if (Arrays.asList(FontSizeSetting.SMALL.getFontSize()).contains(font.toLowerCase())) {
            lang_def = 0;
            configuration.fontScale = (float) 0.5;
        } else if (Arrays.asList(FontSizeSetting.LARGE.getFontSize()).contains(font.toLowerCase())) {
            lang_def = 1;
            configuration.fontScale = (float) 1;
        } else {
            lang_def = 2;
            configuration.fontScale = (float) 1.5;
        }

        getBaseContext().getResources().updateConfiguration(configuration, null);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ChangeSettingsFragment()).commit();
        super.onCreate(savedInstanceState);
    }


    public static class ChangeSettingsFragment extends PreferenceFragment {

        private DbHelper db;

        private boolean onLanguageChange(Preference preference, Object newValue) {
            Locale locale;
            if (newValue.toString().equals(LanguageSetting.EN.getLanguage())) {
                locale = new Locale(LanguageSetting.EN.getLanguage());
            } else {
                locale = new Locale(LanguageSetting.RUS.getLanguage());
            }
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getActivity().getResources().updateConfiguration(configuration, null);
            getActivity().recreate();
            return true;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            db = App.getInstance().getDatabase();
            addPreferencesFromResource(R.xml.settings);
            Preference button = findPreference(Setting.DELETE_ALL.getSetting());
            Preference theme = findPreference(Setting.THEME_SETTINGS.getSetting());
            ListPreference language = (ListPreference) findPreference(Setting.LANG_SETTINGS.getSetting());
            ListPreference font = (ListPreference) findPreference(Setting.FONTSIZE_SETTINGS.getSetting());
            language.setValueIndex(((SettingsActivity) getActivity()).font_def);
            language.setOnPreferenceChangeListener(this::onLanguageChange);
            theme.setOnPreferenceChangeListener(this::onThemeChange);
            font.setValueIndex(((SettingsActivity) getActivity()).lang_def);
            button.setOnPreferenceClickListener(this::onDeleteClick);
            font.setOnPreferenceChangeListener(this::onFontChange);
        }

        private boolean onThemeChange(Preference preference, Object newValue) {
            if ((boolean) newValue) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            getActivity().recreate();
            return true;
        }

        private boolean onDeleteClick(Preference preference) {
            db.timerDao().DeleteAll();
            Intent intent = new Intent();
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
            return true;
        }

        private boolean onFontChange(Preference preference, Object newValue) {
            Configuration configuration = getResources().getConfiguration();
            if (Arrays.asList(FontSizeSetting.SMALL.getFontSize()).contains(newValue.toString().toLowerCase())) {
                configuration.fontScale = (float) 0.5;
            } else if (Arrays.asList(FontSizeSetting.SMALL.getFontSize()).contains(newValue.toString().toLowerCase())) {
                configuration.fontScale = (float) 1;
            } else {
                configuration.fontScale = (float) 1.5;
            }
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getActivity().getBaseContext().getResources().updateConfiguration(configuration, metrics);
            getActivity().recreate();
            return true;
        }
    }
}
