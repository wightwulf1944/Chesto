package me.shiro.chesto;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shiro on 2/23/2016.
 * Parses JsonReaders into a PostList
 */
public final class PostParser {

    private static final String TAG = PostParser.class.getSimpleName();

    public static List<Post> parsePage(final JsonReader json) {
        List<Post> posts = new ArrayList<>();

        try {
            json.beginArray();

            while (json.hasNext()) {
                posts.add(parsePost(json));
            }

            json.endArray();
        } catch (IOException e) {
            Log.d(TAG, "Error parsing json for posts", e);
        } finally {
            try {
                json.close();
            } catch (IOException e) {
                Log.d(TAG, "Error closing json reader", e);
            }
        }

        return posts;
    }

    private static Post parsePost(final JsonReader json) throws IOException {
        Post post = new Post();
        json.beginObject();

        while (json.hasNext()) {
            String name = json.nextName();
            // TODO: check if isempty
            if (json.peek() == JsonToken.NULL) {
                json.nextNull();
            } else {
                switch (name) {
                    case "id":
                        post.setId(json.nextInt());
                        break;
                    case "image_width":
                        post.setImageWidth(json.nextInt());
                        break;
                    case "image_height":
                        post.setImageHeight(json.nextInt());
                        break;
                    case "file_ext":
                        post.setFileExt("." + json.nextString());
                        break;
                    case "file_url":
                        post.setFileUrl(json.nextString());
                        break;
                    case "large_file_url":
                        post.setLargeFileUrl(json.nextString());
                        break;
                    case "preview_file_url":
                        post.setPreviewFileUrl(json.nextString());
                        break;
                    case "tag_string_artist":
                        post.setArtistTag(json.nextString());
                        break;
                    case "tag_string_character":
                        post.setCharacterTag(json.nextString());
                        break;
                    case "tag_string_copyright":
                        post.setCopyrightTag(json.nextString());
                        break;
                    case "tag_string_general":
                        post.setGeneralTag(json.nextString());
                        break;
                    default:
                        json.skipValue();
                        break;
                }
            }
        }

        json.endObject();
        return post;
    }
}
