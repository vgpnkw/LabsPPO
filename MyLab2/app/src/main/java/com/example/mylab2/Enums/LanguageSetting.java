package com.example.mylab2.Enums;

public enum LanguageSetting {
    EN("en"),
    RUS("ru");

    private final String setting;

    LanguageSetting(String sett)
    {
        this.setting = sett;
    }

    public String getLanguage()
    {
        return setting;
    }
}
