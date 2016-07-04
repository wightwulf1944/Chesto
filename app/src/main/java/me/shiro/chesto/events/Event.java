package me.shiro.chesto.events;

/**
 * Created by mzero on 6/26/2016.
 */
public class Event {

    private Event() {}

    public static class PostsLoading {
    }

    public static class PostsLoadingFinished {
    }

    public static class PostListRefreshed {
    }

    public static class PostListUpdated {
        public final int positionStart;
        public final int itemCount;
        public PostListUpdated(int positionStart, int itemCount) {
            this.positionStart = positionStart;
            this.itemCount = itemCount;
        }
    }
}
