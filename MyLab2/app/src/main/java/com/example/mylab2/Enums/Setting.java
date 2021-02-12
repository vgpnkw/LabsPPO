package com.example.mylab2.Enums;

public enum Setting {
    LANG_SETTINGS ("test"),
    THEME_SETTINGS ("theme"),
    FONTSIZE_SETTINGS("fontSize"),
    DELETE_ALL("deleteAll");

    private final String setting;

    Setting(String sett)
    {
        this.setting = sett;
    }

    public String getSetting()
    {
        return setting;
    }
}