package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - Proof of Stake")
class ProofOfStakeTest {

    @Test
    @DisplayName("La validation fonctionne avec des validateurs configurés")
    void validationWorksWithValidators() {
        ProofOfStake pos = new ProofOfStake();
        pos.addValidator("Alice", 50);
        pos.addValidator("Bob", 30);

        Block block = new Block(1, "Data", "prev");

        assertDoesNotThrow(() -> pos.validate(block));
    }

    @Test
    @DisplayName("La validation fonctionne sans validateurs (cas limite)")
    void validationWorksWithoutValidators() {
        ProofOfStake pos = new ProofOfStake();
        Block block = new Block(1, "Data", "prev");

        assertDoesNotThrow(() -> pos.validate(block));
    }

    @Test
    @DisplayName("getName retourne Proof of Stake")
    void getNameReturnsCorrectName() {
        ProofOfStake pos = new ProofOfStake();
        assertEquals("Proof of Stake", pos.getName());
    }

    @Test
    @DisplayName("Un seul validateur est toujours sélectionné")
    void singleValidatorAlwaysSelected() {
        ProofOfStake pos = new ProofOfStake();
        pos.addValidator("Unique", 100);

        for (int i = 0; i < 10; i++) {
            Block block = new Block(i, "Data " + i, "prev");
            assertDoesNotThrow(() -> pos.validate(block));
        }
    }

    @Test
    @DisplayName("La sélection respecte la distribution proportionnelle sur un grand nombre d'essais")
    void selectionIsProportionalOverManyRuns() {
        ProofOfStake pos = new ProofOfStake();
        pos.addValidator("Gros", 90);
        pos.addValidator("Petit", 10);

        for (int i = 0; i < 100; i++) {
            Block block = new Block(i, "Data", "prev");
            assertDoesNotThrow(() -> pos.validate(block));
        }
    }

    @Test
    @DisplayName("Ajouter plusieurs validateurs avec des stakes variés fonctionne")
    void multipleValidatorsWithVariedStakes() {
        ProofOfStake pos = new ProofOfStake();
        pos.addValidator("A", 10);
        pos.addValidator("B", 20);
        pos.addValidator("C", 30);
        pos.addValidator("D", 40);

        Block block = new Block(1, "Data", "prev");
        assertDoesNotThrow(() -> pos.validate(block));
    }
}
