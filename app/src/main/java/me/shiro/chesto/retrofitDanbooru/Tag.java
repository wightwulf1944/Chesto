package me.shiro.chesto.retrofitDanbooru;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shiro on 5/1/2016.
 * Tag object
 */
public class Tag extends RealmObject {
    @PrimaryKey
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

    public String getPostCountStr() {
        if (postCount < 1000) {
            return "(" + postCount + ")";
        } else {
            String postCountStr = String.valueOf(postCount);
            postCountStr = postCountStr.substring(0, postCountStr.length() - 3);
            return "(" + postCountStr + "k)";
        }
    }
}