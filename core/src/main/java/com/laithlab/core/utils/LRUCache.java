package com.laithlab.core.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LRUCache {
    private LruCache<String, Bitmap> lruImageCache;
    private static LRUCache lruCache = null;

    public static LRUCache getInstance() {
        if (lruCache == null) {
            lruCache = new LRUCache();
        }

        return lruCache;
    }

    private LRUCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        lruImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public Bitmap get(String key) {
        return lruImageCache.get(key);
    }

    public void put(String key, Bitmap value) {
        lruImageCache.put(key, value);
    }
}
