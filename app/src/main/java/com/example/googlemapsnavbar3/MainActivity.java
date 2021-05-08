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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.googlemapsnavbar3.places.PlaceFileHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.Text;
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
    public FirebaseFirestore mDB;
    private StoreLocation StoreLocation;
    public FirebaseAuth mAuth;
    String userID;

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
//Execution intention
        startActivity(intent);
    }
    //method to check permissions
    private boolean checkContactPermission(){
        String permission = Manifest.permission.CALL_PHONE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    //store user's location in firebase
    private void StoreLocation(){
        if(StoreLocation != null){
            DocumentReference locationRef = mDB.collection("UserLocations").document(FirebaseAuth.getInstance().getUid());
        }
    }
    private void getUserDetails(){
        if(StoreLocation == null){
            StoreLocation = new StoreLocation();

            DocumentReference userRef = mDB.collection("Users").document(FirebaseAuth.getInstance().getUid());
        }
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
}