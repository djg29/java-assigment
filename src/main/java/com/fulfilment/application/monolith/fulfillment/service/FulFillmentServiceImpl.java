package com.fulfilment.application.monolith.fulfillment.service;

import com.fulfilment.application.monolith.fulfillment.FullfilmentFailureException;
import com.fulfilment.application.monolith.fulfillment.dao.FulfillmentRepo;
import com.fulfilment.application.monolith.fulfillment.dao.entity.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.WebApplicationException;
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

    /**
     *
     * @param fl
     * @return Fulfillment
     * 1. Each `Product` can be fulfilled by a maximum of 2 different `Warehouses` per `Store`
     * 2. Each `Store` can be fulfilled by a maximum of 3 different `Warehouses`
     * 3. Each `Warehouse` can store maximally 5 types of `Products`
     *
     */

    @Override
    public Fulfillment createFulfillment(Fulfillment fl) {
        LOGGER.infof("Create fulfillment with %s", fl);
        try {
            if (!repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName(fl.businessUnitCode, fl.storeName, fl.productName).isEmpty()) {
                throw new FullfilmentFailureException("Assignment already exists");
            }

            var productWarehouses = repo.findByStoreNameAndProductName(fl.storeName, fl.productName);

            if (productWarehouses.size() >= 2) {
                throw new FullfilmentFailureException("Product in store has maxed out");
            }

            long warehousesForStore = repo.countDistinctWarehousesByStore(fl.storeName);

            if (warehousesForStore >= 3) {
                throw new FullfilmentFailureException("Store already has max warehouses assigned to it");
            }

            long productTypesForWarehouse = repo.countDistinctProductsByWarehouse(fl.businessUnitCode);

            if (productTypesForWarehouse >= 5) {
                throw new FullfilmentFailureException("Warehouse already stores max 5 product types");
            }

            repo.persist(FulfillmentAssignment.fromFulfillment(fl));
        } catch (PersistenceException e) {
            LOGGER.error("Database error", e);
            throw new WebApplicationException("Internal database error", 500);
        }
        return fl;
    }
}
