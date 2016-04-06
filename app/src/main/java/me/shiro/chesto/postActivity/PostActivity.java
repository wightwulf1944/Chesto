package me.shiro.chesto.postActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.apmem.tools.layouts.FlowLayout;

import me.shiro.chesto.ImageDownloadService;
import me.shiro.chesto.Post;
import me.shiro.chesto.R;

/**
 * Created by Shiro on 2/25/2016.
 * Displays a post
 */
public final class PostActivity extends AppCompatActivity {

    public static final String POST = "me.shiro.chesto.POST";

    private Post post;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post = getIntent().getParcelableExtra(POST);
        imageView = (ImageView) findViewById(R.id.mainImageView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final FlowLayout bottomSheet = (FlowLayout) findViewById(R.id.bottomSheet);

        new FlowLayoutAdapter(bottomSheet, post);

        DrawableRequestBuilder thumbnail = Glide.with(this)
                .load(post.getPreviewFileUrl())
                .fitCenter();

        RequestListener<String, GlideDrawable> listener = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        };

        Glide.with(this)
                .load(post.getFileUrl())
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(listener)
                .thumbnail(thumbnail)
                .into(imageView);
    }

    public void onDownloadButtonClicked(View view) {
        Snackbar.make(imageView, "Saving Image", Snackbar.LENGTH_INDEFINITE)
                .show();

        Intent intent = new Intent(this, ImageDownloadService.class);
        intent.putExtra(PostActivity.POST, post);
        startService(intent);
    }
}
