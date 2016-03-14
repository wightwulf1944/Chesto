package me.shiro.chesto;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shiro on 3/14/2016.
 * Parses tags from json into objects
 */
public final class TagParser {

    private static final String TAG = TagParser.class.getSimpleName();

    public static List<Tag> parsePage(final JsonReader json) {
        List<Tag> tags = new ArrayList<>(10);

        try {
            json.beginArray();
            for (int i = 0; json.hasNext(); ++i) {
                tags.add(parseTag(json));
            }
            json.endArray();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing json for tags", e);
        } finally {
            try {
                json.close();
            } catch (IOException e) {
                Log.d(TAG, "Error closing json reader", e);
            }
        }

        return tags;
    }

    private static Tag parseTag(final JsonReader json) throws IOException {
        Tag tag = new Tag();
        json.beginObject();
        while (json.hasNext()) {
            final String name = json.nextName();
            if (json.peek() == JsonToken.NULL) {
                json.nextNull();
            } else {
                switch (name) {
                    case "id":
                        tag.id = json.nextInt();
                        break;
                    case "name":
                        tag.tagName = json.nextString();
                        break;
                    case "post_count":
                        tag.postCount = json.nextInt();
                        break;
                    default:
                        json.skipValue();
                        break;
                }
            }
        }
        json.endObject();
        return tag;
    }
}
