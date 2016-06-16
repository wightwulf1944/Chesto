package me.shiro.chesto.postActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.imageDownloadService.ImageDownloadService;

/**
 * Created by Shiro on 2/25/2016.
 * Displays a post
 */
public final class PostActivity extends AppCompatActivity {

    public static final String POST_INDEX = "me.shiro.chesto.POST_INDEX";

    private PostTagLayout postTagLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private int postIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postTagLayout = (PostTagLayout) findViewById(R.id.flowLayout);

        final ImageButton upButton = (ImageButton) findViewById(R.id.upButton);
        if (upButton != null) {
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        final ImageButton infoButton = (ImageButton) findViewById(R.id.bottomSheetInfoButton);
        final View bottomSheet = findViewById(R.id.bottomSheet);
        if (bottomSheet != null && infoButton != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        infoButton.setImageResource(R.drawable.ic_info);
                    } else {
                        infoButton.setImageResource(R.drawable.ic_arrow_hide);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(bottomSheet, "alpha", slideOffset);
                    alphaAnimator.setDuration(0);
                    alphaAnimator.start();
                }
            });

            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            });
        }

        selectedPageChanged(getIntent().getIntExtra(POST_INDEX, -1));


        final PostPager postPager = (PostPager) findViewById(R.id.postPager);
        if (postPager != null) {
            postPager.setAdapter(new PostPagerAdapter(this));
            postPager.setOffscreenPageLimit(2);
            postPager.setCurrentItem(postIndex);
            postPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    selectedPageChanged(position);
                }
            });
        }
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
    }
}
