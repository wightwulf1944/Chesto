package me.shiro.chesto.activityPost;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import me.shiro.chesto.R;
import me.shiro.chesto.Utils;
import me.shiro.chesto.retrofitDanbooru.Post;
import me.shiro.chesto.activityMain.MainActivity;

/**
 * Created by Shiro on 5/9/2016.
 */
public class PostTagLayout extends FlowLayout {

    private static final int MARGIN = Utils.dpToPx(6);
    private static final int PADDING = Utils.dpToPx(2);
    private static final int copyrightTagTextColor = Utils.color(R.color.copyrightTagText);
    private static final int characterTagTextColor = Utils.color(R.color.characterTagText);
    private static final int artistTagTextColor = Utils.color(R.color.artistTagText);
    private static final int generalTagTextColor = Utils.color(R.color.generalTagText);

    private Context mContext;
    private String label;
    private int tagTextColor;

    public PostTagLayout(Context context) {
        this(context, null);
    }

    public PostTagLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PostTagLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        mContext = context;
    }

    public void setPost(final Post post) {
        removeAllViews();

        label = "Copyrights:";
        tagTextColor = copyrightTagTextColor;
        tags(post.getTagStringCopyright());
        label = "Characters:";
        tagTextColor = characterTagTextColor;
        tags(post.getTagStringCharacter());
        label = "Artist:";
        tagTextColor = artistTagTextColor;
        tags(post.getTagStringArtist());
        label = "Tags:";
        tagTextColor = generalTagTextColor;
        tags(post.getTagStringGeneral());
    }

    private void tags(final String tags) {
        if (tags.isEmpty()) {
            return;
        }

        view(label, true);
        for (final String tag : tags.split(" ")) {
            TextView tagView = view(tag, false);
            tagView.setTextColor(tagTextColor);
            tagView.setBackgroundResource(R.drawable.bg_tagview);
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra(SearchManager.QUERY, tag);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private TextView view(final String viewText, final boolean isNewLine) {
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
        params.setNewLine(isNewLine);
        TextView labelView = new TextView(mContext);
        labelView.setText(viewText);
        labelView.setPadding(PADDING, PADDING, PADDING, PADDING);
        addView(labelView, params);
        return labelView;
    }
}
