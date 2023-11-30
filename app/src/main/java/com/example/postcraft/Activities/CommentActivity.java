package com.example.postcraft.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.postcraft.Adapter.CommentAdapter;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.Comment;
import com.example.postcraft.NetworkResponse.CommentResponse;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentActivity extends AppCompatActivity {
    CardView back;
    SwipeRefreshLayout swipeRefresh;

    RecyclerView rcv;

    EditText etv;

    TextView tvNoData;

    ImageView btnAdd;

    String categoryId, PostId;
    RestCall restCall;

    Tools tools;
    Intent i;

    CommentAdapter commentAdapter;

    SharedPreference sharedPreference;

    CircleImageView proImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        i = getIntent();
        if (i != null) {
            categoryId = i.getStringExtra("CategoryId");
            PostId = i.getStringExtra("PostId");
            back = findViewById(R.id.back);
            swipeRefresh = findViewById(R.id.swipeRefresh);
            rcv = findViewById(R.id.rcv);
            etv = findViewById(R.id.etv);
            btnAdd = findViewById(R.id.btnAdd);
            tvNoData = findViewById(R.id.tvNoData);
            tvNoData.setVisibility(View.VISIBLE);
            proImage = findViewById(R.id.proImage);
            sharedPreference = new SharedPreference(CommentActivity.this);
            tools = new Tools(this);
            restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
            String photo = sharedPreference.getStringvalue("PHOTO");
            Glide.with(this).load(photo).error(R.drawable.user).into(proImage);



            getComment();


            swipeRefresh.setOnRefreshListener(() -> {
               getComment();
            });
            back.setOnClickListener(v -> finish());

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addComment();
                }
            });

        }
    }


    public void getComment() {
        tvNoData.setVisibility(View.VISIBLE);
        swipeRefresh.setRefreshing(false);
        tools.showLoading();


        restCall.get_user_comment("get_user_comment", sharedPreference.getStringvalue("USER_ID"), categoryId, PostId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tvNoData.setVisibility(View.VISIBLE);
                            tools.stopLoading();
                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(CommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                if (commentResponse.getCommentList() != null && commentResponse.getCommentList().size() > 0) {
                                    // If there are comments, hide tvNoData
                                    tvNoData.setVisibility(View.GONE);

                                    commentAdapter = new CommentAdapter(commentResponse.getCommentList(), CommentActivity.this);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(CommentActivity.this);
                                    rcv.setLayoutManager(layoutManager);
                                    rcv.setAdapter(commentAdapter);
                                } else {
                                    // If no comments, show tvNoData
                                    tvNoData.setVisibility(View.VISIBLE);
                                }
                            }

                        });
                    }

                });
    }

    public void addComment() {
        swipeRefresh.setRefreshing(false);
        tools.showLoading();

        String CommentText=etv.getText().toString().trim();

        if (CommentText.isEmpty()) {
            etv.setError("Comment cannot be empty");
            etv.requestFocus();
            tools.stopLoading();
            return;
        }


        restCall.user_post_comment("user_post_comment", PostId, sharedPreference.getStringvalue("USER_ID"), categoryId,CommentText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(CommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        runOnUiThread(() -> {
                            tools.stopLoading();

                            if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                etv.setText("");

                                commentAdapter = new CommentAdapter(commentResponse.getCommentList(), CommentActivity.this);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(CommentActivity.this);
                                rcv.setLayoutManager(layoutManager);
                                rcv.setAdapter(commentAdapter);
                                Toast.makeText(CommentActivity.this, "Comment Added successfully", Toast.LENGTH_SHORT).show();
                                getComment();


                            }
                        });
                    }

                });
    }

}