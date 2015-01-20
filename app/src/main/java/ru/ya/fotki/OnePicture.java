package ru.ya.fotki;

import java.io.Serializable;

/**
 * Created by vanya on 17.01.15.
 */
public class OnePicture implements Serializable{
    private String URLS;
    private String URLXL;
    private String URIS;
    private String yandexId;
    private Boolean alreadyLoad;

    public OnePicture(String URIS, String yandexId) {
        this.URIS = URIS;
        this.yandexId = yandexId;
    }

    public OnePicture(String URLS, String URLXL, String yandexId) {
        this.URLS = URLS;
        this.URLXL = URLXL;
        this.yandexId = yandexId;
        this.alreadyLoad = false;
    }
    public void setAlreadyLoad(Boolean alreadyLoad) {
        this.alreadyLoad = alreadyLoad;
    }

    public String getURLXL() {
        return URLXL;
    }

    public String getURIS() {
        return URIS;
    }

    public String getURLS() {
        return URLS;
    }

    public void setURIS(String URIS) {
        this.URIS = URIS;
    }

    public String getHttpXL() {
        return URLXL;
    }

    public String getYandexId() {
        return yandexId;
    }

    public Boolean getAlreadyLoad() {
        return alreadyLoad;
    }

}
