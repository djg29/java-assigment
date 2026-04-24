package com.fulfilment.application.monolith.fulfillment.service;

import com.fulfilment.application.monolith.fulfillment.dao.FulfillmentRepo;
import com.fulfilment.application.monolith.fulfillment.dao.entity.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FulFillmentServiceImpl implements FulFillmentService {

    private static final Logger LOGGER = Logger.getLogger(FulFillmentService.class.getName());

    @Inject FulfillmentRepo repo;

    @Override
    public List<Fulfillment> listAllFulfillments() {
        LOGGER.infof("Fetch a list of all fulfillments");
        return repo.listAll().stream().map(FulfillmentAssignment::toFulfillment).toList();
    }

    @Override
    public List<Fulfillment> findByWarehouseProductAndStore(String businessUnitCode, String storeName, String productName) {
        LOGGER.infof("Fetch a list of all fulfillments by businessUnitCode= %s storeName= %s productName = %productName", businessUnitCode, storeName, productName);
        return repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName(businessUnitCode, storeName, productName).stream().map(FulfillmentAssignment::toFulfillment).toList();
    }

    @Override
    public List<Fulfillment> findByWarehouse(String businessUnitCode) {
        return repo.findByWarehouseBusinessUnitCode(businessUnitCode).stream().map(FulfillmentAssignment::toFulfillment).toList();
    }

    @Override
    public List<Fulfillment> findByProductAndStore(String storeName, String productName) {
        return repo.findByStoreNameAndProductName(storeName, productName).stream().map(FulfillmentAssignment::toFulfillment).toList();
    }

    @Override
    public List<Fulfillment> findByStore(String storeName) {
        return repo.findByStoreName(storeName).stream().map(FulfillmentAssignment::toFulfillment).toList();
    }

    @Override
    public Fulfillment createFulfillment(Fulfillment fl) {
        LOGGER.infof("Create fulfillment with %s", fl);
        if (repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName(fl.businessUnitCode, fl.storeName, fl.productName).size() != 0) {
             throw new RuntimeException("Assignment already exists");
         }

         var productWarehouses = repo.findByStoreNameAndProductName(fl.storeName, fl.productName);

         if (productWarehouses.size() >=2) {
             throw new RuntimeException("Product in store has maxed out");
         }

         long warehousesForStore = repo.countDistinctWarehousesByStore(fl.storeName);

         if (warehousesForStore >= 3) {
             throw new RuntimeException("Store already has max warehouses assigned to it");
         }

        repo.persist(FulfillmentAssignment.fromFulfillment(fl));
        return fl;
    }
}
