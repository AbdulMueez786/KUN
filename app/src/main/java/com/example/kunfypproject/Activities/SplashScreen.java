package com.example.kunfypproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.kunfypproject.MyBroadcastReceiver;
import com.example.kunfypproject.MyService;
import com.example.kunfypproject.R;

public class SplashScreen extends AppCompatActivity {
    private static  int SPLASH_SCREEN=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(isMyServiceRunning(MyService.class)==false){
            MyBroadcastReceiver receiver;
            receiver=new MyBroadcastReceiver();
            IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(receiver,intentFilter);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent=new Intent(SplashScreen.this,Login.class);
                    startActivity(intent);
            }
        },SPLASH_SCREEN);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}