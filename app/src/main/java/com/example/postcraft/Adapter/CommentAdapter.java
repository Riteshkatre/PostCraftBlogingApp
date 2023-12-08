package com.example.postcraft.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.postcraft.Activities.ReplayCommentActivity;
import com.example.postcraft.Activities.SharedPreference;
import com.example.postcraft.Network.RestCall;
import com.example.postcraft.Network.RestClient;
import com.example.postcraft.NetworkResponse.CommentResponse;
import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<CommentResponse.Comment> commentList;
    private Context context;
    private String currentUserID;
    private RestCall restCall;
    private SharedPreference sharedPreference;



    public CommentAdapter(List<CommentResponse.Comment> commentList, Context context, String currentUserID) {
        this.commentList = commentList;
        this.context = context;
        this.currentUserID = currentUserID;
        this.sharedPreference = new SharedPreference(context);
        this.restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentResponse.Comment comment = commentList.get(position);
        holder.userName.setText(comment.getFirstName() + " " + comment.getLastName());
        holder.tvComment.setText(comment.getCommentText());
        try {
            Glide.with(context).load(comment.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ReplayCommentActivity.class);

                i.putExtra("CategoryId", comment.getCategoryId());
                i.putExtra("PostId", comment.getPostId());
                i.putExtra("UserName", comment.getFirstName());
                i.putExtra("comment", comment.getCommentText());
                i.putExtra("user_Profile", comment.getProfileImage());
                i.putExtra("comment_Id", comment.getCommentId());
                i.putExtra("email", comment.getEmail());

                v.getContext().startActivity(i);
            }
        });
        holder.tvEmail.setText(comment.getEmail());
        holder.tvReplyCount.setText(String.valueOf(comment.getReplyCommentCount()));
        holder.rcvReply.setVisibility(comment.isReplyVisible() ? View.VISIBLE : View.GONE);

        holder.tvReplyCount.setOnClickListener(v -> {
            comment.setReplyVisible(!comment.isReplyVisible());
            notifyItemChanged(holder.getAdapterPosition());
        });
        holder.comment.setOnClickListener(v -> {
            comment.setReplyVisible(!comment.isReplyVisible());
            notifyItemChanged(holder.getAdapterPosition());
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        ReplyCommentAdapter replyCommentAdapter = new ReplyCommentAdapter(comment.getReplycommentList(), context,sharedPreference.getStringvalue("USER_ID"));
        holder.rcvReply.setLayoutManager(layoutManager);
        holder.rcvReply.setAdapter(replyCommentAdapter);

        boolean isCurrentUserComment = comment.getUserId().equals(currentUserID);

        // Set visibility of delete button based on user ownership
        holder.delete.setVisibility(isCurrentUserComment ? View.VISIBLE : View.GONE);

        // Set click listener for delete button
        holder.delete.setOnClickListener(v -> {
            if (isCurrentUserComment) {

                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    DeleteComment(

                            comment.getCommentId(),
                            comment.getPostId(),
                            adapterPosition
                    );
                }
            }
        });
    }



    public  void  DeleteComment(String CommentId,String PostId, int position){
        if (restCall == null) {
            restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        }
        restCall.user_comment_delete("user_comment_delete",sharedPreference.getStringvalue("USER_ID"),CommentId,PostId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Api Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(CommentResponse commentResponse) {
                        if (commentResponse != null) {
                            Log.d("DeleteComment", "Response: " + commentResponse.toString());
                            if (commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                                removeItem(position);
                            } else {
                                Log.e("DeleteComment", "Error: " + commentResponse.getMessage());
                                Toast.makeText(context, "Error deleting comment", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });
    }
    private void removeItem(int position) {
        if (position >= 0 && position < commentList.size()) {
            commentList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, commentList.size());
        }
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName, tvComment, tvEmail, tvReply, tvReplyCount;
        ImageView comment, delete;
        RecyclerView rcvReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvReply = itemView.findViewById(R.id.tvReply);
            rcvReply = itemView.findViewById(R.id.rcvReply);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            comment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
