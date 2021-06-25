package com.example.kunfypproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunfypproject.Fragments.HomeFragment;
import com.example.kunfypproject.Fragments.ScheduleFragment;
import com.example.kunfypproject.Fragments.SecurityFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Home extends AppCompatActivity implements TaskBottomSheetDialog.BottomSheetListener{

    List<ScheduledTask> scheduledTasks;
    private String dateSelected="";
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        Bundle arg=new Bundle();
        arg.putString("device_address",bundle
                .getString("device_address"));
        Fragment Home_Fragment=new HomeFragment();
        Home_Fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Home_Fragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.menu:
                    openDrawer();
                    break;
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    Bundle bundle = getIntent().getExtras();
                    Bundle arg=new Bundle();
                    arg.putString("device_address",bundle
                            .getString("device_address"));
                    selectedFragment.setArguments(arg);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, selectedFragment)
                            .commit();
                    break;
                case R.id.schedule:
                    selectedFragment = new ScheduleFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, selectedFragment)
                            .commit();
                    break;
                case R.id.security:
                    selectedFragment = new SecurityFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, selectedFragment)
                            .commit();
                    break;
            }
            // It will help to replace the
            // one fragment to other
            return true;
        }

    };

    void openDrawer(){
        DrawerLayout navDrawer = findViewById(R.id.Home_drawer);
        if(!navDrawer.isDrawerOpen(GravityCompat.START))
            navDrawer.openDrawer(GravityCompat.START);
        else
            navDrawer.closeDrawer(GravityCompat.END);
    }
    @Override
    public void onButtonClicked(String text) { //save this string as a task
        scheduledTasks.add(new ScheduledTask(dateSelected,text));
        List<ScheduledTask> ls = showTasksForToday(dateSelected, scheduledTasks); //gets tasks only for that date
        ScheduledTaskAdapter adapter = new ScheduledTaskAdapter(ls,this);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public List<ScheduledTask> showTasksForToday(String date, List<ScheduledTask> ls){
        List<ScheduledTask> ls2 = new ArrayList<>();
        for(ScheduledTask i:ls){
            if(i.getDate().equalsIgnoreCase(date)){
                ls2.add(i);
            }
        }
        return ls2;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume Function is called");
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

}