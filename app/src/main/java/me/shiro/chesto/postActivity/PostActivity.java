package me.shiro.chesto.postActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import me.shiro.chesto.ChestoApplication;
import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.imageDownloadService.ImageDownloadService;

/**
 * Created by Shiro on 2/25/2016.
 * Displays a post
 * TODO: Consider using snackbar bound to page instead of toast for download
 */
public final class PostActivity extends AppCompatActivity {

    public static final String POST_INDEX = "me.shiro.chesto.POST_INDEX";

    private PostPager postPager;
    private PostTagLayout postTagLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private int postIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postTagLayout = (PostTagLayout) findViewById(R.id.flowLayout);
        postPager = (PostPager) findViewById(R.id.postPager);
        final LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        selectedPageChanged(getIntent().getIntExtra(POST_INDEX, -1));

        postPager.setAdapter(new PostPagerAdapter(this));
        postPager.setCurrentItem(postIndex);
        postPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectedPageChanged(position);
            }
        });
    }

    private void selectedPageChanged(int position) {
        postIndex = position;
        postTagLayout.setPost(PostList.getInstance().get(postIndex));
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
        intent.putExtra(PostActivity.POST_INDEX, postIndex);
        startService(intent);

        final View v = postPager.findViewWithTag(postPager.getCurrentItem()).findViewById(R.id.downloadStatusBar);
        v.setVisibility(View.VISIBLE);
        Handler h = ChestoApplication.getMainThreadHandler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setVisibility(View.INVISIBLE);
            }
        }, 2000);
        Snackbar.make(postPager, "TEST", Snackbar.LENGTH_SHORT).show();
    }
}
