package me.shiro.chesto.danbooruRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shiro on 5/1/2016.
 */
public interface Danbooru {

    Danbooru api = new Retrofit.Builder()
            .baseUrl("http://danbooru.donmai.us/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Danbooru.class);

    @GET("posts.json")
    Call<List<Post>> getPosts(@Query("tags") String tags, @Query("page") int page, @Query("limit") int limit);

    @GET("tags.json?search[order]=count")
    Call<List<Tag>> searchTags(@Query("search[name_matches]") String tags);

    // TODO: use this to get tag post count
    @GET("tags.json")
    Call<List<Tag>> getTags(@Query("search[name]") String tags);
}
