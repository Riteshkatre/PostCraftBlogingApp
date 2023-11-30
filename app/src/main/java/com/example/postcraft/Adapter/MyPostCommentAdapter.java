package com.example.postcraft.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.postcraft.Activities.ReplayCommentActivity;
import com.example.postcraft.NetworkResponse.Comment;
import com.example.postcraft.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostCommentAdapter extends RecyclerView.Adapter<MyPostCommentAdapter.ViewHolder>{
    List<Comment> commentList;
    Context context;

    public MyPostCommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_post_comment, parent, false);
        return new MyPostCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment=commentList.get(position);
        holder.userName.setText(comment.getFirstName());
        holder.tvComment.setText(comment.getCommentText());
        try {
            Glide.with(context).load(comment.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvEmail.setText(comment.getEmail());
        holder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), ReplayCommentActivity.class);

                i.putExtra("CategoryId", comment.getCategoryId());
                i.putExtra("PostId",comment.getPostId());
                i.putExtra("UserName",comment.getFirstName());
                i.putExtra("comment",comment.getCommentText());
                i.putExtra("user_Profile",comment.getProfileImage());
                i.putExtra("comment_Id",comment.getCommentId());
                i.putExtra("email",comment.getEmail());
                v.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName, tvEmail, tvComment,tvReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvReply = itemView.findViewById(R.id.tvReply);


        }
    }
}
