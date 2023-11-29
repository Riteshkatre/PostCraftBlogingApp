package com.example.postcraft.NetworkResponse;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostResponse implements Serializable, Parcelable {

    public final static Creator<PostResponse> CREATOR = new Creator<PostResponse>() {


        public PostResponse createFromParcel(android.os.Parcel in) {
            return new PostResponse(in);
        }

        public PostResponse[] newArray(int size) {
            return (new PostResponse[size]);
        }

    };
    private final static long serialVersionUID = -5210811151639765634L;
    @SerializedName("postList")
    @Expose
    private List<Post> postList;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    protected PostResponse(android.os.Parcel in) {
        in.readList(this.postList, (com.example.postcraft.NetworkResponse.Post.class.getClassLoader()));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PostResponse() {
    }

    public PostResponse(List<Post> postList, String message, String status) {
        super();
        this.postList = postList;
        this.message = message;
        this.status = status;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
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
        dest.writeList(postList);
        dest.writeValue(message);
        dest.writeValue(status);
    }

    public int describeContents() {
        return 0;
    }

}
