package com.example.postcraft.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.Tools;
import com.example.postcraft.NetworkResponse.UserResponce;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReplayCommentActivity extends AppCompatActivity {
    EditText editTextReply;
    ImageView buttonAddReply;

    CircleImageView userProfile;

    TextView userName,tvEmail,tvComment;

    Intent i;

    String categoryId,postId,Name,comment,UserProfile,CommentId,Email;
    RestCall restCall;

    Tools tools;

    CardView back;
    SharedPreference sharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay_comment_actity);
        userName=findViewById(R.id.userName);
        tvEmail=findViewById(R.id.tvEmail);
        tvComment=findViewById(R.id.tvComment);
        userProfile=findViewById(R.id.userProfile);
        buttonAddReply=findViewById(R.id.buttonAddReply);
        editTextReply=findViewById(R.id.editTextReply);
        back=findViewById(R.id.back);

        i = getIntent();
        categoryId=i.getStringExtra("CategoryId");
        postId=i.getStringExtra("PostId");
        Name=i.getStringExtra("UserName");
        comment=i.getStringExtra("comment");
        UserProfile=i.getStringExtra("user_Profile");
        CommentId=i.getStringExtra("comment_Id");
        Email=i.getStringExtra("email");


        userName.setText(Name);
        tvComment.setText(comment);
        tvEmail.setText(Email);
        Glide.with(this).load(UserProfile).error(R.drawable.users).into(userProfile);


        restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        tools=new Tools(this);

        sharedPreference = new SharedPreference(ReplayCommentActivity.this);

        buttonAddReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_post_reply_comment();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    public void user_post_reply_comment() {


        String ReplayCommentText=editTextReply.getText().toString().trim();

        if (ReplayCommentText.isEmpty()) {
            editTextReply.setError("Comment cannot be empty");
            editTextReply.requestFocus();
            tools.stopLoading();
            return;
        }


        restCall.user_post_reply_comment("user_post_reply_comment",CommentId ,categoryId,postId, ReplayCommentText,sharedPreference.getStringvalue("USER_ID"))
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
                            Toast.makeText(ReplayCommentActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onNext(UserResponce commentResponse) {
                        runOnUiThread(() -> {
                            tools.stopLoading();

                            if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                editTextReply.setText("");
                                finish();

                            }
                        });
                    }

                });
    }

}