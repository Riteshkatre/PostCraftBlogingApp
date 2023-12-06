package com.example.postcraft.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.UserResponce;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

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
    ImageView imgPost, imgPhotoClick;
    String currentPhotoPath = "", categoryId;

    EditText postDesc;

    Button btnDone;
    RestCall restCall;

    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;
    int REQUEST_GALLERY_PERMISSION = 102;
    int REQUEST_CAMERA_PERMISSION = 101;
    File CurentPhotoFile;

    SharedPreference sharedPreference;



    Intent i;

    Tools tools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        back = findViewById(R.id.back);
        imgPost = findViewById(R.id.imgPost);
        imgPhotoClick = findViewById(R.id.imgPhotoClick);
        postDesc = findViewById(R.id.postDesc);
        btnDone = findViewById(R.id.btnDone);
        FirebaseApp.initializeApp(this);

        sharedPreference = new SharedPreference(AddPostActivity.this);

        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        tools = new Tools(this);

        i = getIntent();
        categoryId = i.getStringExtra("category_Id");


        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                imgPost.setImageURI(Uri.parse(currentPhotoPath));
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                // Do something with the selected image URI
                imgPost.setImageURI(selectedImageUri);
                currentPhotoPath = getPathFromUri(selectedImageUri);
                CurentPhotoFile=new File(currentPhotoPath);

            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> finish());
        imgPhotoClick.setOnClickListener(v -> {
            try {
                currentPhotoPath = "";
                showImagePickerDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btnDone.setOnClickListener(v -> {
            user_post();
        });
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    }

    private boolean checkCameraPermission() {
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
        CurentPhotoFile = image;
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void user_post() {
        tools.showLoading();

        String description = postDesc.getText().toString().trim();

        if (description.isEmpty()) {
            postDesc.setError("Description cannot be empty");
            postDesc.requestFocus();
            tools.stopLoading();
            return;
        }

        // Subscribe the user to the FCM topic "all_users"
        subscribeToPostTopic();

        RequestBody tag = RequestBody.create(MediaType.parse("text/plain"), "user_post");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), sharedPreference.getStringvalue("USER_ID"));
        RequestBody categoryID = RequestBody.create(MediaType.parse("text/plain"), categoryId);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), description);
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

        restCall.user_post(tag, userId, categoryID, desc, fileToUpload)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<UserResponce>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            Log.e("API Error", e.getMessage());
                            Toast.makeText(AddPostActivity.this, " Post Is Uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }


                    @Override
                    public void onNext(UserResponce userResponce) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            if (userResponce != null && userResponce.getStatus() != null
                                    && userResponce.getStatus().equals(VeriableBag.SUCCESS_CODE)) {
                                if (CurentPhotoFile != null && currentPhotoPath != null) {
                                    Toast.makeText(AddPostActivity.this, "Post Is Uploaded", Toast.LENGTH_SHORT).show();
                                    showNotification("New Post", "Uploaded a new post!!");
                                }
                            } else {
                                Log.e("API Response", "Empty or invalid response: " + userResponce);
                                Toast.makeText(AddPostActivity.this, "Empty or invalid response", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(AddPostActivity.this, "Post Is Uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
    }

    private void subscribeToPostTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("FCM", "Subscribed to topic: all_users");
                        } else {
                            Log.e("FCM", "Failed to subscribe to topic: all_users", task.getException());
                        }
                    }
                });
    }

    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, PostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("category_Id", categoryId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Uri defaultSoundUri = Settings.System.DEFAULT_NOTIFICATION_URI;


        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(1, notification);
    }



}
