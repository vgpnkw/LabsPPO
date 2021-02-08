package com.example.mylab3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegistration extends AppCompatActivity {
    EditText txtName, txtEmail, txtPassword;
    Button btnSignIn, btnSignUp;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://sea-battle-43733-default-rtdb.firebaseio.com/");;
    DatabaseReference reference = db.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        txtName = findViewById(R.id.name);
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignIn = findViewById(R.id.to_sign_in);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputName = txtName.getText().toString();
                String inputEmail = txtEmail.getText().toString();
                String inputPassword = txtPassword.getText().toString();
                reference.child(inputEmail).child("name").setValue(inputName);
                reference.child(inputEmail).child("email").setValue(inputEmail);
                reference.child(inputEmail).child("image").setValue("");
                reference.child(inputEmail).child("password").setValue(inputPassword);
                reference.child(inputEmail).child("statistics").child("wins").setValue("0");
                reference.child(inputEmail).child("statistics").child("games").setValue("0");
                GameStructure gameStructure = new GameStructure(inputName, inputEmail, inputPassword, "", "0", "0");
                Intent intent = new Intent(getApplicationContext(), ActivityListRoom.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivitySignIn.class);
                startActivity(intent);
            }
        });
    }
}
