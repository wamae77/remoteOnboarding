package com.cll.FingerPrintModule.utils;

public class FingerPrint {
    private String imagePath;
    private int nistPosCode;
    private int fingerPosition;
    private int quality;

    public FingerPrint(String imagePath, int nistPosCode, int fingerPosition, int quality) {
        this.imagePath = imagePath;
        this.nistPosCode = nistPosCode;
        this.fingerPosition = fingerPosition;
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "FingerPrint{" +
                "imagePath='" + imagePath + '\'' +
                ", nistPosCode=" + nistPosCode +
                ", fingerPosition=" + fingerPosition +
                ", quality=" + quality +
                '}';
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getNistPosCode() {
        return nistPosCode;
    }

    public void setNistPosCode(int nistPosCode) {
        this.nistPosCode = nistPosCode;
    }

    public int getFingerPosition() {
        return fingerPosition;
    }

    public void setFingerPosition(int fingerPosition) {
        this.fingerPosition = fingerPosition;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}