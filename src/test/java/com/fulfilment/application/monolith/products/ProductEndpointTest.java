package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ProductEndpointTest {

    @Inject
    ProductResource productResource;  // real resource, injected by Quarkus

    @InjectMock
    ProductRepository productRepository;  // mocked repository

    @Inject
    ObjectMapper objectMapper;  // for exception tests

    // Helper to create a sample product
    private Product sampleProduct(Long id, String name) {
        Product p = new Product();
        p.id = id;
        p.name = name;
        p.description = "desc";
        p.price = BigDecimal.valueOf(100.0);
        p.stock = 10;
        return p;
    }

    // ------------------------------------------------
    // GET /product
    // ------------------------------------------------
    @Test
    void get_shouldReturnAllProductsSortedByName() {
        List<Product> products = List.of(
                sampleProduct(1L, "Apple"),
                sampleProduct(2L, "Banana")
        );
        when(productRepository.listAll(any(Sort.class))).thenReturn(products);

        List<Product> result = productResource.get();

        assertEquals(2, result.size());
        assertEquals("Apple", result.get(0).name);
        assertEquals("Banana", result.get(1).name);
        verify(productRepository, times(1)).listAll(any(Sort.class));
    }

    @Test
    void get_whenNoProducts_returnsEmptyList() {
        when(productRepository.listAll(any(Sort.class))).thenReturn(List.of());
        List<Product> result = productResource.get();
        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------
    // GET /product/{id}
    // ------------------------------------------------
    @Test
    void getSingle_whenProductExists_returnsProduct() {
        Product product = sampleProduct(1L, "Laptop");
        when(productRepository.findById(1L)).thenReturn(product);

        Product result = productResource.getSingle(1L);

        assertSame(product, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void getSingle_whenProductNotExists_throws404() {
        when(productRepository.findById(99L)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> productResource.getSingle(99L));
        assertEquals(404, ex.getResponse().getStatus());
        assertTrue(ex.getMessage().contains("99"));
    }

    // ------------------------------------------------
    // POST /product
    // ------------------------------------------------
    @Test
    void create_withValidProduct_returns201() {
        Product product = sampleProduct(null, "New Product");
        Response response = productResource.create(product);

        assertEquals(201, response.getStatus());
        assertSame(product, response.getEntity());
        verify(productRepository).persist(product);
    }

    @Test
    void create_withIdAlreadySet_throws422() {
        Product product = sampleProduct(100L, "Should fail");

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> productResource.create(product));
        assertEquals(422, ex.getResponse().getStatus());
        assertTrue(ex.getMessage().contains("Id was invalidly set"));
        verify(productRepository, never()).persist((Product) any());
    }

    // ------------------------------------------------
    // PUT /product/{id}
    // ------------------------------------------------
    @Test
    void update_whenValidAndProductExists_updatesAndReturnsEntity() {
        Product existing = sampleProduct(1L, "Old Name");
        Product updatePayload = new Product();
        updatePayload.name = "New Name";
        updatePayload.description = "New Desc";
        updatePayload.price = BigDecimal.valueOf(200.0);
        updatePayload.stock = 20;

        when(productRepository.findById(1L)).thenReturn(existing);

        Product result = productResource.update(1L, updatePayload);

        assertSame(existing, result);
        assertEquals("New Name", existing.name);
        assertEquals("New Desc", existing.description);
        assertEquals(BigDecimal.valueOf(200.0), existing.price);
        assertEquals(20, existing.stock);
        verify(productRepository).persist(existing);
    }

    @Test
    void update_whenNameIsNull_throws422() {
        Product updatePayload = new Product();
        updatePayload.name = null;

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> productResource.update(1L, updatePayload));
        assertEquals(422, ex.getResponse().getStatus());
        assertTrue(ex.getMessage().contains("Product Name was not set"));
        verify(productRepository, never()).findById(any());
    }

    @Test
    void update_whenProductNotFound_throws404() {
        Product updatePayload = new Product();
        updatePayload.name = "Any Name";
        when(productRepository.findById(99L)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> productResource.update(99L, updatePayload));
        assertEquals(404, ex.getResponse().getStatus());
        verify(productRepository, never()).persist((Product) any());
    }

    // ------------------------------------------------
    // DELETE /product/{id}
    // ------------------------------------------------
    @Test
    void delete_whenProductExists_returns204AndDeletes() {
        Product product = sampleProduct(1L, "To Delete");
        when(productRepository.findById(1L)).thenReturn(product);

        Response response = productResource.delete(1L);

        assertEquals(204, response.getStatus());
        verify(productRepository).delete(product);
    }

    @Test
    void delete_whenProductNotFound_throws404() {
        when(productRepository.findById(99L)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> productResource.delete(99L));
        assertEquals(404, ex.getResponse().getStatus());
        verify(productRepository, never()).delete(any());
    }

    // ------------------------------------------------
    // ErrorMapper exception mapping test
    // ------------------------------------------------
    @Test
    void errorMapper_mapsWebApplicationException() {
        ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
        mapper.objectMapper = objectMapper; // manually inject ObjectMapper

        WebApplicationException webEx = new WebApplicationException("Custom error", 409);
        Response response = mapper.toResponse(webEx);

        assertEquals(409, response.getStatus());
        JsonNode json = response.readEntity(JsonNode.class);
        assertEquals(409, json.get("code").asInt());
        assertEquals("Custom error", json.get("error").asText());
        assertEquals(webEx.getClass().getName(), json.get("exceptionType").asText());
    }

    @Test
    void errorMapper_mapsGenericExceptionTo500() {
        ProductResource.ErrorMapper mapper = new ProductResource.ErrorMapper();
        mapper.objectMapper = objectMapper;

        RuntimeException runtimeEx = new RuntimeException("Something broke");
        Response response = mapper.toResponse(runtimeEx);

        assertEquals(500, response.getStatus());
        JsonNode json = response.readEntity(JsonNode.class);
        assertEquals(500, json.get("code").asInt());
        assertEquals("Something broke", json.get("error").asText());
        assertEquals(runtimeEx.getClass().getName(), json.get("exceptionType").asText());
    }
}
