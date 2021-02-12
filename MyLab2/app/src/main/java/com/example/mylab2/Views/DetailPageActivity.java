package com.example.mylab2.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mylab2.App;
import com.example.mylab2.DataBase.ActionModel;
import com.example.mylab2.DataBase.TimerModel;
import com.example.mylab2.R;
import com.example.mylab2.Service.TimerService;
import com.example.mylab2.ViewModels.ListViewModels;

import java.util.Arrays;
import java.util.List;

public class DetailPageActivity extends AppCompatActivity
{
    TimerModel timer;
    ListViewModels listViewModel;

    ListView listView;
    ActionPageAdapter actionAdapter;

    List<String> dialogActions;
    DialogAdapter actionsOnActions;

    View addAction;
    View startTimer;
    View saveChanges;

    View colorPicker;
    View deleteAllAction;

    EditText timerName;
    EditText timerDescription;
    Context context = this;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dialogActions = Arrays.asList(getResources().getString(R.string.createCopy),getResources().getString(R.string.reset),getResources().getString(R.string.delete));
        actionsOnActions = new DialogAdapter(context, android.R.layout.select_dialog_item,dialogActions);
        setContentView(R.layout.activity_detail);

        listView = findViewById(R.id.actListView);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {

            Intent intentVibrate = new Intent(getApplicationContext(), TimerService.class);
            startService(intentVibrate);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setNegativeButton("Cancel", null);
            builder.setAdapter(actionsOnActions, (dialog, which) ->
            {
                switch (which)
                {
                    case 0:
                        ActionModel action =new ActionModel( (ActionModel) parent.getItemAtPosition(position));
                        AddAction(action);
                        break;
                    case 1:
                        ResetAction(parent, position);
                        break;
                    case 2:
                        DeleteAction(parent, position);
                        break;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return  true;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timer= App.getInstance().getTimerDao().Get(extras.getInt("id"));
        }

        startTimer = findViewById(R.id.startTimer);
        startTimer.setOnClickListener(view -> StartTimer(timer.Id));

        saveChanges = findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(view -> UpdateTimer(timer));

        colorPicker = findViewById(R.id.colorPicker);
        colorPicker.setBackgroundTintList(ColorStateList.valueOf(timer.Color));

        deleteAllAction = findViewById(R.id.deleteAllActions);
        deleteAllAction.setOnClickListener(view -> App.getInstance().getActionDao().DeleteAllByTimerId(timer.Id));
        actionAdapter = new ActionPageAdapter(context, R.layout.action_list_item, App.getInstance().getActionDao().GetAllByTimerId(timer.Id));
        listView.setAdapter(actionAdapter);
        listViewModel = new ViewModelProvider(this).get(ListViewModels.class);

        addAction = findViewById(R.id.addActoin);
        addAction.setOnClickListener(view -> AddAction());

        timerName = findViewById(R.id.timerName);
        timerName.setText(timer.Name);

        timerDescription = findViewById(R.id.timerDescription);
        timerDescription.setText(timer.Description);


        listViewModel.getActions().observe(this, actions -> {
            actionAdapter = new ActionPageAdapter(context, R.layout.action_list_item, App.getInstance().getActionDao().GetAllByTimerId(timer.Id));
            listView.setAdapter(actionAdapter);
        });
    }

    public void DeleteAction(AdapterView<?> parent, int position)
    {
        App.getInstance().getActionDao().Delete((ActionModel) parent.getItemAtPosition(position));
    }

    public void SetColor(View view)
    {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle(getResources().getString(R.string.сhooseСolor))
                .initialColor(timer.Color)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    timer.Color = selectedColor;
                    colorPicker.setBackgroundTintList(ColorStateList.valueOf(timer.Color));
                })
                .setNegativeButton("cancel", (dialog, which) -> {
                })
                .build()
                .show();
    }

    public void ResetAction(AdapterView<?> parent, int position)
    {
        ActionModel action = (ActionModel) parent.getItemAtPosition(position);
        action.Name="Timer action";
        action.SecondsNumber = 30;
        App.getInstance().getActionDao().Insert(action);
    }


    public void AddAction(ActionModel action)
    {
        App.getInstance().getActionDao().Insert(action);
    }

    public void AddAction()
    {
        ActionModel action = new ActionModel("Timer action", 30 , timer.Id);
        App.getInstance().getActionDao().Insert(action);
    }

    public  void StartTimer(int id)
    {
        Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("color", timer.Color);
        startActivity(intent);
    }

    public void UpdateTimer(TimerModel timer)
    {
        timer.Name = timerName.getText().toString();
        timer.Description = timerDescription.getText().toString();
        App.getInstance().getTimerDao().Update(timer);
    }

}