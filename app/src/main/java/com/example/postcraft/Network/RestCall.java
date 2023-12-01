package com.example.postcraft.Network;


import com.example.postcraft.NetworkResponse.CategoryListResponce;
import com.example.postcraft.NetworkResponse.CommentResponse;
import com.example.postcraft.NetworkResponse.LoginResponce;
import com.example.postcraft.NetworkResponse.PostResponse;
import com.example.postcraft.NetworkResponse.ReplyCommentResponse;
import com.example.postcraft.NetworkResponse.UserResponce;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Single;

public interface RestCall {



    @Multipart
    @POST("user.php")
    Single<UserResponce> user_registration(
            @Part("tag") RequestBody tag,
            @Part("first_name")RequestBody first_name,
            @Part("last_name")RequestBody last_name,
            @Part("email")RequestBody email,
            @Part MultipartBody.Part profile_image,
            @Part("password")RequestBody password);


    @FormUrlEncoded
    @POST("user.php")
    Single<LoginResponce> user_login(
            @Field("tag") String tag,
            @Field("email") String email,
            @Field("password") String password);



    @FormUrlEncoded
    @POST("categoryControllerAPI.php")
    Single<CategoryListResponce> getCategory(
            @Field("tag") String tag);


    @Multipart
    @POST("user.php")
    Single<LoginResponce> user_edit_profile(
            @Part("tag") RequestBody tag,
            @Part("user_id")RequestBody user_id,
            @Part("first_name")RequestBody first_name,
            @Part("last_name")RequestBody last_name,
            @Part MultipartBody.Part profile_image);


    @FormUrlEncoded
    @POST("post.php")
    Single<PostResponse> user_get_post(
            @Field("tag") String tag,
            @Field("category_id") String category_id);
    @FormUrlEncoded
    @POST("post.php")
    Single<PostResponse> get_my_post(
            @Field("tag") String tag,
            @Field("user_id") String user_id);
    @FormUrlEncoded
    @POST("post.php")
    Single<UserResponce> user_delete_post(
            @Field("tag") String tag,
            @Field("post_id") String post_id,
            @Field("user_id") String user_id);

    @Multipart
    @POST("post.php")
    Single<UserResponce> user_post(
            @Part("tag") RequestBody tag,
            @Part("user_id")RequestBody user_id,
            @Part("category_id")RequestBody category_id,
            @Part("post_description")RequestBody post_description,
            @Part MultipartBody.Part post_image);

    @FormUrlEncoded
    @POST("comment.php")
    Single<CommentResponse> get_user_comment(
            @Field("tag") String tag,
            @Field("user_id") String user_id,
            @Field("category_id") String category_id,
            @Field("post_id") String post_id);

    @FormUrlEncoded
    @POST("comment.php")
    Single<CommentResponse> user_post_comment(
            @Field("tag") String tag,
            @Field("post_id") String post_id,
            @Field("user_id") String user_id,
            @Field("category_id") String category_id,
            @Field("comment_text") String comment_text);
    @FormUrlEncoded
    @POST("reply_comment.php")
    Single<UserResponce> user_post_reply_comment(
            @Field("tag") String tag,
            @Field("comment_id") String comment_id,
            @Field("category_id") String category_id,
            @Field("post_id") String post_id,
            @Field("reply_comment") String reply_comment,
            @Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("reply_comment.php")
    Single<CommentResponse> get_reply_comment(
            @Field("tag") String tag);





}
