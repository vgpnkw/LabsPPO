package com.example.labtwoppo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment navController_fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Objects.requireNonNull(navController_fragment).getNavController();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean night = sharedPreferences.getBoolean("night", true);
        if (night) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        boolean language = sharedPreferences.getBoolean("language", true);
        Locale locale;

        if (language) {
            locale = new Locale("ru", "RU");
        }
        else {
            locale = new Locale("en","US");
        }

        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        return view;
    }
}