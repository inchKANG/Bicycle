package com.example.bicycle;

import android.graphics.Bitmap;

import java.io.Serializable;

//중고거래 아이템이 저장되는 클래스
public class ShopData {

    private String shop_title;
    private String shop_contents;
    private String shop_user;
    private String shop_date;

    public Bitmap getShop_image() {
        return Shop_image;
    }

    public void setShop_image(Bitmap shop_image) {
        Shop_image = shop_image;
    }

    private Bitmap Shop_image;



    //객체들을 먼저 만들어 주고, 그 객체들을 alt insert를 통해서 set get 다 넣어준다.

    public String getShop_title() {
        return shop_title;
    }

    public String getShop_contents() {
        return shop_contents;
    }

    public String getShop_user() {
        return shop_user;
    }

    public String getShop_date() {
        return shop_date;
    }




    public void setShop_title(String shop_title) {
        this.shop_title = shop_title;
    }



    public void setShop_contents(String shop_contents) {
        this.shop_contents = shop_contents;
    }



    public void setShop_user(String shop_user) {
        this.shop_user = shop_user;
    }



    public void setShop_date(String shop_date) {
        this.shop_date = shop_date;
    }

    //    public void setIcon(Drawable icon) {
//        iconDrawable = icon ;
//    } 만약 그림을 넣는다면 이런식??
}
