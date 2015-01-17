package ru.ifmo.md.extratask1.photoclient;

import java.util.ArrayList;

/**
 * Created by sergey on 16.01.15.
 */
public class ImageEntry {

    class Author {
        private String name;
        private long uid;

        Author() {

        }

        Author(String name, int uid) {
            this.name = name;
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }
    }

    class ImageVariant {

        ImageVariant() {

        }

        ImageVariant(int height, int width, String href, String size) {
            this.height = height;
            this.width = width;
            this.href = href;
            this.size = size;
        }

        private int height;
        private int width;
        private String href;
        private String size;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    private String title = "";
    private String urlOnWeb = "";
    private String imageWebId = "";
    private String contentUrl = "";
    private ArrayList<ImageVariant> variants = new ArrayList<>();
    private Author author = new Author();

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageWebId() {
        return imageWebId;
    }

    public void setImageWebId(String imageWebId) {
        this.imageWebId = imageWebId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlOnWeb() {
        return urlOnWeb;
    }

    public void setUrlOnWeb(String urlOnWeb) {
        this.urlOnWeb = urlOnWeb;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public ArrayList<ImageVariant> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<ImageVariant> variants) {
        this.variants = variants;
    }

    public ImageEntry(String title, String urlOnWeb, Author author, ArrayList<ImageVariant> variants) {
        this.title = title;
        this.urlOnWeb = urlOnWeb;
        this.author = author;
        this.variants = variants;
    }

    void addImageVariant(int height, int width, String href, String size) {
        variants.add(new ImageVariant(height, width, href, size));
    }

    public ImageEntry() {

    }
}
