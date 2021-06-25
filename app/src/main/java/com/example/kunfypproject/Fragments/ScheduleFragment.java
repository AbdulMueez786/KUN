package com.example.kunfypproject.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.example.kunfypproject.CalendarActivity;
import com.example.kunfypproject.Home;
import com.example.kunfypproject.R;
import com.example.kunfypproject.ScheduledTask;
import com.example.kunfypproject.ScheduledTaskAdapter;
import com.example.kunfypproject.TaskBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;


public class ScheduleFragment extends Fragment  {

    RecyclerView rv;
    List<ScheduledTask> scheduledTasks;
    private String dateSelected="";
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       v=inflater.inflate(R.layout.fragment_schedule, container, false);

        // Inflate the layout for this fragment
        Button addTask = v.findViewById(R.id.add_new_task);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 TaskBottomSheetDialog bottomSheet = new TaskBottomSheetDialog();
                 bottomSheet.show(getActivity().getSupportFragmentManager(),"taskBottomSheet");
            }
        });

        rv = v.findViewById(R.id.scehduledtask_rv);
        scheduledTasks = new ArrayList<>();
        scheduledTasks.add(new ScheduledTask("12/4/2021","Turn on light at 7:30"));
        scheduledTasks.add(new ScheduledTask("12/4/2021","Turn off light at 8:30"));
        scheduledTasks.add(new ScheduledTask("13/4/2021","Turn on fan at 1:30"));
        scheduledTasks.add(new ScheduledTask("18/4/2021","Close door at 10:30"));

        CalendarView cv = v.findViewById(R.id.calendarView);


        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                i1+=1;
                String dateSelected = i2+"/"+i1+"/"+i;
                List<ScheduledTask> ls = showTasksForToday(dateSelected, scheduledTasks); //gets tasks only for that date

                ScheduledTaskAdapter adapter = new ScheduledTaskAdapter(ls, getActivity());
                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(lm);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        });
        v.findViewById(R.id.add_new_task);

        return v;
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
}