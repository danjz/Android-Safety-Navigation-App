package com.example.googlemapsnavbar3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.os.Build;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bottom navigation menu code

        //Find and initialize BottomNavigationView and NavController
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment);

        //Get navigation destination
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.firstFragment);
        topLevelDestinations.add(R.id.mapsFragment);
        topLevelDestinations.add(R.id.thirdFragment);

        //Connect the destinations with their respective button presses
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        //navigation end
    }

    public void saveLocations(View view){
        PlaceFileHandler.savePlacesToFile("safePlaces.txt",this.getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadLocations(View view) throws IOException, ParserConfigurationException, SAXException, ExecutionException, InterruptedException {
        PlaceFileHandler.loadPlacesFromFile("safePlaces.txt",this.getApplicationContext());
    }


}