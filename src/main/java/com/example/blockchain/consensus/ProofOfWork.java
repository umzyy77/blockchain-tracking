package com.example.blockchain.consensus;

import com.example.blockchain.Block;

/**
 * Proof of Work (PoW) : le mineur doit trouver un nonce tel que
 * le hash du bloc commence par un certain nombre de zéros (difficulté).
 */
public class ProofOfWork implements ConsensusMechanism {
    private final int difficulty;

    public ProofOfWork(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void validate(Block block) {
        String target = "0".repeat(difficulty);
        while (!block.hash.startsWith(target)) {
            block.nonce++;
            block.hash = block.calculateHash();
        }
        System.out.println("[PoW] Bloc #" + block.index + " miné avec nonce=" + block.nonce
                + " (difficulté=" + difficulty + ")");
    }

    @Override
    public String getName() {
        return "Proof of Work (difficulté=" + difficulty + ")";
    }
}
