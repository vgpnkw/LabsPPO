package com.example.labthreeppo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    ImageView iv_dice_p1, iv_dice_p2, iv_lives_p1, iv_lives_p2 ;
    TextView tv_player1, tv_player2;
    Random r;
    int livesP1, livesP2;
    int rolledP1, rolledP2;

    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";
    FirebaseDatabase database;
    DatabaseReference messageRef;



    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            roomName = extras.getString("roomName");
            if(roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }

        }


        r = new Random();
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        iv_dice_p1 = findViewById(R.id.iv_dice_p1);
        iv_dice_p2 = findViewById(R.id.iv_dice_p2);

        iv_lives_p1 = findViewById(R.id.iv_lives_p1);
        iv_lives_p2 = findViewById(R.id.iv_lives_p2);

        tv_player1 = findViewById(R.id.tv_player1);
        tv_player2 = findViewById(R.id.tv_player2);

        tv_player1.setText(String.format("%s ROLL", playerName));
        tv_player2.setText(String.format("%s ROLL", playerName));

        livesP1 = 6;
        livesP2 = 6;
        setDiceImage(livesP1, iv_lives_p1);
        setDiceImage(livesP2, iv_lives_p2);


        iv_dice_p1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                rolledP1 = r.nextInt(6) + 1;
                setDiceImage(rolledP1, iv_dice_p1);
                iv_dice_p1.startAnimation(animation);


                if(rolledP2 != 0) {
                    tv_player1.setText(String.format("%s ROLL", playerName));
                    tv_player2.setText(String.format("%s ROLL", playerName));

                    if(rolledP1 > rolledP2) {
                        livesP2--;
                        setDiceImage(livesP2, iv_lives_p2);

                        Toast.makeText(GameActivity.this, "Player 1 WIN", Toast.LENGTH_SHORT).show();
                    }
                    if(rolledP2 > rolledP1){
                        livesP1--;
                        setDiceImage(livesP1, iv_lives_p1);
                        Toast.makeText(GameActivity.this, "Player 2 WIN", Toast.LENGTH_SHORT).show();
                    }
                    if(rolledP1 == rolledP2) {
                        Toast.makeText(GameActivity.this, "Draw ", Toast.LENGTH_SHORT).show();
                    }

                    rolledP2 = 0;
                    rolledP1 = 0;

                    iv_dice_p1.setEnabled(true);
                    iv_dice_p2.setEnabled(true);
                    checkEndGame();

                } else {
                    tv_player1.setText(String.format("%s ROLLED!", playerName));
                    iv_dice_p1.setEnabled(false);
                    message = role + ": ROLLED!";
                    messageRef.setValue(message);
                }
            }
        });

        iv_dice_p2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                rolledP2 = r.nextInt(6) + 1;
                setDiceImage(rolledP2, iv_dice_p2);
                iv_dice_p2.startAnimation(animation);
                if(rolledP1 != 0) {
                    tv_player1.setText(String.format("%s ROLL", playerName));
                    tv_player1.setText(String.format("%s ROLL", playerName));
                    if(rolledP1 > rolledP2) {
                        livesP2--;
                        setDiceImage(livesP2, iv_lives_p2);
                        Toast.makeText(GameActivity.this, "Player 1 WIN", Toast.LENGTH_SHORT).show();
                    }
                    if(rolledP2 > rolledP1){
                        livesP1--;
                        setDiceImage(livesP1, iv_lives_p1);
                        Toast.makeText(GameActivity.this, "Player 2 WIN", Toast.LENGTH_SHORT).show();
                    }

                    if(rolledP1 == rolledP2) {
                        Toast.makeText(GameActivity.this, "Draw ", Toast.LENGTH_SHORT).show();
                    }

                    rolledP1 = 0;
                    rolledP2 = 0;

                    iv_dice_p1.setEnabled(true);
                    iv_dice_p2.setEnabled(true);
                    checkEndGame();

                } else {
                    tv_player2.setText(String.format("%s ROLLED!", playerName));
                    iv_dice_p2.setEnabled(false);
                    message = role + ": ROLLED!";
                    messageRef.setValue(message);
                }
            }
        });
        //listen for incomming message
        messageRef = database.getReference("rooms/" + roomName + "/message");
        message = role + "ROLLED!";
        messageRef.setValue(message);
        addRoomEventListener();

    }
    private  void setDiceImage(int dice, ImageView image) {
        switch (dice) {
            case 1:
                image.setImageResource(R.drawable.dice1);

                break;
            case 2:
                image.setImageResource(R.drawable.dice2);

                break;
            case 3:
                image.setImageResource(R.drawable.dice3);

                break;
            case 4:
                image.setImageResource(R.drawable.dice4);

                break;
            case 5:
                image.setImageResource(R.drawable.dice5);

                break;
            case 6:
                image.setImageResource(R.drawable.dice6);

                break;
            default:
                image.setImageResource(R.drawable.krest);

        }
    }
    private void  checkEndGame() {
        if (livesP1 == 0 || livesP2 == 0) {
            iv_dice_p1.setEnabled(false);
            iv_dice_p2.setEnabled(false);

            String text = "";
            if(livesP1 != 0) {
                text = String.format("Winner %s!", playerName);
            }
            if(livesP2 != 0) {
                text = String.format("Winner %s!", playerName);
            }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage(text);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }
    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(role.equals("host")) {
                    if(snapshot.getValue(String.class).contains("quest:")) {
                        iv_dice_p1.setEnabled(true);
                        Toast.makeText(GameActivity.this, "" + snapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                    } else {
                        iv_dice_p1.setEnabled(true);
                        Toast.makeText(GameActivity.this, "" + snapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageRef.setValue(message);
            }
        });
    }
}