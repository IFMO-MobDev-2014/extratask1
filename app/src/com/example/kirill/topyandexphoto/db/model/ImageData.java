package com.example.kirill.topyandexphoto.db.model;

/**
 * Created by Kirill on 11.01.2015.
 */
public class ImageData {
    private String entryId;
    private String entryUrl;
    private String previewUrl;
    private String bigUrl;
    private String title;
    private String authorName;
    private long published;

    public ImageData(String entryId, String entryUrl, String previewUrl, String bigUrl, String title, String authorName, long published) {
        this.entryId = entryId;
        this.entryUrl = entryUrl;
        this.previewUrl = previewUrl;
        this.bigUrl = bigUrl;
        this.title = title;
        this.authorName = authorName;
        this.published = published;
    }

    public ImageData() {
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getEntryUrl() {
        return entryUrl;
    }

    public void setEntryUrl(String entryUrl) {
        this.entryUrl = entryUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getBigUrl() {
        return bigUrl;
    }

    public void setBigUrl(String bigUrl) {
        this.bigUrl = bigUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getPublished() {
        return published;
    }

    public void setPublished(long published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "entryId='" + entryId + '\'' +
                ", entryUrl='" + entryUrl + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", bigUrl='" + bigUrl + '\'' +
                ", title='" + title + '\'' +
                ", authorName='" + authorName + '\'' +
                ", published=" + published +
                '}';
    }
}