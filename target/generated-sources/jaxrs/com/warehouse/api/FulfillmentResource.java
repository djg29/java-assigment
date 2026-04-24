package com.warehouse.api;

import com.warehouse.api.beans.Fullfilment;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * A JAX-RS interface. An implementation of this interface must be provided.
 */
@Path("/fulfillment")
public interface FulfillmentResource {
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  Fullfilment createAFulfillmentRequest(@NotNull Fullfilment data);
}
