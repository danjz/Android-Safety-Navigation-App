package com.example.googlemapsnavbar3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);

        button = (Button) findViewById(R.id.Nextbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainScreen();
            }
        });
    }
    public void MainScreen() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }


}
