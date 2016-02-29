package me.shiro.chesto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private PostList postList;

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
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.color.darkBackground);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new ViewHolder(imageView);
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
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_placeholder_error)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if(i >= postList.size()) {
            return 1.0;
        } else {
            Post post = postList.get(i);
            return (double) post.getImageWidth() / post.getImageHeight();
        }
    }

    public final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public Post post;

        public ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PostActivity.start(context, post);
        }
    }
}
