package com.fulfilment.application.monolith.fulfillment.dao;

import com.fulfilment.application.monolith.fulfillment.dao.entity.FulfillmentAssignment;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import java.util.List;

@ApplicationScoped
public class FulfillmentRepo implements PanacheRepository<FulfillmentAssignment> {

    public List<FulfillmentAssignment> findByWarehouseBusinessUnitCodeAndStoreNameAndProductName(String businessUnitCode, String storeName, String productName) {
        return find("storeName = : storeName and warehouseBusinessUnitCode = : warehouseBusinessUnitCode and productName = : productName",
                Parameters.with("warehouseBusinessUnitCode", businessUnitCode).and("storeName", storeName).and("productName", productName)).list();
    };

    public List<FulfillmentAssignment> findByWarehouseBusinessUnitCode(String businessUnitCode) {
        return find("warehouseBusinessUnitCode = : warehouseBusinessUnitCode",
                Parameters.with("warehouseBusinessUnitCode", businessUnitCode)).list();
    };

    public List<FulfillmentAssignment> findByStoreNameAndProductName(String storeName, String productName) {
        return find("storeName = : storeName and productName = : productName",
                Parameters.with("storeName", storeName).and("productName", productName)).list();
    };

    public List<FulfillmentAssignment> findByStoreName(String storeName) {
        return find("storeName = : storeName", Parameters.with("storeName", storeName)).list();
    };

    public long countDistinctWarehousesByStore(String storeName) {
        return find("SELECT COUNT(DISTINCT f.warehouseBusinessUnitCode) FROM FulfillmentAssigment f WHERE f.storeName = : storeName",
                Parameters.with("storeName", storeName)).project(Long.class).firstResult();
    }

}
