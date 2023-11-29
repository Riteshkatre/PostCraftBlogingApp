
package com.example.postcraft.NetworkResponse;

import java.io.Serializable;
import java.util.List;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


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
    public final static Creator<CommentResponse> CREATOR = new Creator<CommentResponse>() {


        public CommentResponse createFromParcel(android.os.Parcel in) {
            return new CommentResponse(in);
        }

        public CommentResponse[] newArray(int size) {
            return (new CommentResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -8208574657745158382L;

    @SuppressWarnings({
            "unchecked"
    })
    protected CommentResponse(android.os.Parcel in) {
        in.readList(this.commentList, (com.example.postcraft.NetworkResponse.Comment.class.getClassLoader()));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
    }


    public CommentResponse(List<Comment> commentList, String message, String status) {
        super();
        this.commentList = commentList;
        this.message = message;
        this.status = status;
    }

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

}
