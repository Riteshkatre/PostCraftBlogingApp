package com.example.postcraft.ThreeDotActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.postcraft.Activities.SharedPreference;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.LoginResponce;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.UserResponce;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class UserProfileActivity extends AppCompatActivity {
    CardView back;
    EditText firstName,lastName;
    ImageView imgPhotoClick;
    CircleImageView profileImage;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    ActivityResultLauncher<Intent> cameraLauncher;
    String currentPhotoPath = "";
    private File currentPhotoFile;
    Button btnDone;
    Tools tools;
    SharedPreference sharedPreference;
    RestCall restCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        back=findViewById(R.id.back);
        imgPhotoClick=findViewById(R.id.imgPhotoClick);
        firstName=findViewById(R.id.firstName);
        lastName=findViewById(R.id.lastName);
        profileImage=findViewById(R.id.profileImage);
        btnDone=findViewById(R.id.btnDone);

        sharedPreference=new SharedPreference(this);
        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        tools=new Tools(this);


        String firstName1 = sharedPreference.getStringvalue("FIRST_NAME");
        String lastName1 = sharedPreference.getStringvalue("LAST_NAME");
        String photo = sharedPreference.getStringvalue("PHOTO");

        Log.d("PhotoPath", "Photo Path: " + photo);

        Glide.with(this).load(photo).error(R.drawable.users).into(profileImage);

        firstName.setText(firstName1 );
        lastName.setText(lastName1 );


        btnDone.setOnClickListener(v -> {
            EditUserCall();

        });




        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                profileImage.setImageURI(Uri.parse(currentPhotoPath));
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });

        imgPhotoClick.setOnClickListener(v -> {
            try {
                currentPhotoPath = "";
                if (checkCameraPermission()) {
                    openCamera();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        back.setOnClickListener(v -> finish());
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.postcraft", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoFile = image;
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private void EditUserCall() {

        String firstNameValue = firstName.getText().toString().trim();
        String lastNameValue = lastName.getText().toString().trim();

        if (firstNameValue.isEmpty()) {
            firstName.setError("FirstName cannot be empty");
            firstName.requestFocus();
            tools.stopLoading();
            return;
        }

        else if (lastNameValue.isEmpty()) {
            lastName.setError("LastName cannot be empty");
            lastName.requestFocus();
            tools.stopLoading();
            return;
        }


        else if (currentPhotoFile == null || currentPhotoPath.isEmpty()) {
            Toast.makeText(this, "Please select a new profile photo", Toast.LENGTH_SHORT).show();
            tools.stopLoading();
            return;
        }

        tools.showLoading();
        RequestBody tag = RequestBody.create(MediaType.parse("text/plain"), "user_edit_profile");
        RequestBody bUserId = RequestBody.create(MediaType.parse("text/plain"), sharedPreference.getStringvalue("USER_ID"));
        RequestBody bfirstName = RequestBody.create(MediaType.parse("text/plain"),firstName.getText().toString().trim());
        RequestBody blastName = RequestBody.create(MediaType.parse("text/plain"), lastName.getText().toString().trim());
        MultipartBody.Part fileToUpload=null    ;
        if (currentPhotoFile != null && currentPhotoPath != null) {
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                File file = new File(currentPhotoPath);
                RequestBody rbPhoto = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("profile_image", file.getName(), rbPhoto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        restCall.user_edit_profile(tag,bUserId,bfirstName,blastName,fileToUpload)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<LoginResponce>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tools.stopLoading();
                                Toast.makeText(UserProfileActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(LoginResponce loginResponce) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tools.stopLoading();
                                if (loginResponce != null && loginResponce.getStatus() != null) {
                                    if (loginResponce.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                        if (currentPhotoFile != null && currentPhotoPath != null) {
                                            firstName.setText(bfirstName.toString().trim());
                                            lastName.setText(blastName.toString().trim());
                                        }
                                        Toast.makeText(UserProfileActivity.this, loginResponce.getMessage(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Not able to edit", Toast.LENGTH_SHORT).show();
                                    }
                                } else {


                                    Toast.makeText(UserProfileActivity.this, "Edit Successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }

                });
    }
}