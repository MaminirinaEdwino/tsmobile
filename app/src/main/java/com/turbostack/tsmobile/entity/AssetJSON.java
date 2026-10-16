package com.example.app.entity;

public class AssetJSON {
    public String file_name;
    public String base_64_image;

    public AssetJSON() {}

    public AssetJSON(String fileName, String base64Image) {
        this.file_name = fileName;
        this.base_64_image = base64Image;
    }

    public Asset toModel() {
        return new Asset(this.file_name, this.base_64_image);
    }
}