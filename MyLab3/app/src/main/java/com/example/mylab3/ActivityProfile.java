package com.example.mylab3;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ActivityProfile extends AppCompatActivity {

    EditText editUsername;
    TextView lblMail, lblWins, lblGames;
    Button btnImage, btnName, btnSave;
    ImageView imgUser;
    FirebaseDatabase rootNode = FirebaseDatabase.getInstance("https://sea-battle-43733-default-rtdb.firebaseio.com/");;
    DatabaseReference reference = rootNode.getReference("users");

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editUsername = findViewById(R.id.txt_username);
        lblMail = findViewById(R.id.txt_mail);
        lblWins = findViewById(R.id.txt_wins);
        lblGames = findViewById(R.id.txt_games);
        btnImage = findViewById(R.id.btn_image);
        btnName = findViewById(R.id.btn_name);
        imgUser = findViewById(R.id.img_user);
        btnSave = findViewById(R.id.btn_save_all);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        editUsername.setText(GameStructure.myName);
        lblMail.setText(GameStructure.myEmail);
        String text = "Wins: " + GameStructure.myWins;
        lblWins.setText(text);
        text = "Games: " + GameStructure.myGames;
        lblGames.setText(text);

        Picasso.get().load(Uri.parse(GameStructure.myImage)).into(imgUser);



        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!GameStructure.myName.equals(editUsername.getText().toString()))
                {
                    reference.child(GameStructure.myName).setValue(null);
                    GameStructure.myName=editUsername.getText().toString();
                    reference.child(editUsername.getText().toString()).child("name").setValue(GameStructure.myName);
                    reference.child(editUsername.getText().toString()).child("email").setValue(GameStructure.myEmail);
                    reference.child(editUsername.getText().toString()).child("image").setValue(GameStructure.myImage);
                    reference.child(editUsername.getText().toString()).child("password").setValue(GameStructure.myPassword);
                    reference.child(editUsername.getText().toString()).child("statistics").child("wins").setValue(GameStructure.myName);
                    reference.child(editUsername.getText().toString()).child("statistics").child("games").setValue(GameStructure.myName);
                    Toast.makeText(ActivityProfile.this, "Updated!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileFolder();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                Intent intent = new Intent(getApplicationContext(), ActivityListRoom.class);
                startActivity(intent);
            }
        });
    }

    private void openFileFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedUri = data.getData();
            Picasso.get().load(selectedUri).into(imgUser);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (selectedUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(selectedUri));
            storageTask = fileReference.putFile(selectedUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ActivityProfile.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();

                            GameStructure.myImage = url.toString();
                            reference.child(GameStructure.myName).child("image").setValue(GameStructure.myImage );
                            Picasso.get().load(selectedUri).into(imgUser);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ActivityProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
        }
    }
}
