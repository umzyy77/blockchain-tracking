package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - PBFT")
class PBFTTest {

    @Test
    @DisplayName("La validation fonctionne avec des nœuds configurés")
    void validationWorksWithNodes() {
        PBFT pbft = new PBFT();
        pbft.addNode("Noeud-1");
        pbft.addNode("Noeud-2");
        pbft.addNode("Noeud-3");
        pbft.addNode("Noeud-4");

        Block block = new Block(1, "Data", "prev");

        assertDoesNotThrow(() -> pbft.validate(block));
    }

    @Test
    @DisplayName("La validation ne plante pas sans nœuds")
    void validationHandlesNoNodes() {
        PBFT pbft = new PBFT();
        Block block = new Block(1, "Data", "prev");

        assertDoesNotThrow(() -> pbft.validate(block));
    }

    @Test
    @DisplayName("getName retourne PBFT")
    void getNameReturnsCorrectName() {
        PBFT pbft = new PBFT();
        assertTrue(pbft.getName().contains("PBFT"));
        assertTrue(pbft.getName().contains("Byzantine"));
    }

    @Test
    @DisplayName("Validation avec un seul nœud")
    void validationWithSingleNode() {
        PBFT pbft = new PBFT();
        pbft.addNode("Seul");

        Block block = new Block(1, "Data", "prev");
        assertDoesNotThrow(() -> pbft.validate(block));
    }

    @Test
    @DisplayName("Validation avec deux nœuds")
    void validationWithTwoNodes() {
        PBFT pbft = new PBFT();
        pbft.addNode("N1");
        pbft.addNode("N2");

        Block block = new Block(1, "Data", "prev");
        assertDoesNotThrow(() -> pbft.validate(block));
    }

    @Test
    @DisplayName("Validation avec nombre impair de nœuds")
    void validationWithOddNumberOfNodes() {
        PBFT pbft = new PBFT();
        pbft.addNode("N1");
        pbft.addNode("N2");
        pbft.addNode("N3");

        Block block = new Block(1, "Data", "prev");
        assertDoesNotThrow(() -> pbft.validate(block));
    }

    @Test
    @DisplayName("Validation répétée sur plusieurs blocs ne crashe pas")
    void repeatedValidationWorks() {
        PBFT pbft = new PBFT();
        pbft.addNode("N1");
        pbft.addNode("N2");
        pbft.addNode("N3");
        pbft.addNode("N4");
        pbft.addNode("N5");

        for (int i = 0; i < 20; i++) {
            Block block = new Block(i, "Data " + i, "prev");
            assertDoesNotThrow(() -> pbft.validate(block));
        }
    }
}
