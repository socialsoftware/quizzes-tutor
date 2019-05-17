package com.example.tutor.image;

import java.io.Serializable;


public class ImageDTO implements Serializable {
    private String url;
    private Integer width;

    public ImageDTO(Image image) {
        this.width = image.getWidth();
        this.url = image.getUrl();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}