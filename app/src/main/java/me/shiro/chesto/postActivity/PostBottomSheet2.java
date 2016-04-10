package me.shiro.chesto.postActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apmem.tools.layouts.FlowLayout;

import me.shiro.chesto.Post;
import me.shiro.chesto.R;

/**
 * Created by mzero on 4/10/2016.
 */
public class PostBottomSheet2 extends BottomSheetDialogFragment {

    private static final String KEY_POST = PostBottomSheet2.class.getName() + "POST";

    static PostBottomSheet2 newInstance(final Post post) {
        PostBottomSheet2 instance = new PostBottomSheet2();
        Bundle args = new Bundle();
        args.putParcelable(KEY_POST, post);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_post_bottomsheet, container, false);

        FlowLayout flowLayout = (FlowLayout) rootView.findViewById(R.id.flowLayout);
        Post post = getArguments().getParcelable(KEY_POST);
        new FlowLayoutAdapter(flowLayout, post);

        return rootView;
    }
}
