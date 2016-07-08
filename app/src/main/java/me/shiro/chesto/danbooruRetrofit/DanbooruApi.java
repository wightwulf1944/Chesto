package me.shiro.chesto.danbooruRetrofit;

import android.util.Log;

import java.util.Iterator;
import java.util.List;

import me.shiro.chesto.PostList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shiro on 6/30/2016.
 * Wrapper class for using auto-generated Danbooru class
 */
public class DanbooruApi {

    private static final String TAG = DanbooruApi.class.getSimpleName();

    public static final Danbooru api = new Retrofit.Builder()
            .baseUrl("http://danbooru.donmai.us/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Danbooru.class);

    public static void getPosts(String tags, int page) {
        api.getPosts(tags, page).enqueue(new ReceivePostsCallback());
    }

    private static class ReceivePostsCallback implements Callback<List<Post>> {

        @Override
        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
            final List<Post> newPostList = response.body();

            Iterator<Post> i = newPostList.iterator();
            while (i.hasNext()) {
                if (i.next().getPreviewFileUrl() == null) {
                    i.remove();
                }
            }

            PostList.getInstance().onReceiveMorePosts(newPostList);
        }

        @Override
        public void onFailure(Call<List<Post>> call, Throwable t) {
            Log.e(TAG, "Error fetching more posts", t);
        }
    }
}
