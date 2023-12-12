package com.example.postcraft.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.postcraft.Activities.CommentActivity;
import com.example.postcraft.NetworkResponse.Post;
import com.example.postcraft.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> postList;
    Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post model = postList.get(position);
        holder.userName.setText(model.getFirstName() + " " + model.getLastName());
        holder.tvDesc.setText(model.getPostDescription());

        if (model.getPostImage() != null && !model.getPostImage().isEmpty()) {
            holder.postImageLayout.setVisibility(View.VISIBLE);
            holder.postImage.setVisibility(View.VISIBLE);

            try {
                Glide.with(context).load(model.getPostImage())
                        .placeholder(null)
                        .error((RequestBuilder<Drawable>) null)
                        .into(holder.postImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.postImageLayout.setVisibility(View.GONE);
            holder.postImage.setVisibility(View.GONE);
        }


        holder.tvDesc.setText(model.getPostDescription());

        try {
            Glide.with(context).load(model.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.likeCount.setText(model.getPostDate());
        holder.commentCount.setText(String.valueOf(model.getCommentCount()));
        holder.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), CommentActivity.class);
                i.putExtra("CategoryId", model.getCategoryId());
                i.putExtra("PostId", model.getPostId());

                v.getContext().startActivity(i);

            }
        });

//***************************************************************************************************
        //FOR ON CLICK SHOW FULL SIZE IMAGE
        holder.postImage.setOnClickListener(v -> showImageInDialog(model.getPostImage()));
        holder.userProfile.setOnClickListener(v -> showProfileImageInDialog(model.getProfileImage()));
    }

    // display the image^&&& for using dialog_image_xml
    private void showImageInDialog(String imageUrl) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_post_image);

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
//************************************************************************************************
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
//        return postResponceList != null ? postResponceList.size() : 0;
        return postList.size();


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfile;
        TextView userName, tvDesc, likeCount, commentCount, likePlus;
        ImageView postImage, postLike, postComment;
        RelativeLayout postImageLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postImage = itemView.findViewById(R.id.postImage);
            postImageLayout = itemView.findViewById(R.id.postImageLayout);
            postComment = itemView.findViewById(R.id.postComment);/*
            likePlus = itemView.findViewById(R.id.likePlus);
            postLike = itemView.findViewById(R.id.postLike);*/

        }
    }
}
