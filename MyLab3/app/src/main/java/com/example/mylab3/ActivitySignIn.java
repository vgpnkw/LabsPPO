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

public class ActivitySignIn extends AppCompatActivity {
    EditText txtEmail, txtPassword;
    Button btnSignIn, btnSignUp;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://sea-battle-43733-default-rtdb.firebaseio.com/");
    DatabaseReference reference = db.getReference("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        btnSignUp = findViewById(R.id.to_sign_up);
        btnSignIn = findViewById(R.id.btn_sign_in);
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtPassword.getText().toString().isEmpty() || !txtPassword.getText().toString().isEmpty()) {
                    tryToSignIn();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRegistration.class);
                startActivity(intent);
            }
        });
    }

    private void tryToSignIn() {
        final String inputEmail = txtEmail.getText().toString().trim();
        final String inputPassword = txtPassword.getText().toString().trim();
        Query checkUser = reference.orderByChild("email").equalTo(inputEmail);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    txtEmail.setError(null);
                    String passwordDB = dataSnapshot.child(inputEmail).child("password").getValue(String.class);
                    if (passwordDB.equals(inputPassword)) {
                        txtEmail.setError(null);
                        String nameDB = dataSnapshot.child(inputEmail).child("name").getValue(String.class);
                        String imageDB = dataSnapshot.child(inputEmail).child("image").getValue(String.class);
                        String winsDB = dataSnapshot.child(inputEmail).child("statistics").child("wins").getValue(String.class);
                        String gamesDB = dataSnapshot.child(inputEmail).child("statistics").child("games").getValue(String.class);
                        GameStructure gameStructure = new GameStructure(nameDB, inputEmail, inputPassword, imageDB, winsDB, gamesDB);
                        Intent intent = new Intent(getApplicationContext(), ActivityListRoom.class);
                        startActivity(intent);
                    } else {
                        txtPassword.setError("Wrong Password");
                        txtPassword.requestFocus();
                    }
                } else {
                    txtEmail.setError("Wrong email");
                    txtEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
