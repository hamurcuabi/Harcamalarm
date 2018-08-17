package com.emrehmrc.harcamalarm.models;

public class SpinnerItemModel {
    private int imageId;
    private String descp;

    public SpinnerItemModel(int imageId, String descp) {
        this.imageId = imageId;
        this.descp = descp;
    }

    public SpinnerItemModel() {

    }

    public int getImageId() {

        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }
}
