package me.shiro.chesto.postActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.apmem.tools.layouts.FlowLayout;

import butterknife.ButterKnife;
import me.shiro.chesto.Post;
import me.shiro.chesto.R;
import me.shiro.chesto.Utils;

/**
 * Created by mzero on 4/10/2016.
 */
public final class PostBottomSheet extends BottomSheetDialogFragment {

    private static final String KEY_POST = PostBottomSheet.class.getName() + "POST";

    static PostBottomSheet newInstance(final Post post) {
        PostBottomSheet instance = new PostBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable(KEY_POST, post);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_post_bottomsheet, container, false);

        FlowLayout flowLayout = ButterKnife.findById(rootView, R.id.flowLayout);
        Post post = getArguments().getParcelable(KEY_POST);
        new FlowLayoutAdapter(flowLayout, post);

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), getTheme());

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = ButterKnife.findById(d, R.id.design_bottom_sheet);
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setPeekHeight(Utils.dpToPx(8));
            }
        });

        return dialog;
    }
}
