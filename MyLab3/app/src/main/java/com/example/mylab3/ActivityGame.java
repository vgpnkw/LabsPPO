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
    private final Button[][] field = new Button[10][10];
    private final Boolean[][] myShips = new Boolean[10][10];
    private final Boolean[][] opponentShips = new Boolean[10][10];
    private final int[][] shots = new int[10][10];
    private final int[][] shotsOpponent = new int[10][10];
    ArrayList<String> ships = new ArrayList<>();
    Button btnHuge, btnMedium, btnSmall;
    Button btnMain;
    TextView opponentName;
    TextView id;
    ImageView opponentImage;
    Ships selectShip;
    int sizeShip;
    boolean iGo = false;
    boolean isBattle = false;


    FirebaseDatabase db = FirebaseDatabase.getInstance("https://my-lab3-43733-default-rtdb.firebaseio.com/");;
    DatabaseReference reference = db.getReference("games");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        opponentName = findViewById(R.id.txt_opponent);
        id = findViewById(R.id.txt_id);
        id.setText(GameStructure.Id);
        opponentImage = findViewById(R.id.img_user);
        fillOpponent();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                field[i][j] = findViewById(resID);
                field[i][j].setOnClickListener(this);
                field[i][j].setBackgroundColor(Color.BLUE);
                field[i][j].setText(buttonID);
                field[i][j].setTextColor(Color.BLUE);
                myShips[i][j] = false;
                opponentShips[i][j] = false;
                shots[i][j] = 0;
                shotsOpponent[i][j] = 0;
            }
        }

        if(GameStructure.Role.equals("creator")){
            iGo = true;
        }

        btnSmall = findViewById(R.id.small_ship);
        btnSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShip = Ships.LITTLE;
            }
        });

        btnMedium = findViewById(R.id.medium_ship);
        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShip = Ships.MEDIUM;
            }
        });

        btnHuge = findViewById(R.id.huge_ship);
        btnHuge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectShip = Ships.BIG;
            }
        });

        btnMain = findViewById(R.id.btn_game);
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnAction;
                if (GameStructure.Action.equals("notReady")) {
                    btnAction = "Wait opponent";
                    btnMain.setText(btnAction);
                    GameStructure.Action = "ready";
                    reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue(GameStructure.Action);
                    if(GameStructure.opponentAction.equals("ready")){
                        isBattle = true;
                        drawShips();
                    }
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), ActivityListRoom.class);
                    startActivity(intent);
                }
            }
        });
        checkShips();
        OpponentEventListener();
    }

    @Override
    public void onClick(View v) {
        if(!isBattle)
        {
            if (selectShip == Ships.NULL) {
                return;
            } else if(selectShip == Ships.LITTLE) {
                sizeShip = Ships.LITTLE.getSize();
            } else if(selectShip == Ships.MEDIUM) {
                sizeShip = Ships.MEDIUM.getSize();
            } else {
                sizeShip = Ships.BIG.getSize();
            }
            fillField(((Button) v).getText().toString());
            checkShips();
        }else if(iGo){
            checkShot(((Button) v).getText().toString());
        }
    }

    public void checkShot(String shot){
        String subStr = shot.substring(7, 9);
        char c = subStr.charAt(0);
        int Y = Character.getNumericValue(c);
        c = subStr.charAt(1);
        int X = Character.getNumericValue(c);
        if(shots[Y][X] != 0) {
            return;
        }
        if(opponentShips[Y][X])
        {
            field[Y][X].setBackgroundColor(Color.YELLOW);
            field[Y][X].setTextColor(Color.YELLOW);
            opponentShips[Y][X] = false;
            ships.remove(subStr);
            shots[Y][X] = 2;
        }else {
            field[Y][X].setBackgroundColor(Color.WHITE);
            field[Y][X].setTextColor(Color.WHITE);
            shots[Y][X] = 1;
        }
        reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue(shot);
        iGo = false;
        drawShips();
        Victoty();
    }

    public void Victoty(){
        if(ships.isEmpty()){
            btnMain.setText("You won!");
            btnMain.setEnabled(true);
            reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue("won");
            reference = db.getReference("users");


            Query checkUser = reference.orderByChild("name").equalTo(GameStructure.myName);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String winsDB = dataSnapshot.child(GameStructure.myName).child("statistics").child("wins").getValue(String.class);
                        String gamesDB = dataSnapshot.child(GameStructure.myName).child("statistics").child("games").getValue(String.class);
                        int wins = Integer.parseInt(winsDB);
                        int games = Integer.parseInt(gamesDB);
                        wins++;
                        games++;

                        reference.child(GameStructure.myName).child("statistics").child("wins").setValue(String.valueOf(wins));
                        reference.child(GameStructure.myName).child("statistics").child("games").setValue(String.valueOf(games));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void checkShips(){
        btnHuge.setEnabled(Ships.BIG.getCount() != 0);
        btnMedium.setEnabled(Ships.MEDIUM.getCount() != 0);
        btnSmall.setEnabled(Ships.LITTLE.getCount() != 0);
        btnMain.setEnabled(Ships.BIG.getCount() == 0 && Ships.MEDIUM.getCount() == 0 && Ships.LITTLE.getCount() == 0);
        sizeShip = 0;
    }

    public void fillField(String buttonName){
        String subStr = buttonName.substring(7, 9);
        char c = subStr.charAt(0);
        int Y = Character.getNumericValue(c);
        c = subStr.charAt(1);
        int X = Character.getNumericValue(c);
        for (int i=0; i<sizeShip; i++){
            if (myShips[Y][X] != myShips[Y][X + i]  || X+sizeShip>10) {
                return;
            }
        }
        if(sizeShip == 4)
        {
            Ships.BIG.countMinus();
        }else if (sizeShip == 3){
            Ships.MEDIUM.countMinus();
        }else {
            Ships.LITTLE.countMinus();
        }
        for (int i=0; i<sizeShip; i++){
            myShips[Y][X+i] = true;
            field[Y][X+i].setBackgroundColor(Color.BLACK);
            field[Y][X+i].setTextColor(Color.BLACK);
            String yx = String.valueOf(Y) + String.valueOf(X+i);
            reference.child(GameStructure.Id).child(GameStructure.Role).child("ships").child(yx).setValue("a");
        }
        sizeShip=0;
    }

    public void fillOpponent(){
        if(!GameStructure.opponentName.equals("")){
            opponentName.setText(GameStructure.opponentName);
            if(GameStructure.opponentImage != null)
            {
                Picasso.get().load(Uri.parse(GameStructure.opponentImage)).into(opponentImage);
            }
        }
    }

    public void OpponentEventListener() {
        DatabaseReference checkRef = db.getReference("games/" + GameStructure.Id + "/" + GameStructure.OpponentRole );
        checkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opponentData();
                GameStructure.opponentAction = snapshot.child("action").getValue(String.class);
                GameStructure.opponentName = snapshot.child("name").getValue(String.class);
                GameStructure.opponentImage = snapshot.child("image").getValue(String.class);
                if(GameStructure.opponentAction.equals("notReady")){
                    fillOpponent();
                }else if(GameStructure.opponentAction.equals("won")){
                    btnMain.setEnabled(true);
                    btnMain.setText("You lost...");
                }else if(GameStructure.opponentAction.equals("ready")){
                    if(GameStructure.Action.equals("ready")){
                        drawShips();
                        isBattle = true;
                    }
                }else if(GameStructure.opponentAction.contains("button")){
                    String substr = GameStructure.opponentAction.substring(7, 9);
                    char c = substr.charAt(0);
                    int Y = Character.getNumericValue(c);
                    c = substr.charAt(1);
                    int X = Character.getNumericValue(c);

                    if(myShips[Y][X])
                    {
                        shotsOpponent[Y][X] = 2;
                    }else {
                        shotsOpponent[Y][X] = 1;
                    }
                    iGo = true;
                    drawShips();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void opponentData() {
        DatabaseReference ref = db.getReference("games").child(GameStructure.Id).child(GameStructure.OpponentRole).child("ships");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    fillData(postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fillData(String data){
        char c = data.charAt(0);
        int Y = Character.getNumericValue(c);
        c = data.charAt(1);
        int X = Character.getNumericValue(c);
        opponentShips[Y][X] = true;
        ships.add(data);
    }

    public void drawShips(){
        if(!iGo){
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
}