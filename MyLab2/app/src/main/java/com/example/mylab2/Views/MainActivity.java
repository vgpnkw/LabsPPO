package com.example.mylab2.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mylab2.App;
import com.example.mylab2.DataBase.TimerModel;
import com.example.mylab2.R;
import com.example.mylab2.Service.TimerService;
import com.example.mylab2.ViewModels.ListViewModels;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> dialogActions;
    DialogAdapter actionsOnTimer;

    ListView timerListView;

    Context context = this;

    TimerListAdapter timerAdapter;
    View addTimer;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dialogActions = Arrays.asList(getResources().getString(R.string.play),getResources().getString(R.string.edit),getResources().getString(R.string.createCopy),getResources().getString(R.string.delete));
        actionsOnTimer = new DialogAdapter(context, android.R.layout.select_dialog_item,dialogActions);
        setContentView(R.layout.activity_main);

        addTimer =  findViewById(R.id.addTimerButton);
        addTimer.setOnClickListener(view -> AddTimer(new TimerModel()));

        timerAdapter = new TimerListAdapter(context, R.layout.timer_list_item, App.getInstance().getTimerDao().GetAll());

        timerListView = findViewById(R.id.timerListView);
        timerListView.setSelector(android.R.color.transparent);
        timerListView.setDivider(null);
        timerListView.setDividerHeight(20);
        timerListView.setAdapter(timerAdapter);

        timerListView.setOnItemClickListener((parent, view, position, id) -> EditTimer(parent, position));

        timerListView.setOnItemLongClickListener((parent, view, position, id) -> {

            Intent intentVibrate = new Intent(getApplicationContext(), TimerService.class);
            startService(intentVibrate);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setNegativeButton("Cancel", null);
            builder.setAdapter(actionsOnTimer, (dialog, which) ->
            {
                switch (which)
                {
                    case 0:
                        PlayTimer(parent, position);
                        break;
                    case 1:
                        EditTimer(parent, position);
                        break;
                    case 2:
                        TimerModel timer =new TimerModel( (TimerModel) parent.getItemAtPosition(position));
                        AddTimer(timer);
                        break;
                    case 3:
                        DeleteTimer(parent, position);
                        break;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return  true;
        });

        new ViewModelProvider(this).get(ListViewModels.class).getTimers().observe(this, timers -> {
            timerAdapter = new TimerListAdapter(context, R.layout.timer_list_item, App.getInstance().getTimerDao().GetAll());
            timerListView.setAdapter(timerAdapter);
        });
    }


    public void PlayTimer(AdapterView<?> parent, int position)
    {
        Intent intent = new Intent(context, TimerActivity.class);
        intent.putExtra("id", ((TimerModel) parent.getItemAtPosition(position)).Id);
        startActivity(intent);
    }

    public void EditTimer(AdapterView<?> parent, int position)
    {
        Intent intent = new Intent(context, DetailPageActivity.class);
        intent.putExtra("id", ((TimerModel) parent.getItemAtPosition(position)).Id);
        startActivity(intent);
    }

    public void DeleteTimer(AdapterView<?> parent, int position)
    {
        App.getInstance().getTimerDao().Delete(((TimerModel) parent.getItemAtPosition(position)));
    }

    public void AddTimer(TimerModel timer)
    {
        App.getInstance().getTimerDao().Insert(timer);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(0, 1, 0, getResources().getString(R.string.settings));
        menuItem.setIntent(new Intent(this, SettingsActivity.class));
        return super.onCreateOptionsMenu(menu);
    }
}