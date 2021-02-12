package com.example.mylab3;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class ActivityGame extends AppCompatActivity implements View.OnClickListener {
    Button btnHuge, btnMedium, btnSmall;
    Button btnMain;
    TextView opponentName;
    TextView id;
    ImageView opponentImage;

    private final Button[][] field = new Button[10][10];


    private GameViewModel gViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        setContentView(R.layout.activity_game);

        opponentName = findViewById(R.id.txt_opponent);
        id = findViewById(R.id.txt_id);
        id.setText(GameStructure.Id);
        opponentImage = findViewById(R.id.img_user);
        gViewModel.fillOpponent(opponentName, opponentImage);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                field[i][j] = findViewById(resID);
                field[i][j].setOnClickListener(this);
                gViewModel.setCell(field[i][j], i, j, buttonID);
            }
        }

        gViewModel.checkUser();

        btnSmall = findViewById(R.id.small_ship);
        btnMedium = findViewById(R.id.medium_ship);
        btnHuge = findViewById(R.id.huge_ship);

        btnSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gViewModel.setShip(Ships.LITTLE);
            }
        });
        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gViewModel.setShip(Ships.MEDIUM);
            }
        });

        btnHuge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gViewModel.setShip(Ships.BIG);
            }
        });

        btnMain = findViewById(R.id.btn_game);
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GameStructure.Action.equals("notReady")) {
                    gViewModel.changeTurn(btnMain, field);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), ActivityListRoom.class);
                    startActivity(intent);
                }
            }
        });
        gViewModel.checkShips(btnHuge, btnSmall, btnMedium, btnMain);
        gViewModel.OpponentEventListener(btnMain, field, opponentName, opponentImage);
    }

    @Override
    public void onClick(View v) {
        gViewModel.clickField(v, field,  btnMain, btnHuge, btnSmall,  btnMedium);
    }

}
