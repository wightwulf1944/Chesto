package me.shiro.chesto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Shiro on 2/25/2016.
 * Displays a post
 */
public class PostActivity extends AppCompatActivity {

    private static final String POST = "me.shiro.chesto.POST";

    private Post post;
    private ImageView imageView;

    public static void start(Context context, Post post) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra(POST, post);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        post = getIntent().getParcelableExtra(POST);
        imageView = (ImageView) findViewById(R.id.mainImageView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Glide.with(this)
                .load(post.getFileUrl())
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        //Let glide handle exception by returning 'false'
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .thumbnail(
                        Glide.with(this)
                                .load(post.getPreviewFileUrl())
                                .fitCenter()
                )
                .into(imageView);
    }

    public void onDownloadButtonClicked(View view) {
        Snackbar.make(imageView, "Saving Image", Snackbar.LENGTH_INDEFINITE)
                .show();
        Glide.with(this)
                .load(post.getFileUrl())
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        try {
                            File file = saveImage(resource, post.getId() + post.getFileExt());
                            sendBroadcast(
                                    new Intent(
                                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                            Uri.fromFile(file)
                                    )
                            );
                            Snackbar.make(imageView, "Image Saved", Snackbar.LENGTH_SHORT)
                                    .show();
                        } catch (IOException e) {
                            Log.d("TEST", "Error saving image", e);
                            Snackbar.make(imageView, "Error Saving Image", Snackbar.LENGTH_SHORT);
                        }
                    }
                });
    }

    private File saveImage(File sourceFile, String name) throws IOException {
        File destinationPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
                        + "/Chesto/"
        );
        if (!destinationPath.exists()) {
            destinationPath.mkdirs();
        }
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
