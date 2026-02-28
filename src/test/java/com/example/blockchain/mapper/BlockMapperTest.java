package com.example.blockchain.mapper;

import com.example.blockchain.Block;
import com.example.blockchain.dto.BlockResponse;
import com.example.blockchain.dto.ValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - BlockMapper")
class BlockMapperTest {

    private BlockMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BlockMapper();
    }

    @Test
    @DisplayName("toResponse mappe tous les champs d'un bloc simple")
    void toResponseMapsSimpleBlock() {
        Block block = new Block(1, "Données test", "prevhash");

        BlockResponse response = mapper.toResponse(block);

        assertEquals(1, response.index());
        assertEquals("Données test", response.data());
        assertEquals("prevhash", response.previousHash());
        assertNotNull(response.hash());
        assertNotNull(response.timestamp());
        assertEquals(0, response.nonce());
        assertNull(response.eventId());
        assertNull(response.artist());
        assertNull(response.status());
        assertNull(response.owner());
    }

    @Test
    @DisplayName("toResponse mappe tous les champs d'un bloc enrichi")
    void toResponseMapsEnrichedBlock() {
        Block block = new Block(2, "Ticket acheté", "prev",
                "EVT-001", "Stromae", "ACHETE", "Alice");

        BlockResponse response = mapper.toResponse(block);

        assertEquals(2, response.index());
        assertEquals("Ticket acheté", response.data());
        assertEquals("EVT-001", response.eventId());
        assertEquals("Stromae", response.artist());
        assertEquals("ACHETE", response.status());
        assertEquals("Alice", response.owner());
    }

    @Test
    @DisplayName("toResponseList mappe une liste de blocs")
    void toResponseListMapsList() {
        Block block1 = new Block(0, "Genèse", "0");
        Block block2 = new Block(1, "Bloc 1", block1.getHash());
        Block block3 = new Block(2, "Bloc 2", block2.getHash(),
                "EVT-001", "Artiste", "ACHETE", "Bob");

        List<BlockResponse> responses = mapper.toResponseList(List.of(block1, block2, block3));

        assertEquals(3, responses.size());
        assertEquals("Genèse", responses.get(0).data());
        assertEquals("Bloc 1", responses.get(1).data());
        assertEquals("EVT-001", responses.get(2).eventId());
    }

    @Test
    @DisplayName("toResponseList sur liste vide retourne liste vide")
    void toResponseListEmptyReturnsEmpty() {
        List<BlockResponse> responses = mapper.toResponseList(List.of());
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("toValidationResponse avec chaîne valide")
    void toValidationResponseValid() {
        ValidationResponse response = mapper.toValidationResponse(true, 5);

        assertTrue(response.valid());
        assertEquals(5, response.size());
        assertEquals("La chaîne est valide", response.message());
    }

    @Test
    @DisplayName("toValidationResponse avec chaîne corrompue")
    void toValidationResponseInvalid() {
        ValidationResponse response = mapper.toValidationResponse(false, 3);

        assertFalse(response.valid());
        assertEquals(3, response.size());
        assertEquals("ATTENTION: La chaîne est corrompue !", response.message());
    }
}
