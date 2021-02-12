package com.example.mylab3;

import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GameViewModel extends ViewModel {
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://my-lab3-43733-default-rtdb.firebaseio.com/");;
    DatabaseReference reference = db.getReference("games");
    
    private final MutableLiveData<int[][]> shots = new MutableLiveData<new int[10][10]>();
    private final MutableLiveData<int[][]> shotsOpponent = new MutableLiveData<new int[10][10]>();
    ArrayList<String> ships = new ArrayList<>();
    Ships selectShip;
    int sizeShip;
    boolean iGo = false;
    boolean isBattle = false;
    private final MutableLiveData<Boolean[][]> myShips = new MutableLiveData<new Boolean[10][10]>();
    private final MutableLiveData<Boolean[][]> opponentShips = new MutableLiveData<new Boolean[10][10]>();
    

    public void setCell(Button cell, int i, int j, String buttonID){
        cell.setBackgroundColor(Color.BLUE);
        cell.setText(buttonID);
        cell.setTextColor(Color.BLUE);
        myShips[i][j] = false;
        opponentShips[i][j] = false;
        shots[i][j] = 0;
        shotsOpponent[i][j] = 0;
    }

    public  void checkUser(){
        if(GameStructure.Role.equals("creator")){
            iGo = true;
        }
    }

    public void setShip(Ships ship){
        selectShip = ship;
    }

    public  void clickField(View v, Button[][] field, Button btnMain, Button btnHuge, Button btnSmall, Button btnMedium){
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
            fillField(((Button) v).getText().toString(), field);
            checkShips(btnHuge, btnSmall,  btnMedium, btnMain);
        }else if(iGo){
            checkShot(((Button) v).getText().toString(), field,  btnMain);
        }
    }

    public  void changeTurn(Button btnMain, Button[][] field){
        String info = "Wait opponent";
        btnMain.setText(info);
        GameStructure.Action = "ready";
        reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue(GameStructure.Action);
        if(GameStructure.opponentAction.equals("ready")){
            isBattle = true;
            drawShips(btnMain, field);
        }
    }

    public void checkShot(String shot, Button[][] field, Button btnMain){
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
        drawShips(btnMain, field);
        Victoty(btnMain);
    }

    public void Victoty(Button btnMain){
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

    public void checkShips(Button btnHuge, Button btnSmall, Button btnMedium, Button btnMain){
        btnHuge.setEnabled(Ships.BIG.getCount() != 0);
        btnMedium.setEnabled(Ships.MEDIUM.getCount() != 0);
        btnSmall.setEnabled(Ships.LITTLE.getCount() != 0);
        btnMain.setEnabled(Ships.BIG.getCount() == 0 && Ships.MEDIUM.getCount() == 0 && Ships.LITTLE.getCount() == 0);
        sizeShip = 0;
    }

    public void fillField(String buttonName, Button[][] field){
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
                    drawShips(btnMain, field);
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

    public void drawShips(Button btnMain, Button[][] field){
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
