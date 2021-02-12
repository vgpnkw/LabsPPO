package com.example.mylab2.Views;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mylab2.App;
import com.example.mylab2.DataBase.ActionModel;
import com.example.mylab2.DataBase.TimerModel;
import com.example.mylab2.Enums.Status;
import com.example.mylab2.R;
import com.example.mylab2.Service.TimerService;
import com.example.mylab2.ViewModels.TimerViewModel;

import java.util.List;
import java.util.Objects;

public class TimerActivity extends AppCompatActivity {

    TimerAdapter timerPageAdapter;

    TimerService service;
    ServiceConnection connection;
    Intent intent;

    TimerModel timer;
    List<ActionModel> actionList;
    ListView actListView;

    View next;
    TextView actionName;
    TextView secondNumber;
    View pause;
    View reset;
    View previous;
    TimerViewModel timerViewModel;

    private final String timer_action = "TIMER_ACTION";
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer);

        timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        actListView = findViewById(R.id.actionList);

        reset = findViewById(R.id.resetTimer);
        pause = findViewById(R.id.pauseTimer);
        next  = findViewById(R.id.nextAction);
        previous = findViewById(R.id.previousAction);

        actionName = findViewById(R.id.ActionName);
        secondNumber = findViewById(R.id.secondNumber);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                service = ((TimerService.MyBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerViewModel.setTime(Objects.requireNonNull(intent.getExtras()).getInt("currentTime"));
                actionName.setText(intent.getExtras().getString("actionName"));
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(timer_action));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timer = App.getInstance().getTimerDao().Get(extras.getInt("id"));
            actionList = App.getInstance().getActionDao().GetAllByTimerId(timer.Id);
        }

        View timerView = findViewById(R.id.timer);
        timerView.setBackgroundColor(timer.Color);

        timerPageAdapter = new TimerAdapter(this, R.layout.timer_action_item, actionList);
        actListView.setAdapter(timerPageAdapter);

        intent = new Intent(this, TimerService.class);
        intent.putExtra("timerId", timer.Id);

        if(!timerViewModel.IsNotActionStart()){
            StartTimerService();
        }

        timerViewModel.getPosition().observe(this, integer -> {
            if (service != null){
                service.setActionNumber(integer);
            }
        });

        timerViewModel.getTime().observe(this, integer -> secondNumber.setText(integer.toString()));

        actListView.setOnItemClickListener((adapterView, view, i, l) -> timerViewModel.setPosition(i));


        next.setOnClickListener(view -> service.NextAction());
        pause.setOnClickListener(view -> service.PauseTimer());
        reset.setOnClickListener(view -> service.ResetTimer());
        previous.setOnClickListener(view -> service.PrevAction());
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unbindService(connection);
    }


    public void StartTimerService()
    {
        timerViewModel.setStatus(Status.STARTED);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(intent,connection,0);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (service != null){
            service.PauseTimer();
        }
        stopService(intent);
    }
}