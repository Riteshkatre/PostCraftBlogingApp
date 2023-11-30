package com.example.postcraft.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.postcraft.Adapter.CommentAdapter;
import com.example.postcraft.Adapter.MyPostCommentAdapter;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.CommentResponse;
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

    CardView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_comment);
        rcv=findViewById(R.id.rcv);
        back=findViewById(R.id.back);
        swipeRefresh=findViewById(R.id.swipeRefresh);
        sharedPreference=new SharedPreference(this);
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

                            Log.e("##", e.getLocalizedMessage());
                            Toast.makeText(MyPostCommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        runOnUiThread(() -> {


                            if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {

                                myPostCommentAdapter = new MyPostCommentAdapter(commentResponse.getCommentList(), MyPostCommentActivity.this);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MyPostCommentActivity.this);
                                rcv.setLayoutManager(layoutManager);
                                rcv.setAdapter(myPostCommentAdapter);

                            }
                        });
                    }

                });
    }

}