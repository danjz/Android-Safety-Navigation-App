package com.example.googlemapsnavbar3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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

import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private TextView testing;
    private String text;

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


    public void storeInput(View view) {
        EditText phoneNumber = (EditText)findViewById(R.id.phone_number);
        TextView testing = (TextView)findViewById(R.id.textView5);
        text = phoneNumber.getText().toString();
        Toast.makeText(this, "The phone number has been saved", Toast.LENGTH_SHORT).show();
        testing.setText(text);
    }
    public void callPhone(View view){
        //TODO add some code so that if permission is denied it is requested back.
//obtain the entered phone number
        String phone = text;
        if (text == null){
            Toast.makeText(this, "It's null lol", Toast.LENGTH_SHORT).show();
        }
        if(!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+1234));
//Execution intention
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please input mobile phone number", Toast.LENGTH_LONG).show();
        }
    }
}