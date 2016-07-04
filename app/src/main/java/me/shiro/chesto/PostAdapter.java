package me.shiro.chesto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.shiro.chesto.events.Event;
import me.shiro.chesto.postActivity.PostActivity;

/**
 * Created by Shiro on 2/23/2016.
 * PostAdapter
 */
public final class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private static final int REQUEST_THRESHOLD = 20;
    private static final PostList postList = PostList.getInstance();
    private final Context context;

    public PostAdapter(Context context) {
        EventBus.getDefault().register(this);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(R.drawable.ic_image_broken)
                .setFailureImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context, hierarchy);
        return new ViewHolder(simpleDraweeView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == postList.size() - REQUEST_THRESHOLD) {
            postList.requestMorePosts();
        }

        holder.simpleDraweeView.setImageURI(postList.get(position).getPreviewFileUrl());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Subscribe
    public void onDatasetChanged(Event.PostListUpdated event) {
        notifyItemRangeInserted(event.positionStart, event.itemCount);
    }

    @Subscribe
    public void onDatasetChanged(Event.PostListRefreshed event) {
        notifyDataSetChanged();
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final SimpleDraweeView simpleDraweeView;

        ViewHolder(SimpleDraweeView v) {
            super(v);
            simpleDraweeView = v;
            simpleDraweeView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PostActivity.class);
            intent.putExtra(PostActivity.POST_INDEX, getAdapterPosition());
            context.startActivity(intent);
        }
    }
}
