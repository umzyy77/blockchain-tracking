package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proof of Work (PoW) : le mineur doit trouver un nonce tel que
 * le hash du bloc commence par un certain nombre de zéros (difficulté).
 */
public class ProofOfWork implements ConsensusMechanism {

    private static final Logger logger = LoggerFactory.getLogger(ProofOfWork.class);

    private final int difficulty;

    public ProofOfWork(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void validate(Block block) {
        String target = "0".repeat(difficulty);
        while (!block.getHash().startsWith(target)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(block.calculateHash());
        }
        logger.info("[PoW] Bloc #{} miné avec nonce={} (difficulté={})",
                block.getIndex(), block.getNonce(), difficulty);
    }

    @Override
    public String getName() {
        return "Proof of Work (difficulté=" + difficulty + ")";
    }
}
