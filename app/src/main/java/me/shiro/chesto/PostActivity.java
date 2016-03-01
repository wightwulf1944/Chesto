package me.shiro.chesto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Shiro on 2/25/2016.
 * Dispays a post
 */
public class PostActivity extends AppCompatActivity {

    private static final String POST = "me.shiro.chesto.POST";

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

        Post post = getIntent().getParcelableExtra(POST);
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
}
