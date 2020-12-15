package com.namrahrasool.i170018_i170010_project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userid;
    String courseName,section,name,date;
    Boolean present;
    static Context c;
    List<String> ls;
    String currPage;
    public void setSection(String i){
        this.section=i;
    }
    public String getSection(){
        return this.section;
    }
    public void setDate(String i){
        this.date=i;
    }
    public String getDate(){
        return this.date;
    }
    public void setName(String i){
        this.name=i;
    }
    public String getName(){
        return this.name;
    }
    public void setCourseName(String i){
        this.courseName=i;
    }
    public String getCourseName(){
        return this.courseName;
    }
    public void setAttendance(boolean i){
        this.present=i;
    }
    public MyRecyclerViewAdapter(List<String> ls, Context c, String page) {
        this.c=c;
        this.ls=ls;
        this.currPage=page;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemrow;
        if(this.currPage=="attendance" || this.currPage=="editattendance"){
            itemrow= LayoutInflater.from(c).inflate(R.layout.row,parent,false);
        }else{
            itemrow= LayoutInflater.from(c).inflate(R.layout.grid,parent,false);
        }

        return new  MyViewHolder(itemrow);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyRecyclerViewAdapter.MyViewHolder holder, final int position) {
        final String courseName;
        if(this.currPage=="attendance"){
            String str=ls.get(position);
            final String[] arrOfStr = str.split("!");
            holder.name.setText(arrOfStr[1]);
            holder.rollNo.setText(arrOfStr[0]);
            //Picasso.get().load(arrOfStr[2]).into(holder.image);
            Picasso.get().load(arrOfStr[2]).networkPolicy(NetworkPolicy.OFFLINE).
                    into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(arrOfStr[2]).into(holder.image);
                        }
                    });
            holder.grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] options = {"Present", "Absent", "Take Photo","Cancel"};
                    android.app.AlertDialog.Builder builder = new 	android.app.AlertDialog.Builder(c);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                //Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //((Activity)c).startActivityForResult(pickPhoto , 200);

                                //Intent i=new Intent();
                                //i.setType("image/*");
                                //i.setAction(Intent.ACTION_GET_CONTENT);
                                //((Activity)c).startActivityForResult(Intent.createChooser(i,"choose an image"),200);

                                Intent intent = new Intent(c ,DetectActivity.class);
                                intent.putExtra("name",holder.name.getText());
                                intent.putExtra("rollno",holder.rollNo.getText());
                                intent.putExtra("section",getSection());
                                intent.putExtra("course",getCourseName());
                                intent.putExtra("date",getDate());
                                c.startActivity(intent);

                                mAuth = FirebaseAuth.getInstance();
                                database= FirebaseDatabase.getInstance();
                                firebaseUser= mAuth.getCurrentUser();
                                userid=firebaseUser.getUid();
                                reference=database.getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
                                reference.keepSynced(true);
                                reference.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        String a=snapshot.getValue(String.class);
                                        Toast.makeText(c, a, Toast.LENGTH_LONG).show();
                                        if(a.equals("present")){
                                                holder.pora.setText("Present!");
                                        }
                                    }
                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    }
                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                    }
                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                //holder.pora.setText("Present!");

                                dialog.dismiss();
                            } else if (options[item].equals("Present")) {
                                holder.pora.setText("Present!");
                                mAuth = FirebaseAuth.getInstance();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("attendance", "present");
                                firebaseUser= mAuth.getCurrentUser();
                                userid=firebaseUser.getUid();
                                reference= FirebaseDatabase.getInstance().getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
                                reference.keepSynced(true);
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(c, name + "   present!!!", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(c,"Attendance not saved!!!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }else if (options[item].equals("Absent")) {
                                holder.pora.setText("Absent!");
                                mAuth = FirebaseAuth.getInstance();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("attendance", "absent");
                                firebaseUser= mAuth.getCurrentUser();
                                userid=firebaseUser.getUid();
                                reference= FirebaseDatabase.getInstance().getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
                                reference.keepSynced(true);
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(c, name + "   absent!!!", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(c,"Attendance not saved!!!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
        else if(this.currPage=="editattendance"){
            String str=ls.get(position);
            final String[] arrOfStr = str.split("!");
            int length=arrOfStr.length;
            holder.rollNo.setText(arrOfStr[0]);
            holder.name.setText(arrOfStr[1]);
            //Picasso.get().load(arrOfStr[2]).into(holder.image);
            Picasso.get().load(arrOfStr[2]).networkPolicy(NetworkPolicy.OFFLINE).
                into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(arrOfStr[2]).into(holder.image);
                    }
                });
////////////////////////////////////////////////////////////////////////////////////////////////
            //final String[] arrOfStr1 = getDate().split("/");
            //String day=arrOfStr1[0];
            //String month=arrOfStr1[1];
            ///String year=arrOfStr1[2];
            mAuth = FirebaseAuth.getInstance();
            database= FirebaseDatabase.getInstance();
            firebaseUser= mAuth.getCurrentUser();
            userid=firebaseUser.getUid();
            reference=database.getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
            reference.keepSynced(true);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String a=snapshot.getValue(String.class);
                    Toast.makeText(c, a, Toast.LENGTH_LONG).show();
                    if(a.equals("present")){
                        holder.pora.setText("Present!");
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
            holder.grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] options = {"Present", "Absent", "Take Photo","Cancel"};
                    android.app.AlertDialog.Builder builder = new 	android.app.AlertDialog.Builder(c);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Take Photo")) {
                                //Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //((Activity)c).startActivityForResult(pickPhoto , 200);

                                //Intent i=new Intent();
                                //i.setType("image/*");
                                //i.setAction(Intent.ACTION_GET_CONTENT);
                                //((Activity)c).startActivityForResult(Intent.createChooser(i,"choose an image"),200);

                                Intent intent = new Intent(c ,DetectActivity.class);
                                intent.putExtra("name",holder.name.getText());
                                intent.putExtra("rollno",holder.rollNo.getText());
                                intent.putExtra("section",getSection());
                                intent.putExtra("course",getCourseName());
                                intent.putExtra("date",getDate());
                                c.startActivity(intent);

                                mAuth = FirebaseAuth.getInstance();
                                database= FirebaseDatabase.getInstance();
                                firebaseUser= mAuth.getCurrentUser();
                                userid=firebaseUser.getUid();
                                reference=database.getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
                                reference.keepSynced(true);
                                reference.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        String a=snapshot.getValue(String.class);
                                        Toast.makeText(c, a, Toast.LENGTH_LONG).show();
                                        if(a.equals("present")){
                                            holder.pora.setText("Present!");
                                        }
                                    }
                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    }
                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                    }
                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                //holder.pora.setText("Present!");

                                dialog.dismiss();
                            } else if (options[item].equals("Present")) {
                                holder.pora.setText("Present!");
                                mAuth = FirebaseAuth.getInstance();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("attendance", "present");
                                firebaseUser= mAuth.getCurrentUser();
                                userid=firebaseUser.getUid();
                                reference= FirebaseDatabase.getInstance().getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
                                reference.keepSynced(true);
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(c, name + "   present!!!", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(c,"Attendance not saved!!!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }else if (options[item].equals("Absent")) {
                                holder.pora.setText("Absent!");
                                mAuth = FirebaseAuth.getInstance();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("attendance", "absent");
                                firebaseUser= mAuth.getCurrentUser();
                                userid=firebaseUser.getUid();
                                reference= FirebaseDatabase.getInstance().getReference("attendance").child(userid).child(getCourseName()).child(getSection()).child(getDate()).child(holder.rollNo.getText().toString());
                                reference.keepSynced(true);
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(c, name + "   absent!!!", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(c,"Attendance not saved!!!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
        else {
            if(position%2==0){
                holder.course.setBackgroundColor(Color.parseColor("#d10027"));
            }
            holder.course.setText(ls.get(position));
            if(this.currPage=="sections"){
                holder.grid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(c ,DateActivity.class);
                        intent.putExtra("course",getCourseName());
                        intent.putExtra("section",ls.get(position));
                        c.startActivity(intent);
                    }
                });
            }
            if(this.currPage=="courses"){
                holder.grid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(c ,SectionsActivity.class);
                        intent.putExtra("course",ls.get(position));
                        c.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView course;
        TextView pora;
        TextView rollNo,name;
        LinearLayout grid;
        ImageButton imageButton;
        de.hdodenhof.circleimageview.CircleImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            if(currPage=="attendance" || currPage=="editattendance"){
                pora=itemView.findViewById(R.id.pora);
                grid=itemView.findViewById(R.id.rowww);
                image=itemView.findViewById(R.id.image);
                name=itemView.findViewById(R.id.name);
                rollNo=itemView.findViewById(R.id.rollNo);
            }else{
                course=itemView.findViewById(R.id.info_text);
                grid=itemView.findViewById(R.id.gridd);
                imageButton=itemView.findViewById(R.id.img);
            }
        }
    }
}


