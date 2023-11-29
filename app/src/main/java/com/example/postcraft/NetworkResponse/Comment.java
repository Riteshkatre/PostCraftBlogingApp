
package com.example.postcraft.NetworkResponse;

import java.io.Serializable;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment implements Serializable, Parcelable
{

    @SerializedName("comment_id")
    @Expose
    private String commentId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("comment_text")
    @Expose
    private String commentText;
    public final static Creator<Comment> CREATOR = new Creator<Comment>() {


        public Comment createFromParcel(android.os.Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return (new Comment[size]);
        }

    }
            ;
    private final static long serialVersionUID = -7356470349056049533L;

    @SuppressWarnings({
            "unchecked"
    })
    protected Comment(android.os.Parcel in) {
        this.commentId = ((String) in.readValue((String.class.getClassLoader())));
        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.postId = ((String) in.readValue((String.class.getClassLoader())));
        this.lastName = ((String) in.readValue((String.class.getClassLoader())));
        this.firstName = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.profileImage = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryId = ((String) in.readValue((String.class.getClassLoader())));
        this.commentText = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Comment() {
    }

    /**
     *
     * @param lastName
     * @param firstName
     * @param commentId
     * @param postId
     * @param profileImage
     * @param userId
     * @param commentText
     * @param email
     * @param categoryId
     */
    public Comment(String commentId, String userId, String postId, String lastName, String firstName, String email, String profileImage, String categoryId, String commentText) {
        super();
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.profileImage = profileImage;
        this.categoryId = categoryId;
        this.commentText = commentText;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(commentId);
        dest.writeValue(userId);
        dest.writeValue(postId);
        dest.writeValue(lastName);
        dest.writeValue(firstName);
        dest.writeValue(email);
        dest.writeValue(profileImage);
        dest.writeValue(categoryId);
        dest.writeValue(commentText);
    }

    public int describeContents() {
        return  0;
    }

}
