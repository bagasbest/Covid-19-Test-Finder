package com.covid19.test_finder.home.history;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryModel implements Parcelable {

    private String historyId;
    private String userId;
    private String status;
    private String location;
    private String address;
    private String dateTime;
    private String checkMethod;
    private String paymentMethod;
    private long price;
    private String phone;
    private String result;
    private String paymentProof;
    private String img;

    public HistoryModel(){}

    protected HistoryModel(Parcel in) {
        historyId = in.readString();
        userId = in.readString();
        status = in.readString();
        location = in.readString();
        address = in.readString();
        dateTime = in.readString();
        checkMethod = in.readString();
        paymentMethod = in.readString();
        price = in.readLong();
        phone = in.readString();
        result = in.readString();
        paymentProof = in.readString();
        img = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(historyId);
        dest.writeString(userId);
        dest.writeString(status);
        dest.writeString(location);
        dest.writeString(address);
        dest.writeString(dateTime);
        dest.writeString(checkMethod);
        dest.writeString(paymentMethod);
        dest.writeLong(price);
        dest.writeString(phone);
        dest.writeString(result);
        dest.writeString(paymentProof);
        dest.writeString(img);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCheckMethod() {
        return checkMethod;
    }

    public void setCheckMethod(String checkMethod) {
        this.checkMethod = checkMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(String paymentProof) {
        this.paymentProof = paymentProof;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
