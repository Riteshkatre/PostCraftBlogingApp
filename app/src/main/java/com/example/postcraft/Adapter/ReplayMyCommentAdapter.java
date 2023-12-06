package com.example.postcraft.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class ReplayMyCommentAdapter extends RecyclerView.Adapter<ReplayMyCommentAdapter.ViewHolder> {
    private List<CommentResponse.Comment.Replycomment> replycommentList;
    private Context context;
    private RestCall restCall;
    private SharedPreference sharedPreference;

    public ReplayMyCommentAdapter(List<CommentResponse.Comment.Replycomment> replycommentList, Context context) {
        this.replycommentList = replycommentList;
        this.context = context;
        this.sharedPreference = new SharedPreference(context);
        this.restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_my_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentResponse.Comment.Replycomment model = replycommentList.get(position);
        holder.userName.setText(model.getFirstName());
        holder.tvComment.setText(model.getReplyComment());
        try {
            Glide.with(context).load(model.getProfileImage()).placeholder(R.drawable.background).error(R.drawable.ic_launcher_foreground).into(holder.userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvEmail.setText(model.getEmail());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    DeleteReplyCommentApiCall(
                            model.getReplyCommentId(),
                            model.getCommentId(),
                            model.getPostId(),
                            adapterPosition
                    );
                }
            }
        });
    }

    private void DeleteReplyCommentApiCall(String ReplyCommentId, String CommentId, String PostId, int position) {
        if (restCall == null) {
            restCall = RestClient.createService(RestCall.class, VeriableBag.BASE_URL, VeriableBag.API_KEY);
        }

        restCall.user_reply_comment_delete("user_reply_comment_delete", ReplyCommentId, sharedPreference.getStringvalue("USER_ID"), CommentId, PostId)
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
                        if (commentResponse != null && commentResponse.getStatus().equalsIgnoreCase(VeriableBag.SUCCESS_CODE)) {
                            removeItem(position);
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void removeItem(int position) {
        if (position >= 0 && position < replycommentList.size()) {
            replycommentList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, replycommentList.size());
        }
    }

    @Override
    public int getItemCount() {
        return replycommentList != null ? replycommentList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userProfile;
        private TextView userName, tvEmail, tvComment;
        private ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvComment = itemView.findViewById(R.id.tvComment);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
