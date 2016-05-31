package me.shiro.chesto.postActivity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
final public class PostPagerAdapter extends PagerAdapter {

    private static final PostList postList = PostList.getInstance();
    private final Context mContext;
    private final LayoutInflater inflater;
    private final ViewHolderProvider vhProvider;

    public PostPagerAdapter(final Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        vhProvider = new ViewHolderProvider();
        postList.registerPostPagerAdapter(this);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder vh = vhProvider.getViewHolder();
        vh.setPost(position);
        container.addView(vh.rootView);
        return vh;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder vh = (ViewHolder) object;
        vhProvider.recycleViewHolder(vh);
        container.removeView(vh.rootView);
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ViewHolder) object).rootView;
    }

    private final class ViewHolderProvider {
        private Queue<ViewHolder> recycledHolders = new LinkedList<>();

        private ViewHolder getViewHolder() {
            final ViewHolder viewHolder;
            if (recycledHolders.isEmpty()) {
                viewHolder = new ViewHolder();
            } else {
                viewHolder = recycledHolders.remove();
            }
            return viewHolder;
        }

        private void recycleViewHolder(ViewHolder viewHolder) {
            recycledHolders.add(viewHolder);
        }
    }

    private final class ViewHolder {
        private View rootView;
        private PhotoView photoView;
        private ProgressBar progressBar;
        private Post post;

        private ViewHolder() {
            rootView = inflater.inflate(R.layout.activity_post_page, null);
            photoView = (PhotoView) rootView.findViewById(R.id.photoView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        }

        private void setPost(Post post) {
            DrawableRequestBuilder thumbnail = Glide.with(mContext)
                    .load(post.getPreviewFileUrl())
                    .fitCenter();

        private void setPost(final int position) {
            post = postList.get(position);
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
        }
    }
}
