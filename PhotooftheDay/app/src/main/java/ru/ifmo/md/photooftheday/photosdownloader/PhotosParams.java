package ru.ifmo.md.photooftheday.photosdownloader;

import android.os.Build;

/**
 * Created by vadim on 17/01/15.
 */
public class PhotosParams {
    public final String feature;
    public final String sort;
    public final String sortDirection;
    public final String only;
    public final String exclude;
    public final int counter;
    public final int imageSize;

    private PhotosParams(String feature, String sort, String sortDirection, String only, String exclude, int counter, int imageSize) {
        this.feature = feature;
        this.sort = sort;
        this.sortDirection = sortDirection;
        this.only = only;
        this.exclude = exclude;
        this.counter = counter;
        this.imageSize = imageSize;
    }

    public static class Builder {
        private String feature = "fresh_today";
        private String sort = "highest_rating";
        private String sortDirection = "desc";
        private String only = "0";
        private String exclude = "0";
        private int counter = 1;
        private int imageSize = 3;

        public Builder() {
        }

        public Builder setFeature(String feature) {
            this.feature = feature;
            return this;
        }

        public Builder setSort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }

        public Builder setOnly(String only) {
            this.only = only;
            return this;
        }

        public Builder setExclude(String exclude) {
            this.exclude = exclude;
            return this;
        }

        public Builder setCounter(int counter) {
            this.counter = counter;
            return this;
        }

        public Builder setImageSize(int imageSize) {
            this.imageSize = imageSize;
            return this;
        }

        public PhotosParams build() {
            return new PhotosParams(feature, sort, sortDirection, only, exclude, counter, imageSize);
        }
    }
}
