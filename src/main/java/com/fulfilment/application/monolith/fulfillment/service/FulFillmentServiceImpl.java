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

    @Override
    public Fulfillment createFulfillment(Fulfillment fl) {
        LOGGER.infof("Create fulfillment with %s", fl);
        try {
            if (repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName(fl.businessUnitCode, fl.storeName, fl.productName).size() != 0) {
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

            repo.persist(FulfillmentAssignment.fromFulfillment(fl));
        } catch (PersistenceException e) {
            LOGGER.error("Database error", e);
            throw new WebApplicationException("Internal database error", 500);
        }
        return fl;
    }
}
