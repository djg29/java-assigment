package com.fulfilment.application.monolith.location;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class LocationGatewayTest {

    private final LocationGateway gateway = new LocationGateway();

    @ParameterizedTest
    @CsvSource({
            "ZWOLLE-001, 1, 40",
            "ZWOLLE-002, 2, 50",
            "AMSTERDAM-001, 5, 100",
            "AMSTERDAM-002, 3, 75",
            "TILBURG-001, 1, 40",
            "HELMOND-001, 1, 45",
            "EINDHOVEN-001, 2, 70",
            "VETSBY-001, 1, 90"
    })
    void testResolveByIdentifier_ExistingIdentifiers_ReturnsLocationWithCorrectFields(
            String identifier, int expectedCapacity, int expectedVolume) {
        // When
        Location location = gateway.resolveByIdentifier(identifier);

        // Then
        assertNotNull(location);
        assertEquals(identifier, location.identification);
    }

    @Test
    void testResolveByIdentifier_NonExistingIdentifier_ReturnsNull() {
        // Given
        String nonExisting = "UNKNOWN-999";

        // When
        Location location = gateway.resolveByIdentifier(nonExisting);

        // Then
        assertNull(location);
    }

    @ParameterizedTest
    @ValueSource(strings = {"zwolle-001", "Amsterdam-001", "tilburg-001", "HELMOND-999"})
    void testResolveByIdentifier_CaseSensitive_ReturnsNullForDifferentCase(String identifierWithWrongCase) {
        // When
        Location location = gateway.resolveByIdentifier(identifierWithWrongCase);

        // Then
        assertNull(location, "Identifier should be matched case‑sensitively");
    }

}
