package me.shiro.chesto.imageDownloadService;

import android.net.Uri;

import me.shiro.chesto.danbooruRetrofit.Post;

/**
 * Created by Shiro on 5/2/2016.
 */
public interface DownloadStatusListener {

    void onDownloadStarted(final Post p);

    void onDownloadFinished(final Post p, final Uri uri);

    void onDownloadError(final Post p);
}
