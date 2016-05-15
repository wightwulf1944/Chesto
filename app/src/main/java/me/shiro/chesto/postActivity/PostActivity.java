package me.shiro.chesto.postActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    private PostTagLayout postTagLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private int postIndex;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postTagLayout = (PostTagLayout) findViewById(R.id.flowLayout);
        final PostPager postPager = (PostPager) findViewById(R.id.postPager);
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

        if (toast == null) {
            toast = Toast.makeText(this, "Download Queued", Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
