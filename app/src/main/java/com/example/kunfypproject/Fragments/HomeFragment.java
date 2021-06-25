package com.example.kunfypproject.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kunfypproject.Appliance;
import com.example.kunfypproject.MyRVAdapter;
import com.example.kunfypproject.MyRVAdapter1;
import com.example.kunfypproject.NewAppliance;
import com.example.kunfypproject.R;
import com.example.kunfypproject.Schedule;

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
    private MyRVAdapter myRVAdapter;
    private MyRVAdapter1 myRVAdapter1;
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


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Link the buttons and textViews to respective views


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {										//if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
                    Log.d("dataInPrint",recDataString.toString());
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        //Log.d("dataInPrint",dataInPrint);
                        int dataLength = dataInPrint.length();

                        if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                        {
                            String data = recDataString.substring(1, endOfLineIndex);

                            Log.d("dataInPrint",data);
                            Log.d("dataInPrintX",data.substring(2,data.length()));
                            Log.d("dataInPrintXX",data.substring(0,1));
                            //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                            if(data.substring(0,1).equalsIgnoreCase("s"))
                            {

                                //stats.setText("Motor Status: Stopped");
                            }
                            else if(data.substring(0,1).equalsIgnoreCase("r"))
                            {
                                //stats.setText("Motor Status: Running");
                            }
                            //moister.setText("Moister="+data.substring(2,data.length()));// deleted sendoing data
                        }
                        recDataString.delete(0, recDataString.length());
                    }

                    //recDataString.delete(0, recDataString.length());
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v= inflater.inflate(R.layout.fragment_home, container, false);


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
        ls.add(new Schedule("Room's Fan on at 7:00 pm on Tuesday","android.resource://com.example.kunfypproject/drawable/fan"));
        ls.add(new Schedule("Room's Door close at 8:20 pm on Thursday","android.resource://com.example.kunfypproject/drawable/door"));
        ls.add(new Schedule("Room's window close at 9:00 pm on Friday","android.resource://com.example.kunfypproject/drawable/window"));
        ls.add(new Schedule("Room's Bulb off at 11:00 pm on Friday","android.resource://com.example.kunfypproject/drawable/bulb"));

        ls1 = new ArrayList<>();
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/fan"));
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/door"));
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/bulb"));
        ls1.add(new Appliance("android.resource://com.example.kunfypproject/drawable/window"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcv1.setLayoutManager(layoutManager);
        rcv1.setItemAnimator(new DefaultItemAnimator());
        myRVAdapter = new MyRVAdapter(getActivity(),ls);
        rcv1.setAdapter(myRVAdapter);

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
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        //Intent intent = getIntent();
        Bundle arg=getArguments();
        //Get the MAC address from the DeviceListActivty via EXTRA
        address = arg.getString("device_address");

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



        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcv2.setLayoutManager(layoutManager1);
        rcv2.setItemAnimator(new DefaultItemAnimator());
        myRVAdapter1 = new MyRVAdapter1(getActivity(),ls1,mConnectedThread1,mConnectedThread2
                ,mConnectedThread3,mConnectedThread4);
        rcv2.setAdapter(myRVAdapter1);
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

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


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

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
               /// System.out.println(mmOutStream);
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