package com.example.postcraft.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.postcraft.Adapter.PostAdapter;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.Post;
import com.example.postcraft.NetworkResponse.PostResponse;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostActivity extends AppCompatActivity {
    CardView back;
    RecyclerView rcvPost;
    List<PostResponse> postResponseList;

    List<Post> postList;
    ImageView home, addPost;
    CircleImageView userProfile;

    SharedPreference sharedPreference;

    SwipeRefreshLayout swipeRefresh;

    PostAdapter postAdapter;
    Intent i;

    TextView tvNoData;
    String categoryId;
    RestCall restCall;

    Tools tools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        i = getIntent();
        if (i != null) {
            categoryId = i.getStringExtra("CategoryId");

            back = findViewById(R.id.back);
            rcvPost = findViewById(R.id.rcvPost);
            home = findViewById(R.id.home);
            addPost = findViewById(R.id.addPost);
            userProfile = findViewById(R.id.userProfile);
            swipeRefresh = findViewById(R.id.swipeRefresh);
            tvNoData = findViewById(R.id.tvNoData);
            tvNoData.setVisibility(View.VISIBLE);


            postResponseList = new ArrayList<>();
            postList = new ArrayList<>();

            restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
            tools=new Tools(this);

            sharedPreference = new SharedPreference(PostActivity.this);
            String photo = sharedPreference.getStringvalue("PHOTO");
            Glide.with(this).load(photo).error(R.drawable.baseline_remove_red_eye_24).into(userProfile);

            back.setOnClickListener(v -> finish());

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPost();

                }
            });

            addPost.setOnClickListener(v -> {
                tools.showLoading();
                Intent i = new Intent(PostActivity.this, AddPostActivity.class);
                i.putExtra("category_Id", categoryId);
                startActivity(i);


            });
            userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tools.showLoading();
                    Intent i=new Intent(PostActivity.this, MyPostActivity.class);
                    startActivity(i);

                }
            });

            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getPost();

                }
            });

        }
        getPost();
    }




    public void getPost() {
        swipeRefresh.setRefreshing(false);
        tools.showLoading();

        restCall.user_get_post("user_get_post", categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PostResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tools.stopLoading();
                            tvNoData.setVisibility(View.GONE);
                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(PostActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(PostResponse postResponse) {
                        runOnUiThread(() -> {
                            tools.stopLoading();

                            if (postResponse != null && postResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                if (postResponse.getPostList() != null && postResponse.getPostList().size() > 0) {
                                    tvNoData.setVisibility(View.GONE);

                                    postAdapter = new PostAdapter(postResponse.getPostList(), PostActivity.this);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(PostActivity.this);
                                    rcvPost.setLayoutManager(layoutManager);
                                    rcvPost.setAdapter(postAdapter);
                                } else {
                                    tvNoData.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tools.stopLoading();
        getPost();
    }
}