package ru.ifmo.md.extratask1;

/**
 * Created by Mikhail on 18.01.15.
 */
public class LinkAndId {
    private String fullSizeLink;
    private String myId;

    LinkAndId(String fullSizeLink, String myId) {
        this.fullSizeLink = fullSizeLink;
        this.myId = myId;
    }

    LinkAndId(){

    }

    public String getFullSizeLink() {
        return this.fullSizeLink;
    }

    public String getMyId() {
        return this.myId;
    }

    public void setFullSizeLink(String fullSizeLink) {
        this.fullSizeLink = fullSizeLink;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
