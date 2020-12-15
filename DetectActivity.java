package com.namrahrasool.i170018_i170010_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.namrahrasool.i170018_i170010_project.Helper.GraphicOverlay;
import com.namrahrasool.i170018_i170010_project.Helper.RectOverlay;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DetectActivity extends AppCompatActivity {
    Button faceDetectButton;
    GraphicOverlay graphicOverlay;
    CameraView cameraView;
    AlertDialog alertDialog;
    FirebaseDatabase database;
    DatabaseReference reference;
    Intent intent;
    Bitmap bitmap;
    ImageLabeler labeler;
    String course,section,name,date,rollno,userid;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    Boolean present;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        course = intent.getStringExtra("course");
        rollno = intent.getStringExtra("rollno");
        section = intent.getStringExtra("section");
        date = intent.getStringExtra("date");
        name = intent.getStringExtra("name");
        Toast.makeText(DetectActivity.this, course + ", " + section+ ", " +name+ ", " + date, Toast.LENGTH_LONG).show();

        faceDetectButton=findViewById(R.id.detect_face_btn);
        graphicOverlay=findViewById(R.id.graphic_overlay);
        cameraView=findViewById(R.id.camera_view);
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
        alertDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait, Loading...")
                .setCancelable(false)
                .build();
        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();
                bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                InputImage image;
                try {
                    //image = InputImage.fromFilePath(getApplicationContext(), data.getData());
                    image = InputImage.fromBitmap(bitmap, getRotationCompensation(getCameraId(),DetectActivity.this,getApplicationContext()));
                    labeler.process(image)
                            .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                @Override
                                public void onSuccess(List<ImageLabel> labels) {
                                    // Task completed successfully
                                    // ...
                                    Toast.makeText(DetectActivity.this, "Got results from mlkit!!!", Toast.LENGTH_SHORT).show();
                                    float max=0;
                                    String maxLabel="";
                                    for (ImageLabel label : labels) {
                                        String text = label.getText();
                                        float confidence = label.getConfidence();
                                        if(confidence>max){
                                            max=confidence;
                                            maxLabel=text;
                                        }
                                        int index = label.getIndex();
                                        Log.d("confidence",text+"   "+confidence);
                                    }
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    if(name.startsWith(maxLabel)){
                                        present=true;
                                        hashMap.put("attendance", "present");
                                    }
                                    else{
                                        present=false;
                                        hashMap.put("attendance", "absent");
                                    }
                                    firebaseUser= mAuth.getCurrentUser();
                                    userid=firebaseUser.getUid();
                                    reference= FirebaseDatabase.getInstance().getReference("attendance").child(userid).child(course).child(section).child(date).child(rollno);
                                    reference.keepSynced(true);
                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                if(present) {
                                                    Toast.makeText(DetectActivity.this, name + "   present!!!", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    Toast.makeText(DetectActivity.this, name + "   absent!!!", Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                Toast.makeText(DetectActivity.this,"Attendance not saved!!!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    Toast.makeText(DetectActivity.this, "Failed to get results from mlkit!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                cameraView.stop();
                processFaceDetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void processFaceDetection(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector= FirebaseVision.getInstance().getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                getFaceResults(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetectActivity.this,"Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter=0;
        for(FirebaseVisionFace face:firebaseVisionFaces){
            Rect rect=face.getBoundingBox();
            RectOverlay rectOverlay=new RectOverlay(graphicOverlay,rect);
            graphicOverlay.add(rectOverlay);
            counter=counter+1;
        }
        alertDialog.dismiss();
        finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraView.stop();
    }
    @Override
    protected void onResume(){
        super.onResume();
        cameraView.start();
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
}