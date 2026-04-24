package com.fulfilment.application.monolith.fulfillment.model;

public class Fulfillment {

    public String businessUntiCode;
    public String productName;
    public String storeName;

    public String getBusinessUntiCode() {
        return businessUntiCode;
    }

    public void setBusinessUntiCode(String businessUntiCode) {
        this.businessUntiCode = businessUntiCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Fulfillment(String businessUntiCode, String productName, String storeName) {
        this.businessUntiCode = businessUntiCode;
        this.productName = productName;
        this.storeName = storeName;
    }
}
