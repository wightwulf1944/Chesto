package me.shiro.chesto.danbooruRetrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shiro on 5/1/2016.
 * TODO: add getters
 */
public class Tag {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("post_count")
    private int postCount;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPostCount() {
        return postCount;
    }
}
