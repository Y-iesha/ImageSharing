package com.example.imagesharing.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Image extends BmobObject implements Serializable {
    private String title;
    private User author;
    private BmobFile pic;
    private int dlCount;
    private BmobRelation likes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }
    

    public int getDlCount() {
        return dlCount;
    }

    public void setDlCount(int dlCount) {
        this.dlCount = dlCount;
    }


    public BmobRelation getLikes() {
        return likes;
    }

    public Image setLikes(BmobRelation likes) {
        this.likes = likes;
        return this;
    }
}
