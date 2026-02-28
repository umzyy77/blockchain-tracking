package com.example.blockchain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - Block")
class BlockTest {

    @Test
    @DisplayName("Le hash est calculé automatiquement à la création")
    void hashIsComputedOnCreation() {
        Block block = new Block(0, "Test", "0");

        assertNotNull(block.hash);
        assertFalse(block.hash.isEmpty());
        assertEquals(64, block.hash.length(), "Un hash SHA-256 fait 64 caractères hex");
    }

    @Test
    @DisplayName("Le hash est déterministe (recalcul identique)")
    void hashIsDeterministic() {
        Block block = new Block(1, "Donnée", "abc123");

        String recalculated = block.calculateHash();
        assertEquals(block.hash, recalculated);
    }

    @Test
    @DisplayName("Deux blocs avec des données différentes ont des hashs différents")
    void differentDataProducesDifferentHash() {
        Block block1 = new Block(0, "Donnée A", "0");
        Block block2 = new Block(0, "Donnée B", "0");

        assertNotEquals(block1.hash, block2.hash);
    }

    @Test
    @DisplayName("Le previousHash est correctement stocké")
    void previousHashIsStored() {
        String prevHash = "aabbccdd";
        Block block = new Block(1, "Data", prevHash);

        assertEquals(prevHash, block.previousHash);
    }

    @Test
    @DisplayName("Le timestamp est non null à la création")
    void timestampIsSet() {
        Block block = new Block(0, "Data", "0");

        assertNotNull(block.timestamp);
        assertFalse(block.timestamp.isEmpty());
    }

    @Test
    @DisplayName("Constructeur enrichi initialise les champs métier")
    void enrichedConstructorSetsBusinessFields() {
        Block block = new Block(1, "Ticket acheté", "prev",
                "EVT-001", "Stromae", "ACHETE", "Alice");

        assertEquals("EVT-001", block.eventId);
        assertEquals("Stromae", block.artist);
        assertEquals("ACHETE", block.status);
        assertEquals("Alice", block.owner);
    }

    @Test
    @DisplayName("Les champs métier influencent le hash")
    void businessFieldsAffectHash() {
        Block block1 = new Block(1, "Data", "prev",
                "EVT-001", "Stromae", "ACHETE", "Alice");
        Block block2 = new Block(1, "Data", "prev",
                "EVT-002", "Stromae", "ACHETE", "Alice");

        assertNotEquals(block1.hash, block2.hash);
    }

    @Test
    @DisplayName("Le nonce est initialisé à 0")
    void nonceIsInitializedToZero() {
        Block block = new Block(0, "Data", "0");
        assertEquals(0, block.nonce);
    }

    @Test
    @DisplayName("Modifier le nonce change le hash recalculé")
    void changingNonceChangesHash() {
        Block block = new Block(0, "Data", "0");
        String originalHash = block.hash;

        block.nonce = 42;
        String newHash = block.calculateHash();

        assertNotEquals(originalHash, newHash);
    }

    @Test
    @DisplayName("toString contient les informations principales")
    void toStringContainsMainInfo() {
        Block block = new Block(0, "Livraison", "0",
                "EVT-001", "Artiste", "ACHETE", "Bob");

        String str = block.toString();
        assertTrue(str.contains("Livraison"));
        assertTrue(str.contains("EVT-001"));
        assertTrue(str.contains("Artiste"));
        assertTrue(str.contains("ACHETE"));
        assertTrue(str.contains("Bob"));
    }

    @Test
    @DisplayName("Constructeur simple laisse les champs métier à null")
    void simpleConstructorLeavesBusinessFieldsNull() {
        Block block = new Block(0, "Data", "0");

        assertNull(block.eventId);
        assertNull(block.artist);
        assertNull(block.status);
        assertNull(block.owner);
    }
}
