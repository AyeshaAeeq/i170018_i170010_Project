package com.namrahrasool.i170018_i170010_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateScheduleActivity extends AppCompatActivity {
    ImageView back,save,logout;
    Spinner courses,sections,day,month,year,hour,min,am;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userid,selectedCourse,selectedSection,selectedDate,selectedTime;
    List<String> courseList,sectionList;
    Intent intentt;
    PendingIntent pendingIntent;
    AlarmManager amm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        mAuth = FirebaseAuth.getInstance();
        courses=(Spinner)findViewById(R.id.courses);
        sections=(Spinner)findViewById(R.id.sections);
        courseList= new ArrayList<String>();
        database= FirebaseDatabase.getInstance();
        firebaseUser= mAuth.getCurrentUser();
        userid=firebaseUser.getUid();
        sections=(Spinner)findViewById(R.id.sections);
        sectionList= new ArrayList<String>();
        selectedCourse="no";
        amm = (AlarmManager) CreateScheduleActivity.this.getSystemService(Context.ALARM_SERVICE);

        database.getReference("courses").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    Course c=areaSnapshot.getValue(Course.class);
                    courseList.add(c.getName());
                }

                ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(CreateScheduleActivity.this, android.R.layout.simple_spinner_item, courseList);
                courseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                courses.setAdapter(courseAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        courses.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse=courseList.get(i);
                Toast.makeText(getApplicationContext(),"Course: "+selectedCourse,Toast.LENGTH_LONG).show();
                sectionList= new ArrayList<String>();
                database.getReference("sections").child(userid).child(selectedCourse).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            Section s = areaSnapshot.getValue(Section.class);
                            sectionList.add(s.getName());
                        }

                        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<String>(CreateScheduleActivity.this, android.R.layout.simple_spinner_item, sectionList);
                        sectionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);//android.R.layout.simple_spinner_dropdown_item);
                        sections.setAdapter(sectionAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }));

        day=(Spinner)findViewById(R.id.day);
        String[] dayList = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        ArrayAdapter<String> dayAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,dayList);
        dayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        day.setAdapter(dayAdapter);
        month=(Spinner)findViewById(R.id.month);
        String[] monthList = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
        ArrayAdapter<String> monthAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,monthList);
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        month.setAdapter(monthAdapter);
        year=(Spinner)findViewById(R.id.year);
        String[] yearList = new String[]{"2019","2020","2021"};
        ArrayAdapter<String> yearAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,yearList);
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        year.setAdapter(yearAdapter);

        hour=(Spinner)findViewById(R.id.hour);
        String[] hourList = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};
        ArrayAdapter<String> hourAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,hourList);
        hourAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        hour.setAdapter(hourAdapter);

        min=(Spinner)findViewById(R.id.min);
        String[] minList = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
        ArrayAdapter<String> minAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,minList);
        minAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        min.setAdapter(minAdapter);

        am=(Spinner)findViewById(R.id.am);
        String[] amList = new String[]{"am","pm"};
        ArrayAdapter<String> amAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,amList);
        amAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        am.setAdapter(amAdapter);

        back=findViewById(R.id.back);
        save=findViewById(R.id.save);
        logout=findViewById(R.id.logout);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSection=sections.getSelectedItem().toString();
                selectedDate=day.getSelectedItem().toString()+"-"+month.getSelectedItem().toString()+"-"+year.getSelectedItem().toString();//- separator
                selectedTime=hour.getSelectedItem().toString()+" "+min.getSelectedItem().toString()+" "+am.getSelectedItem().toString();//space separator

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("course", selectedCourse);
                hashMap.put("section", selectedSection);
                hashMap.put("day", selectedDate);
                hashMap.put("time", selectedTime);
                firebaseUser= mAuth.getCurrentUser();
                userid=firebaseUser.getUid();
                reference= FirebaseDatabase.getInstance().getReference("schedule").child(userid).child(selectedCourse+"_"+selectedSection+"_"+selectedDate+"_"+selectedTime);//_ separator
                reference.keepSynced(true);
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CreateScheduleActivity.this, "Schedule saved!!!", Toast.LENGTH_LONG).show();
                            ////////////////////
                            Intent calIntent = new Intent(Intent.ACTION_INSERT);
                            calIntent.setType("vnd.android.cursor.item/event");
                            calIntent.putExtra(CalendarContract.Events.TITLE, "Class Schedule");
                            //calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Beach House");
                            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "You have a "+selectedCourse+" class of section "+selectedSection+" on "+selectedDate+" at "+selectedTime+".");
                            GregorianCalendar calDate = new GregorianCalendar(Integer.parseInt(year.getSelectedItem().toString()), Integer.parseInt(month.getSelectedItem().toString())-1, Integer.parseInt(day.getSelectedItem().toString()));
                            calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                            int sec=0;
                            if(am.getSelectedItem().toString().equals("am")) {
                                sec = ((Integer.parseInt(hour.getSelectedItem().toString()) * 60) + Integer.parseInt(min.getSelectedItem().toString())) * 60;
                            }
                            else{
                                sec = (((Integer.parseInt(hour.getSelectedItem().toString())+12) * 60) + Integer.parseInt(min.getSelectedItem().toString())) * 60;

                            }
                            sec=sec*1000;
                            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                    calDate.getTimeInMillis()+sec);
                            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                    calDate.getTimeInMillis()+sec);
                            //calIntent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=10;WKST=SU");//BYDAY=TU,TH");
                            startActivity(calIntent);
                            //////////////////
                        }else{
                            Toast.makeText(CreateScheduleActivity.this,"Schedule not saved!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                Toast.makeText(getApplicationContext(), "Schedule created successfully!"+ String.valueOf(courses.getSelectedItem()) + String.valueOf(sections.getSelectedItem())+String.valueOf(day.getSelectedItem())+String.valueOf(month.getSelectedItem())+String.valueOf(year.getSelectedItem())+String.valueOf(hour.getSelectedItem())+String.valueOf(min.getSelectedItem())+String.valueOf(am.getSelectedItem()),
                        Toast.LENGTH_LONG).show();

                try{
                    sendNotification("You have a "+selectedCourse+" class of section "+selectedSection+" on "+selectedDate+" at "+selectedTime+".","Class Schedule");
                }catch(JSONException e){
                    e.printStackTrace();
                }
                Intent intent1=new Intent(CreateScheduleActivity.this,CoursesActivity.class);
                startActivity(intent1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreateScheduleActivity.this,CoursesActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(CreateScheduleActivity.this);
                builder.setMessage("Do you want to Logout ?");
                builder.setTitle("Logout !");
                builder.setCancelable(false);
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent=new Intent(CreateScheduleActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                builder
                        .setNegativeButton(
                                "No",
                                new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    void sendNotification(String message,String name) throws JSONException {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String playerId=status.getSubscriptionStatus().getUserId();
        OneSignal.postNotification(new JSONObject("{'contents':{'en':'" +
                        message +
                        "'},'headings':{'en':'"+
                        name+
                        "'},'include_player_ids': ['" +
                        playerId+
                        "'], 'data':{'date':'"+selectedDate+"','time':'"+selectedTime+"'}}"),
                new OneSignal.PostNotificationResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d("response",response.toString());
                        Toast.makeText(CreateScheduleActivity.this,
                                "Notification Sent",
                                Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(JSONObject response) {
                        Log.d("response",response.toString());
                    }
                });
    }
}