package com.fulfilment.application.monolith.fulfillment.controller;


import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.service.FulFillmentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;

import org.jboss.logging.Logger;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentController {

    private static final Logger LOGGER = Logger.getLogger(FulfillmentController.class.getName());

    @Inject FulFillmentService service;
    @Inject
    Validator validator;

    @GET
    public List<Fulfillment> getAllfulfillments() {
        LOGGER.info("get all available fulfillments");
        var lst = service.listAllFulfillments();
        LOGGER.infof("returned a list with %d fulfillments", lst.size());
        return lst;
    }

    @POST
    @Transactional
    public Response create(@Valid Fulfillment fulfillment) {
        LOGGER.infof("create fulfillment with %s", fulfillment);
        service.createFulfillment(fulfillment);
        LOGGER.info("successfully created fulfillment");
        return Response.ok(fulfillment).status(201).build();
    }
}
