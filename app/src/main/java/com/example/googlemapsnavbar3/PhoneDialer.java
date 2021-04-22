package com.example.googlemapsnavbar3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneDialer extends MainActivity {
    public void callPhone(String phoneNumber){
//obtain the entered phone number
        String phone = phoneNumber;
        if(!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
//Execution intention
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please input mobile phone number", Toast.LENGTH_LONG).show();
        }
    }
}

