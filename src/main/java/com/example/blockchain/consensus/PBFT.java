package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

/**
 * Practical Byzantine Fault Tolerance (PBFT).
 * Accord par vote entre nœuds connus. Le bloc est validé si au moins
 * 2/3 des nœuds votent en sa faveur (tolérance aux pannes byzantines).
 */
public class PBFT implements ConsensusMechanism {

    private static final Logger logger = LoggerFactory.getLogger(PBFT.class);

    private final List<String> nodes;
    private final SecureRandom random = new SecureRandom();

    public PBFT() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(String nodeName) {
        nodes.add(nodeName);
    }

    @Override
    public void validate(Block block) {
        if (nodes.isEmpty()) {
            logger.warn("[PBFT] Aucun nœud configuré !");
            return;
        }

        int requiredVotes = (2 * nodes.size() / 3) + 1;
        int votes = 0;
        List<String> voters = new ArrayList<>();

        for (String node : nodes) {
            if (random.nextDouble() < 0.85) {
                votes++;
                voters.add(node);
            }
        }

        boolean accepted = votes >= requiredVotes;
        logger.info("[PBFT] Bloc #{} | Votes: {}/{} (requis: {}) | {} | Votants: {}",
                block.getIndex(), votes, nodes.size(), requiredVotes,
                accepted ? "ACCEPTÉ" : "REJETÉ", voters);

        if (!accepted) {
            logger.warn("[PBFT] ATTENTION: Le bloc n'a pas atteint le consensus !");
        }
    }

    @Override
    public String getName() {
        return "PBFT (Practical Byzantine Fault Tolerance)";
    }
}
