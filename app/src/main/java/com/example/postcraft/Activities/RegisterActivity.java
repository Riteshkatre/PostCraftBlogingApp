package com.example.postcraft.Activities;

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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {

    CardView btnSignUp,back;

    TextView tvSignIn;
    EditText etFirstName, etLastName, etEmail, etPhone, etPassword;

    RestCall restCall;

    ImageView showpassword;
    ImageView imgPhotoClick;
    CircleImageView imgUser;

    private static final int REQUEST_CAMERA_PERMISSION = 101;

    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;
    int REQUEST_GALLERY_PERMISSION = 102;
    String currentPhotoPath = "";
    private File currentPhotoFile;


    private boolean isPasswordVisible = false;

    Tools tools;
    private Uri selectedImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnSignUp = findViewById(R.id.btnSignUp);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        tvSignIn = findViewById(R.id.tvSignIn);
        showpassword = findViewById(R.id.showpassword);
        back = findViewById(R.id.back);
        imgUser = findViewById(R.id.imgUser);
        imgPhotoClick = findViewById(R.id.imgPhotoClick);
        tools=new Tools(this);


        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if (currentPhotoFile != null) {
                    selectedImageUri = Uri.fromFile(currentPhotoFile);
                    imgUser.setImageURI(selectedImageUri);
                    currentPhotoPath = currentPhotoFile.getAbsolutePath();
                }
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });


        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                imgUser.setImageURI(selectedImageUri);
                currentPhotoPath = getRealPathFromUri(selectedImageUri);
            } else {
                Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
            }
        });
        imgPhotoClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentPhotoPath = "";
                    showImagePickerDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });




        btnSignUp.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(firstName)) {
                etFirstName.setError("First name is required");
                etFirstName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                etLastName.setError("Last name is required");
                etLastName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (!isValidEmail(email)) {
                etEmail.setError("Invalid email format (format should be abc@gmail.com)");
                etEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            } else if (!isValidPassword(password)) {
                etPassword.setError("Password must be 8 characters long and include both alphabetic and numeric characters");
                etPassword.requestFocus();
                return;
            }

            if (selectedImageUri == null && (currentPhotoFile == null || currentPhotoPath.isEmpty())) {
                Toast.makeText(this, "Please select a profile photo", Toast.LENGTH_SHORT).show();
                tools.stopLoading();

            }
            else {
                user_registration();
            }


        });

        tvSignIn.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LogingActivity.class);
            startActivity(i);
            finish();
        });
        showpassword.setOnClickListener(view -> togglePasswordVisibility());
        back.setOnClickListener(v -> finish()) ;
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



    private boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            etPassword.setError("Password should be at least 8 characters");
            etPassword.requestFocus();
            return false;
        }
        boolean hasAlphabetic = false;
        boolean hasNumeric = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasAlphabetic = true;
            } else if (Character.isDigit(c)) {
                hasNumeric = true;
            }
            if (hasAlphabetic && hasNumeric) {
                break;
            }
        }

        if (!hasAlphabetic || !hasNumeric) {
            etPassword.setError("Password must contain both alphabetic and numeric characters");
            etPassword.requestFocus();
            return false;
        }
        if (!containsSpecialCharacter(password)) {
            etPassword.setError("Password must contain at least one special character");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
    private boolean containsSpecialCharacter(String str) {
        String specialCharacters = "!@#$%^&*()-_+=<>?/[]{}|";
        for (char c : str.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public void user_registration() {
        tools.showLoading();
        RequestBody tag = RequestBody.create(MediaType.parse("text/plain"), "user_registration");
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), etFirstName.getText().toString().trim());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), etLastName.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), etEmail.getText().toString().trim());
        MultipartBody.Part fileToUpload = null;
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"),etPassword.getText().toString().trim());

        if (selectedImageUri != null) {
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                File file = new File(getRealPathFromUri(selectedImageUri));
                RequestBody rbPhoto = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("profile_image", file.getName(), rbPhoto);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        restCall.user_registration(tag, first_name, last_name, email,fileToUpload,password)
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
                                Toast.makeText(RegisterActivity.this, "this is wrong", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    @Override
                    public void onNext(UserResponce userResponce) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            if (userResponce != null && userResponce.getStatus() != null
                                    && userResponce.getStatus().equals(VeriableBag.SUCCESS_CODE)) {
                                if (currentPhotoFile != null && currentPhotoPath != null) {
                                    Toast.makeText(RegisterActivity.this, ""+userResponce.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LogingActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                // Log the response for debugging
                                Log.e("API Response", "Empty or invalid response: " + userResponce);
                                Toast.makeText(RegisterActivity.this, ""+userResponce.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


       });

}


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showpassword.setImageDrawable(ContextCompat.getDrawable(RegisterActivity.this, R.drawable.ic_eye_close));
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showpassword.setImageDrawable(ContextCompat.getDrawable(RegisterActivity.this, R.drawable.baseline_remove_red_eye_24));
        }

        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;

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
        currentPhotoFile = image;
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}