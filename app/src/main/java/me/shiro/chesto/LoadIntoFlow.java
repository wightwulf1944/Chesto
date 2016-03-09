package me.shiro.chesto;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by Shiro on 3/10/2016.
 * Responsible for loading data into a flowlayout
 */
public class LoadIntoFlow {

    private static final int MARGIN = Utils.dpToPx(6);
    private static final int PADDING = Utils.dpToPx(2);
    private static int TAG_BG_COLOR;
    private final FlowLayout layout;
    private final Context context;
    private String label;
    private int tagTextColor;

    public static LoadIntoFlow layout(FlowLayout layout) {
        return new LoadIntoFlow(layout);
    }

    private LoadIntoFlow(final FlowLayout layout) {
        this.layout = layout;
        this.context = layout.getContext();
        TAG_BG_COLOR = ContextCompat.getColor(context, android.R.color.darker_gray);
    }

    public LoadIntoFlow label(final String label) {
        this.label = label;
        return this;
    }

    public LoadIntoFlow tagTextColor(final int colorId) {
        tagTextColor = ContextCompat.getColor(context, colorId);
        return this;
    }

    public LoadIntoFlow tags(final String[] tags) {
        if (tags != null && tags.length > 0) {
            TextView labelView = view(label, true);
            layout.addView(labelView);
            for (final String tag : tags) {
                TextView tagView = view(tag, false);
                tagView.setBackgroundColor(TAG_BG_COLOR);
                tagView.setTextColor(tagTextColor);
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
        return this;
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
