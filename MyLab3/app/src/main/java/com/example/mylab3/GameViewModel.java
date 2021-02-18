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
    private ArrayList<String> ships = new ArrayList<>();
    private List<Integer> coordinates = new ArrayList<Integer>();
    Ships selectShip;
    int sizeShip;
    boolean shipopponent;
    private boolean iGo = false;
    private boolean isBattle = false;
    private final MutableLiveData<Boolean[][]> myShips = new MutableLiveData<new Boolean[10][10]>();
    private final MutableLiveData<Boolean[][]> opponentShips = new MutableLiveData<new Boolean[10][10]>();


    public void setCell(int i, int j, String buttonID){
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

    public void getCount()
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
    }

    public void refactorString(String shot)
    {
        coordinates.clear();
        String subStr = shot.substring(7, 9);
        char c = subStr.charAt(0);
        int Y = Character.getNumericValue(c);
        coordinates.add(Y);
        c = subStr.charAt(1);
        int X = Character.getNumericValue(c);
        coordinates.add(X);
    }


    public int [] getcheckShot(String shot){
        refactorString(shot);
        int Y = coordinates.get(0);
        int X = coordinates.get(1);;
        if(shots[Y][X] != 0) {
            return;
        }
        if(opponentShips[Y][X])
        {
            opponentShips[Y][X] = false;
            opponentShips = true;
            ships.remove(shot.substring(7, 9));
            shots[Y][X] = 2;
        }else {
            shots[Y][X] = 1;
        }
        reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue(shot);
        return temp;
    }

    public boolean isShip()
    {
        return opponentShips;
    }

    public boolean emptyShip()
    {
        return ships.isEmpty();
    }

    public void Victoty(){
        if(ships.isEmpty()){
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

    public void getfillField(String buttonName){
        refactorString(buttonName);
        int Y = coordinates.get(0);
        int X = coordinates.get(1);
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

        //return temp;
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

    public void setBattle(boolean battle) {
        isBattle = battle;
    }

    public void setiGo(boolean iGo) {
        this.iGo = iGo;
    }

    public boolean isiGo() {
        return iGo;
    }

    public boolean isBattle() {
        return isBattle;
    }

    public List<Integer> getCoordinates() {
        return coordinates;
    }
}
