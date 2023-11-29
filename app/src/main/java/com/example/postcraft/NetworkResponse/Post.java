package com.example.postcraft.NetworkResponse;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable, Parcelable {

    public final static Creator<Post> CREATOR = new Creator<Post>() {


        public Post createFromParcel(android.os.Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return (new Post[size]);
        }

    };
    private final static long serialVersionUID = -8462922981840899057L;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("post_image")
    @Expose
    private String postImage;
    @SerializedName("post_description")
    @Expose
    private String postDescription;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("post_date")
    @Expose
    private String postDate;
    @SerializedName("like_count")
    @Expose
    private Integer likeCount;
    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;

    protected Post(android.os.Parcel in) {
        this.postId = ((String) in.readValue((String.class.getClassLoader())));
        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryId = ((String) in.readValue((String.class.getClassLoader())));
        this.firstName = ((String) in.readValue((String.class.getClassLoader())));
        this.lastName = ((String) in.readValue((String.class.getClassLoader())));
        this.postImage = ((String) in.readValue((String.class.getClassLoader())));
        this.postDescription = ((String) in.readValue((String.class.getClassLoader())));
        this.profileImage = ((String) in.readValue((String.class.getClassLoader())));
        this.postDate = ((String) in.readValue((String.class.getClassLoader())));
        this.likeCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.commentCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Post() {
    }

    public Post(String postId, String userId, String categoryId, String firstName, String lastName, String postImage, String postDescription, String profileImage, String postDate, Integer likeCount, Integer commentCount) {
        super();
        this.postId = postId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postImage = postImage;
        this.postDescription = postDescription;
        this.profileImage = profileImage;
        this.postDate = postDate;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(postId);
        dest.writeValue(userId);
        dest.writeValue(categoryId);
        dest.writeValue(firstName);
        dest.writeValue(lastName);
        dest.writeValue(postImage);
        dest.writeValue(postDescription);
        dest.writeValue(profileImage);
        dest.writeValue(postDate);
        dest.writeValue(likeCount);
        dest.writeValue(commentCount);
    }

    public int describeContents() {
        return 0;
    }

}
