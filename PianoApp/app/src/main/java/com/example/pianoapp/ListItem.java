package com.example.pianoapp;

import android.graphics.Bitmap;

public class ListItem {
    private Bitmap mThumbnail = null;
    private String mTitle = null;

//    空のコンストラクタ
    public ListItem() {};

//    コンストラクタ
//    @param thumbnail サムネイル画像
//    @param title タイトル
    public ListItem(Bitmap thumbnail, String title) {
        mThumbnail = thumbnail;
        mTitle = title;
    }

//    サムネイル画像を設定
    public void setThumbnail(Bitmap thumbnail) {
        mThumbnail = thumbnail;
    }

//    タイトルを設定
    public void setmTitle(String title) {
        mTitle = title;
    }
//    サムネイル画像を取得
    public Bitmap getThumbnail() {
        return mThumbnail;
    }

//    タイトルを取得
    public String getTitle() {
        return mTitle;
    }
}