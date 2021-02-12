package com.example.mylab2.Views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mylab2.DataBase.TimerModel;
import com.example.mylab2.R;

import java.util.List;

public class TimerListAdapter extends ArrayAdapter<TimerModel>
{
    private LayoutInflater inflater;
    private int layout;
    private List<TimerModel> timers;
    private Context context;

    public TimerListAdapter(Context context, int resource, List<TimerModel> timers)
    {
        super(context, resource, timers);
        this.context = context;
        this.timers = timers;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TimerModel timer = timers.get(position);

        View view=inflater.inflate(this.layout, parent, false);

        TextView timerName = (TextView)view.findViewById(R.id.timerName);
        TextView timerDescription = (TextView)view.findViewById(R.id.timerDescription);
        TextView timerCreatedDate = (TextView)view.findViewById(R.id.timerCreatedDate);

        timerName.setText(timer.Name);
        timerDescription.setText(timer.Description);
        timerCreatedDate.setText(timer.CreatedDate);

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.timerListViewItem);
        linearLayout.setBackgroundTintList(ColorStateList.valueOf(timer.Color));

        return view;
    }
}
