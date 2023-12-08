package com.example.postcraft.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.postcraft.Adapter.MyPostCommentAdapter;
import com.example.postcraft.Adapter.ReplayMyCommentAdapter;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.CommentResponse;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPostCommentActivity extends AppCompatActivity {
    RecyclerView rcv;
    SwipeRefreshLayout swipeRefresh;

    RestCall restCall;

    SharedPreference sharedPreference;

    String categoryId, PostId;

    Intent i;

    MyPostCommentAdapter myPostCommentAdapter;

    ReplayMyCommentAdapter replayMyCommentAdapter;

    TextView tvNoData;

    CardView back;

    Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_my_post_comment);
        rcv = findViewById(R.id.rcv);
        back = findViewById(R.id.back);
        tvNoData = findViewById(R.id.tvNoData);
        tvNoData.setVisibility(View.VISIBLE);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        sharedPreference = new SharedPreference(this);
        tools=new Tools(this);
        i = getIntent();
        categoryId = i.getStringExtra("CategoryId");
        PostId = i.getStringExtra("PostId");
        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        getComment();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getComment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    public void getComment() {
        swipeRefresh.setRefreshing(false);
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
                            tools.stopLoading();


                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(MyPostCommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        runOnUiThread(() -> {


                            if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                tvNoData.setVisibility(View.GONE);
                                tools.stopLoading();





                                myPostCommentAdapter = new MyPostCommentAdapter(commentResponse.getCommentList(), MyPostCommentActivity.this);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MyPostCommentActivity.this);
                                rcv.setLayoutManager(layoutManager);
                                rcv.setAdapter(myPostCommentAdapter);
                                getReplyComment();

                                myPostCommentAdapter.SetUpInterface(new MyPostCommentAdapter.PostClick() {
                                    @Override
                                    public void DeleteClick(CommentResponse.Comment post) {

                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyPostCommentActivity.this);
                                        alertDialog.setTitle("Alert!!");
                                        alertDialog.setMessage("Are you sure, you want to delete  Comment ");

                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DeleteComment(post.getCommentId());
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

                            }
                        });
                    }

                });
    }


    public void getReplyComment() {

        swipeRefresh.setRefreshing(false);

        restCall.get_reply_comment("get_reply_comment")
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
                            Toast.makeText(MyPostCommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        runOnUiThread(() -> {


                            if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                tvNoData.setVisibility(View.GONE);
                                tools.stopLoading();


                            }
                        });
                    }

                });
    }


    public  void  DeleteComment(String CommentId){
        swipeRefresh.setRefreshing(true);
        restCall.user_comment_delete("user_comment_delete",sharedPreference.getStringvalue("USER_ID"),CommentId,PostId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentResponse>() {
                    @Override
                    public void onCompleted() {
                        getComment();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(MyPostCommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                            myPostCommentAdapter.removeCommentById(CommentId);
                            myPostCommentAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                            getComment();

                        }

                    }
                });
    }

}