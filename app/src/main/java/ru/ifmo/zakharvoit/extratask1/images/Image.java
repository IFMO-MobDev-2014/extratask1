package ru.ifmo.zakharvoit.extratask1.images;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
public class Image implements Parcelable {
    private final String title;
    private final byte[] contents;
    private final String largeLink;

    public Image(String title, byte[] contents, String largeLink) {
        this.title = title;
        this.contents = contents;
        this.largeLink = largeLink;
    }

    public String getTitle() {
        return title;
    }

    public byte[] getContents() {
        return contents;
    }

    public String getLargeLink() {
        return largeLink;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeByteArray(this.contents);
        dest.writeString(this.largeLink);
    }

    private Image(Parcel in) {
        this.title = in.readString();
        this.contents = in.createByteArray();
        this.largeLink = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
