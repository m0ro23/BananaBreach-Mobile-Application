package com.bananabreach.data.models;

public class Transaction {

    private String transactionId;
    private String merchantName;
    private double amount;
    private String timestamp;
    private String status;

    public Transaction(String transactionId, String merchantName, double amount,
                        String timestamp, String status) {
        this.transactionId = transactionId;
        this.merchantName = merchantName;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public double getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }
}
