package me.shiro.chesto.postActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import me.shiro.chesto.mainActivity.MainActivity;
import me.shiro.chesto.Post;
import me.shiro.chesto.R;
import me.shiro.chesto.Utils;

/**
 * Created by Shiro on 4/6/2016.
 * Adapter that takes a post and sets it's tags onto a FlowLayout
 */
final class FlowLayoutAdapter {

    private static final int MARGIN = Utils.dpToPx(6);
    private static final int PADDING = Utils.dpToPx(2);

    private final FlowLayout layout;
    private final Context context;
    private String label;
    private int tagTextColor;

    FlowLayoutAdapter(final FlowLayout layout, final Post post) {
        this.layout = layout;
        this.context = layout.getContext();

        label = "Copyrights:";
        tagTextColor = ContextCompat.getColor(context, R.color.copyrightTagText);
        tags(post.getCopyrightTag());
        label = "Characters:";
        tagTextColor = ContextCompat.getColor(context, R.color.characterTagText);
        tags(post.getCharacterTag());
        label = "Artist:";
        tagTextColor = ContextCompat.getColor(context, R.color.artistTagText);
        tags(post.getArtistTag());
        label = "Tags:";
        tagTextColor = ContextCompat.getColor(context, R.color.generalTagText);
        tags(post.getGeneralTag());
    }

    private void tags(final String[] tags) {
        if (tags != null && tags.length > 0) {
            layout.addView(view(label, true));
            for (final String tag : tags) {
                TextView tagView = view(tag, false);
                tagView.setTextColor(tagTextColor);
                tagView.setBackgroundResource(R.drawable.bg_tagview);
                layout.addView(tagView);

                tagView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setAction(Intent.ACTION_SEARCH);
                        intent.putExtra(SearchManager.QUERY, tag);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    private TextView view(final String viewText, final boolean isNewLine) {
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        params.setNewLine(isNewLine);
        TextView labelView = new TextView(context);
        labelView.setText(viewText);
        labelView.setPadding(PADDING, PADDING, PADDING, PADDING);
        labelView.setLayoutParams(params);
        return labelView;
    }
}
