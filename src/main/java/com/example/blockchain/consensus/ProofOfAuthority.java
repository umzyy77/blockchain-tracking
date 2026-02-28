package com.example.blockchain.consensus;

import com.example.blockchain.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Proof of Authority (PoA) : validation par un nombre restreint
 * d'acteurs de confiance (autorités). Chaque autorité valide à tour de rôle.
 */
public class ProofOfAuthority implements ConsensusMechanism {
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
            System.out.println("[PoA] Aucune autorité configurée !");
            return;
        }

        String authority = authorities.get(currentIndex % authorities.size());
        currentIndex++;
        System.out.println("[PoA] Bloc #" + block.index + " validé par l'autorité: " + authority);
    }

    @Override
    public String getName() {
        return "Proof of Authority";
    }
}
