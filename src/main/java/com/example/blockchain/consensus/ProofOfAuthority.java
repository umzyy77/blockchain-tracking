package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Proof of Authority (PoA) : validation par un nombre restreint
 * d'acteurs de confiance (autorités). Chaque autorité valide à tour de rôle.
 */
public class ProofOfAuthority implements ConsensusMechanism {

    private static final Logger logger = LoggerFactory.getLogger(ProofOfAuthority.class);

    private final List<String> authorities;
    private int currentIndex = 0;

    public ProofOfAuthority() {
        this.authorities = new ArrayList<>();
    }

    public void addAuthority(String authority) {
        authorities.add(authority);
    }

    @Override
    public void validate(Block block) {
        if (authorities.isEmpty()) {
            logger.warn("[PoA] Aucune autorité configurée !");
            return;
        }

        String authority = authorities.get(currentIndex % authorities.size());
        currentIndex++;
        logger.info("[PoA] Bloc #{} validé par l'autorité: {}", block.getIndex(), authority);
    }

    @Override
    public String getName() {
        return "Proof of Authority";
    }
}
