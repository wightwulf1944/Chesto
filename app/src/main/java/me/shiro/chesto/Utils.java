package me.shiro.chesto;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Shiro on 2/27/2016.
 * Miscellaneous helper methods
 */
public final class Utils {

    public static Context appContext;

    public static int pxToDp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                appContext.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                appContext.getResources().getDisplayMetrics());
    }
}
