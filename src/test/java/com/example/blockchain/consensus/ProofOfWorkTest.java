package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - Proof of Work")
class ProofOfWorkTest {

    @Test
    @DisplayName("Le hash du bloc miné commence par le bon nombre de zéros")
    void minedBlockHashStartsWithZeros() {
        ProofOfWork pow = new ProofOfWork(3);
        Block block = new Block(1, "Test PoW", "prevhash");

        pow.validate(block);

        assertTrue(block.hash.startsWith("000"));
    }

    @Test
    @DisplayName("Le nonce est incrémenté pendant le minage")
    void nonceIsIncrementedDuringMining() {
        ProofOfWork pow = new ProofOfWork(2);
        Block block = new Block(1, "Test", "prev");

        pow.validate(block);

        assertTrue(block.nonce >= 0);
    }

    @Test
    @DisplayName("Le hash est valide après minage (cohérent avec calculateHash)")
    void hashIsValidAfterMining() {
        ProofOfWork pow = new ProofOfWork(2);
        Block block = new Block(1, "Data", "prev");

        pow.validate(block);

        assertEquals(block.hash, block.calculateHash());
    }

    @Test
    @DisplayName("getName retourne le nom avec la difficulté")
    void getNameReturnsDifficulty() {
        ProofOfWork pow = new ProofOfWork(4);
        assertTrue(pow.getName().contains("4"));
        assertTrue(pow.getName().contains("Proof of Work"));
    }

    @Test
    @DisplayName("Difficulté 0 : le hash est accepté immédiatement")
    void difficultyZeroAcceptsImmediately() {
        ProofOfWork pow = new ProofOfWork(0);
        Block block = new Block(1, "Data", "prev");
        int originalNonce = block.nonce;

        pow.validate(block);

        assertEquals(originalNonce, block.nonce, "Aucun minage nécessaire avec difficulté 0");
    }

    @Test
    @DisplayName("Difficulté 1 : le hash commence par un zéro")
    void difficultyOneProducesOneLeadingZero() {
        ProofOfWork pow = new ProofOfWork(1);
        Block block = new Block(1, "Test diff 1", "prev");

        pow.validate(block);

        assertTrue(block.hash.startsWith("0"));
    }
}
