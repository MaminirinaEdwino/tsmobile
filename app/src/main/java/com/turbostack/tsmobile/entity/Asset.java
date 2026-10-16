package com.turbostack.tsmobile.entity;

public class Asset {
    private String fileName;
    private String base64Image;

    public Asset() {}

    public Asset(String fileName, String base64Image) {
        this.fileName = fileName;
        this.base64Image = base64Image;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getBase64Image() {
        return this.base64Image;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public AssetJSON toJSON() {
        return new AssetJSON(this.fileName, this.base64Image);
    }
}