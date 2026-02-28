package com.example.blockchain.consensus;

import com.example.blockchain.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Practical Byzantine Fault Tolerance (PBFT).
 * Accord par vote entre nœuds connus. Le bloc est validé si au moins
 * 2/3 des nœuds votent en sa faveur (tolérance aux pannes byzantines).
 */
public class PBFT implements ConsensusMechanism {
    private final List<String> nodes;
    private final Random random = new Random();

    public PBFT() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(String nodeName) {
        nodes.add(nodeName);
    }

    @Override
    public void validate(Block block) {
        if (nodes.isEmpty()) {
            System.out.println("[PBFT] Aucun nœud configuré !");
            return;
        }

        int requiredVotes = (2 * nodes.size() / 3) + 1;
        int votes = 0;
        List<String> voters = new ArrayList<>();

        for (String node : nodes) {
            // Simulation : chaque nœud a 85% de chance de voter positivement
            if (random.nextDouble() < 0.85) {
                votes++;
                voters.add(node);
            }
        }

        boolean accepted = votes >= requiredVotes;
        System.out.println("[PBFT] Bloc #" + block.index
                + " | Votes: " + votes + "/" + nodes.size()
                + " (requis: " + requiredVotes + ")"
                + " | " + (accepted ? "ACCEPTÉ" : "REJETÉ")
                + " | Votants: " + voters);

        if (!accepted) {
            System.out.println("[PBFT] ATTENTION: Le bloc n'a pas atteint le consensus !");
        }
    }

    @Override
    public String getName() {
        return "PBFT (Practical Byzantine Fault Tolerance)";
    }
}
