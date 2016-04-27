package me.shiro.chesto.postActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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

import me.shiro.chesto.Const;
import me.shiro.chesto.Post;
import me.shiro.chesto.R;
import me.shiro.chesto.imageDownloadService.ImageDownloadService;

/**
 * Created by Shiro on 2/25/2016.
 * Displays a post
 */
public final class PostActivity extends AppCompatActivity {

    public static final String POST = "me.shiro.chesto.POST";

    private ImageView imageView;
    private FlowLayout flowLayout;
    private Post post;
    private DownloadStatusReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageView = (ImageView) findViewById(R.id.mainImageView);
        flowLayout = (FlowLayout) findViewById(R.id.flowLayout);

        final LocalBroadcastManager broadcastManager =
                LocalBroadcastManager.getInstance(PostActivity.this);
        receiver = new DownloadStatusReceiver();
        IntentFilter filter;
        filter = new IntentFilter(Const.IMAGE_DL_START);
        broadcastManager.registerReceiver(receiver, filter);
        filter = new IntentFilter(Const.IMAGE_DL_FINISH);
        filter.addDataScheme("file");
        broadcastManager.registerReceiver(receiver, filter);
        filter = new IntentFilter(Const.IMAGE_DL_ERROR);
        broadcastManager.registerReceiver(receiver, filter);

        post = getIntent().getParcelableExtra(POST);

        DrawableRequestBuilder thumbnail = Glide.with(this)
                .load(post.getPreviewFileUrl())
                .fitCenter();

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
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

        new FlowLayoutAdapter(flowLayout, post);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void onDownloadButtonClicked(View view) {
        Intent intent = new Intent(this, ImageDownloadService.class);
        intent.putExtra(PostActivity.POST, post);
        startService(intent);
    }

    private class DownloadStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final int postId = intent.getIntExtra(Const.POST_ID, -1);
            if (postId != post.getId()) {
                return;
            }

            switch (intent.getAction()) {

                case Const.IMAGE_DL_START:
                    Snackbar.make(imageView, "Saving Image", Snackbar.LENGTH_INDEFINITE)
                            .show();
                    break;

                case Const.IMAGE_DL_FINISH:
                    final Uri fileUri = intent.getData();
                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(fileUri, "image/*");
                            startActivity(intent);
                        }
                    };
                    Snackbar.make(imageView, "Image Saved", Snackbar.LENGTH_LONG)
                            .setAction("Open", listener)
                            .show();
                    break;

                case Const.IMAGE_DL_ERROR:
                    Snackbar.make(imageView, "Error Saving Image", Snackbar.LENGTH_LONG);
                    break;
            }
        }
    }
}
