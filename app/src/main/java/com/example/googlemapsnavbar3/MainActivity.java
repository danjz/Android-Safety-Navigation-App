package com.example.googlemapsnavbar3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.example.googlemapsnavbar3.places.PlaceFileHandler;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private String usernumber;
    public FirebaseFirestore mDB = FirebaseFirestore.getInstance();
    private UserLocation mUserLocation;
    public FirebaseAuth mAuth;

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

        if(!checkContactPermission()){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},99);
        }
    }

    public void saveLocations(View view){
        PlaceFileHandler.savePlacesToFile("safePlaces.txt",this.getApplicationContext());
        Context context = this.getApplicationContext();
        File file = context.getFileStreamPath("safePlaces.txt");
        if (file.exists()){
            Log.d("MainAc - saveLocations", "Location File exists");
        }
        else{
            Log.d("MainAc - saveLocations", "Location File doesn't exists");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadLocations(View view) throws IOException, ParserConfigurationException, SAXException, ExecutionException, InterruptedException {
        PlaceFileHandler.loadPlacesFromFile("safePlaces.txt",this.getApplicationContext());
    }

    public void storeInput(View view) {
        EditText phoneNumber = (EditText)findViewById(R.id.phone_number);
        usernumber = phoneNumber.getText().toString();
        setNumber(this, usernumber);
        Toast.makeText(this, "The phone number has been saved", Toast.LENGTH_SHORT).show();
    }

    //call phone method that receives the MainActivity view
    public void callPhone(View view){
        CheckBox checkBox = findViewById(R.id.checkBox);
        String phone = usernumber;
        getUserDetails();
        if (!checkContactPermission()){
            //request for permission if denied before
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},99);
        }
        if (checkBox.isChecked()){
            Intent callingpolice = new Intent(Intent.ACTION_CALL, Uri.parse("tel:999"));
            startActivity(callingpolice);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ getNumber(this)));
            startActivity(intent);
        }
    }
    //check calling permission
    private boolean checkContactPermission(){
        String permission = Manifest.permission.CALL_PHONE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    //set global emergency phone number variable
    public static void setNumber(Context context, String number){
        SharedPreferences prefs = context.getSharedPreferences("com.example.googlemapsnavbar3",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("number", number);
        editor.commit();
    }
    //retrieve phone number variable
    public static String getNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.example.googlemapsnavbar3", 0);
        return prefs.getString("number", "");
    }

    //police number
    public void police(View view) {
        usernumber = "999";
    }
    //allow the user to log out of the app
    public void LogOut(View view) {
        mAuth.signOut();
        FirebaseAuth.getInstance().signOut();
    }

    //get User object
    public void getUserDetails(){
        if(mUserLocation == null) {
            mUserLocation = new UserLocation();
        }
        DocumentReference userRef = mDB.collection("In Emergency").document(FirebaseAuth.getInstance().getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful()){
                User user = task.getResult().toObject(User.class);
                mUserLocation.setUser(user);
                mUserLocation.setmUserLocation(null);
                getLastKnownLocation();
                    }
                }
            });
    }
    //save user location
    public void saveUserLocation() {
        DocumentReference locationRef = mDB.collection("Users").document(FirebaseAuth.getInstance().getUid());
        locationRef.set(mUserLocation);
    }

    //TODO get the user's last known location
    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mUserLocation.setTimestamp(null);
        saveUserLocation();
    }
}