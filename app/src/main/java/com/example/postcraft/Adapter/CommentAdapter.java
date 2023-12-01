package com.example.postcraft.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.postcraft.Activities.ReplayCommentActivity;
import com.example.postcraft.NetworkResponse.CommentResponse;
import com.example.postcraft.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<CommentResponse.Comment> commentList;
    Context context;


    public CommentAdapter(List<CommentResponse.Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentResponse.Comment comment=commentList.get(position);
        holder.userName.setText(comment.getFirstName());
        holder.tvComment.setText(comment.getCommentText());
        try {
            Glide.with(context).load(comment.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        ReplyCommentAdapter catalogProductAdapter = new ReplyCommentAdapter(comment.getReplycommentList(),context);
        holder.rcvReply.setLayoutManager(layoutManager);
        holder.rcvReply.setAdapter(catalogProductAdapter);





    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName,tvComment,tvEmail,tvReply,tvReplyCount;
        ImageView comment;
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

        }
    }
}
