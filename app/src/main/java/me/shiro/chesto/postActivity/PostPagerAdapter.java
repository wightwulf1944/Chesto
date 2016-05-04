package me.shiro.chesto.postActivity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.LinkedList;
import java.util.Queue;

import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.danbooruRetrofit.Post;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Shiro on 5/4/2016.
 */
final class PostPagerAdapter extends PagerAdapter {

    private static final PostList postList = PostList.getInstance();
    private final Context mContext;
    private final LayoutInflater inflater;
    private Queue<ViewHolder> recycledHolders = new LinkedList<>();

    public PostPagerAdapter(final Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ViewHolder viewHolder;
        if (recycledHolders.isEmpty()) {
            viewHolder = new ViewHolder(container);
        } else {
            viewHolder = recycledHolders.remove();
        }
        viewHolder.setPost(postList.get(position));
        return viewHolder;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder viewHolder = (ViewHolder) object;
        recycledHolders.add(viewHolder);
        container.removeView(viewHolder.rootView);
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ViewHolder) object).rootView;
    }

    private final class ViewHolder {
        private ViewGroup mContainer;
        private View rootView;
        private PhotoView photoView;
        private ProgressBar progressBar;

        private ViewHolder(ViewGroup container) {
            mContainer = container;
            rootView = inflater.inflate(R.layout.activity_post_page, container, false);
            photoView = (PhotoView) rootView.findViewById(R.id.photoView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        }

        private void setPost(Post post) {
            DrawableRequestBuilder thumbnail = Glide.with(mContext)
                    .load(post.getPreviewFileUrl())
                    .fitCenter();

            progressBar.setVisibility(View.VISIBLE);
            RequestListener<String, GlideDrawable> listener = new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            };

            Glide.with(mContext)
                    .load(post.getFileUrl())
                    .error(R.drawable.ic_image_broken)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(listener)
                    .thumbnail(thumbnail)
                    .into(photoView);

            mContainer.addView(rootView);
        }
    }
}
