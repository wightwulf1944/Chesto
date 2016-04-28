package me.shiro.chesto;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import java.io.File;

/**
 * Created by Shiro on 2/27/2016.
 * Miscellaneous helper methods
 */
public final class Utils {

    private static Context appContext = ChestoApplication.getInstance();

    public static int pxToDp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                appContext.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                appContext.getResources().getDisplayMetrics());
    }

    public static File imageFileSaveDir() {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/Chesto/"
        );
    }

    public static int color(int colorId) {
        return ContextCompat.getColor(appContext, colorId);
    }
}
