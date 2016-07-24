package me.shiro.chesto.retrofitDanbooru;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shiro on 5/1/2016.
 */
public interface Danbooru {

    @GET("posts.json")
    Call<List<Post>> getPosts(@Query("tags") String tags, @Query("page") int page);

    @GET("tags.json?search[order]=count&search[hide_empty]=yes")
    Call<List<Tag>> searchTags(@Query("search[name_matches]") String tags);

    // TODO: use this to get tag post count
    @GET("tags.json")
    Call<List<Tag>> getTags(@Query("search[name]") String tags);
}
