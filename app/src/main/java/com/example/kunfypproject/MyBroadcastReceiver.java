package com.example.kunfypproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static boolean connection;



    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(intent.getAction())){
            boolean conn=intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
            this.connection=conn;

            final Intent intentService = new Intent(context, MyService.class);
            intentService.putExtra(MyService.INTENT_HANDLE_CONNECTIVITY_CHANGE, "");
            context.startService(intentService);

            if(conn){
                 Toast.makeText(context,"Disconnected",Toast.LENGTH_LONG).show();
            }else{
                 Toast.makeText(context,"Connected",Toast.LENGTH_LONG).show();
            }
        }
        if("com.armughanaslam.broadcastsender2".equalsIgnoreCase(intent.getAction())){
            Toast.makeText(context,intent.getStringExtra("msg"),Toast.LENGTH_LONG).show();
        }
    }

}

