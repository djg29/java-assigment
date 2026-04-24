
package com.warehouse.api.beans;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "businessUnitCode",
    "productName",
    "storeName"
})
@Generated("jsonschema2pojo")
public class Fullfilment {

    @JsonProperty("businessUnitCode")
    private String businessUnitCode;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("storeName")
    private String storeName;

    @JsonProperty("businessUnitCode")
    public String getBusinessUnitCode() {
        return businessUnitCode;
    }

    @JsonProperty("businessUnitCode")
    public void setBusinessUnitCode(String businessUnitCode) {
        this.businessUnitCode = businessUnitCode;
    }

    @JsonProperty("productName")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @JsonProperty("storeName")
    public String getStoreName() {
        return storeName;
    }

    @JsonProperty("storeName")
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

}
