package com.example.googlemapsnavbar3;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class BatterySavingSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.settings_batterysaving);

    }
}

