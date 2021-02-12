package com.example.mylab2.Enums;

public enum FontSizeSetting {
    SMALL(new String[]{"уменьшенный", "small"}),
    LARGE(new String[]{"увеличенный", "large"});

    private final String [] setting;

    FontSizeSetting(String[] sett)
    {
        this.setting = sett;
    }

    public String[] getFontSize()
    {
        return setting;
    }
}