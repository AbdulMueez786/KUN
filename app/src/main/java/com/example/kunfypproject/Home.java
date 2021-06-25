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
/*
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume Function is called");
     /*   //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();
        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        System.out.println("oooooooooooooooooooooooooooooooo");
        System.out.println(mConnectedThread);
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");*/
    }

    @Override
    public void onPause()
    {
        super.onPause();
 /*       try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
        */
    }
/*
    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



    //create new class for connect thread
    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
    */
}