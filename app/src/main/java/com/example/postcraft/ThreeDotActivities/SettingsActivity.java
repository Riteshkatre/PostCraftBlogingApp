package com.example.postcraft.ThreeDotActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.postcraft.Activities.HomeActivity;
import com.example.postcraft.Activities.MainActivity;
import com.example.postcraft.Activities.SharedPreference;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.UserResponce;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import java.util.concurrent.Executor;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity {
    CardView back;
    SwitchCompat SwitchLock;
    private static final String SWITCH_STATE_KEY = "biometricSwitchState";
    private boolean isBiometricEnabled = false;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    RelativeLayout lyt;
    private SharedPreferences sharedPreferences;

    ImageView delete;

    RestCall restCall;
    Tools tools;

    SharedPreference sharedPreference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        back=findViewById(R.id.back);
        SwitchLock=findViewById(R.id.SwitchLock);

        lyt = findViewById(R.id.lyt);
        delete = findViewById(R.id.delete);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isBiometricEnabled = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false);
        SwitchLock.setChecked(isBiometricEnabled);

        sharedPreference=new SharedPreference(this);
        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        tools=new Tools(this);



        SwitchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isBiometricEnabled = isChecked;
            sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isBiometricEnabled).apply();

//            updateLockImage(isBiometricEnabled);  // Update the lock image

            if (isChecked) {
                enableBiometricAuthentication();
            } else {

                disableBiometricAuthentication();
            }
        });

        if (isBiometricEnabled) {
            lyt.setVisibility(View.GONE);
            enableBiometricAuthentication();
        }


        back.setOnClickListener(v -> {
          Intent i= new Intent(SettingsActivity.this, HomeActivity.class);
          startActivity(i);

        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (biometricPrompt != null) {
            biometricPrompt.cancelAuthentication();
        }
    }


    private void enableBiometricAuthentication() {
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            Executor executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(SettingsActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(SettingsActivity.this, "Biometric authentication enabled", Toast.LENGTH_SHORT).show();
                    lyt.setVisibility(View.VISIBLE);
                }
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        finish();
                    }
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Insert fingerprint")
                    .setDescription("Use Fingerprint To Log In")
                    .setDeviceCredentialAllowed(true)
                    .build();
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, "Device doesn't support biometric authentication", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableBiometricAuthentication() {

        Toast.makeText(this, "Biometric authentication disabled", Toast.LENGTH_SHORT).show();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?")
                .setMessage("Deleting your account will permanently remove all your data. Are you sure you want to proceed?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteAccountApi();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }

  private  void DeleteAccountApi(){
        tools.showLoading();
        restCall.user_delete("user_delete",sharedPreference.getStringvalue("USER_ID"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserResponce>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(SettingsActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }

                    @Override
                    public void onNext(UserResponce userResponce) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (userResponce != null && userResponce.getStatus() != null && userResponce.getStatus().equals(VeriableBag.SUCCESS_CODE)){
                                    sharedPreference.clearPref();
                                    sharedPreference.setLoggedIn(false);
                                    Intent i=new Intent(SettingsActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }
                        });

                    }
                });
  }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i= new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}