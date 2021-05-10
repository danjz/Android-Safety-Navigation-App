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
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.googlemapsnavbar3.places.PlaceFileHandler;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private TextView testing;
    private String text;
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
        TextView testing = (TextView)findViewById(R.id.textView5);
        text = phoneNumber.getText().toString();
        setNumber(this, text);
        Toast.makeText(this, "The phone number has been saved", Toast.LENGTH_SHORT).show();
    }

    //call phone method that receives the MainActivity view
    public void callPhone(View view){
        String phone = text;
        if (!checkContactPermission()){
            //request for permission if not asked before
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},99);
        }
        if (text == null){
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ getNumber(this)));
        getUserDetails();
//Execution intention
        startActivity(intent);
    }
    //method to check permissions
    private boolean checkContactPermission(){
        String permission = Manifest.permission.CALL_PHONE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    //Method to set global emergency phone number variable
    public static void setNumber(Context context, String number){
        SharedPreferences prefs = context.getSharedPreferences("com.example.googlemapsnavbar3",0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("number", number);
        editor.commit();
    }
    //Method to retrieve phone number variable
    public static String getNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.example.googlemapsnavbar3", 0);
        return prefs.getString("number", "");
    }

    public void police(View view) {
        text = "999";
    }
    public void LogOut(View view) {
        mAuth.getInstance().signOut();
    }

    public void getUserDetails(){
        if(mUserLocation == null) {
            mUserLocation = new UserLocation();
        }
        DocumentReference userRef = mDB.collection(FirebaseAuth.getInstance().getUid()).document(FirebaseAuth.getInstance().getUid());
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
    public void saveUserLocation() {
        DocumentReference locationRef = mDB.collection("Users").document(FirebaseAuth.getInstance().getUid());
        locationRef.set(mUserLocation);
    }

    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mUserLocation.setTimestamp(null);
    }
}