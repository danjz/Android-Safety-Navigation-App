package com.example.googlemapsnavbar3;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogOut extends AppCompatActivity {

    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FirebaseAuth.getInstance().signOut();
    }

}
