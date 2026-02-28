package com.example.blockchain.consensus;

import com.example.blockchain.Block;

import java.util.*;

/**
 * Proof of Stake (PoS) : les validateurs sont choisis proportionnellement
 * à leur mise (stake). Plus un validateur a de jetons, plus il a de chances
 * d'être sélectionné.
 */
public class ProofOfStake implements ConsensusMechanism {
    private final Map<String, Integer> stakes;
    private final Random random = new Random();

    public ProofOfStake() {
        this.stakes = new LinkedHashMap<>();
    }

    public void addValidator(String name, int stake) {
        stakes.put(name, stake);
    }

    /**
     * Sélectionne un validateur proportionnellement à sa mise.
     */
    private String selectValidator() {
        int totalStake = stakes.values().stream().mapToInt(Integer::intValue).sum();
        if (totalStake == 0) return "Aucun validateur";

        int pick = random.nextInt(totalStake);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : stakes.entrySet()) {
            cumulative += entry.getValue();
            if (pick < cumulative) {
                return entry.getKey();
            }
        }
        return stakes.keySet().iterator().next();
    }

    @Override
    public void validate(Block block) {
        String validator = selectValidator();
        System.out.println("[PoS] Bloc #" + block.index + " validé par " + validator
                + " (stake=" + stakes.getOrDefault(validator, 0) + ")");
    }

    @Override
    public String getName() {
        return "Proof of Stake";
    }
}
