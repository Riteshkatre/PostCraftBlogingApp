package com.example.postcraft.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.LoginResponce;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import rx.Subscriber;
import rx.schedulers.Schedulers;

public class LogingActivity extends AppCompatActivity {

    CardView btnLogin;

    EditText etEmail, etPassword;

    String email1, password1;

    TextView tvSignUp;


    RestCall restCall;
    ImageView showpassword;
    private SharedPreference sharedPreference;

    private boolean isPasswordVisible = false;

    Tools tools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_loging);

        btnLogin = findViewById(R.id.btnLogin);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        tvSignUp = findViewById(R.id.tvSignUp);
        showpassword = findViewById(R.id.showpassword);
        sharedPreference = new SharedPreference(this);
        tools= new Tools(this);


        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);


        btnLogin.setOnClickListener(v -> {
            email1 = etEmail.getText().toString().trim();
            password1 = etPassword.getText().toString().trim();

            if (email1.isEmpty() || password1.isEmpty()) {

                etEmail.setError("Email is required");
                etEmail.requestFocus();
                etPassword.setError("Password is required");
                etPassword.requestFocus();

                Toast.makeText(LogingActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            } else {

                user_login(email1, password1);

            }

        });

        tvSignUp.setOnClickListener(v -> {
            Intent i = new Intent(LogingActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });

        showpassword.setOnClickListener(view -> togglePasswordVisibility());
    }


    public void user_login(String email, String password) {
        String type = "Android";
        tools.showLoading();
        restCall.user_login("user_login", email, password,type)
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
                                Log.e("API Error", e.getMessage());
                                Toast.makeText(LogingActivity.this, "this is wrong", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }


                    @Override
                    public void onNext(LoginResponce loginResponce) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tools.stopLoading();
                                if (loginResponce.getStatus().equals(VeriableBag.SUCCESS_CODE)) {

                                    sharedPreference.setStringvalue("USER_ID",loginResponce.getUserId());
                                    sharedPreference.setStringvalue("FIRST_NAME", loginResponce.getFirstName());
                                    sharedPreference.setStringvalue("LAST_NAME", loginResponce.getLastName());
                                    sharedPreference.setStringvalue("EMAIL", loginResponce.getEmail());
                                    sharedPreference.setStringvalue("PHOTO",loginResponce.getProfileImage());
                                    sharedPreference.setLoggedIn(true);
                                    Toast.makeText(LogingActivity.this, ""+loginResponce.getMessage(), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LogingActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(LogingActivity.this, "invalid candidate please Register Email First ", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });

    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showpassword.setImageDrawable(ContextCompat.getDrawable(LogingActivity.this, R.drawable.ic_eye_close));
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showpassword.setImageDrawable(ContextCompat.getDrawable(LogingActivity.this, R.drawable.baseline_remove_red_eye_24));
        }

        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;

    }
}