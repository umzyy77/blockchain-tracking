package com.example.blockchain;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.blockchain.dto.BlockResponse;
import com.example.blockchain.dto.TicketRequest;
import com.example.blockchain.exception.BlockNotFoundException;
import com.example.blockchain.mapper.BlockMapper;

@DisplayName("Tests d'intégration - BlockchainController (RESTful)")
class BlockchainControllerIntegrationTest {

    private BlockchainController controller;

    @BeforeEach
    void setUp() {
        controller = new BlockchainController(new Blockchain(), new BlockMapper());
    }

    // --- GET /api/blocks ---

    @Test
    @DisplayName("GET /api/blocks retourne la chaîne avec le bloc de genèse")
    void getAllReturnsGenesisBlock() {
        List<BlockResponse> chain = controller.getAll();

        assertNotNull(chain);
        assertEquals(1, chain.size());
        assertEquals(0, chain.getFirst().index());
    }

    @Test
    @DisplayName("GET /api/blocks retourne tous les blocs après ajout")
    void getAllReturnsAllBlocks() {
        controller.create(new TicketRequest("Bloc 1", null, null, null, null));
        controller.create(new TicketRequest("Bloc 2", null, null, null, null));

        assertEquals(3, controller.getAll().size());
    }

    // --- GET /api/blocks/{index} ---

    @Test
    @DisplayName("GET /api/blocks/0 retourne le bloc de genèse")
    void getByIdReturnsGenesisBlock() {
        BlockResponse response = controller.getById(0);

        assertNotNull(response);
        assertEquals(0, response.index());
    }

    @Test
    @DisplayName("GET /api/blocks/{index} lève BlockNotFoundException si index invalide")
    void getByIdThrowsBlockNotFoundException() {
        assertThrows(BlockNotFoundException.class, () -> controller.getById(999));
    }

    @Test
    @DisplayName("GET /api/blocks/{index} retourne le bon bloc après ajout")
    void getByIdReturnsCorrectBlock() {
        controller.create(new TicketRequest("Test", "EVT-001", "Stromae", "ACHETE", "Alice"));

        BlockResponse response = controller.getById(1);

        assertEquals("Test", response.data());
        assertEquals("EVT-001", response.eventId());
    }

    // --- POST /api/blocks ---

    @Test
    @DisplayName("POST /api/blocks crée un bloc simple")
    void createSimpleBlock() {
        BlockResponse response = controller.create(
                new TicketRequest("Colis expédié", null, null, null, null));

        assertNotNull(response);
        assertEquals("Colis expédié", response.data());
    }

    @Test
    @DisplayName("POST /api/blocks crée un bloc avec données de billetterie")
    void createTicketBlock() {
        BlockResponse response = controller.create(
                new TicketRequest("Ticket acheté", "EVT-001", "Stromae", "ACHETE", "Alice"));

        assertEquals("EVT-001", response.eventId());
        assertEquals("Stromae", response.artist());
        assertEquals("ACHETE", response.status());
        assertEquals("Alice", response.owner());
    }

    @Test
    @DisplayName("POST /api/blocks sans data utilise la valeur par défaut")
    void createWithoutDataUsesDefault() {
        BlockResponse response = controller.create(
                new TicketRequest(null, null, null, null, null));

        assertEquals("Opération ticket", response.data());
    }

    // --- Workflow complet ---

    @Test
    @DisplayName("Workflow complet RESTful : POST puis GET all puis GET by id")
    void fullRestWorkflow() {
        controller.create(new TicketRequest("Premier bloc", null, null, null, null));
        controller.create(new TicketRequest("Ticket", "EVT-002", "PNL", "ACHETE", "Bob"));

        assertEquals(3, controller.getAll().size());

        BlockResponse block = controller.getById(2);
        assertEquals("EVT-002", block.eventId());
        assertEquals("PNL", block.artist());
    }
}
