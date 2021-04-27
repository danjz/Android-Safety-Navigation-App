package com.example.googlemapsnavbar3;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

public class StoreLocation extends AppCompatActivity {
    private GeoPoint mUserLocation;
    public FirebaseFirestore mDB;
    private @ServerTimestamp String timestmap;
    private User user;

    public StoreLocation(GeoPoint mUserLocation, String timestmap, User user) {
        this.mUserLocation = mUserLocation;
        this.timestmap = timestmap;
        this.user = user;
    }
    public StoreLocation() {

    }

    public GeoPoint getmUserLocation() {
        return mUserLocation;
    }

    public FirebaseFirestore getmDB() {
        return mDB;
    }

    public String getTimestmap() {
        return timestmap;
    }

    public User getUser() {
        return user;
    }

    public void setmUserLocation(GeoPoint mUserLocation) {
        this.mUserLocation = mUserLocation;
    }

    public void setmDB(FirebaseFirestore mDB) {
        this.mDB = mDB;
    }

    public void setTimestmap(String timestmap) {
        this.timestmap = timestmap;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loginscreen);

        mDB = FirebaseFirestore.getInstance();
    }

}
