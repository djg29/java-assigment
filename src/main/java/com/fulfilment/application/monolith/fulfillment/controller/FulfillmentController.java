package com.fulfilment.application.monolith.fulfillment.controller;


import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.service.FulFillmentService;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentController {

    private static final Logger LOGGER = Logger.getLogger(FulfillmentController.class.getName());

    @Inject FulFillmentService service;

    @GET
    public List<Fulfillment> getAllfulfillments() {
        var lst = service.listAllFulfillments();
        return lst;
    }

    @POST
    @Transactional
    public Response create(Fulfillment fulfillment) {
        service.createFulfillment(fulfillment);
        return Response.ok(fulfillment).status(201).build();
    }
}
