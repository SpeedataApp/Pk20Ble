package com.amobletool.bluetooth.le.downexample.bean;

/**
 * Created by 张明_ on 2017/11/24.
 */

public class JsonData {

    /**
     * length : 12
     * height : 1
     * width : 1
     * billNo : 123
     * isOk : 1
     * uid : 123
     * weight : 1
     */

    private String length;
    private String height;
    private String width;
    private String billNo;
    private String isOk = "1";
    private String uid;
    private String weight;
    private String createTime;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getIsOk() {
        return isOk;
    }

    public void setIsOk(String isOk) {
        this.isOk = isOk;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
