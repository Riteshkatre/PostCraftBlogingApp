package com.example.postcraft.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.postcraft.NetworkResponse.CommentResponse;
import com.example.postcraft.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyCommentAdapter extends RecyclerView.Adapter<ReplyCommentAdapter.ViewHolder>{
    List<CommentResponse.Comment.Replycomment>replycommentList;
    Context context;

    public ReplyCommentAdapter(List<CommentResponse.Comment.Replycomment> replycommentList, Context context) {
        this.replycommentList = replycommentList;
        this.context = context;
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_comment, parent, false);
        return new ReplyCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       CommentResponse.Comment.Replycomment model=replycommentList.get(position);
        holder.userName.setText(model.getFirstName());
        holder.tvComment.setText(model.getReplyComment());
        try {
            Glide.with(context).load(model.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvEmail.setText(model.getEmail());




    }

    @Override
    public int getItemCount() {
        return replycommentList != null ? replycommentList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName, tvEmail, tvComment;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvComment = itemView.findViewById(R.id.tvComment);





        }
    }
}
