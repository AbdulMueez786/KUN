package com.example.kunfypproject.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kunfypproject.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

public class TaskBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private int selected_item1, selected_item2, selected_item3;
    List<String> applianceList,commandList,timeList;
    String task;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        Button save = view.findViewById(R.id.save_task);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //task = create_task(applianceList.get(selected_item1),
                 //       commandList.get(selected_item2),
                 //       timeList.get(selected_item3));
                //Log.d("ye tag", task);
                //mListener.onButtonClicked(task);
                Log.d("yo", "came here ");
                dismiss();
            }
        });

        Spinner spinner1 = view.findViewById(R.id.spinner1);
        Spinner spinner2 = view.findViewById(R.id.spinner2);
        Spinner spinner3 = view.findViewById(R.id.spinner3);

        applianceList = Arrays.asList("Fan","Light","Door");
        commandList = Arrays.asList("on","off");
        timeList = Arrays.asList("1:00","2:00","3:00","4:00","5:00","6:00","7:00");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item, applianceList);
        spinner1.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item, commandList);
        spinner2.setAdapter(adapter2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item, timeList);
        spinner3.setAdapter(adapter3);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selected_item1 = position; //this is your selected appliance

            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selected_item2 = position; //this is your selected command

            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selected_item3 = position; //this is your selected time

            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement BottomSheetListener");
        }

    }

    public String create_task(String appliance, String command, String time){
        String task = "turn "+command+" "+appliance+" at "+time;
        return task;
    }
}
