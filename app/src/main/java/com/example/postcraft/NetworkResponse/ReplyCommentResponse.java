package com.example.postcraft.NetworkResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReplyCommentResponse {
    @SerializedName("replycommentList")
    @Expose
    private List<Replycomment> replycommentList;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    public final static Parcelable.Creator<CommentResponse> CREATOR = new Parcelable.Creator<CommentResponse>() {


        public CommentResponse createFromParcel(android.os.Parcel in) {
            return new CommentResponse(in);
        }

        public CommentResponse[] newArray(int size) {
            return (new CommentResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = 3291181204106026318L;




    public ReplyCommentResponse(List<Replycomment> replycommentList, String message, String status) {
        super();
        this.replycommentList = replycommentList;
        this.message = message;
        this.status = status;
    }

    public List<Replycomment> getReplycommentList() {
        return replycommentList;
    }

    public void setReplycommentList(List<Replycomment> replycommentList) {
        this.replycommentList = replycommentList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeList(replycommentList);
        dest.writeValue(message);
        dest.writeValue(status);
    }

    public int describeContents() {
        return  0;
    }

    public class Replycomment implements Serializable
    {

        @SerializedName("reply_comment_id")
        @Expose
        private String replyCommentId;
        @SerializedName("comment_id")
        @Expose
        private String commentId;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("comment_text")
        @Expose
        private String commentText;
        @SerializedName("reply_comment")
        @Expose
        private String replyComment;

                ;
        private final static long serialVersionUID = -3413529831445754939L;

        @SuppressWarnings({
                "unchecked"
        })
        protected Replycomment(android.os.Parcel in) {
            this.replyCommentId = ((String) in.readValue((String.class.getClassLoader())));
            this.commentId = ((String) in.readValue((String.class.getClassLoader())));
            this.userId = ((String) in.readValue((String.class.getClassLoader())));
            this.firstName = ((String) in.readValue((String.class.getClassLoader())));
            this.lastName = ((String) in.readValue((String.class.getClassLoader())));
            this.profileImage = ((String) in.readValue((String.class.getClassLoader())));
            this.commentText = ((String) in.readValue((String.class.getClassLoader())));
            this.replyComment = ((String) in.readValue((String.class.getClassLoader())));
        }


        public Replycomment(String replyCommentId, String commentId, String userId, String firstName, String lastName, String profileImage, String commentText, String replyComment) {
            super();
            this.replyCommentId = replyCommentId;
            this.commentId = commentId;
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.profileImage = profileImage;
            this.commentText = commentText;
            this.replyComment = replyComment;
        }




        public String getReplyCommentId() {
            return replyCommentId;
        }

        public void setReplyCommentId(String replyCommentId) {
            this.replyCommentId = replyCommentId;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getCommentText() {
            return commentText;
        }

        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }

        public String getReplyComment() {
            return replyComment;
        }

        public void setReplyComment(String replyComment) {
            this.replyComment = replyComment;
        }

        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeValue(replyCommentId);
            dest.writeValue(commentId);
            dest.writeValue(userId);
            dest.writeValue(firstName);
            dest.writeValue(lastName);
            dest.writeValue(profileImage);
            dest.writeValue(commentText);
            dest.writeValue(replyComment);
        }

        public int describeContents() {
            return  0;
        }

    }

}
