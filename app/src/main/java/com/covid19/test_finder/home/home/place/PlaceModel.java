package com.covid19.test_finder.home.home.place;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceModel implements Parcelable {

    private String uid;
    private String location;
    private String address;
    private String phone;
    private String img;
    private String lon;
    private String lat;
    private long swab;
    private long pcr;

   public PlaceModel(){

   }

    protected PlaceModel(Parcel in) {
        uid = in.readString();
        location = in.readString();
        address = in.readString();
        phone = in.readString();
        img = in.readString();
        lon = in.readString();
        lat = in.readString();
        swab = in.readLong();
        pcr = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(location);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(img);
        dest.writeString(lon);
        dest.writeString(lat);
        dest.writeLong(swab);
        dest.writeLong(pcr);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceModel> CREATOR = new Creator<PlaceModel>() {
        @Override
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        @Override
        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public long getSwab() {
        return swab;
    }

    public void setSwab(long swab) {
        this.swab = swab;
    }

    public long getPcr() {
        return pcr;
    }

    public void setPcr(long pcr) {
        this.pcr = pcr;
    }
}
