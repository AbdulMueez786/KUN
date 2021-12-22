package com.example.kunfypproject.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kunfypproject.R;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private ImageView ip_camera,security;
    private CardView light,fan,door,window,schedule;
    private ImageView mic;

    boolean flag_light=false,flag_fan=false,flag_door=false,flag_window=false;
    private String path;
    private Set<String> intents;
    private List<String> intentList;
    private List<String> sentenceList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        ip_camera=findViewById(R.id.ip_camera);
        light=findViewById(R.id.light);
        fan=findViewById(R.id.fan);
        door=findViewById(R.id.door);
        window=findViewById(R.id.window);
        schedule=findViewById(R.id.schedule);
        mic=findViewById(R.id.mic);
        security=findViewById(R.id.security);
        ip_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri= Uri.parse("http://192.168.1.9:81/stream");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,police_notify.class));
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                if(flag_light==true){
                    light.setCardBackgroundColor(Color.GRAY);
                    FirebaseDatabase.getInstance().getReference().child("LED_STATUS").setValue(1);
                    flag_light=false;
                }
                else if(flag_light==false){
                    light.setCardBackgroundColor(Color.WHITE);
                    FirebaseDatabase.getInstance().getReference().child("LED_STATUS").setValue(0);
                    flag_light=true;
                }

            }
        });

        fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag_fan==true){
                    fan.setCardBackgroundColor(Color.GRAY);
                    FirebaseDatabase.getInstance().getReference().child("FAN_STATUS").setValue(0);
                    flag_fan=false;
                }
                else if(flag_fan==false){
                    fan.setCardBackgroundColor(Color.WHITE);
                    FirebaseDatabase.getInstance().getReference().child("FAN_STATUS").setValue(1);
                    flag_fan=true;
                }
            }
        });

        door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag_door==true){
                    door.setCardBackgroundColor(Color.GRAY);
                    FirebaseDatabase.getInstance().getReference().child("DOOR_STATUS").setValue(1);
                    flag_door=false;
                }
                else if(flag_door==false){
                    door.setCardBackgroundColor(Color.WHITE);
                    FirebaseDatabase.getInstance().getReference().child("DOOR_STATUS").setValue(0);
                    flag_door=true;
                }
            }
        });

        window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag_window==true){
                    window.setCardBackgroundColor(Color.GRAY);
                    FirebaseDatabase.getInstance().getReference().child("WINDOW_STATUS").setValue(1);
                    flag_window=false;
                }
                else if(flag_window==false){
                    window.setCardBackgroundColor(Color.WHITE);
                    FirebaseDatabase.getInstance().getReference().child("WINDOW_STATUS").setValue(0);
                    flag_window=true;
                }
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ScheduledEvents.class));
            }
        });

        intentList = new ArrayList<String>();
        sentenceList = new ArrayList<String>();

        path = this.getApplicationInfo().dataDir + File.separatorChar + "intent_dataset2.csv";

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   getSpeechInput();
            }
        });

    }

    public void getSpeechInput() {

        //Speech to text part
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 10);

        }
        else{
            Toast.makeText(this, "Feature not supported in your device!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case 10:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    //Naive Bayes Model Part
                    readFile(path); //read intent_dataset.csv file
                    String intentText = performNaiveBayes(result.get(0).toString()); //do Naive Bayes

                    System.out.println(intentText);
                    System.out.println(intentText=="open door");
                    if(intentText.matches("turn on light")){
                        flag_light=false;
                        FirebaseDatabase.getInstance().getReference().child("LED_STATUS").setValue(1);
                    }
                    else if(intentText.matches("turn off light")){
                        flag_light=true;
                        FirebaseDatabase.getInstance().getReference().child("LED_STATUS").setValue(0);
                    }
                    else if(intentText.matches("turn on fan")){
                        flag_fan=false;
                        FirebaseDatabase.getInstance().getReference().child("FAN_STATUS").setValue(0);
                    }
                    else if(intentText.matches("turn off fan")){
                        flag_fan=true;
                        FirebaseDatabase.getInstance().getReference().child("FAN_STATUS").setValue(1);
                    }
                    else if(intentText.matches("open door")){
                        flag_door=false;
                        FirebaseDatabase.getInstance().getReference().child("DOOR_STATUS").setValue(1);
                    }
                    else if(intentText.matches("close door")){
                        flag_door=true;
                        FirebaseDatabase.getInstance().getReference().child("DOOR_STATUS").setValue(0);
                    }
                    else if(intentText.matches("open window")){
                        flag_window=false;
                        FirebaseDatabase.getInstance().getReference().child("WINDOW_STATUS").setValue(1);
                    }
                    else if(intentText.matches("close window")){
                        flag_window=true;
                        FirebaseDatabase.getInstance().getReference().child("WINDOW_STATUS").setValue(0);
                    }


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

}