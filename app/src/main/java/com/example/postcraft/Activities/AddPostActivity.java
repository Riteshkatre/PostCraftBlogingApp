package com.example.postcraft.Activities;

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

import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.UserResponce;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class AddPostActivity extends AppCompatActivity {

    CardView back;
    ImageView imgPost,imgPhotoClick;
    String currentPhotoPath = "",categoryId;

    EditText postDesc;

    Button btnDone;
    RestCall restCall;

    ActivityResultLauncher<Intent> cameraLauncher;
    int REQUEST_CAMERA_PERMISSION = 101;
    File CurentPhotoFile ;

    SharedPreference sharedPreference;

    Intent i;

    Tools tools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        back=findViewById(R.id.back);
        imgPost=findViewById(R.id.imgPost);
        imgPhotoClick=findViewById(R.id.imgPhotoClick);
        postDesc=findViewById(R.id.postDesc);
        btnDone=findViewById(R.id.btnDone);
        sharedPreference=new SharedPreference(AddPostActivity.this);

        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        tools=new Tools(this);

       i=getIntent();
        categoryId=i.getStringExtra("category_Id");

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                imgPost.setImageURI(Uri.parse(currentPhotoPath));
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });


        back.setOnClickListener(v -> finish());
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

        btnDone.setOnClickListener(v -> {
            user_post();
        });
    }
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
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
        CurentPhotoFile = image;
        currentPhotoPath = image.getAbsolutePath();
        return image;}



    public void user_post() {
        tools.showLoading();

        String description = postDesc.getText().toString().trim();

        if (description.isEmpty()) {
            postDesc.setError("Description cannot be empty");
            postDesc.requestFocus();
            tools.stopLoading();
            return;
        }
        RequestBody tag = RequestBody.create(MediaType.parse("text/plain"), "user_post");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"),sharedPreference.getStringvalue("USER_ID") );
        RequestBody categoryID = RequestBody.create(MediaType.parse("text/plain"),categoryId);
        RequestBody  desc= RequestBody.create(MediaType.parse("text/plain"), description);
        MultipartBody.Part fileToUpload = null;
        if (fileToUpload == null && currentPhotoPath != "") {
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                File file = new File(currentPhotoPath);
                RequestBody rbPhoto = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("post_image", file.getName(), rbPhoto);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        }

        restCall.user_post(tag, userId, categoryID, desc,fileToUpload)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<UserResponce>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tools.stopLoading();
                                Log.e("API Error", e.getMessage());
                                Toast.makeText(AddPostActivity.this, "this is wrong", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    @Override
                    public void onNext(UserResponce userResponce) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            if (userResponce != null && userResponce.getStatus() != null
                                    && userResponce.getStatus().equals(VeriableBag.SUCCESS_CODE)) {
                                if (CurentPhotoFile != null && currentPhotoPath != null) {
                                    Intent intent = new Intent(AddPostActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            } else {
                                // Log the response for debugging
                                Log.e("API Response", "Empty or invalid response: " + userResponce);
                                Toast.makeText(AddPostActivity.this, "Empty or invalid response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                });

    }
}