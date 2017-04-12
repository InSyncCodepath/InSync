package com.codepath.insync.utils;


import com.codepath.insync.models.retrofit.Audio;
import com.codepath.insync.models.retrofit.LoginCredential;
import com.codepath.insync.models.retrofit.Photo;
import com.codepath.insync.models.retrofit.RenderResponse;
import com.codepath.insync.models.retrofit.Video;
import com.codepath.insync.models.retrofit.VideoStatus;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface PicovicoService {
    static String PICOVICO_URL = "http://uapi-f1.picovico.com/v2.1/";

    @FormUrlEncoded
    @POST("login/app")
    public Call<LoginCredential> login(
            @Field(Constants.APP_ID_STR) String appId,
            @Field(Constants.APP_SECRET_STR) String appSecret
    );

    @FormUrlEncoded
    @POST("me/photos")
    public Call<Photo> uploadPhoto(
            @Header(Constants.ACCESS_KEY_HEADER) String accessKey,
            @Header(Constants.ACCESS_TOKEN_HEADER) String accessToken,
            @Field(Constants.SOURCE_STR) String source,
            @Field(Constants.URL_STR) String url,
            @Field(Constants.THUMBNAIL_URL_STR) String thumbnailUrl
    );

    @FormUrlEncoded
    @POST("me/musics")
    public Call<Audio> uploadAudio(
            @Header(Constants.ACCESS_KEY_HEADER) String accessKey,
            @Header(Constants.ACCESS_TOKEN_HEADER) String accessToken,
            @Field(Constants.URL_STR) String url,
            @Field(Constants.PREVIEW_URL_STR) String previewUrl
    );

    @FormUrlEncoded
    @POST("me/videos")
    public Call<Video> createVideo(
            @Header(Constants.ACCESS_KEY_HEADER) String accessKey,
            @Header(Constants.ACCESS_TOKEN_HEADER) String accessToken,
            @Field(Constants.NAME_STR) String videoName
    );

    @FormUrlEncoded
    @POST("me/videos/{"+Constants.VIDEO_ID_STR+"}")
    public Call<Video> addVideoAssets(
            @Path(Constants.VIDEO_ID_STR) String videoId,
            @Header(Constants.ACCESS_KEY_HEADER) String accessKey,
            @Header(Constants.ACCESS_TOKEN_HEADER) String accessToken,
            @Field(Constants.NAME_STR) String videoName,
            @Field(Constants.STYLE_STR) String style,
            //@Field(Constants.CREDIT_STR) String [][]credits,
            @Field(Constants.ASSETS_STR) JSONArray assets
    );

    @POST("me/videos/{"+Constants.VIDEO_ID_STR+"}/render")
    public Call<RenderResponse> renderVideo(
            @Path(Constants.VIDEO_ID_STR) String videoId,
            @Header(Constants.ACCESS_KEY_HEADER) String accessKey,
            @Header(Constants.ACCESS_TOKEN_HEADER) String accessToken
    );

    @GET("me/videos/{"+Constants.VIDEO_ID_STR+"}")
    public Call<VideoStatus> getVideo(
            @Path(Constants.VIDEO_ID_STR) String videoId,
            @Header(Constants.ACCESS_KEY_HEADER) String accessKey,
            @Header(Constants.ACCESS_TOKEN_HEADER) String accessToken
    );
}
