package com.example.postcraft.Adapter;

import android.app.Dialog;
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

public class MyPostCommentAdapter extends RecyclerView.Adapter<MyPostCommentAdapter.ViewHolder>{
    List<CommentResponse.Comment> commentList;
    Context context;
    PostClick postClick;



    public interface PostClick {
        void DeleteClick(CommentResponse.Comment post);
    }
    public void SetUpInterface(MyPostCommentAdapter.PostClick postClick1){
        this.postClick = postClick1;
    }
    public MyPostCommentAdapter(List<CommentResponse.Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    public void removeCommentById(String commentId) {
        for (CommentResponse.Comment comment : commentList) {
            if (comment.getCommentId().equals(commentId)) {
                commentList.remove(comment);
                break;
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_post_comment, parent, false);
        return new MyPostCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentResponse.Comment comment=commentList.get(position);
        holder.userName.setText(comment.getFirstName());
        holder.tvComment.setText(comment.getCommentText());
        holder.tvEmail.setText(comment.getEmail());
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        ReplayMyCommentAdapter catalogProductAdapter = new ReplayMyCommentAdapter(comment.getReplycommentList(),context);
        holder.rcvReply.setLayoutManager(layoutManager);
        holder.rcvReply.setAdapter(catalogProductAdapter);

        holder.tvReplyCount.setText(String.valueOf(comment.getReplyCommentCount()));
        holder.rcvReply.setVisibility(comment.isReplyVisible() ? View.VISIBLE : View.GONE);

        holder.tvReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setReplyVisible(!comment.isReplyVisible());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        holder.comment.setOnClickListener(v -> {
            comment.setReplyVisible(!comment.isReplyVisible());
            notifyItemChanged(holder.getAdapterPosition());

        });
        holder.delete.setOnClickListener(v -> postClick.DeleteClick(comment));


        holder.userProfile.setOnClickListener(v -> showProfileImageInDialog(comment.getProfileImage()));
    }

    private void showProfileImageInDialog(String imageUrl) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_pfofile_image);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        try {
            Glide.with(context).load(imageUrl).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Close the dialog
        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        ImageView comment,delete;
        TextView userName, tvEmail, tvComment,tvReply,tvReplyCount;
        RecyclerView rcvReply;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvReply = itemView.findViewById(R.id.tvReply);
            rcvReply = itemView.findViewById(R.id.rcvReply);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            comment = itemView.findViewById(R.id.comment);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
