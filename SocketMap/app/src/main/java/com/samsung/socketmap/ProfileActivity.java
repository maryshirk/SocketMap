package com.samsung.socketmap;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samsung.socketmap.models.User;

public class ProfileActivity extends AppCompatActivity {

    AppCompatButton btnEditInfo;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    ConstraintLayout root;
    TextView nameTextView, emailTextView, phoneTextView, cityTextView;
    EditText emailEditText, phoneEditText, cityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        btnEditInfo = findViewById(R.id.btn_edit_info);

        nameTextView = findViewById(R.id.profile_name);
        emailTextView = findViewById(R.id.profile_email);
        phoneTextView = findViewById(R.id.profile_teleph);
        cityTextView = findViewById(R.id.profile_city);

        emailEditText = findViewById(R.id.profile_email_et);
        phoneEditText= findViewById(R.id.profile_teleph_et);
        cityEditText = findViewById(R.id.profile_city_et);

        root = findViewById(R.id.profile);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = db.getReference("Users");

        DatabaseReference userRef = users.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String city = dataSnapshot.child("city").getValue(String.class);

                nameTextView.setText(name);
                emailEditText.setText(email);
                if (!phone.isEmpty()) {
                    phoneEditText.setText(phone);
                } else {
                    phoneEditText.setText("не указан");
                }
                cityEditText.setText(city);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });


        cityEditText.setOnClickListener(v -> {
            cityEditText.setFocusableInTouchMode(true);
            cityEditText.setFocusable(true);
        });

        phoneEditText.setOnClickListener(v -> {
            if (phoneEditText.getText().toString() == "не указан") {
                phoneEditText.setText("");
            }
            phoneEditText.setFocusableInTouchMode(true);
            phoneEditText.setFocusable(true);
        });

        emailEditText.setOnClickListener(v -> {
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setFocusable(true);
        });

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEditText.getText().toString() != "" & cityEditText.getText().toString() != "") {
                    userRef.child("city").setValue(cityEditText.getText().toString());
                    userRef.child("email").setValue(emailEditText.getText().toString());
                    userRef.child("phone").setValue(phoneEditText.getText().toString());
                }
                emailEditText.setFocusableInTouchMode(false);
                emailEditText.setFocusable(false);
                phoneEditText.setFocusableInTouchMode(false);
                phoneEditText.setFocusable(false);
                cityEditText.setFocusableInTouchMode(false);
                cityEditText.setFocusable(false);
            }
        });


        ImageView ratingIcon = findViewById(R.id.rating_icon);
        ratingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, RatingActivity.class);
                startActivity(intent);
            }
        });

        ImageView mapIcon = findViewById(R.id.map_icon);
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }
}
