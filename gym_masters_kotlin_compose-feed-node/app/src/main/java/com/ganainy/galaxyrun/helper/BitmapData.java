package com.ganainy.galaxyrun.helper;

/**
 * Stores ID and dimensions of a bitmap
 */
public class BitmapData {

    private BitmapID id;
    private int width;
    private int height;

    public BitmapData(BitmapID imageID, int imageWidth, int imageHeight) {
        this.id = imageID;
        this.width = imageWidth;
        this.height = imageHeight;
    }

    public BitmapID getId() {
        return id;
    }

    public void setId(BitmapID id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return id.toString() + "(" + width + "," + height + ")";
    }
}
