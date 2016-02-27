package me.shiro.chesto;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Shiro on 2/27/2016.
 * Miscellaneous helper methods
 */
public class Utils {
    public static int pxToDp(int px, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
