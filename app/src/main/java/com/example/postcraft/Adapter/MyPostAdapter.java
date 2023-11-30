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
import com.example.postcraft.Activities.CommentActivity;
import com.example.postcraft.Activities.MyPostCommentActivity;
import com.example.postcraft.Activities.ReplayCommentActivity;
import com.example.postcraft.NetworkResponse.Post;
import com.example.postcraft.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {
    List<Post> postList;
    Context context;
    PostClick postClick;

    public interface PostClick {
        void DeleteClick(Post post);
    }
    public void SetUpInterface(MyPostAdapter.PostClick postClick1){
        this.postClick = postClick1;
    }
    public MyPostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypost, parent, false);
        return new MyPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post model = postList.get(position);
        holder.userName.setText(model.getFirstName());
        holder.tvDesc.setText(model.getPostDescription());
        try {
            Glide.with(context).load(model.getPostImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.postImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glide.with(context).load(model.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.likeCount.setText(model.getPostDate());
        holder.commentCount.setText(String.valueOf(model.getCommentCount()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postClick.DeleteClick(model);

            }
        });
        holder.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), MyPostCommentActivity.class);
                i.putExtra("CategoryId", model.getCategoryId());
                i.putExtra("PostId",model.getPostId());
                v.getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName, tvDesc, likeCount, commentCount;
        ImageView postImage, postLike, postComment, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postImage = itemView.findViewById(R.id.postImage);
            postComment = itemView.findViewById(R.id.postComment);
            delete = itemView.findViewById(R.id.delete);



        }
    }
}
