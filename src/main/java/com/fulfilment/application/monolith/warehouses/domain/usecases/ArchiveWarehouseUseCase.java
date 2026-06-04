package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(ArchiveWarehouseUseCase.class.getName());

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    warehouse.setArchivedAt(LocalDateTime.now());
    LOGGER.info("archiving warehouse: " + warehouse);
    try {
        warehouseStore.update(warehouse);
    } catch (PersistenceException e) {
        LOGGER.error("Database error", e);
        throw new WebApplicationException("Internal database error", 500);
    }
  }
}
