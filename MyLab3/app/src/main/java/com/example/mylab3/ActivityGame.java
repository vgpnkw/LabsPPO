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
        fillOpponent(opponentName, opponentImage);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                field[i][j] = findViewById(resID);
                field[i][j].setOnClickListener(this);
                field[i][j].setBackgroundColor(Color.BLUE);
                field[i][j].setText(buttonID);
                field[i][j].setTextColor(Color.BLUE);
                gViewModel.setCell(i, j, buttonID);
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
                    changeTurn(btnMain, field);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), ActivityListRoom.class);
                    startActivity(intent);
                }
            }
        });
        gViewModel.checkShips(btnHuge, btnSmall, btnMedium, btnMain);
        OpponentEventListener(btnMain, field, opponentName, opponentImage);
    }

    public  void clickField(View v, Button[][] field, Button btnMain, Button btnHuge, Button btnSmall, Button btnMedium){
        if(!gViewModel.isBattle())
        {
            gViewModel.getCount();
            fillField(((Button) v).getText().toString(), field);
            checkShips(btnHuge, btnSmall,  btnMedium, btnMain);
        }else if(gViewModel.isiGo()){
            checkShot(((Button) v).getText().toString(), field,  btnMain);
        }
    }

    public  void changeTurn(Button btnMain, Button[][] field){
        String info = "Wait opponent";
        btnMain.setText(info);
        GameStructure.Action = "ready";
        reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue(GameStructure.Action);
        if(GameStructure.opponentAction.equals("ready")){
            gViewModel.setBattle(true);
            drawShips(btnMain, field);
        }
    }

    public void checkShot(String shot, Button[][] field, Button btnMain){
        gViewModel.getcheckShot(shot);
        int Y = gViewModel.getCoordinates().get(0);
        int X = gViewModel.getCoordinates().get(1);
        if(gViewModel.isShip())
        {
            field[Y][X].setBackgroundColor(Color.YELLOW);
            field[Y][X].setTextColor(Color.YELLOW);
        }else {
            field[Y][X].setBackgroundColor(Color.WHITE);
            field[Y][X].setTextColor(Color.WHITE);
        }
        gViewModel.setiGo(false);
        drawShips(btnMain, field);
        if(gViewModel.emptyShip())
        {
        btnMain.setText("You won!");
        btnMain.setEnabled(true);
        }
        gViewModel.Victoty();
    }

    public void checkShips(Button btnHuge, Button btnSmall, Button btnMedium, Button btnMain){
        btnHuge.setEnabled(Ships.BIG.getCount() != 0);
        btnMedium.setEnabled(Ships.MEDIUM.getCount() != 0);
        btnSmall.setEnabled(Ships.LITTLE.getCount() != 0);
        btnMain.setEnabled(Ships.BIG.getCount() == 0 && Ships.MEDIUM.getCount() == 0 && Ships.LITTLE.getCount() == 0);
        gViewModel.sizeShip = 0;
    }

    public void fillField(String ButtonName, Button[][] field)
    {
        gViewModel.getfillField(ButtonName);
        int Y = gViewModel.getCoordinates().get(0);
        int X = gViewModel.getCoordinates().get(1);
        for (int i=0; i<gViewModel.sizeShip; i++){
            myShips[Y][X+i] = true;
            field[Y][X+i].setBackgroundColor(Color.BLACK);
            field[Y][X+i].setTextColor(Color.BLACK);
            String yx = String.valueOf(Y) + String.valueOf(X+i);
            reference.child(GameStructure.Id).child(GameStructure.Role).child("ships").child(yx).setValue("a");
        }
        gViewModel.sizeShip=0;
    }

    public void fillOpponent(TextView opponentName, ImageView opponentImage){
        if(!GameStructure.opponentName.equals("")){
            opponentName.setText(GameStructure.opponentName);
            if(GameStructure.opponentImage != null)
            {
                Picasso.get().load(Uri.parse(GameStructure.opponentImage)).into(opponentImage);
            }
        }
    }

    public void OpponentEventListener(Button btnMain, Button[][] field, TextView opponentName, ImageView opponentImage) {
        DatabaseReference checkRef = db.getReference("games/" + GameStructure.Id + "/" + GameStructure.OpponentRole );
        checkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opponentData();
                GameStructure.opponentAction = snapshot.child("action").getValue(String.class);
                GameStructure.opponentName = snapshot.child("name").getValue(String.class);
                GameStructure.opponentImage = snapshot.child("image").getValue(String.class);
                if(GameStructure.opponentAction.equals("notReady")){
                    fillOpponent(opponentName, opponentImage);
                }else if(GameStructure.opponentAction.equals("won")){
                    btnMain.setEnabled(true);
                    btnMain.setText("You lost...");
                }else if(GameStructure.opponentAction.equals("ready")){
                    if(GameStructure.Action.equals("ready")){
                        drawShips(btnMain, field);
                        gViewModel.setBattle(true);
                    }
                }else if(GameStructure.opponentAction.contains("button")){
                    String substr = GameStructure.opponentAction.substring(7, 9);
                    char c = substr.charAt(0);
                    int Y = Character.getNumericValue(c);
                    c = substr.charAt(1);
                    int X = Character.getNumericValue(c);

                    if(gViewModel.myShips[Y][X])
                    {
                        shotsOpponent[Y][X] = 2;
                    }else {
                        shotsOpponent[Y][X] = 1;
                    }
                    gViewModel.setiGo(true);
                    drawShips(btnMain, field);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void drawShips(Button btnMain, Button[][] field){
        if(!gViewModel.isiGo()){
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if(myShips[i][j])
                    {
                        field[i][j].setBackgroundColor(Color.BLACK);
                        field[i][j].setTextColor(Color.BLACK);
                    }else {
                        if(shotsOpponent[i][j] == 0) {
                            field[i][j].setBackgroundColor(Color.BLUE);
                            field[i][j].setTextColor(Color.BLUE);
                        }
                    }
                    if(shotsOpponent[i][j] == 1) {
                        field[i][j].setBackgroundColor(Color.WHITE);
                        field[i][j].setTextColor(Color.WHITE);
                    }else  if (shotsOpponent[i][j] == 2) {
                        field[i][j].setBackgroundColor(Color.YELLOW);
                        field[i][j].setTextColor(Color.YELLOW);
                    }
                }
            }
            btnMain.setText("Opponent's turn");
        }else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if(shots[i][j] == 0) {
                        field[i][j].setBackgroundColor(Color.BLUE);
                        field[i][j].setTextColor(Color.BLUE);
                    }     else    if(shots[i][j] == 1) {
                        field[i][j].setBackgroundColor(Color.WHITE);
                        field[i][j].setTextColor(Color.WHITE);
                    }else if (shots[i][j] == 2) {
                        field[i][j].setBackgroundColor(Color.YELLOW);
                        field[i][j].setTextColor(Color.YELLOW);
                    }
                }
            }
            btnMain.setText("Your turn");
        }
    }

    @Override
    public void onClick(View v) {
        clickField(v, field,  btnMain, btnHuge, btnSmall,  btnMedium);
    }

}
