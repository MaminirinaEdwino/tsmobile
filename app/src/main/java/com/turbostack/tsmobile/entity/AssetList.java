package com.turbostack.tsmobile.entity;

import java.util.ArrayList;
import java.util.List;

public class AssetList {
    private List<Asset> assets;

    public AssetList() {
        this.assets = new ArrayList<>();
    }

    public AssetList(List<Asset> assets) {
        this.assets = assets;
    }

    public List<Asset> getAssets() {
        return this.assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public void addAsset(Asset asset) {
        this.assets.add(asset);
    }
}