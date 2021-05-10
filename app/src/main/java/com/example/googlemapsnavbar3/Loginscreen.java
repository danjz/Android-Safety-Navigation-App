package com.example.googlemapsnavbar3;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Loginscreen extends AppCompatActivity {

    //google sign in client
    private GoogleSignInClient mGoogleSignInClient;
    //create random int value
    private final static int RC_SIGN_IN = 1;
    //global firebase auth
    private FirebaseAuth mAuth;
    //userID for storing in firestore
    String userID;
    //firestore database
    private FirebaseFirestore mDB;


    @Override
    protected void onStart() {
        super.onStart();
        //assign user from google account to local user var
        FirebaseUser user = mAuth.getCurrentUser();
        mDB.clearPersistence();
        if(user != null){
            //if user var isn't null, user has signed in, init MainActivity.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //display login screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loginscreen);

        //init firebaseauth and firestore
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();

        //sends request to google to sign in
        createRequest();

        //listening for sign in button clicks using lambdax
        ImageView signIn = (ImageView) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(v -> signIn());
    }

    //google sign in request
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    //Accessing GoogleSSO
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //after user has signed in
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // error message
                Toast.makeText(this, "SHA1 fingerprint error",Toast.LENGTH_LONG).show();
            }
        }
    }

    //linking the google account with user account in firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = mDB.collection("users").document(userID);
                            Map<String,Object> userMap = new HashMap<>();
                            userMap.put("userID", userID);
                            documentReference.set(userMap);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            mAuth.signOut();
                            Toast.makeText(Loginscreen.this, "Sorry you've been banned from the app", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}