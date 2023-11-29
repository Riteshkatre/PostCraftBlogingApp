package com.example.postcraft.ThreeDotActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.postcraft.R;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity {
    CardView back;
    SwitchCompat SwitchLock;
    private static final String SWITCH_STATE_KEY = "biometricSwitchState";
    private boolean isBiometricEnabled = false;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    RelativeLayout lyt;
    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        back=findViewById(R.id.back);
        SwitchLock=findViewById(R.id.SwitchLock);

        lyt = findViewById(R.id.lyt);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isBiometricEnabled = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false);
        SwitchLock.setChecked(isBiometricEnabled);


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


        back.setOnClickListener(v -> finish());

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
}