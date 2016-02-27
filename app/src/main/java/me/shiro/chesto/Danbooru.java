package me.shiro.chesto;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Shiro on 2/24/2016.
 * Makes api requests and returns a JsonReader
 */
public final class Danbooru {

    private static final String TAG = Danbooru.class.getSimpleName();

    private final StringBuilder request;
    private boolean isFirstArgument;

    public Danbooru() {
        isFirstArgument = true;
        request = new StringBuilder("https://danbooru.donmai.us/");
    }

    public Posts posts() {
        request.append("posts.json");
        return new Posts();
    }

    public class Posts {

        public Danbooru.Posts page(final int page) {
            request.append(delimiter());
            request.append("page=");
            request.append(page);
            return this;
        }

        public Danbooru.Posts limit(final int limit) {
            request.append(delimiter());
            request.append("limit=");
            request.append(limit);
            return this;
        }

        public Danbooru.Posts tags(String tags) {
            request.append(delimiter());
            request.append("tags=");
            request.append(tags.replace(" ", "+"));
            return this;
        }

        public String make() {
            return Danbooru.this.make();
        }
    }

    private char delimiter() {
        if (isFirstArgument) {
            isFirstArgument = false;
            return '?';
        } else {
            return '&';
        }
    }

    private String make() {
        return request.toString();
    }
}
