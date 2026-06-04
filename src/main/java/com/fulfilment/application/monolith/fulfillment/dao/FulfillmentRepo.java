package com.fulfilment.application.monolith.fulfillment.dao;

import com.fulfilment.application.monolith.fulfillment.controller.FulfillmentController;
import com.fulfilment.application.monolith.fulfillment.dao.entity.FulfillmentAssignment;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.validation.constraints.NotBlank;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class FulfillmentRepo implements PanacheRepository<FulfillmentAssignment> {

    private static final Logger LOGGER = Logger.getLogger(FulfillmentRepo.class.getName());

    public List<FulfillmentAssignment> findByWarehouseBusinessUnitCodeAndStoreNameAndProductName(String businessUnitCode, String storeName, String productName) {
        LOGGER.infof("Find by businessUnitCode=%s storeName=%s productName=%s", businessUnitCode, storeName, productName);
        return find("storeName = : storeName and warehouseBusinessUnitCode = : warehouseBusinessUnitCode and productName = : productName",
                Parameters.with("warehouseBusinessUnitCode", businessUnitCode).and("storeName", storeName).and("productName", productName)).list();
    };

    public List<FulfillmentAssignment> findByWarehouseBusinessUnitCode(String businessUnitCode) {
        LOGGER.infof("Find by businessUnitCode=%s", businessUnitCode);
        return find("warehouseBusinessUnitCode = : warehouseBusinessUnitCode",
                Parameters.with("warehouseBusinessUnitCode", businessUnitCode)).list();
    };

    public List<FulfillmentAssignment> findByStoreNameAndProductName(String storeName, String productName) {
        LOGGER.infof("Find by storeName=%s productName=%s", storeName, productName);
        return find("storeName = : storeName and productName = : productName",
                Parameters.with("storeName", storeName).and("productName", productName)).list();
    };

    public List<FulfillmentAssignment> findByStoreName(String storeName) {
        LOGGER.infof("Find by storeName=%s", storeName);
        return find("storeName = : storeName", Parameters.with("storeName", storeName)).list();
    };

    public long countDistinctWarehousesByStore(String storeName) {
        LOGGER.infof("Find distinct warehouses by storeName=%s", storeName);
        return find("SELECT COUNT(DISTINCT f.warehouseBusinessUnitCode) FROM FulfillmentAssignment f WHERE f.storeName = : storeName",
                Parameters.with("storeName", storeName)).project(Long.class).firstResult();
    }

    public long countDistinctProductsByWarehouse(String businessUnitCode) {
        return getEntityManager().createQuery("SELECT COUNT(DISTINCT productName) FROM FulfillmentAssignment WHERE warehouseBusinessUnitCode = :warehouseBusinessUnitCode", Long.class)
                .setParameter("warehouseBusinessUnitCode", businessUnitCode).getSingleResult();
    }
}
