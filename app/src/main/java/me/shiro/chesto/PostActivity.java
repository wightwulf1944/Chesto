package me.shiro.chesto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Shiro on 2/25/2016.
 * Dispays a post
 */
public class PostActivity extends AppCompatActivity {

    private static final String POST = "me.shiro.chesto.POST";

    private Post post;

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
        ImageView imageView = (ImageView) findViewById(R.id.mainImageView);

        Glide.with(this)
                .load(post.getFileUrl())
                .error(R.drawable.ic_image_broken)
                .thumbnail(
                        Glide.with(this)
                                .load(post.getPreviewFileUrl())
                                .fitCenter()
                )
                .into(imageView);
    }

    public void onDownloadButtonClicked(View view) {
        Glide.with(this)
                .load(post.getFileUrl())
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        File downloadsDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                                        + "/Chesto/"
                        );

                        try {
                            saveImage(resource, downloadsDir, post.getId() + post.getFileExt());
                        } catch (IOException e) {
                            Log.d("TEST", "Error saving image", e);
                        }

                    }
                });
    }

    private void saveImage(File sourceFile, File destinationPath, String name) throws IOException {
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
    }
}
