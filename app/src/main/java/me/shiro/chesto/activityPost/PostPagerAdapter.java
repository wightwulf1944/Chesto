package me.shiro.chesto.activityPost;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.Queue;

import me.shiro.chesto.PostList;
import me.shiro.chesto.R;
import me.shiro.chesto.Utils;
import me.shiro.chesto.retrofitDanbooru.Post;
import me.shiro.chesto.events.Event;
import me.shiro.chesto.fresco.zoomable.ZoomableDraweeView;

/**
 * Created by Shiro on 5/4/2016.
 */
final public class PostPagerAdapter extends PagerAdapter {

    private static final PostList postList = PostList.getInstance();
    private final Context mContext;
    private final LayoutInflater inflater;
    private final ViewHolderProvider vhProvider;

    public PostPagerAdapter(final Context context) {
        EventBus.getDefault().register(this);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        vhProvider = new ViewHolderProvider();
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

    @Subscribe
    public void onDataSetChanged(Event.PostListUpdated event) {
        notifyDataSetChanged();
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
        private ZoomableDraweeView rootView;

        private ViewHolder() {
            rootView = (ZoomableDraweeView) inflater.inflate(R.layout.activity_post_page, null);

            ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
            progressBarDrawable.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            progressBarDrawable.setBarWidth(Utils.dpToPx(2));

            GenericDraweeHierarchy frescoHierarchy = rootView.getHierarchy();
            frescoHierarchy.setProgressBarImage(progressBarDrawable);

            rootView.setHierarchy(frescoHierarchy);
        }

        private void setPost(final int position) {
            final Post post = postList.get(position);
            final ImageRequest lowRes = ImageRequest.fromUri(post.getPreviewFileUrl());
            final ImageRequest highRes = ImageRequest.fromUri(post.getFileUrl());

            //TODO: notify that webm and mp4 is not supported

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(lowRes)
                    .setImageRequest(highRes)
                    .setAutoPlayAnimations(true)
                    .setTapToRetryEnabled(true)
                    .setOldController(rootView.getController())
                    .build();

            rootView.setController(controller);
        }
    }
}
