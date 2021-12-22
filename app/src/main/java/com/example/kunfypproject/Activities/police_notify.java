package com.example.kunfypproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kunfypproject.R;

public class police_notify extends AppCompatActivity {


    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_notify);

        send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED){
                        sendSMS();
                    }
                    else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);

                    }
                }
            }
        });

    }
    private void sendSMS(){
        String phoneNo="123";
        String SMS="Name : "+"Abdul Mueez\n Address : "+"E 11/3 ,STREET 63, H#: 207,Islamabad \n"+"Thread of intruder,send help.";
        try{
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,SMS,null,null);
            Toast.makeText(this,"Message is sent",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}