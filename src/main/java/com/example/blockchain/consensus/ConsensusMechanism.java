package com.example.blockchain.consensus;

import com.example.blockchain.Block;

/**
 * Interface commune pour tous les mécanismes de consensus.
 */
public interface ConsensusMechanism {
    void validate(Block block);
    String getName();
}
