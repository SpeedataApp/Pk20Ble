package com.amobletool.bluetooth.le;

/**
 * Created by lenovo-pc on 2017/5/8.
 */

public class infoEntity {
    public infoEntity() {
        super();
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public infoEntity(int num, String scanDatas, int l, int w, int h, int g) {
        super();
        this.scanDatas = scanDatas;
        this.l = l;
        this.w = w;
        this.h = h;
        this.g = g;
        this.num = num;
    }

    private int num = 0;
    private String scanDatas = "";
    private int l = 0;
    private int w = 0;
    private int h = 0;
    private int g = 0;

    public String getScanDatas() {
        return scanDatas;
    }

    public void setScanDatas(String scanDatas) {
        this.scanDatas = scanDatas;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }
}
