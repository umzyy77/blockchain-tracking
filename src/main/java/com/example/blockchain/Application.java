package com.example.blockchain;

import com.example.blockchain.consensus.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.io.IOException;


@SpringBootApplication
public class Application {

    public static void main(String[] args) throws IOException {
        System.out.println("========================================");
        System.out.println("  BLOCKCHAIN TRACKING - BILLETTERIE");
        System.out.println("========================================\n");

        // --- Étape 1 : Blockchain de base ---
        System.out.println("--- Blockchain de base (tracking logistique) ---");
        Blockchain logistique = new Blockchain();
        logistique.addBlock("Marchandise prise en charge par le transporteur");
        logistique.addBlock("Passage en douane validé");
        logistique.addBlock("Colis livré au client final");
        logistique.displayChain();

        // --- Étape 2 : Vérification d'intégrité ---
        System.out.println("\n--- Vérification d'intégrité ---");
        System.out.println("Chaîne valide ? " + logistique.isChainValid());

        // --- Étape 3 : Blockchain billetterie enrichie ---
        System.out.println("\n--- Blockchain Billetterie ---");
        Blockchain billetterie = new Blockchain();

        // Démonstration des 4 mécanismes de consensus

        // PoW
        System.out.println("\n[Consensus: Proof of Work]");
        billetterie.setConsensusMechanism(new ProofOfWork(3));
        billetterie.addBlock("Ticket créé", "EVT-001", "Stromae", "CREE", "Organisateur");

        // PoS
        System.out.println("\n[Consensus: Proof of Stake]");
        ProofOfStake pos = new ProofOfStake();
        pos.addValidator("Alice", 50);
        pos.addValidator("Bob", 30);
        pos.addValidator("Charlie", 20);
        billetterie.setConsensusMechanism(pos);
        billetterie.addBlock("Ticket acheté", "EVT-001", "Stromae", "ACHETE", "Alice");

        // PBFT
        System.out.println("\n[Consensus: PBFT]");
        PBFT pbft = new PBFT();
        pbft.addNode("Noeud-Paris");
        pbft.addNode("Noeud-Lyon");
        pbft.addNode("Noeud-Marseille");
        pbft.addNode("Noeud-Bordeaux");
        billetterie.setConsensusMechanism(pbft);
        billetterie.addBlock("Ticket revendu", "EVT-001", "Stromae", "REVENDU", "Bob");

        // PoA
        System.out.println("\n[Consensus: Proof of Authority]");
        ProofOfAuthority poa = new ProofOfAuthority();
        poa.addAuthority("Autorité-Ministère");
        poa.addAuthority("Autorité-Douane");
        billetterie.setConsensusMechanism(poa);
        billetterie.addBlock("Ticket revendu", "EVT-001", "Stromae", "REVENDU", "Charlie");

        // Sans consensus pour les dernières étapes
        billetterie.setConsensusMechanism(null);
        billetterie.addBlock("Ticket utilisé", "EVT-001", "Stromae", "UTILISE", "Charlie");
        billetterie.addBlock("Ticket invalidé", "EVT-001", "Stromae", "INVALIDE", "Charlie");

        System.out.println("\n--- Chaîne complète billetterie ---");
        billetterie.displayChain();

        System.out.println("\nIntégrité billetterie : " + billetterie.isChainValid());

        // --- Étape 4 : Export JSON ---
        billetterie.saveToFile("blockchain.json");

        // --- Lancement API REST Spring Boot ---
        System.out.println("\n--- Démarrage API REST Spring Boot ---");
        System.out.println("Endpoints disponibles:");
        System.out.println("  GET  /api/blockchain/chain    - Afficher la chaîne");
        System.out.println("  POST /api/blockchain/block    - Ajouter un bloc");
        System.out.println("  POST /api/blockchain/ticket   - Ajouter un ticket");
        System.out.println("  GET  /api/blockchain/validate - Vérifier intégrité");
        System.out.println("  GET  /api/blockchain/export   - Exporter en JSON");

        SpringApplication.run(Application.class, args);

        // --- Lancement interface Swing ---
        SwingUtilities.invokeLater(() -> {
            BlockchainSwingUI ui = new BlockchainSwingUI();
            ui.setVisible(true);
        });
    }
}
