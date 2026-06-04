package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.fulfillment.FullfilmentFailureException;
import com.fulfilment.application.monolith.fulfillment.controller.FulfillmentController;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ReplaceWarehouseOperation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.List;

@Path("warehouse")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class.getName());

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
  public Warehouse createANewWarehouseUnit(@Valid Warehouse data) {
    createWarehouseOperation.create(data);
    return data;
  }

  @GET
  @Path("{id}")
  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    var entity = warehouseRepository.findByBusinessUnitCode(id);
    if (entity == null) {
        throw new WebApplicationException("Warehouse with id of " + id + " does not exist.", 404);
    }
    return entity;
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
      String businessUnitCode, @Valid Warehouse data) {
    replaceWarehouseOperation.replace(data);
    return data;
  }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<RuntimeException> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(RuntimeException exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = ((WebApplicationException) exception).getResponse().getStatus();

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code).entity(exceptionJson).build();
        }
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
