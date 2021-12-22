package com.example.kunfypproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kunfypproject.Adapters.home_appliance_adapter;
import com.example.kunfypproject.Fragments.HomeFragment;
import com.example.kunfypproject.Fragments.ScheduleFragment;
import com.example.kunfypproject.Fragments.SecurityFragment;
import com.example.kunfypproject.R;
import com.example.kunfypproject.Models.ScheduledTask;
import com.example.kunfypproject.Adapters.scheduledEvent_adapter;
import com.example.kunfypproject.Fragments.TaskBottomSheetDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Home extends AppCompatActivity implements TaskBottomSheetDialog.BottomSheetListener {

    List<ScheduledTask> scheduledTasks;
    private String dateSelected="";
    RecyclerView rv;
    String path;
    Set<String> intents;
    List<String> intentList;
    List<String> sentenceList;
    private  Fragment selectedFragment;
    public String command="";
    boolean f=false;
    Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread1;
    private ConnectedThread mConnectedThread2;
    private ConnectedThread mConnectedThread3;
    private ConnectedThread mConnectedThread4;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        System.out.println("----------------Firebase ------------------");
    /*    FirebaseDatabase
                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/").getReference()
                .child("Light").setValue("OK").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Failure");
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                System.out.println("On complete");
            }
        });*/
        System.out.println("----------------Firebase------------------");
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        Bundle arg=new Bundle();
        address=getIntent().getStringExtra("device_address");
        arg.putString("device_address",bundle
                .getString("device_address"));
        System.out.println("----------------asdd------------------");
        System.out.println(address);
        Fragment Home_Fragment=new HomeFragment();
        Home_Fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, Home_Fragment).commit();
        intentList = new ArrayList<String>();
        sentenceList = new ArrayList<String>();
        selectedFragment=null;
        path = this.getApplicationInfo().dataDir + File.separatorChar + "intent_dataset2.csv";
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            selectedFragment = null;
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
        scheduledEvent_adapter adapter = new scheduledEvent_adapter(ls,this);
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

    public void getSpeechInput(HomeFragment.ConnectedThread mConnectedThread1) throws InterruptedException {
       // this.mConnectedThread1=mConnectedThread1;
        System.out.println("955555555555555555555555   IN     55555555555555555555555555555");
        System.out.println("--------------get----------------");
        System.out.println(mConnectedThread1);
        //Speech to text part
        //this.mConnectedThread1.write("<Light_ON>");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 10);

        }
        else{
            Toast.makeText(this, "Feature not supported in your device!", Toast.LENGTH_SHORT).show();
        }
        //while(f==false){
        //    Thread.sleep(250);
        //    System.out.println("hhhhh");
       // }
        System.out.println("955555555555555555555555   out     55555555555555555555555555555");
        System.out.println("955555555555555555555555555555555555555555555555555555555555555555");
        System.out.println(this.command);

    }

    public void light_on(){

    }

    public  String getCommand(){
        return this.command;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    s();
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    readFile(path); //read intent_dataset.csv file
                    String intentText = performNaiveBayes(result.get(0).toString());
                    System.out.println("--------------off----------------");
                    //System.out.println(mConnectedThread1);


                    this.command=intentText;
                    f=true;
                    System.out.println(this.command);
                    if(intentText.matches("turn on light")) {
                        //light_on();
                        mConnectedThread1.write("<Light_ON>");
                        System.out.println("Light On");
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("ON");
                        mConnectedThread.write("<Light_ON>");*/
                    }
                    else if(intentText.matches("turn off light")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/
                        System.out.println("Light OFF");
                        mConnectedThread1.write("<Light_OFF>");
                    }
                    else if(intentText.matches("turn on fan")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/
                        System.out.println("Light OFF");
                        mConnectedThread1.write("<Fan_ON>");
                    }
                    else if(intentText.matches("turn off fan")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/
                        System.out.println("Light OFF");
                        mConnectedThread1.write("<Fan_OFF>");
                    }
                    else if(intentText.matches("open door")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/
                        System.out.println("Door open");
                        mConnectedThread2.write("<Door_Open>");
                    }
                    else if(intentText.matches("close door")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/
                        System.out.println("Door Close");
                        mConnectedThread2.write("<Door_Close>");
                    }
                    else if(intentText.matches("open window")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/
                        System.out.println("Open Window");
                        mConnectedThread3.write("<Window_Open>");

                    }
                    else if(intentText.matches("close window")){
                        /*FirebaseDatabase
                                .getInstance("https://kunfypproject-default-rtdb.firebaseio.com/")
                                .getReference().child("led").setValue("OFF");*/    mConnectedThread4.write("<Window_Open>");   mConnectedThread1.write("<Light_OFF>");
                        System.out.println("Close Window");
                        mConnectedThread3.write("<Window_Close>");
                    }

                    p();
                }
        }
    }

    /////////////////NAIVE BAYES CODE STARTS HERE/////////////////
    public void readFile(String path) {
        boolean first = true;
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.intent_dataset2);

            BufferedReader reader = new BufferedReader(new InputStreamReader(in_s));
            String line = reader.readLine();
            while (line != null) {

                line = reader.readLine();
                String[] spl = line.split(",");
                intentList.add(spl[1]);
                sentenceList.add(spl[0]);
            }

        } catch (Exception e) {
            System.out.println("Cant read this");
        }
    }

    public Dictionary makeDictionary(){
        Dictionary wordCount = new Hashtable();
        List<String> wordList = new ArrayList<String>();

        //get list of all words in dataset
        for(int x=0;x<sentenceList.size();x++){
            String[] words = sentenceList.get(x).split(" ");
            for(int y=0;y<words.length;y++){
                wordList.add(words[y]);
            }
        }

        //store unique words in a set
        Set<String> uniqueWords = new HashSet<String>(wordList);
        System.out.println(uniqueWords);

        //store count of unique words in dictionary
        String[] uniqueWords2 = uniqueWords.toArray(new String[uniqueWords.size()]);
        int c = 0;
        for(int x=0;x<uniqueWords.size();x++){
            c = 0;
            for(int y = 0;y < wordList.size();y++){
                if(uniqueWords2[x].equals(wordList.get(y))){
                    c++;
                }
            }
            wordCount.put(uniqueWords2[x],c);
        }

        return wordCount;
    }

    public float calculateProbability(String s, String i){
        float val = 0;

        boolean found = false; //if found in current sentence
        float c = 0; //total count
        float ic = 0; //intent count

        for(int x=0;x<sentenceList.size();x++){
            String[] sentenceChunks = sentenceList.get(x).split(" ");
            for(int y = 0;y<sentenceChunks.length;y++){
                if(s.equals(sentenceChunks[y])){
                    c++;
                    found = true;
                    break;
                }
            }
            if(found && i.equals(intentList.get(x))){
                ic++;
                found = false;
            }
        }

        //calculate probability that word s appears in intent i
        val = ic/c;

        return val;
    }

    public String getIntent(Dictionary wordCount, String sentence){
        String intt = "";
        float[] probabilities = new float[intents.size()];

        String[] words = sentence.split(" ");
        String[] intentArr = intents.toArray(new String[intents.size()]);

        for(int x=0;x<intents.size();x++){
            float total = 1;
            for(int y=0;y<words.length;y++){
                total = total * calculateProbability(words[y], intentArr[x]);
            }
            probabilities[x] = total;
        }

        //traverse intent probabilities and find max value
        int maxIndex = 0;
        float maxValue = probabilities[0];
        for(int x=0;x<intentArr.length;x++){
            if(probabilities[x]>maxValue){
                maxIndex = x;
                maxValue = probabilities[x];
            }
        }

        intt = intentArr[maxIndex];

        return intt;
    }

    public String performNaiveBayes(String sentence){
        intents = new HashSet<String>(intentList);
        Dictionary wordCount = makeDictionary();
        String finalIntent = getIntent(wordCount, sentence);
        return finalIntent;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    /*
    @Override
    public void onResume()
    {

        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceList.EXTRA_DEVICE_ADDRESS);

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
    }
    */
    void s(){

        //Get MAC address from DeviceListActivity via intent


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
        mConnectedThread1 = new ConnectedThread(btSocket);
        mConnectedThread1.start();
        mConnectedThread2 = new ConnectedThread(btSocket);
        mConnectedThread2.start();
        mConnectedThread3 = new ConnectedThread(btSocket);
        mConnectedThread3.start();
        mConnectedThread4 = new ConnectedThread(btSocket);
        mConnectedThread4.start();

    }
    void p(){
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }/*
    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }
*/
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
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("Connected Thread - constructore");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            //Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_LONG).show();
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("Connected Thread - run");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
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
                    //Toast.makeText(getBaseContext(), "Runstat - exception", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
        //write method
        public void write(String input) {

            System.out.println("Connected Thread - write");

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

}