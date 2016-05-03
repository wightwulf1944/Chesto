package me.shiro.chesto.imageDownloadService;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import me.shiro.chesto.danbooruRetrofit.Post;

/**
 * Created by Shiro on 5/2/2016.
 */
class DownloadStatusObservable {

    private List<DownloadStatusListener> listeners = new ArrayList<>();

    void addListener(final DownloadStatusListener listener) {
        listeners.add(listener);
    }

    void removeListener(final DownloadStatusListener listener) {
        listeners.remove(listener);
    }

    void notifyStarted(final Post post) {
        for (DownloadStatusListener listener : listeners) {
            listener.onDownloadStarted(post);
        }
    }

    void notifyFinished(final Post post, final Uri fileUri) {
        for (DownloadStatusListener listener : listeners) {
            listener.onDownloadFinished(post, fileUri);
        }
    }

    void notifyError(final Post post) {
        for (DownloadStatusListener listener : listeners) {
            listener.onDownloadError(post);
        }
    }
}
