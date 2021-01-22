package com.example.detectiontext.models;

public class ImgTT {
    String text, image,timestamp;

    public ImgTT() {
    }

    public ImgTT(String text, String image, String timestamp) {
        this.text = text;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
