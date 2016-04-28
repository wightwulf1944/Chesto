package me.shiro.chesto;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Shiro on 4/28/2016.
 */
public final class ChestoApplication extends Application {

    private static ChestoApplication instance;
    private static Handler mainThreadHandler;

    public static Context getContext() {
        return instance;
    }

    public static Handler getMainThreadHandler() {
        if (mainThreadHandler == null) {
            mainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return mainThreadHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
