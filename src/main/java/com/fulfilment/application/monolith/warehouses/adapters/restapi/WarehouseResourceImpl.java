package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ReplaceWarehouseOperation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.transaction.Transactional;
import java.util.List;

@Path("warehouse")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject private WarehouseRepository warehouseRepository;
  @Inject private ArchiveWarehouseOperation archiveWarehouseOperation;
  @Inject private CreateWarehouseOperation createWarehouseOperation;
  @Inject private ReplaceWarehouseOperation replaceWarehouseOperation;

  @GET
  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @POST
  @Transactional
  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    createWarehouseOperation.create(data);
    return data;
  }

  @GET
  @Path("{id}")
  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    return warehouseRepository.findByBusinessUnitCode(id);
  }

  @DELETE
  @Transactional
  @Path("{id}")
  @Override
  public void archiveAWarehouseUnitByID(String id) {
    var warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse != null) {
      archiveWarehouseOperation.archive(warehouse);
    }
    // else do nothing for now
  }

  @POST
  @Transactional
  @Path("{businessUnitCode}/replacement")
  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    replaceWarehouseOperation.replace(data);
    return data;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
