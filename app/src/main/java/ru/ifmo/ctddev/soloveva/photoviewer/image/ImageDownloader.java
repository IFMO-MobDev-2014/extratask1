package ru.ifmo.ctddev.soloveva.photoviewer.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by maria on 28.09.14.
 * Thread-safe
 */
public class ImageDownloader implements Closeable {
    private final Map<String, Bitmap> cache = new HashMap<>();
    private final ConcurrentMap<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
    private final ReadWriteLock closeLock = new ReentrantReadWriteLock();
    private boolean closed = false;

    public Bitmap download(String url) throws IOException {
        closeLock.readLock().lock();
        try {
            if (closed) {
                throw new IllegalStateException("ImageDownloader is already closed");
            }
            ReadWriteLock lock = locks.get(url);
            if (lock == null) {
                locks.putIfAbsent(url, new ReentrantReadWriteLock());
                lock = locks.get(url);
            }
            lock.readLock().lock();
            try {
                if (cache.containsKey(url)) {
                    return cache.get(url);
                }
            } finally {
                lock.readLock().unlock();
            }
            lock.writeLock().lock();
            try {
                if (cache.containsKey(url)) {
                    return cache.get(url);
                }
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                cache.put(url, bitmap);
                return bitmap;
            } finally {
                lock.writeLock().unlock();
            }
        } finally {
            closeLock.readLock().unlock();
        }
    }

    public boolean isCached(String url) {
        return cache.containsKey(url);
    }

    public Bitmap getFromCache(String url) {
        return cache.get(url);
    }

    @Override
    public void close() throws IOException {
        closeLock.writeLock().lock();
        try {
            closed = true;
            for (Bitmap bitmap : cache.values()) {
                bitmap.recycle();
            }
            cache.clear();
            locks.clear();
        } finally {
            closeLock.writeLock().unlock();
        }
    }
}