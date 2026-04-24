package com.fulfilment.application.monolith.fulfillment.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Fulfillment {

    @NotBlank(message = "businessunitcode cannot be empty")
    public String businessUnitCode;

    @NotBlank(message = "productName cannot be empty")
    public String productName;

    @NotBlank(message = "storeName cannot be empty")
    public String storeName;

    public String getBusinessUntiCode() {
        return businessUnitCode;
    }

    public void setBusinessUntiCode(String businessUnitCode) {
        this.businessUnitCode = businessUnitCode;
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

    public Fulfillment(String businessUnitCode, String productName, String storeName) {
        this.businessUnitCode = businessUnitCode;
        this.productName = productName;
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "Fulfillment{" +
                "businessUnitCode='" + businessUnitCode + '\'' +
                ", productName='" + productName + '\'' +
                ", storeName='" + storeName + '\'' +
                '}';
    }
}
