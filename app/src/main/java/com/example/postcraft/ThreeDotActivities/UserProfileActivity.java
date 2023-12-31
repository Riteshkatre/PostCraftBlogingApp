package com.example.postcraft.ThreeDotActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.postcraft.Activities.SharedPreference;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.LoginResponce;
import com.example.postcraft.NetworkResponse.Tools;
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
    ActivityResultLauncher<Intent> galleryLauncher;
    int REQUEST_GALLERY_PERMISSION = 102;
    String currentPhotoPath = "";
    private File currentPhotoFile;
    Button btnDone;
    Tools tools;
    SharedPreference sharedPreference;
    RestCall restCall;
    private Uri selectedImageUri;


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
                if (currentPhotoFile != null) {
                    selectedImageUri = Uri.fromFile(currentPhotoFile);
                    profileImage.setImageURI(selectedImageUri);
                    currentPhotoPath = currentPhotoFile.getAbsolutePath();
                }
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });


        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                profileImage.setImageURI(selectedImageUri);
                currentPhotoPath = getRealPathFromUri(selectedImageUri);
                currentPhotoFile=new File(currentPhotoPath);
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });

        imgPhotoClick.setOnClickListener(v -> {
            showImagePickerDialog();
        });


        back.setOnClickListener(v -> finish());
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
    }

    private void updateUIAfterEdit(String newFirstName, String newLastName, String newPhoto) {
        runOnUiThread(() -> {
            firstName.setText(newFirstName);
            lastName.setText(newLastName);


            Glide.with(UserProfileActivity.this).load(newPhoto).error(R.drawable.users).into(profileImage);

            Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });
    }



    private void EditUserCall() {
        tools.showLoading();
        String firstNameValue = firstName.getText().toString();
        String lastNameValue = lastName.getText().toString();

        if (currentPhotoFile == null || currentPhotoPath == null) {
            tools.stopLoading();
            Toast.makeText(UserProfileActivity.this, "Please select a profile photo", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody tag = RequestBody.create(MediaType.parse("text/plain"), "user_edit_profile");
        RequestBody bUserId = RequestBody.create(MediaType.parse("text/plain"), sharedPreference.getStringvalue("USER_ID"));
        RequestBody bfirstName = RequestBody.create(MediaType.parse("text/plain"), firstNameValue);
        RequestBody blastName = RequestBody.create(MediaType.parse("text/plain"), lastNameValue);
        MultipartBody.Part fileToUpload = null;

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

        restCall.user_edit_profile(tag, bUserId, bfirstName, blastName, fileToUpload)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<LoginResponce>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        tools.stopLoading();
                        runOnUiThread(() -> {
                            Toast.makeText(UserProfileActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(LoginResponce loginResponce) {
                        tools.stopLoading();
                        runOnUiThread(() -> {
                            if (loginResponce != null && loginResponce.getStatus() != null && loginResponce.getStatus().equals(VeriableBag.SUCCESS_CODE)) {
                                sharedPreference.setStringvalue("FIRST_NAME", loginResponce.getFirstName());
                                sharedPreference.setStringvalue("LAST_NAME", loginResponce.getLastName());
                                sharedPreference.setStringvalue("PHOTO", loginResponce.getProfileImage());
                                updateUIAfterEdit(loginResponce.getFirstName(), loginResponce.getLastName(), loginResponce.getProfileImage());
                               /* Intent i = new Intent(UserProfileActivity.this, HomeActivity.class);

                                startActivity(i);*/
                                finish();
                            } else {
                                Toast.makeText(UserProfileActivity.this, "" + loginResponce.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
    private void showImagePickerDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (checkCameraPermission()) {
                            openCamera();
                        }
                        break;
                    case 1:
                        if (checkGalleryPermission()) {
                            openGallery();
                        }
                        break;
                }
            }
        });
        builder.show();
    }    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private boolean checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);
            return false;
        }
        return true;
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
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
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.postcraft.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
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

}