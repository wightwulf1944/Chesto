package me.shiro.chesto.postActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.apmem.tools.layouts.FlowLayout;

import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.danbooruRetrofit.Post;
import me.shiro.chesto.imageDownloadService.DownloadStatusListener;
import me.shiro.chesto.imageDownloadService.ImageDownloadService;

/**
 * Created by Shiro on 2/25/2016.
 * Displays a post
 */
public final class PostActivity extends AppCompatActivity implements DownloadStatusListener {

    public static final String POST = "me.shiro.chesto.POST";

    private ImageView imageView;
    private BottomSheetBehavior bottomSheetBehavior;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageView = (ImageView) findViewById(R.id.photoView);

        final LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        ImageDownloadService.addDownloadStatusListener(this);

        post = getIntent().getParcelableExtra(POST);

        PostPager postPager = (PostPager) findViewById(R.id.postPager);
        postPager.setAdapter(new PostPagerAdapter(this));
        postPager.setCurrentItem(PostList.getInstance().indexOf(post));

        final FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        new FlowLayoutAdapter(flowLayout, post);
    }

    @Override
    protected void onDestroy() {
        ImageDownloadService.removeDownloadStatusListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public void onDownloadButtonClicked(View view) {
        Intent intent = new Intent(this, ImageDownloadService.class);
        intent.putExtra(PostActivity.POST, post);
        startService(intent);
    }

    @Override
    public void onDownloadStarted(final Post p) {
        if (post.equals(p)) {
            Snackbar.make(imageView, "Saving Image", Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }

    @Override
    public void onDownloadFinished(final Post p, final Uri uri) {
        if (post.equals(p)) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "image/*");
                    startActivity(intent);
                }
            };
            Snackbar.make(imageView, "Image Saved", Snackbar.LENGTH_LONG)
                    .setAction("Open", listener)
                    .show();
        }
    }

    @Override
    public void onDownloadError(Post p) {
        if (post.equals(p)) {
            Snackbar.make(imageView, "Error Saving Image", Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
