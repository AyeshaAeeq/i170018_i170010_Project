package com.namrahrasool.i170018_i170010_project;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;


public class DateActivity extends AppCompatActivity {

    public TextView date;
    public TextView tv1;
    public Button strt;
    public Button edit;
    ImageView back;
    Intent intent;
    static String selectedDate;
    String course,section;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        intent = getIntent();
        course = intent.getStringExtra("course");
        section = intent.getStringExtra("section");
        Toast.makeText(DateActivity.this, course + ", " + section, Toast.LENGTH_LONG).show();
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DateActivity.this,SectionsActivity.class);
                intent.putExtra("course",course);
                startActivity(intent);
            }
        });
        date=(TextView)findViewById(R.id.tv);
        tv1=(TextView)findViewById(R.id.tv1);
        strt=(Button)findViewById(R.id.start) ;
        edit=(Button)findViewById(R.id.edit) ;
        strt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                start();
            }
        });
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                edit();
            }
        });
        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        tv1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            TextView tv = (TextView) getActivity().findViewById(R.id.tv);
            selectedDate=Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year);
            tv.setText(Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year)+" selected!");
        }
    }
    public void start() {
        Intent intent = new Intent(this,AttendanceActivity.class);
        intent.putExtra("date",selectedDate);
        intent.putExtra("course",course);
        intent.putExtra("section",section);
        startActivity(intent);
    }
    public void edit() {
        Intent intent = new Intent(this,EditAttendanceActivity.class);
        intent.putExtra("date",selectedDate);
        intent.putExtra("course",course);
        intent.putExtra("section",section);
        startActivity(intent);
    }
}
