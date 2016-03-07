package me.shiro.chesto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shiro on 2/23/2016.
 * Represents a single post
 * converted to parcelable by http://www.parcelabler.com/
 */
public class Post implements Parcelable {

    private final static String BASE_URL = "http://danbooru.donmai.us";
    private int id;
    private int imageWidth;
    private int imageHeight;
    private String fileExt;
    private String fileUrl;
    private String largeFileUrl;
    private String previewFileUrl;
    private String[] artistTag;
    private String[] copyrightTag;
    private String[] characterTag;
    private String[] generalTag;

    public Post() {
        //Default constructor
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            Post x = (Post) obj;
            return id == x.getId();
        } else {
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileUrl() {
        return (fileUrl == null) ? null : BASE_URL + fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLargeFileUrl() {
        return (largeFileUrl == null) ? null : BASE_URL + largeFileUrl;
    }

    public void setLargeFileUrl(String largeFileUrl) {
        this.largeFileUrl = largeFileUrl;
    }

    public String getPreviewFileUrl() {
        return (previewFileUrl == null) ? null : BASE_URL + previewFileUrl;
    }

    public void setPreviewFileUrl(String previewFileUrl) {
        this.previewFileUrl = previewFileUrl;
    }

    public String[] getArtistTag() {
        return artistTag;
    }

    public void setArtistTag(String artistTag) {
        this.artistTag = artistTag.split(" ");
    }

    public String[] getCopyrightTag() {
        return copyrightTag;
    }

    public void setCopyrightTag(String copyrightTag) {
        this.copyrightTag = copyrightTag.split(" ");
    }

    public String[] getCharacterTag() {
        return characterTag;
    }

    public void setCharacterTag(String characterTag) {
        this.characterTag = characterTag.split(" ");
    }

    public String[] getGeneralTag() {
        return generalTag;
    }

    public void setGeneralTag(String generalTag) {
        this.generalTag = generalTag.split(" ");
    }

    // Auto-generated code by http://www.parcelabler.com/
    protected Post(Parcel in) {
        id = in.readInt();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        fileExt = in.readString();
        fileUrl = in.readString();
        largeFileUrl = in.readString();
        previewFileUrl = in.readString();
        artistTag = in.createStringArray();
        copyrightTag = in.createStringArray();
        characterTag = in.createStringArray();
        generalTag = in.createStringArray();
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
        dest.writeString(fileUrl);
        dest.writeString(largeFileUrl);
        dest.writeString(previewFileUrl);
        dest.writeStringArray(artistTag);
        dest.writeStringArray(copyrightTag);
        dest.writeStringArray(characterTag);
        dest.writeStringArray(generalTag);
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