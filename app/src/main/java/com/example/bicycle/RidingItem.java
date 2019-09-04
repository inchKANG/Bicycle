package com.example.bicycle;

import android.graphics.Bitmap;

public class RidingItem {
    String SavedTime;
    String SavedDistance;
    String SavedDate;
    Bitmap SavedMap;


    public String getSavedTime() {
        return SavedTime;
    }

    public void setSavedTime(String savedTime) {
        SavedTime = savedTime;
    }

    public String getSavedDistance() {
        return SavedDistance;
    }

    public void setSavedDistance(String savedDistance) {
        SavedDistance = savedDistance;
    }

    public String getSavedDate() {
        return SavedDate;
    }

    public void setSavedDate(String savedDate) {
        SavedDate = savedDate;
    }

    public Bitmap getSavedMap() {
        return SavedMap;
    }

    public void setSavedMap(Bitmap savedMap) {
        SavedMap = savedMap;
    }
}
