package com.fulfilment.application.monolith.fulfillment.dao.entity;

import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class FulfillmentAssignment {

    @Id @GeneratedValue public Long id;

    @Column(nullable = false)
    public String warehouseBusinessUnitCode;

    @Column(nullable = false)
    public String storeName;

    @Column(nullable = false)
    public String productName;

    public Fulfillment toFulfillment() {
        return new Fulfillment(this.warehouseBusinessUnitCode, this.productName, this.storeName);
    }

    public static FulfillmentAssignment fromFulfillment(Fulfillment fulfillment) {
        var fl = new FulfillmentAssignment();
        fl.productName = fulfillment.productName;
        fl.storeName = fulfillment.storeName;
        fl.warehouseBusinessUnitCode = fulfillment.businessUnitCode;
        return fl;
    }

}
