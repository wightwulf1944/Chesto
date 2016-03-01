package me.shiro.chesto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

/**
 * Created by Shiro on 2/23/2016.
 * PostAdapter
 */
public class PostAdapter
        extends RecyclerView.Adapter<PostAdapter.ViewHolder>
        implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private static final String TAG = PostAdapter.class.getName();

    private Context context;
    private final PostList postList;

    public PostAdapter(PostList postList) {
        this.postList = postList;
        postList.registerPostAdapter(this);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        context = recyclerView.getContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(context));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == postList.size() - Const.REQUEST_THRESHOLD) {
            postList.requestMorePosts();
        }

        Post post = postList.get(position);
        holder.post = post;

        Glide.with(context)
                .load(post.getPreviewFileUrl())
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= postList.size()) {
            return 1.0;
        } else {
            Post post = postList.get(i);
            return (double) post.getImageWidth() / post.getImageHeight();
        }
    }

    public final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;
        public Post post;

        public ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
            mImageView.setOnClickListener(this);
            mImageView.setBackgroundResource(R.color.lightBackground);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }

        @Override
        public void onClick(View v) {
            PostActivity.start(context, post);
        }
    }
}
