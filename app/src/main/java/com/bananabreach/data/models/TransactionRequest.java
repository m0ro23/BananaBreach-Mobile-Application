package com.bananabreach.data.models;

import com.google.gson.annotations.SerializedName;

public class TransactionRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("merchant_name")
    private String merchantName;

    @SerializedName("amount")
    private double amount;

    public TransactionRequest(String userId, String merchantName, double amount) {
        this.userId = userId;
        this.merchantName = merchantName;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
