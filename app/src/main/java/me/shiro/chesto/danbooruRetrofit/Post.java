package me.shiro.chesto.danbooruRetrofit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shiro on 5/1/2016.
 * Post object
 */
public class Post implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("image_width")
    private int imageWidth;
    @SerializedName("image_height")
    private int imageHeight;
    @SerializedName("tag_string")
    private String tagString;
    @SerializedName("file_ext")
    private String fileExt;
    @SerializedName("tag_string_artist")
    private String tagStringArtist;
    @SerializedName("tag_string_character")
    private String tagStringCharacter;
    @SerializedName("tag_string_copyright")
    private String tagStringCopyright;
    @SerializedName("tag_string_general")
    private String tagStringGeneral;
    @SerializedName("file_url")
    private String fileUrl;
    @SerializedName("preview_file_url")
    private String previewFileUrl;

    private static final String BASE_URL = "http://danbooru.donmai.us";

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            Post x = (Post) obj;
            return id == x.getId();
        } else {
            return false;
        }
    }

    public String getFileName() {
        return id + "." + fileExt;
    }

    public int getId() {
        return id;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public String getTagString() {
        return tagString;
    }

    public String getTagStringArtist() {
        return tagStringArtist;
    }

    public String getTagStringCharacter() {
        return tagStringCharacter;
    }

    public String getTagStringCopyright() {
        return tagStringCopyright;
    }

    public String getTagStringGeneral() {
        return tagStringGeneral;
    }

    public String getFileUrl() {
        return (fileUrl == null) ? null : BASE_URL + fileUrl;
    }

    public String getPreviewFileUrl() {
        return (previewFileUrl == null) ? null : BASE_URL + previewFileUrl;
    }

    // Generated code by http://www.parcelabler.com/
    protected Post(Parcel in) {
        id = in.readInt();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        fileExt = in.readString();
        tagStringArtist = in.readString();
        tagStringCharacter = in.readString();
        tagStringCopyright = in.readString();
        tagStringGeneral = in.readString();
        fileUrl = in.readString();
        previewFileUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(imageWidth);
        dest.writeInt(imageHeight);
        dest.writeString(fileExt);
        dest.writeString(tagStringArtist);
        dest.writeString(tagStringCharacter);
        dest.writeString(tagStringCopyright);
        dest.writeString(tagStringGeneral);
        dest.writeString(fileUrl);
        dest.writeString(previewFileUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
