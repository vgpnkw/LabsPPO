package com.example.mylab2.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mylab2.App;
import com.example.mylab2.DataBase.ActionModel;
import com.example.mylab2.R;

import java.util.List;

public class ActionPageAdapter extends ArrayAdapter<ActionModel>
{

    private LayoutInflater inflater;
    private int layout;
    private List<ActionModel> actions;


    public ActionPageAdapter(Context context, int res, List<ActionModel> actions)
    {
        super(context, res, actions);
        this.actions = actions;
        this.layout = res;
        this.inflater = LayoutInflater.from(context);
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view=inflater.inflate(this.layout, parent, false);

        EditText actionName = (EditText) view.findViewById(R.id.nameEditText);
        View sub = (View)view.findViewById(R.id.minusButton);
        View add = (View)view.findViewById(R.id.plusButton);
        TextView seconds = (TextView)view.findViewById(R.id.secondsTextView);
        View edit = view.findViewById(R.id.editAction);

        ActionModel action = actions.get(position);

        actionName.setText(action.Name);
        seconds.setText(Integer.toString(action.SecondsNumber));

        add.setOnClickListener(view1 -> {
            action.SecondsNumber++;
            App.getInstance().getActionDao().Update(action);
        });

        sub.setOnClickListener(view2 -> {
            action.SecondsNumber--;
            App.getInstance().getActionDao().Update(action);
        });

        edit.setOnClickListener(view3 -> {
            action.Name = actionName.getText().toString();
            App.getInstance().getActionDao().Update(action);
        });
        return view;
    }

}
