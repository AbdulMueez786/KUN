package com.example.kunfypproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewAppliance extends AppCompatActivity {
    EditText name, port;
    Spinner type;
    Button btn;

    List<String> appliances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appliance);

        name = findViewById(R.id.new_appliance_name);
        //port = findViewById(R.id.new_appliance_port);
        type = findViewById(R.id.spinner_appliance_type);
        btn = findViewById(R.id.add_appliance);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        appliances = Arrays.asList("Fan","Light","Door","Window");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, appliances);
        type.setAdapter(adapter1);
    }
}