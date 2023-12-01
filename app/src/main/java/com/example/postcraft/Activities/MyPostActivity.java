package com.example.postcraft.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.postcraft.Adapter.CategoryAdapter;
import com.example.postcraft.Adapter.MyPostAdapter;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.CategoryListResponce;
import com.example.postcraft.NetworkResponse.Post;
import com.example.postcraft.NetworkResponse.PostResponse;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.UserResponce;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPostActivity extends AppCompatActivity {

    RestCall restCall;
    Tools tools;

    RecyclerView rcv;

    SharedPreference sharedPreference;

    MyPostAdapter myPostAdapter;
    TextView tvNoData;
CardView back;
   SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        rcv=findViewById(R.id.rcv);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        sharedPreference=new SharedPreference(this);
        tools=new Tools(this);
        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        //myPostAdapter = new MyPostAdapter( new ArrayList<>(),MyPostActivity.this);
        tvNoData = findViewById(R.id.tvNoData);
        back = findViewById(R.id.back);
        tvNoData.setVisibility(View.VISIBLE);


        rcv.setAdapter(myPostAdapter);
        getMyPost();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyPost();

            }
        });
        back.setOnClickListener(v -> finish());


    }




    public void getMyPost() {
        tvNoData.setVisibility(View.VISIBLE);
        swipeRefresh.setRefreshing(false);
        tools.showLoading();
        restCall.get_my_post("get_my_post",sharedPreference.getStringvalue("USER_ID"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PostResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(() -> {
                            tvNoData.setVisibility(View.VISIBLE);
                            tools.stopLoading();
                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(MyPostActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(PostResponse postResponse) {
                        runOnUiThread(() -> {

                            tools.stopLoading();
                            if (postResponse != null
                                    && postResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)
                                    && postResponse.getPostList() != null
                                    && postResponse.getPostList().size() > 0) {
                                tvNoData.setVisibility(View.GONE);
                                myPostAdapter = new MyPostAdapter( postResponse.getPostList(),MyPostActivity.this);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(MyPostActivity.this);
                                rcv.setLayoutManager(layoutManager);
                                rcv.setAdapter(myPostAdapter);

                                myPostAdapter.SetUpInterface(new MyPostAdapter.PostClick() {
                                    @Override
                                    public void DeleteClick(Post post) {

                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyPostActivity.this);
                                        alertDialog.setTitle("Alert!!");
                                        alertDialog.setMessage("Are you sure, you want to delete  Post ");

                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deletePost(post.getPostId());
                                                dialogInterface.dismiss();
                                            }
                                        });

                                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        alertDialog.show();
                                    }


                                });
                            }else {
                                tvNoData.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
    }





    public  void deletePost(String postId){
        tools.showLoading();
        restCall.user_delete_post("user_delete_post",postId,sharedPreference.getStringvalue("USER_ID"))
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
                            Toast.makeText(MyPostActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }

                    @Override
                    public void onNext(UserResponce userResponce) {
                        tools.stopLoading();
                        if (userResponce.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)){
                            Toast.makeText(MyPostActivity.this, "delete successful", Toast.LENGTH_SHORT).show();
                            myPostAdapter.removePost(postId);
                        }

                    }
                });


    }








}