package com.example.blockchain;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.blockchain.consensus.PBFT;
import com.example.blockchain.consensus.ProofOfAuthority;
import com.example.blockchain.consensus.ProofOfStake;
import com.example.blockchain.consensus.ProofOfWork;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String EVENT_ID = "EVT-001";
    private static final String ARTIST = "Stromae";
    private static final String CHARLIE = "Charlie";

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("  BLOCKCHAIN TRACKING - BILLETTERIE");
        logger.info("========================================");

        // --- Etape 1 : Blockchain de base ---
        logger.info("--- Blockchain de base (tracking logistique) ---");
        Blockchain logistique = new Blockchain();
        logistique.addBlock("Marchandise prise en charge par le transporteur");
        logistique.addBlock("Passage en douane validé");
        logistique.addBlock("Colis livré au client final");
        logistique.displayChain();

        // --- Etape 2 : Vérification d'intégrité ---
        logger.info("--- Vérification d'intégrité ---");
        logger.info("Chaîne valide ? {}", logistique.isChainValid());

        // --- Etape 3 : Blockchain billetterie enrichie ---
        logger.info("--- Blockchain Billetterie ---");
        Blockchain billetterie = new Blockchain();

        // PoW
        logger.info("[Consensus: Proof of Work]");
        billetterie.setConsensusMechanism(new ProofOfWork(3));
        billetterie.addBlock("Ticket créé", EVENT_ID, ARTIST, "CREE", "Organisateur");

        // PoS
        logger.info("[Consensus: Proof of Stake]");
        ProofOfStake pos = new ProofOfStake();
        pos.addValidator("Alice", 50);
        pos.addValidator("Bob", 30);
        pos.addValidator(CHARLIE, 20);
        billetterie.setConsensusMechanism(pos);
        billetterie.addBlock("Ticket acheté", EVENT_ID, ARTIST, "ACHETE", "Alice");

        // PBFT
        logger.info("[Consensus: PBFT]");
        PBFT pbft = new PBFT();
        pbft.addNode("Noeud-Paris");
        pbft.addNode("Noeud-Lyon");
        pbft.addNode("Noeud-Marseille");
        pbft.addNode("Noeud-Bordeaux");
        billetterie.setConsensusMechanism(pbft);
        billetterie.addBlock("Ticket revendu", EVENT_ID, ARTIST, "REVENDU", "Bob");

        // PoA
        logger.info("[Consensus: Proof of Authority]");
        ProofOfAuthority poa = new ProofOfAuthority();
        poa.addAuthority("Autorité-Ministère");
        poa.addAuthority("Autorité-Douane");
        billetterie.setConsensusMechanism(poa);
        billetterie.addBlock("Ticket revendu", EVENT_ID, ARTIST, "REVENDU", CHARLIE);

        // Sans consensus pour les dernières étapes
        billetterie.setConsensusMechanism(null);
        billetterie.addBlock("Ticket utilisé", EVENT_ID, ARTIST, "UTILISE", CHARLIE);
        billetterie.addBlock("Ticket invalidé", EVENT_ID, ARTIST, "INVALIDE", CHARLIE);

        logger.info("--- Chaîne complète billetterie ---");
        billetterie.displayChain();

        logger.info("Intégrité billetterie : {}", billetterie.isChainValid());

        // --- Etape 4 : Export JSON ---
        billetterie.saveToFile("blockchain.json");

        // --- Lancement API REST Spring Boot ---
        logger.info("--- Démarrage API REST Spring Boot ---");
        logger.info("Endpoints disponibles:");
        logger.info("  GET    /api/blocks        - Récupérer tous les blocs");
        logger.info("  GET    /api/blocks/{id}   - Récupérer un bloc par index");
        logger.info("  POST   /api/blocks        - Créer un nouveau bloc");

        SpringApplication app = new SpringApplication(Application.class);
        app.setHeadless(false);
        app.run(args);

        // --- Lancement interface Swing (seulement si un écran est disponible) ---
        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> {
                BlockchainSwingUI ui = new BlockchainSwingUI();
                ui.setVisible(true);
            });
        } else {
            logger.info("Mode headless détecté : interface Swing désactivée.");
        }
    }
}
