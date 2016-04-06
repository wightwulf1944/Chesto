package me.shiro.chesto;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import me.shiro.chesto.postActivity.PostActivity;

/**
 * Created by Shiro on 4/6/2016.
 * Handles downloading images through glide
 */
public class ImageDownloadService extends Service {

    private static final String TAG = ImageDownloadService.class.getName();

    private static final File destinationPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES + "/Chesto/"
    );

    @Override
    public void onCreate() {
        Log.i(TAG, "ImageDownloadService created");

        if (!destinationPath.exists()) {
            destinationPath.mkdirs();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "ImageDownloadService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Refuse binding
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Post post = intent.getParcelableExtra(PostActivity.POST);

        Glide.with(this)
                .load(post.getFileUrl())
                .downloadOnly(new DownloadTarget(post));

        return START_NOT_STICKY;
    }

    private class DownloadTarget extends SimpleTarget<File> {

        private final Post post;

        private DownloadTarget(final Post post) {
            this.post = post;
        }

        @Override
        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
            try {
                final File file = saveImage(resource, post.getId() + post.getFileExt());
                final Uri fileUri = Uri.fromFile(file);
                final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(fileUri);
                sendBroadcast(intent);

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(fileUri, "image/*");
                        startActivity(intent);
                    }
                };

                Log.i(TAG, "Image successfully downloaded");
//                Snackbar.make(imageView, "Image Saved", Snackbar.LENGTH_LONG)
//                        .setAction("Open", listener)
//                        .show();

            } catch (IOException e) {
                Log.d("TEST", "Error saving image", e);
//                Snackbar.make(imageView, "Error Saving Image", Snackbar.LENGTH_SHORT);
            }
        }
    }

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
}
