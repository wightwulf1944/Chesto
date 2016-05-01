package me.shiro.chesto.imageDownloadService;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import me.shiro.chesto.Const;
import me.shiro.chesto.danbooruRetrofit.Post;

/**
 * Created by Shiro on 4/7/2016.
 * Responsible for notifying recievers of image download status
 */
final class DownloadStatusBroadcaster {

    private final LocalBroadcastManager broadcastManager;

    DownloadStatusBroadcaster(final Context context) {
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    void broadcastStarted(final Post post) {
        final Intent intent = new Intent(Const.IMAGE_DL_START);
        intent.putExtra(Const.POST_ID, post.getId());
        broadcastManager.sendBroadcast(intent);
    }

    void broadcastFinished(final Post post, final Uri fileUri) {
        final Intent intent = new Intent(Const.IMAGE_DL_FINISH, fileUri);
        intent.putExtra(Const.POST_ID, post.getId());
        broadcastManager.sendBroadcast(intent);
    }

    void broadcastError(final Post post) {
        final Intent intent = new Intent(Const.IMAGE_DL_ERROR);
        intent.putExtra(Const.POST_ID, post.getId());
        broadcastManager.sendBroadcast(intent);
    }
}
