package com.amobletool.bluetooth.le.downexample.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2017/7/12.
 */
@Entity
public class Word {
    @Id(autoincrement = false)
    private String id;

    private String word;

    @Generated(hash = 354130733)
    public Word(String id, String word) {
        this.id = id;
        this.word = word;
    }

    @Generated(hash = 3342184)
    public Word() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
