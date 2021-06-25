package com.example.kunfypproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements TaskBottomSheetDialog.BottomSheetListener {
    RecyclerView rv;
    List<ScheduledTask> scheduledTasks;
    private String dateSelected="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);



        Button addTask = findViewById(R.id.add_new_task);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // TaskBottomSheetDialog bottomSheet = new TaskBottomSheetDialog();
                //bottomSheet.show(getSupportFragmentManager(),"taskBottomSheet");
            }
        });

        rv = findViewById(R.id.scehduledtask_rv);
        scheduledTasks = new ArrayList<>();
        scheduledTasks.add(new ScheduledTask("12/4/2021","Turn on light at 7:30"));
        scheduledTasks.add(new ScheduledTask("12/4/2021","Turn off light at 8:30"));
        scheduledTasks.add(new ScheduledTask("13/4/2021","Turn on fan at 1:30"));
        scheduledTasks.add(new ScheduledTask("18/4/2021","Close door at 10:30"));

        CalendarView cv = findViewById(R.id.calendarView);


        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                i1+=1;
                String dateSelected = i2+"/"+i1+"/"+i;
                List<ScheduledTask> ls = showTasksForToday(dateSelected, scheduledTasks); //gets tasks only for that date

                ScheduledTaskAdapter adapter = new ScheduledTaskAdapter(ls,CalendarActivity.this);
                RecyclerView.LayoutManager lm = new LinearLayoutManager(CalendarActivity.this);
                rv.setLayoutManager(lm);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        });

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
    public void onButtonClicked(String text) { //save this string as a task
        scheduledTasks.add(new ScheduledTask(dateSelected,text));
        List<ScheduledTask> ls = showTasksForToday(dateSelected, scheduledTasks); //gets tasks only for that date

        ScheduledTaskAdapter adapter = new ScheduledTaskAdapter(ls,CalendarActivity.this);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(CalendarActivity.this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}