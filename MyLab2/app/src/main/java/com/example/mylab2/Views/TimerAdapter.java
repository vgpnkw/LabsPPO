package com.example.mylab2.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mylab2.DataBase.ActionModel;
import com.example.mylab2.R;

import java.util.List;

public class TimerAdapter extends ArrayAdapter<ActionModel>
{
    private LayoutInflater inflater;
    private int layout;
    private List<ActionModel> actions;

    public TimerAdapter(Context context, int resource, List<ActionModel> actions) {
        super(context, resource, actions);
        this.actions = actions;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view=inflater.inflate(this.layout, parent, false);

        TextView actionName = (TextView)view.findViewById(R.id.actTextView);
        TextView seconds = (TextView)view.findViewById(R.id.actSecTextView);

        ActionModel action = actions.get(position);
        actionName.setText(action.Name);
        seconds.setText(Integer.toString(action.SecondsNumber));

        return view;
    }
}
