package com.example.blockchain;

import com.example.blockchain.dto.BlockRequest;
import com.example.blockchain.dto.BlockResponse;
import com.example.blockchain.dto.TicketRequest;
import com.example.blockchain.dto.ValidationResponse;
import com.example.blockchain.mapper.BlockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests d'intégration - BlockchainController")
class BlockchainControllerIntegrationTest {

    private BlockchainController controller;

    @BeforeEach
    void setUp() {
        controller = new BlockchainController(new Blockchain(), new BlockMapper());
    }

    @Test
    @DisplayName("GET /chain retourne la chaîne avec le bloc de genèse")
    void getChainReturnsGenesisBlock() {
        List<BlockResponse> chain = controller.getChain();

        assertNotNull(chain);
        assertEquals(1, chain.size());
        assertEquals(0, chain.getFirst().index());
        assertEquals("0", chain.getFirst().previousHash());
    }

    @Test
    @DisplayName("POST /block ajoute un bloc et le retourne")
    void postBlockAddsBlockAndReturnsIt() {
        BlockResponse result = controller.addBlock(new BlockRequest("Colis expédié"));

        assertNotNull(result);
        assertEquals("Colis expédié", result.data());
        assertNotNull(result.hash());
        assertNotNull(result.previousHash());
    }

    @Test
    @DisplayName("POST /block sans data utilise la valeur par défaut")
    void postBlockWithoutDataUsesDefault() {
        BlockResponse result = controller.addBlock(new BlockRequest(null));

        assertEquals("Bloc sans données", result.data());
    }

    @Test
    @DisplayName("POST /block avec data vide utilise la valeur par défaut")
    void postBlockWithBlankDataUsesDefault() {
        BlockResponse result = controller.addBlock(new BlockRequest("   "));

        assertEquals("Bloc sans données", result.data());
    }

    @Test
    @DisplayName("POST /ticket ajoute un ticket avec données métier")
    void postTicketAddsEnrichedBlock() {
        BlockResponse result = controller.addTicketBlock(
                new TicketRequest("Ticket acheté", "EVT-001", "Stromae", "ACHETE", "Alice"));

        assertNotNull(result);
        assertEquals("EVT-001", result.eventId());
        assertEquals("Stromae", result.artist());
        assertEquals("ACHETE", result.status());
        assertEquals("Alice", result.owner());
    }

    @Test
    @DisplayName("POST /ticket sans champs optionnels utilise les valeurs par défaut")
    void postTicketWithoutOptionalFieldsUsesDefaults() {
        BlockResponse result = controller.addTicketBlock(
                new TicketRequest(null, null, null, null, null));

        assertEquals("Opération ticket", result.data());
        assertEquals("", result.eventId());
        assertEquals("", result.artist());
        assertEquals("", result.status());
        assertEquals("", result.owner());
    }

    @Test
    @DisplayName("GET /validate retourne que la chaîne est valide")
    void validateReturnsValid() {
        ValidationResponse result = controller.validate();

        assertTrue(result.valid());
        assertEquals("La chaîne est valide", result.message());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("GET /validate après ajout de blocs retourne la bonne taille")
    void validateAfterAddsReturnsCorrectSize() {
        controller.addBlock(new BlockRequest("Bloc 1"));
        controller.addBlock(new BlockRequest("Bloc 2"));

        ValidationResponse result = controller.validate();

        assertTrue(result.valid());
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("GET /export retourne du JSON contenant le hash")
    void exportReturnsJsonString() {
        String json = controller.exportJson();

        assertNotNull(json);
        assertTrue(json.contains("hash"));
        assertTrue(json.contains("previousHash"));
    }

    @Test
    @DisplayName("GET /export contient les données des blocs ajoutés")
    void exportContainsAddedBlockData() {
        controller.addBlock(new BlockRequest("Données exportées"));

        String json = controller.exportJson();

        assertTrue(json.contains("Données exportées"));
    }

    @Test
    @DisplayName("Workflow complet : ajout de blocs puis validation")
    void fullWorkflowViaController() {
        controller.addBlock(new BlockRequest("Premier bloc"));

        controller.addTicketBlock(
                new TicketRequest("Ticket", "EVT-002", "PNL", "ACHETE", "Bob"));

        ValidationResponse validation = controller.validate();
        assertTrue(validation.valid());
        assertEquals(3, validation.size());

        List<BlockResponse> chain = controller.getChain();
        assertEquals(3, chain.size());

        String json = controller.exportJson();
        assertTrue(json.contains("EVT-002"));
        assertTrue(json.contains("PNL"));
    }
}
