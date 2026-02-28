package com.example.blockchain.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - TicketRequest DTO")
class TicketRequestTest {

    @Test
    @DisplayName("Tous les champs sont conservés quand renseignés")
    void allFieldsKeptWhenProvided() {
        TicketRequest request = new TicketRequest("Achat", "EVT-001", "Stromae", "ACHETE", "Alice");

        assertEquals("Achat", request.data());
        assertEquals("EVT-001", request.eventId());
        assertEquals("Stromae", request.artist());
        assertEquals("ACHETE", request.status());
        assertEquals("Alice", request.owner());
    }

    @Test
    @DisplayName("Tous les champs null sont remplacés par les valeurs par défaut")
    void nullFieldsReplacedByDefaults() {
        TicketRequest request = new TicketRequest(null, null, null, null, null);

        assertEquals("Opération ticket", request.data());
        assertEquals("", request.eventId());
        assertEquals("", request.artist());
        assertEquals("", request.status());
        assertEquals("", request.owner());
    }

    @Test
    @DisplayName("data vide est remplacé par défaut, les autres champs sont gardés")
    void blankDataReplacedOthersKept() {
        TicketRequest request = new TicketRequest("  ", "EVT-002", "PNL", "REVENDU", "Bob");

        assertEquals("Opération ticket", request.data());
        assertEquals("EVT-002", request.eventId());
        assertEquals("PNL", request.artist());
        assertEquals("REVENDU", request.status());
        assertEquals("Bob", request.owner());
    }
}
