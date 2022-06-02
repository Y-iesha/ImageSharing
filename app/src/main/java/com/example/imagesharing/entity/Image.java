package com.example.imagesharing.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Image extends BmobObject implements Serializable {
    private String title;
    private BmobUser author;
    private BmobFile pic;
    //private BmobRelation likes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BmobUser getAuthor() {
        return author;
    }

    public void setAuthor(BmobUser author) {
        this.author = author;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }


//    public BmobRelation getLikes() {
//        return likes;
//    }
//
//    public void setLikes(BmobRelation likes) {
//        this.likes = likes;
//    }
}
