package com.codepath.insync.utils;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.codepath.insync.R;
import com.codepath.insync.listeners.OnVideoCreateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Music;
import com.codepath.insync.models.retrofit.Audio;
import com.codepath.insync.models.retrofit.LoginCredential;
import com.codepath.insync.models.retrofit.Photo;
import com.codepath.insync.models.retrofit.RenderResponse;
import com.codepath.insync.models.retrofit.Video;
import com.codepath.insync.models.retrofit.VideoStatus;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MediaClient {
    private static String TAG = "MediaClient";
    private static int RENDER_SUCCESS = 7101;
    public static int VIDEO_FETCH_DELAY = 5000;
    private static PicovicoService service;
    LoginCredential loginCredential;
    private List<Photo> photos;
    Audio audio;
    Video video;
    Music music;
    Event event;
    List<ParseFile> images;
    String theme;
    Context context;
    VideoStatus videoStatus;
    OnVideoCreateListener videoCreateListener;

    public MediaClient(OnVideoCreateListener listener, Context context, Event event, List<ParseFile> images, String theme) {
        this.videoCreateListener = listener;
        this.context = context;
        this.event = event;
        this.images = images;
        this.theme = theme;
    }

    private void initPivocoService() {
        if (service == null) {
            OkHttpClient client = new OkHttpClient();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            Gson gson = gsonBuilder.create();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(PicovicoService.PICOVICO_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service = retrofit.create(PicovicoService.class);
        }
    }
    public String createHighlights() {
        initPivocoService();
        loginCredential = LoginCredential.getLoginCredential();
        if (loginCredential == null) {
            login();
        } else {
            //uploadPhoto();
            getVideo();
        }
        return "";
    }

    private void login() {
        Call<LoginCredential> loginCredentialCall = service.login(
                context.getResources().getString(R.string.picovico_app_id),
                context.getResources().getString(R.string.picovico_app_secret));

        Callback<LoginCredential> loginCredentialCallback = new Callback<LoginCredential>() {
            @Override
            public void onResponse(Call<LoginCredential> call, Response<LoginCredential> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Login failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());

                } else {
                    LoginCredential.setLoginCredential(response.body());
                    loginCredential = LoginCredential.getLoginCredential();
                    //uploadPhoto();
                    getVideo();
                }

            }

            @Override
            public void onFailure(Call<LoginCredential> call, Throwable t) {
                Log.e(
                        TAG,
                        "Login failed with error: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };

        loginCredentialCall.enqueue(loginCredentialCallback);
    }

    private void uploadPhoto() {
        photos = new ArrayList<>();
        final int[] photoCount = {0};

        Callback<Photo> photoCallback = new Callback<Photo>() {
            @Override
            public void onResponse(Call<Photo> call, Response<Photo> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Photo upload failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());
                } else {
                    photos.add(response.body());
                }
                photoCount[0]++;
                if (photoCount[0] == images.size()) {
                    createAudio();
                }

            }

            @Override
            public void onFailure(Call<Photo> call, Throwable t) {
                Log.e(
                        TAG,
                        "Photo upload failed with error: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };

        for (ParseFile image: images) {
            Call<Photo> photoCall = service.uploadPhoto(
                    loginCredential.getAccessKey(),
                    loginCredential.getAccessToken(),
                    "google",
                    image.getUrl(),
                    image.getUrl()
                    );
            photoCall.enqueue(photoCallback);
        }
    }

    private void createAudio() {

        final Callback<Audio> audioCallback = new Callback<Audio>() {
            @Override
            public void onResponse(Call<Audio> call, Response<Audio> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Audio creation failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());
                } else {
                    audio = response.body();
                    createVideo();
                }
            }

            @Override
            public void onFailure(Call<Audio> call, Throwable t) {
                Log.e(
                        TAG,
                        "Audio creation failed with error: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };


        Music.findMusic(theme, new FindCallback<Music>() {
            @Override
            public void done(List<Music> musics, ParseException e) {
                if (e == null) {
                    music = musics.get(0);
                    String audioUrl = music.getAudio().getUrl();
                    Call<Audio> audioCall = service.uploadAudio(
                            loginCredential.getAccessKey(),
                            loginCredential.getAccessToken(),
                            audioUrl,
                            audioUrl
                    );
                    audioCall.enqueue(audioCallback);
                } else {
                    Log.e(TAG, "Music lookup failed with error: "+e.getLocalizedMessage());
                    videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, e.getLocalizedMessage());
                }
            }
        });


    }
    private void createVideo() {
        Call<Video> videoCall = service.createVideo(
                loginCredential.getAccessKey(),
                loginCredential.getAccessToken(),
                event.getObjectId()+"_"+event.getName()
        );

        Callback<Video> videoCallback = new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Video creation failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());
                } else {
                    video = response.body();
                    addVideoAssets();
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e(
                        TAG,
                        "Video creation failed with error: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };

        videoCall.enqueue(videoCallback);
    }

    private void addVideoAssets() {
        String [][]credits = {{"Thank you", "for coming and making it so special"}};
        JSONArray assets = new JSONArray();

        int endTime = Constants.CLIP_DURATION;

        for (Photo photo: photos) {
            JSONObject photoAsset = new JSONObject();
            try {
                photoAsset.put(Constants.START_TIME_STR, endTime - Constants.CLIP_DURATION);
                photoAsset.put(Constants.END_TIME_STR, endTime);
                photoAsset.put(Constants.ASSET_ID_STR, photo.getId());
                photoAsset.put(Constants.NAME_STR, Constants.IMAGE_STR);
                assets.put(photoAsset);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            endTime += Constants.CLIP_DURATION;
        }

        JSONObject audioAsset = new JSONObject();
        try {
            audioAsset.put(Constants.START_TIME_STR, 0);
            audioAsset.put(Constants.END_TIME_STR, music.getDuration());
            audioAsset.put(Constants.ASSET_ID_STR, audio.getId());
            audioAsset.put(Constants.NAME_STR, Constants.MUSIC_STR);
            assets.put(audioAsset);
        } catch (JSONException e) {
            e.printStackTrace();
            videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, e.getLocalizedMessage());
            return;
        }

        Call<Video> assetCall = service.addVideoAssets(
                video.getId(),
                loginCredential.getAccessKey(),
                loginCredential.getAccessToken(),
                event.getName(),
                theme,
                //credits,
                assets
        );

        Callback<Video> assetCallback = new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Asset creation failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());
                } else {
                    renderVideo();
                }
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.e(
                        TAG,
                        "Asset creation failed with error: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };
        assetCall.enqueue(assetCallback);
    }

    private void renderVideo() {
        Call<RenderResponse> renderResponseCall = service.renderVideo(
                video.getId(),
                loginCredential.getAccessKey(),
                loginCredential.getAccessToken()
        );

        Callback<RenderResponse> renderResponseCallback = new Callback<RenderResponse>() {
            @Override
            public void onResponse(Call<RenderResponse> call, Response<RenderResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Video rendering failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());
                } else {
                    RenderResponse renderResponse = response.body();
                    if (renderResponse.getStatus() == RENDER_SUCCESS) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getVideo();

                    } else {
                        Log.e(
                                TAG,
                                "Video rendering failed with error: "+renderResponse.getStatus()+" and message: "+renderResponse.getMessage());
                        videoCreateListener.onCreateFailure(renderResponse.getStatus(), renderResponse.getMessage());
                    }

                }

            }

            @Override
            public void onFailure(Call<RenderResponse> call, Throwable t) {
                Log.e(
                        TAG,
                        "Video rendering failed with error: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };

        renderResponseCall.enqueue(renderResponseCallback);
    }

    private void getVideo() {
        Call<VideoStatus> videoStatusCall = service.getVideo(
                "nNCaH",
                loginCredential.getAccessKey(),
                loginCredential.getAccessToken()
        );

        Callback<VideoStatus> videoStatusCallback = new Callback<VideoStatus>() {
            @Override
            public void onResponse(Call<VideoStatus> call, Response<VideoStatus> response) {
                if (!response.isSuccessful()) {
                    Log.e(
                            TAG,
                            "Video get failed with error: "+response.code()+" and message: "+response.message());
                    videoCreateListener.onCreateFailure(response.code(), response.message());
                } else {
                    videoStatus = response.body();
                    Video video = videoStatus.getVideo();
                    if (video != null && video.getVideoInfo(360) != null) {
                        videoCreateListener.onCreateSuccess(event, video.getVideoInfo(360).getUrl());
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoStatus> call, Throwable t) {
                Log.e(
                        TAG,
                        "Video get failed with error: "+ Constants.DEFAULT_ERR_CODE+" and message: "+t.getLocalizedMessage());
                videoCreateListener.onCreateFailure(Constants.DEFAULT_ERR_CODE, t.getLocalizedMessage());
            }
        };

        videoStatusCall.enqueue(videoStatusCallback);
    }


}
