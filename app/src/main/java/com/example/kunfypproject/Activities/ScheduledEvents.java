package com.example.kunfypproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunfypproject.Adapters.RvAdapter;
import com.example.kunfypproject.Models.CalendarEvent;
import com.example.kunfypproject.Models.Schedule;
import com.example.kunfypproject.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.internal.cache.DiskLruCache;

public class ScheduledEvents extends AppCompatActivity {
    ImageButton previous;
    ImageButton next;
    TextView month;
    Button newEvent;

    Date currentDate;

    RecyclerView rv;
    RvAdapter adapter;

    List<CalendarEvent> ls;
    List<CalendarEvent> ls2;
    boolean flag_i=false;
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat(" MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_events);

        rv = findViewById(R.id.rv);
        previous = (ImageButton) findViewById(R.id.previous);
        next = (ImageButton) findViewById(R.id.next);
        month = findViewById(R.id.month);
        newEvent = findViewById(R.id.new_event);


        Calendar cal = Calendar.getInstance();
        month.setText(dateFormatMonth.format(cal.getTime()));

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true);

        ls = new ArrayList<>();







       /* Calendar calen = new GregorianCalendar(2021, Calendar.OCTOBER, 4, 5, 30);
        calen.set( Calendar.AM_PM, Calendar.PM);
        ls.add(new CalendarEvent("Event1", "This Event1", calen.getTime(), colorCode));
        AddEventToCalendar(calen, colorCode);


        colorCode = randomColorCode();
        calen = new GregorianCalendar(2021, Calendar.NOVEMBER, 8,8, 0);
        calen.set( Calendar.AM_PM, Calendar.AM);
        ls.add(new CalendarEvent("Event3", "This Event3", calen.getTime(), colorCode));
        AddEventToCalendar(calen, colorCode);
*/
        ls2 = new ArrayList<CalendarEvent>();

        currentDate = Calendar.getInstance().getTime();

        ShowOnlySelectedDayEvents();

        compactCalendar.shouldScrollMonth(false);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();

                currentDate = dateClicked;

                ShowOnlySelectedDayEvents();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
                month.setText(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                compactCalendar.scrollLeft();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compactCalendar.scrollRight();
            }
        });

        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScheduledEvents.this,CreateEvent.class);
                startActivityForResult(i,23);
            }
        });
    }

    public void openActivityForResult(){
        Intent i = new Intent(ScheduledEvents.this,CreateEvent.class);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== 23) {
            // There are no request codes
            //Intent data = result.getData();

            String name = data.getStringExtra("Name");
            String description = data.getStringExtra("Description");
            String AMPM = data.getStringExtra("AMPM");
            int hr = data.getIntExtra("Hours",0);
            int mint = data.getIntExtra("Minutes",0);
            int year = data.getIntExtra("Year",0);
            int month = data.getIntExtra("Month",0);
            int day = data.getIntExtra("Day",0);


            //add to event list
            Calendar calen = new GregorianCalendar(year, month, day, hr, mint);
            if(AMPM.equals("AM"))
                calen.set( Calendar.AM_PM, Calendar.AM);
            else
                calen.set( Calendar.AM_PM, Calendar.PM);

            int cCode = randomColorCode();
            ls.add(new CalendarEvent(name, description, calen.getTime(), cCode));
            adapter.notifyDataSetChanged();

            AddEventToCalendar(calen, cCode);
        }


    }




    public void AddEventToCalendar(Calendar calen, int colorCode){

        //convert date to milliseconds
        long millis = calen.getTimeInMillis();
        Log.d("Milli", String.valueOf(millis));

        //add an event showing dot on calendar
        Event ev1 = new Event(colorCode, millis, "Eventt");
        compactCalendar.addEvent(ev1);
    }

    public int randomColorCode(){
        //add colors to an array
        List<Integer> colorList = Arrays.asList(Color.RED,Color.BLUE, Color.GREEN,Color.MAGENTA, Color.YELLOW, Color.BLACK);
        Random rand = new Random();
        int index = rand.nextInt(6);
        return colorList.get(index);
    }

    private void ShowOnlySelectedDayEvents() {
        ls2 = new ArrayList<CalendarEvent>();

        ///////////////FIREBASE CODE////////////////////////
        FirebaseDatabase.getInstance().getReference("schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot datasnapshot) {

                adapter=new RvAdapter(ls2,ScheduledEvents.this);
                ls.clear();
                ls2.clear();

                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    int colorCode = randomColorCode();
                    Schedule i=snapshot.getValue(Schedule.class);
                    String year = i.getDate();
                    year = year.substring(year.length()-4);
                    String month = i.getDate();
                    month = month.substring(11,13);
                    String day = i.getDate();
                    day = day.substring(8,10);
                    String hour = i.getDate();
                    hour = hour.substring(0,2);
                    String minute = i.getDate();
                    minute = minute.substring(3,5);
                    String AMPM = i.getDate().substring(5,7);

                    Calendar calen = new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day),Integer.valueOf(hour), Integer.valueOf(minute));
                    if(AMPM.equals("AM"))
                        calen.set( Calendar.AM_PM, Calendar.AM);
                    else
                        calen.set( Calendar.AM_PM, Calendar.PM);

                    System.out.println("iioiioi");
                    ls.add(new CalendarEvent(i.getName(), i.getStatus(), calen.getTime(), colorCode));
                    //adapter.notifyDataSetChanged();
                    if(flag_i==false) {
                        AddEventToCalendar(calen, colorCode);
                    }

                }
                flag_i=true;
                f();
                adapter = new RvAdapter(ls2, ScheduledEvents.this);
                RecyclerView.LayoutManager lm = new LinearLayoutManager(ScheduledEvents.this);
                rv.setLayoutManager(lm);
                rv.setAdapter(adapter);

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
        ///////////////FIREBASE CODE////////////////////////





    }
    void f(){

//        have only current day events in list
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
        SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy");


        for(int i=0;i<ls.size();i++){
            Calendar cal2 = new GregorianCalendar();
            cal2.setTime(currentDate);
            int date = Integer.parseInt(formatter.format(cal2.getTime()));
            int month = Integer.parseInt(formatter2.format(cal2.getTime()));
            int year = Integer.parseInt(formatter3.format(cal2.getTime()));

            Calendar cal3 = new GregorianCalendar();
            cal3.setTime(ls.get(i).getTimeAndDate());

            int date2 = Integer.parseInt(formatter.format(cal3.getTime()));
            int month2 = Integer.parseInt(formatter2.format(cal3.getTime()));
            int year2 = Integer.parseInt(formatter3.format(cal3.getTime()));
            System.out.println(currentDate);
            System.out.println(ls.get(i).getTimeAndDate());
            System.out.println(date);
            System.out.println(month);
            System.out.println(year);
            System.out.println(date2);
            System.out.println(month2);
            System.out.println(year2);

            if(date == date2){
                if(month == month2){
                    if(year == year2){
                        ls2.add(ls.get(i));
                        Toast.makeText(this, "Working", Toast.LENGTH_LONG);
                    }
                    else
                        Log.d("Year",String.valueOf(year+" - "+year2));
                }
                else
                    Log.d("Month",String.valueOf(month+" - "+month2));
            }
            else
                Log.d("Date",String.valueOf(date+" - "+date2));
        }



    }
}