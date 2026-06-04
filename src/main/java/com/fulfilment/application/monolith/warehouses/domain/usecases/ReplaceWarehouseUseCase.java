package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(ReplaceWarehouseUseCase.class.getName());

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

    /**
     *
     * @param newWarehouse
     * Bug fix
     * Ensure the new warehouse's capacity can accommodate the stock from the warehouse being replaced.
     */
  @Override
  public void replace(Warehouse newWarehouse) {
    LOGGER.info("replacing warehouse: " + newWarehouse);
    try {
        var existing = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
        if (existing.stock.equals(newWarehouse.stock) && newWarehouse.capacity >= existing.capacity) {
            warehouseStore.update(newWarehouse);
        }

    } catch (PersistenceException e) {
        LOGGER.error("Database error", e);
        throw new WebApplicationException("Internal database error", 500);
    }
  }
}
