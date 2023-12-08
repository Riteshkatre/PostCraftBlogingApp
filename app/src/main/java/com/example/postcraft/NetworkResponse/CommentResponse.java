
package com.example.postcraft.NetworkResponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class CommentResponse implements Serializable, Parcelable
{

    @SerializedName("commentList")
    @Expose
    private List<Comment> commentList;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    private final static long serialVersionUID = -8208574657745158382L;




    public CommentResponse(List<Comment> commentList, String message, String status) {
        super();
        this.commentList = commentList;
        this.message = message;
        this.status = status;
    }

    protected CommentResponse(Parcel in) {
        message = in.readString();
        status = in.readString();
    }

    public static final Creator<CommentResponse> CREATOR = new Creator<CommentResponse>() {
        @Override
        public CommentResponse createFromParcel(Parcel in) {
            return new CommentResponse(in);
        }

        @Override
        public CommentResponse[] newArray(int size) {
            return new CommentResponse[size];
        }
    };

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
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
        dest.writeList(commentList);
        dest.writeValue(message);
        dest.writeValue(status);
    }

    public int describeContents() {
        return  0;
    }

    public class Comment implements Serializable {

        @SerializedName("comment_id")
        @Expose
        private String commentId;
        @SerializedName("post_id")
        @Expose
        private String postId;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("category_id")
        @Expose
        private String categoryId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("comment_text")
        @Expose
        private String commentText;
        @SerializedName("reply_comment_count")
        @Expose
        private Integer replyCommentCount;
        @SerializedName("replycommentList")
        @Expose
        private List<Replycomment> replycommentList;

        private boolean isReplyVisible;

        // ... other fields, constructors, getters, and setters

        public boolean isReplyVisible() {
            return isReplyVisible;
        }

        public void setReplyVisible(boolean replyVisible) {
            isReplyVisible = replyVisible;
        }


        ;
        private final static long serialVersionUID = -1500483560038013002L;


        public Comment(String commentId, String postId, String firstName, String lastName, String email, String profileImage, String categoryId, String commentText, Integer replyCommentCount, List<Replycomment> replycommentList) {
            super();
            this.commentId = commentId;
            this.postId = postId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.profileImage = profileImage;
            this.categoryId = categoryId;
            this.commentText = commentText;
            this.replyCommentCount = replyCommentCount;
            this.replycommentList = replycommentList;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCommentText() {
            return commentText;
        }

        public void setCommentText(String commentText) {
            this.commentText = commentText;
        }

        public Integer getReplyCommentCount() {
            return replyCommentCount;
        }

        public void setReplyCommentCount(Integer replyCommentCount) {
            this.replyCommentCount = replyCommentCount;
        }

        public List<Replycomment> getReplycommentList() {
            return replycommentList;
        }

        public void setReplycommentList(List<Replycomment> replycommentList) {
            this.replycommentList = replycommentList;
        }

        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeValue(commentId);
            dest.writeValue(postId);
            dest.writeValue(firstName);
            dest.writeValue(lastName);
            dest.writeValue(email);
            dest.writeValue(profileImage);
            dest.writeValue(categoryId);
            dest.writeValue(commentText);
            dest.writeValue(replyCommentCount);
            dest.writeList(replycommentList);
        }

        public int describeContents() {
            return 0;
        }


        public class Replycomment implements Serializable {

            @SerializedName("post_id")
            @Expose
            private String postId;
            @SerializedName("comment_id")
            @Expose
            private String commentId;
            @SerializedName("reply_comment_id")
            @Expose
            private String replyCommentId;
            @SerializedName("user_id")
            @Expose
            private String userId;
            @SerializedName("first_name")
            @Expose
            private String firstName;
            @SerializedName("email")
            @Expose
            private String email;
            @SerializedName("profile_image")
            @Expose
            private String profileImage;
            @SerializedName("reply_comment")
            @Expose
            private String replyComment;

            private final static long serialVersionUID = 8422045364575005588L;

            @SuppressWarnings({
                    "unchecked"
            })
            protected Replycomment(android.os.Parcel in) {
                this.postId = ((String) in.readValue((String.class.getClassLoader())));
                this.commentId = ((String) in.readValue((String.class.getClassLoader())));
                this.replyCommentId = ((String) in.readValue((String.class.getClassLoader())));
                this.userId = ((String) in.readValue((String.class.getClassLoader())));
                this.firstName = ((String) in.readValue((String.class.getClassLoader())));
                this.email = ((String) in.readValue((String.class.getClassLoader())));
                this.profileImage = ((String) in.readValue((String.class.getClassLoader())));
                this.replyComment = ((String) in.readValue((String.class.getClassLoader())));
            }

            /**
             * No args constructor for use in serialization
             */
            public Replycomment() {
            }

            /**
             * @param firstName
             * @param replyCommentId
             * @param replyComment
             * @param commentId
             * @param postId
             * @param profileImage
             * @param userId
             * @param email
             */
            public Replycomment(String postId, String commentId, String replyCommentId, String userId, String firstName, String email, String profileImage, String replyComment) {
                super();
                this.postId = postId;
                this.commentId = commentId;
                this.replyCommentId = replyCommentId;
                this.userId = userId;
                this.firstName = firstName;
                this.email = email;
                this.profileImage = profileImage;
                this.replyComment = replyComment;
            }

            public String getPostId() {
                return postId;
            }

            public void setPostId(String postId) {
                this.postId = postId;
            }

            public String getCommentId() {
                return commentId;
            }

            public void setCommentId(String commentId) {
                this.commentId = commentId;
            }

            public String getReplyCommentId() {
                return replyCommentId;
            }

            public void setReplyCommentId(String replyCommentId) {
                this.replyCommentId = replyCommentId;
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

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getProfileImage() {
                return profileImage;
            }

            public void setProfileImage(String profileImage) {
                this.profileImage = profileImage;
            }

            public String getReplyComment() {
                return replyComment;
            }

            public void setReplyComment(String replyComment) {
                this.replyComment = replyComment;
            }

            public void writeToParcel(android.os.Parcel dest, int flags) {
                dest.writeValue(postId);
                dest.writeValue(commentId);
                dest.writeValue(replyCommentId);
                dest.writeValue(userId);
                dest.writeValue(firstName);
                dest.writeValue(email);
                dest.writeValue(profileImage);
                dest.writeValue(replyComment);
            }

            public int describeContents() {
                return 0;
            }
        }


    }
}
