package com.example.kunfypproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kunfypproject.Models.CalendarEvent;
import com.example.kunfypproject.Models.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MyService extends Service {
    public static final String INTENT_HANDLE_CONNECTIVITY_CHANGE ="" ;
    private MediaPlayer player;
    private Handler handler;
    @Override
    public void onCreate() {
        System.out.println("My Service - onCreate");
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }



@Override
public int onStartCommand(Intent intent, int flags, int startId) {
    System.out.println("My Service - onStartCommand");
    if (intent != null) {
        final Bundle bundle = intent.getExtras();

        // Handle the intent INTENT_HANDLE_CONNECTIVITY_CHANGEif any
        if ((bundle != null) && (bundle.get(INTENT_HANDLE_CONNECTIVITY_CHANGE) != null)) {
            handleConnectivity();
        }


    }
    System.out.println("io");
    return START_STICKY;//it will allow the recreation of a service
}

// helping functions
    private void handleConnectivity() {


        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected()) {
            SimpleDateFormat Y = new SimpleDateFormat("yyyy");
            SimpleDateFormat M = new SimpleDateFormat("MM");
            SimpleDateFormat D = new SimpleDateFormat("dd");
            SimpleDateFormat h = new SimpleDateFormat("KK");
            SimpleDateFormat m = new SimpleDateFormat("mm");
            SimpleDateFormat AMPM = new SimpleDateFormat("a");

            String year = Y.format(new Date());
            String month = M.format(new Date());
            String date = D.format(new Date());
            String hour = h.format(new Date());
            String min = m.format(new Date());
            String am_pm = AMPM.format(new Date());

            String toastText = hour+":"+min+am_pm+" "+date+"/"+month+"/"+year;
            System.out.println(toastText);
            System.out.println(year);
            System.out.println(month);
            System.out.println(date);
            System.out.println(hour);
            System.out.println(min);
            System.out.println(am_pm);

            handler = new Handler();
            /* your code here */
            new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(this, 1 * 60 * 1000); // every 1 minutes
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");

                    System.out.println("KKKKKKKKKLLLLLLLLLLLLLLLLKKKKKKKKKKKK");

                    System.out.println("");
                    System.out.println("");
                    System.out.println("");
                    System.out.println("");

                    FirebaseDatabase.getInstance().getReference("schedule")
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                            for(DataSnapshot snapshot: datasnapshot.getChildren()){

                                Schedule i=snapshot.getValue(Schedule.class);
                                if(i.getDate().matches(toastText)){

                                    if(i.getName().matches("Light")){
                                       if(i.getStatus().matches("On")){
                                           FirebaseDatabase.getInstance().getReference().child("LED_STATUS").setValue(1);
                                       }
                                       else if(i.getStatus().matches("Off")){
                                           FirebaseDatabase.getInstance().getReference().child("LED_STATUS").setValue(0);
                                       }
                                    }
                                    else if(i.getName().matches("Fan")){
                                        if(i.getStatus().matches("On")){
                                            FirebaseDatabase.getInstance().getReference().child("FAN_STATUS").setValue(0);
                                        }
                                        else if(i.getStatus().matches("Off")){
                                            FirebaseDatabase.getInstance().getReference().child("FAN_STATUS").setValue(1);
                                        }
                                    }
                                    else if(i.getName().matches("Door")){
                                        if(i.getStatus().matches("On")){
                                            FirebaseDatabase.getInstance().getReference().child("DOOR_STATUS").setValue(1);
                                        }
                                        else if(i.getStatus().matches("Off")){
                                            FirebaseDatabase.getInstance().getReference().child("DOOR_STATUS").setValue(0);
                                        }
                                    }
                                    else if(i.getName().matches("Window")){
                                        if(i.getStatus().matches("On")){
                                            FirebaseDatabase.getInstance().getReference().child("WINDOW_STATUS").setValue(1);
                                        }
                                        else if(i.getStatus().matches("Off")){
                                            FirebaseDatabase.getInstance().getReference().child("WINDOW_STATUS").setValue(0);
                                        }
                                    }
                                }


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }.run();

            // WE ARE CONNECTED: DO SOMETHING
            System.out.println("connected");
        }
        else {
            // WE ARE NOT: DO SOMETHING ELSE
            System.out.println("disconnected");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("My Service - onbind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("My Service - onDestroyed");
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

}