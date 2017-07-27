package com.amobletool.bluetooth.le.downexample.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2017/7/14.
 */
@Entity
public class LiuCheng {
    @Id(autoincrement = false)
    private String id;

    private String code;
    private String name;
    private String renWuCode;
    private String renWuName;

    private String isCheck;
    @Generated(hash = 1787683313)
    public LiuCheng(String id, String code, String name, String renWuCode,
            String renWuName, String isCheck) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.renWuCode = renWuCode;
        this.renWuName = renWuName;
        this.isCheck = isCheck;
    }
    @Generated(hash = 1697005971)
    public LiuCheng() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRenWuCode() {
        return this.renWuCode;
    }
    public void setRenWuCode(String renWuCode) {
        this.renWuCode = renWuCode;
    }
    public String getRenWuName() {
        return this.renWuName;
    }
    public void setRenWuName(String renWuName) {
        this.renWuName = renWuName;
    }
    public String getIsCheck() {
        return this.isCheck;
    }
    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }
}
