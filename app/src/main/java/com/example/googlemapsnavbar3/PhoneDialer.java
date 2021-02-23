package com.example.googlemapsnavbar3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneDialer extends Activity {

    //Declare control objects
    private EditText et_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//Find controls in view
        et_phone = (EditText)findViewById(R.id.et_phone);
    }

    /**
     * The realization of dialing
     * @param v
     */
    public void callPhone(View v){
//obtain the entered phone number
        String phone = et_phone.getText().toString();


        if(!TextUtils.isEmpty(phone)){

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));


//Execution intention
            startActivity(intent);


        }else{

            Toast.makeText(this, "Please input mobile phone number", Toast.LENGTH_LONG).show();

        }
    }
}

