package com.example.mylab2.Views;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class DialogAdapter extends ArrayAdapter<String>
{
    private List<String> actions;

    public DialogAdapter(Context context, int resource, List<String> actions)
    {
        super(context, resource, actions);
        this.actions = actions;
    }
}
