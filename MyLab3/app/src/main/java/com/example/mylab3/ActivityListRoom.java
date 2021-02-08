package com.example.mylab3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class ActivityListRoom extends AppCompatActivity {

    Button btnSignOut, btnProfile, btnCreate, btnFind;
    EditText txtKey;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://sea-battle-43733-default-rtdb.firebaseio.com/");;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room);
        btnSignOut = findViewById(R.id.sign_out);
        btnProfile = findViewById(R.id.profile);
        btnCreate = findViewById(R.id.btn_create);
        btnFind = findViewById(R.id.btn_find);
        txtKey = findViewById(R.id.id);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivitySignIn.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityProfile.class);
                startActivity(intent);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = db.getReference("games");
                GameStructure.Id = UUID.randomUUID().toString().substring(0, 5);
                GameStructure.Role = "creator";
                GameStructure.OpponentRole = "guest";
                GameStructure.Action = "notReady";
                GameStructure.opponentName = "";
                reference.child(GameStructure.Id).child("id").setValue(GameStructure.Id);
                reference.child(GameStructure.Id).child(GameStructure.Role).child("name").setValue(GameStructure.myName);
                reference.child(GameStructure.Id).child(GameStructure.Role).child("action").setValue(GameStructure.Action);
                reference.child(GameStructure.Id).child(GameStructure.Role).child("image").setValue(GameStructure.myImage);
                reference.child(GameStructure.Id).child(GameStructure.OpponentRole).child("action").setValue("a");
                Intent intent = new Intent(getApplicationContext(), ActivityGame.class);
                startActivity(intent);
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = txtKey.getText().toString().trim();
                reference = db.getReference("games");
                Query checkUser = reference.orderByChild("id").equalTo(input);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            txtKey.setError(null);
                            GameStructure.Role = "guest";
                            GameStructure.OpponentRole = "creator";
                            GameStructure.Action = "notReady";
                            GameStructure.opponentName = dataSnapshot.child(input).child("creator").child("name").getValue(String.class);
                            GameStructure.opponentImage = dataSnapshot.child(input).child("creator").child("image").getValue(String.class);
                            GameStructure.Id = dataSnapshot.child(input).child("id").getValue(String.class);
                            reference.child(input).child(GameStructure.Role).child("image").setValue(GameStructure.myImage);
                            reference.child(input).child(GameStructure.Role).child("name").setValue(GameStructure.myName);
                            reference.child(input).child(GameStructure.Role).child("action").setValue(GameStructure.Action);
                            Intent intent = new Intent(getApplicationContext(), ActivityGame.class);
                            startActivity(intent);
                        } else {
                            txtKey.setError("No such room exist");
                            txtKey.requestFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

}
