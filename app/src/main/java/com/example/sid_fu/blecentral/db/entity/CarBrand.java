package com.example.sid_fu.blecentral.db.entity;

/**
 * Created by Administrator on 2016/6/27.
 */
public class CarBrand {
    private String seriesName;
    private String brandName;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    private String ename;
    private String cname;
    private String initial;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private String releaseTime;

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDerailleur() {
        return derailleur;
    }

    public void setDerailleur(String derailleur) {
        this.derailleur = derailleur;
    }

    public String getGears() {
        return gears;
    }

    public void setGears(String gears) {
        this.gears = gears;
    }

    public String getFtyre() {
        return ftyre;
    }

    public void setFtyre(String ftyre) {
        this.ftyre = ftyre;
    }

    public String getBtyre() {
        return btyre;
    }

    public void setBtyre(String btyre) {
        this.btyre = btyre;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    private String displacement;
    private String weight;
    private String derailleur;
    private String gears;
    private String ftyre;
    private String btyre;
    private String fuelType;

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public String toString()
    {
        return "displacement"+displacement + "weight"+weight + "derailleur"+derailleur+ "gears"+ gears+  "ftyre"+ftyre + "btyre"+btyre;
    }
}
