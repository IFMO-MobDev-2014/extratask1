package mariashka.editors;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mariashka on 1/16/15.
 */
public class PhotoItem implements Parcelable{
    String name;
    byte[] smallImg;
    byte[] bigImg;

    PhotoItem(String name, byte[] small, byte[] big){
        this.name = name;
        smallImg = small;
        bigImg = big;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(smallImg.length);
        dest.writeByteArray(smallImg);
    }

    public static final Parcelable.Creator<PhotoItem> CREATOR = new Parcelable.Creator<PhotoItem>() {
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    private PhotoItem(Parcel parcel) {
        name = parcel.readString();
        int i = parcel.readInt();
        smallImg = new byte[i];
        parcel.readByteArray(smallImg);
    }

}
