package me.shiro.chesto.postActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;

import org.apmem.tools.layouts.FlowLayout;

import me.shiro.chesto.Post;
import me.shiro.chesto.R;

/**
 * Created by Shiro on 4/10/2016.
 */
class PostBottomSheet extends BottomSheetDialog {

    private final Post post;

    public PostBottomSheet(@NonNull Activity context, @NonNull Post post) {
        super(context);
        setOwnerActivity(context);
        this.post = post;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_post_bottomsheet);

        final FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        new FlowLayoutAdapter(flowLayout, post);
    }
}
