package com.example.kunfypproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.kunfypproject.R;


public class SecurityCamera extends AppCompatActivity {
    Button start;
    EditText URL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_camera);
    }
}