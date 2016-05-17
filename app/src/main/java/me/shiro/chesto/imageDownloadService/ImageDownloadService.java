package me.shiro.chesto.imageDownloadService;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import me.shiro.chesto.PostList;
import me.shiro.chesto.danbooruRetrofit.Post;
import me.shiro.chesto.postActivity.PostActivity;

/**
 * Created by Shiro on 4/6/2016.
 * Handles downloading images through glide
 * TODO: This service does not get destroyed
 */
public final class ImageDownloadService extends Service {

    private static final String TAG = ImageDownloadService.class.getName();
    private static final File destinationPath = imageFileSaveDir();

    private static ImageDownloadNotification notification;

    private static File saveImage(File sourceFile, String name) throws IOException {

        File destFile = new File(destinationPath, name);

        FileChannel inChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel outChannel = new FileOutputStream(destFile).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
        return destFile;
    }

    private static File imageFileSaveDir() {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/Chesto/"
        );
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "ImageDownloadService created");

        notification = new ImageDownloadNotification(this);

        if (!destinationPath.exists()) {
            destinationPath.mkdirs();
        }
    }

    @Override
    public void onDestroy() {
        notification = null;
        Log.i(TAG, "ImageDownloadService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Refuse binding
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Post post = PostList.getInstance().get(intent.getIntExtra(PostActivity.POST_INDEX, -1));

        notification.notifyStarted();

        Glide.with(this)
                .load(post.getFileUrl())
                .downloadOnly(new DownloadTarget(post.getFileName()));

        return START_NOT_STICKY;
    }

    private class DownloadTarget extends SimpleTarget<File> {

        private final String mFilename;

        private DownloadTarget(final String filename) {
            mFilename = filename;
        }

        @Override
        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
            try {
                final File file = saveImage(resource, mFilename);
                final Uri fileUri = Uri.fromFile(file);

                final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(fileUri);
                sendBroadcast(intent);

                notification.notifyFinished();
            } catch (IOException e) {
                Log.d(TAG, "Error saving image", e);
            }
        }
    }
}
