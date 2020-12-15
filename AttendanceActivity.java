package com.namrahrasool.i170018_i170010_project;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AttendanceActivity extends AppCompatActivity {
    MyRecyclerViewAdapter adapter;
    FloatingActionButton profile;
    Button submit;
    ImageView back;
    Intent intent;
    String course,section,date;
    List<String> data;
    ImageLabeler labeler;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        course = intent.getStringExtra("course");
        section = intent.getStringExtra("section");
        date = intent.getStringExtra("date");
        Toast.makeText(AttendanceActivity.this, course + ", " + section+ ", " + date, Toast.LENGTH_LONG).show();

        AutoMLImageLabelerLocalModel localModel =
                new AutoMLImageLabelerLocalModel.Builder()
                        .setAssetFilePath("model/manifest.json")
                        // or .setAbsoluteFilePath(absolute file path to manifest file)
                        .build();
        AutoMLImageLabelerOptions autoMLImageLabelerOptions =
                new AutoMLImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.0f)  // Evaluate your model in the Firebase console
                        // to determine an appropriate value.
                        .build();
        labeler = ImageLabeling.getClient(autoMLImageLabelerOptions);


        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Attendance marked successfully!",
                        Toast.LENGTH_LONG).show();
                Intent intent=new Intent(AttendanceActivity.this,CoursesActivity.class);
                startActivity(intent);
            }
        });
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AttendanceActivity.this,DateActivity.class);
                intent.putExtra("course",course);
                intent.putExtra("section",section);
                startActivity(intent);
            }
        });
        profile=(FloatingActionButton)findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(AttendanceActivity.this);
                builder.setMessage("Do you want to Logout ?");
                builder.setTitle("Logout !");
                builder.setCancelable(false);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(AttendanceActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton(
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
        data= new ArrayList<String>();
        database= FirebaseDatabase.getInstance();
        firebaseUser= mAuth.getCurrentUser();
        userid=firebaseUser.getUid();
        reference=database.getReference("students").child(userid).child(course).child(section);
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student s=snapshot.getValue(Student.class);
                data.add(snapshot.getKey()+"!"+s.getName()+"!"+s.getImage());//id,name,image
                adapter.notifyDataSetChanged();
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

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter(data, this,"attendance");
        adapter.setSection(section);
        adapter.setDate(date);
        adapter.setCourseName(course);
        recyclerView.setAdapter(adapter);

    }
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.d("Bad rotation value: ",rotationCompensation+"");
        }
        return result;
    }

    private String getCameraId() {
        int curCameraId = 0;

        if (Camera.getNumberOfCameras() > 0) {
            curCameraId = (curCameraId + 1) % Camera.getNumberOfCameras();
        } else {
            curCameraId = 0;
        }
        return curCameraId+"";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200 && resultCode==RESULT_OK){
           // selectedImage = (Bitmap) data.getExtras().get("data");
           // Log.d("picture",selectedImage.toString());
            InputImage image;
            try {
                //image = InputImage.fromFilePath(getApplicationContext(), data.getData());
                image = InputImage.fromBitmap((Bitmap) data.getExtras().get("data"), getRotationCompensation(getCameraId(),AttendanceActivity.this,getApplicationContext()));
                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                // Task completed successfully
                                // ...
                                Toast.makeText(AttendanceActivity.this, "Got results from mlkit!!!", Toast.LENGTH_SHORT).show();
                                for (ImageLabel label : labels) {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();
                                    Log.d("confidence",text+"   "+confidence);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                Toast.makeText(AttendanceActivity.this, "Failed to get results from mlkit!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
