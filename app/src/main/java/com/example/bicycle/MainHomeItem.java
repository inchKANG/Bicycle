package com.example.bicycle;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class MainHomeItem {

    String homeUser;
    String homeText;
    String homeDate;
    Bitmap homeImage;

    public String getHomeCode() {
        return homeCode;
    }

    public void setHomeCode(String homeCode) {
        this.homeCode = homeCode;
    }

    String homeCode;

    public String getHomeUser() {
        return homeUser;
    }

    public String getHomeText() {
        return homeText;
    }

    public String getHomeDate() {
        return homeDate;
    }

    public Bitmap getHomeImage() {
        return homeImage;
    }

    public void setHomeUser(String homeUser) {
        this.homeUser = homeUser;
    }

    public void setHomeText(String homeText) {
        this.homeText = homeText;
    }

    public void setHomeDate(String homeDate) {
        this.homeDate = homeDate;
    }

    public void setHomeImage(Bitmap homeImage) {
        this.homeImage = homeImage;
    }
}
