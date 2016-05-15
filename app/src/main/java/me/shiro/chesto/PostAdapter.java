package me.shiro.chesto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.shiro.chesto.postActivity.PostActivity;

/**
 * Created by Shiro on 2/23/2016.
 * PostAdapter
 */
public final class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private static final int REQUEST_THRESHOLD = 20;

    private final Context context;
    private final PostList postList = PostList.getInstance();

    public PostAdapter(Context context) {
        this.context = context;
        postList.registerPostAdapter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(context));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == postList.size() - REQUEST_THRESHOLD) {
            postList.requestMorePosts();
        }

        Glide.with(context)
                .load(postList.get(position).getPreviewFileUrl())
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mImageView;

        ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PostActivity.class);
            intent.putExtra(PostActivity.POST_INDEX, getAdapterPosition());
            context.startActivity(intent);
        }
    }
}
