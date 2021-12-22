package com.example.kunfypproject.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kunfypproject.Activities.Home;
import com.example.kunfypproject.Models.Appliance;
import com.example.kunfypproject.Adapters.home_events_adapter;
import com.example.kunfypproject.Adapters.home_appliance_adapter;
import com.example.kunfypproject.Models.NewAppliance;
import com.example.kunfypproject.R;
import com.example.kunfypproject.Models.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {
    private RecyclerView rcv1,rcv2;
    List<Schedule> ls;
    List<Appliance> ls1;
    private home_events_adapter scheduledEventsadapter;
    private home_appliance_adapter appliancecontroladapter;

    ImageView connection;
    Handler bluetoothIn;
    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread4;
    private ConnectedThread mConnectedThread1;
    private ConnectedThread mConnectedThread2;
    private ConnectedThread mConnectedThread3;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    ImageView btnSpeak;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v= inflater.inflate(R.layout.fragment_home, container, false);
       /*
       FirebaseDatabase.getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                .getReference().child("led").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str=dataSnapshot.getValue(String.class);
                System.out.println(str);
                System.out.println("-------------------------------");
                if(str.matches("ON")){
                    mConnectedThread3.write("<Light_ON>");
                    System.out.println("ON");
                }
                else {
                    mConnectedThread3.write("<Light_OFF>");
                    System.out.println("OFF");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        btnSpeak=v.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK");
                //if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    //((Home)getActivity()).startActivityForResult(intent, 10);
                try {
                    ((Home)getActivity()).getSpeechInput(mConnectedThread1);
                    b();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("--------------ui----------------");
                    System.out.println(mConnectedThread1);

                //}
                //else{
                    //Toast.makeText(this, "Feature not supported in your device!", Toast.LENGTH_SHORT).show();
                //}
            }
        });
        rcv1 = v.findViewById(R.id.h_rcv1);
        rcv1.setHasFixedSize(true);

        rcv2 = v.findViewById(R.id.h_rcv2);
        rcv2.setHasFixedSize(true);

        connection=v.findViewById(R.id.connection);
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), NewAppliance.class);
                getActivity().startActivity(intent);
            }
        });

        ls= new ArrayList<>();

        ls1 = new ArrayList<>();
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/fan"));
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/door"));
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/bulb"));
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/window"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcv1.setLayoutManager(layoutManager);
        rcv1.setItemAnimator(new DefaultItemAnimator());
        scheduledEventsadapter = new home_events_adapter(getActivity(),ls);
        rcv1.setAdapter(scheduledEventsadapter);

        return  v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
    }



    @Override
    public void onResume(){
        System.out.println("-----------------Resume-------------------");
        super.onResume();



        Bundle arg=getArguments();
        //Get the MAC address from the DeviceListActivty via EXTRA
        address = arg.getString("device_address");
        System.out.println("----------------asdd------------------");
        System.out.println(address);
        //System.out.println(address);
        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getActivity().getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
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
        mConnectedThread1 = new ConnectedThread(btSocket);
        mConnectedThread1.start();
        mConnectedThread2 = new ConnectedThread(btSocket);
        mConnectedThread2.start();
        mConnectedThread3 = new ConnectedThread(btSocket);
        mConnectedThread3.start();
        mConnectedThread4 = new ConnectedThread(btSocket);
        mConnectedThread4.start();
        System.out.println("-------------------+--------------------");
        System.out.println(mConnectedThread1);
        /*
        FirebaseDatabase.getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                .getReference().child("led").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str=dataSnapshot.getValue(String.class);
                System.out.println(str);
                System.out.println("-------------------------------");
                if(str.matches("ON")){
                    mConnectedThread3.write("<Light_ON>");
                    System.out.println("ON");
                }
                else {
                    mConnectedThread3.write("<Light_OFF>");
                    System.out.println("OFF");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcv2.setLayoutManager(layoutManager1);
        rcv2.setItemAnimator(new DefaultItemAnimator());
        appliancecontroladapter = new home_appliance_adapter(getActivity(),ls1,mConnectedThread1,mConnectedThread2
                ,mConnectedThread3,mConnectedThread4);
        rcv2.setAdapter(appliancecontroladapter);
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }
    void a(){
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getActivity().getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
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
        mConnectedThread1 = new ConnectedThread(btSocket);
        mConnectedThread1.start();
        mConnectedThread2 = new ConnectedThread(btSocket);
        mConnectedThread2.start();
        mConnectedThread3 = new ConnectedThread(btSocket);
        mConnectedThread3.start();
        mConnectedThread4 = new ConnectedThread(btSocket);
        mConnectedThread4.start();
        System.out.println("-------------------+--------------------");
        System.out.println(mConnectedThread1);
        /*
        FirebaseDatabase.getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                .getReference().child("led").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String str=dataSnapshot.getValue(String.class);
                System.out.println(str);
                System.out.println("-------------------------------");
                if(str.matches("ON")){
                    mConnectedThread3.write("<Light_ON>");
                    System.out.println("ON");
                }
                else {
                    mConnectedThread3.write("<Light_OFF>");
                    System.out.println("OFF");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcv2.setLayoutManager(layoutManager1);
        rcv2.setItemAnimator(new DefaultItemAnimator());
        appliancecontroladapter = new home_appliance_adapter(getActivity(),ls1,mConnectedThread1,mConnectedThread2
                ,mConnectedThread3,mConnectedThread4);
        rcv2.setAdapter(appliancecontroladapter);
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
    }
    void b(){
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }
@Override
public void onPause(){
    super.onPause();
    try
    {
        //Don't leave Bluetooth sockets open when leaving activity
        btSocket.close();
    } catch (IOException e2) {
        //insert code to deal with this
    }
}
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public void onDetach(){
        super.onDetach();
    }


    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getActivity().getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
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
                    //bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {System.out.println("write function called -------");
                System.out.println(mmOutStream);
                mmOutStream.write(msgBuffer);
                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                //Toast.makeText(getActivity().getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
             //  System.out.println(mmOutStream);
                // getActivity().finish();

            }
        }
    }

}