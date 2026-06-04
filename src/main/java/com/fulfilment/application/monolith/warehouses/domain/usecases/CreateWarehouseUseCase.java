package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.adapters.restapi.WarehouseResourceImpl;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(CreateWarehouseUseCase.class.getName());

  private final WarehouseStore warehouseStore;
  private final LocationGateway locationGateway;

  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationGateway locationGateway) {
    this.warehouseStore = warehouseStore;
    this.locationGateway = locationGateway;
  }

    /**
     *
     * @param warehouse
     * Bug fix
     * 1. Check if a new warehouse can be created at the specified location or if the maximum number
     * of warehouses has already been reached.
     * 2. Validate the warehouse capacity, ensuring it does not exceed the maximum capacity associated
     * with the location and that it can handle the stock informed.
     */
  @Override
  public void create(Warehouse warehouse) {
    LOGGER.info("creating warehouse: " + warehouse);
    var lg = new LocationGateway();
    var loc = lg.resolveByIdentifier(warehouse.getLocation());
    try {
        var existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
        if (loc != null && existing == null) {
            var warehousesByLoc = warehouseStore.findByLocation(warehouse.location).stream().filter(w -> w.getArchivedAt() != null).toList();
            var maxCapacityExceeded = warehousesByLoc.stream().mapToInt(Warehouse::getCapacity).sum() + warehouse.capacity > loc.maxCapacity;
            if (!maxCapacityExceeded && warehousesByLoc.size() < loc.maxNumberOfWarehouses) {
                warehouseStore.create(warehouse);
            }
        }
    } catch (PersistenceException e) {
        LOGGER.error("Database error", e);
        throw new WebApplicationException("Internal database error", 500);
    }
  }
}
