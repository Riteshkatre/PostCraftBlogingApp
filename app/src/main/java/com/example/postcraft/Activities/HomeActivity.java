package com.example.postcraft.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.postcraft.Adapter.CategoryAdapter;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.CategoryListResponce;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;
import com.example.postcraft.ThreeDotActivities.ContactUsActivity;
import com.example.postcraft.ThreeDotActivities.HelpActivity;
import com.example.postcraft.ThreeDotActivities.SettingsActivity;
import com.example.postcraft.ThreeDotActivities.UserProfileActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView imgSearch, mic;
    CardView threeDotImageView;
    NavigationView navigationView;

    TextView userName, userEmail;

    CircleImageView userImage;
    EditText searchbar;

    RestCall restCall;

    CategoryAdapter categoryAdapter;

    SharedPreference sharedPreference;
    RecyclerView rcv;
    Tools tools;
    TextView tvNoData;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private static final int VOICE_SEARCH_REQUEST_CODE = 200;

    SwipeRefreshLayout swipeRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_home);
        threeDotImageView = findViewById(R.id.threedot);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        rcv = findViewById(R.id.rcv);
        imgSearch = findViewById(R.id.imgSearch);
        imgSearch.setVisibility(View.VISIBLE);
        searchbar = findViewById(R.id.searchbar);
        tvNoData = findViewById(R.id.tvNoData);
        mic = findViewById(R.id.mic);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        sharedPreference = new SharedPreference(HomeActivity.this);
        tools = new Tools(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isBiometricEnabled = sharedPreferences.getBoolean("biometricSwitchState", false);

        if (isBiometricEnabled) {
            drawerLayout.setVisibility(View.GONE);
            enableBiometricAuthentication();
        }

        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        categoryAdapter = new CategoryAdapter(HomeActivity.this, new ArrayList<>());

        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.userName);
        userEmail = headerView.findViewById(R.id.userEmail);
        userImage = headerView.findViewById(R.id.userImage);


        searchbar.setOnFocusChangeListener((v, hasFocus) -> {
            searchbar.setHint(hasFocus ? null : "Search Name Here");
            imgSearch.setVisibility(View.VISIBLE);
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                categoryAdapter.Search(charSequence, rcv,tvNoData);

                if (charSequence.length() > 0) {

                    mic.setVisibility(View.GONE);
                } else {

                    mic.setVisibility(View.VISIBLE);
                }

                updateNoDataVisibility();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opneVoiceDialog();
            }
        });

        swipeRefresh.setOnRefreshListener(() -> getCategory());

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        threeDotImageView.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_item1) {
                Intent i = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(i);
            } else if (itemId == R.id.menu_item2) {
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
                finish();
            } else if (itemId == R.id.menu_item4) {
                Intent i = new Intent(HomeActivity.this, ContactUsActivity.class);
                startActivity(i);
            } else if (itemId == R.id.menu_item5) {
                Intent i = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(i);
            } else if (itemId == R.id.menu_item3) {
                showLogoutConfirmationDialog();
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        getCategory();

    }
//for open voice dialog*******************************************************************************
    private void opneVoiceDialog() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(i, VOICE_SEARCH_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!arrayList.isEmpty()) {
                String voice = arrayList.get(0);
                searchCategory(voice);
            } else {
                Toast.makeText(this, "No voice input detected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Can't Complete The Action", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchCategory(String categoryName) {

        categoryAdapter.Search(categoryName, rcv,tvNoData);



    }
//********************************************************************************************************
private void updateNoDataVisibility() {
    boolean isSearchResultsEmpty = categoryAdapter.isEmpty();
    if (isSearchResultsEmpty) {
        tvNoData.setVisibility(View.VISIBLE);
    } else {
        tvNoData.setVisibility(View.GONE);
    }
}

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Confirmation")
                .setMessage("Are you sure you want to exit the app?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> finish()) // Close the app
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss the dialog
                .show();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirmation")
                .setCancelable(false)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedPreference.clearPref();
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void getCategory() {

        tools.showLoading();
        rcv.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        restCall.getCategory("getCategory")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CategoryListResponce>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tvNoData.setVisibility(View.VISIBLE);
                            tools.stopLoading();
                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(HomeActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(CategoryListResponce categoryListResponce) {
                        runOnUiThread(() -> {
                            tvNoData.setVisibility(View.GONE);
                            tools.stopLoading();
                            if (categoryListResponce != null
                                    && categoryListResponce.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)
                                    && categoryListResponce.getCategoryList() != null
                                    && categoryListResponce.getCategoryList().size() > 0) {
                                categoryAdapter = new CategoryAdapter(HomeActivity.this, categoryListResponce.getCategoryList());

                                LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);
                                rcv.setLayoutManager(layoutManager);
                                rcv.setAdapter(categoryAdapter);
                            }
                        });
                    }
                });
    }

    private void enableBiometricAuthentication() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            Executor executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(HomeActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    drawerLayout.setVisibility(View.VISIBLE);
                }

                @Override
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
//                    .setNegativeButtonText("Cancel")
                    .setDeviceCredentialAllowed(true)
                    .build();
            biometricPrompt.authenticate(promptInfo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String firstName = sharedPreference.getStringvalue("FIRST_NAME");
        String lastName = sharedPreference.getStringvalue("LAST_NAME");
        String email = sharedPreference.getStringvalue("EMAIL");
        String photo = sharedPreference.getStringvalue("PHOTO");

        Log.d("PhotoPath", "Photo Path: " + photo);

        Glide.with(this).load(photo).error(R.drawable.baseline_remove_red_eye_24).into(userImage);

        userName.setText(firstName + " " + lastName);
        userEmail.setText(email);
        userName.setSelected(true);
        userEmail.setSelected(true);

    }
}
