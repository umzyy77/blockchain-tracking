package com.example.blockchain;

import com.example.blockchain.consensus.ConsensusMechanism;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service métier : chaîne de blocs avec vérification d'intégrité, export JSON et consensus.
 */
@Service
public class Blockchain {
    private final List<Block> chain;
    private ConsensusMechanism consensusMechanism;

    public Blockchain() {
        chain = new ArrayList<>();
        chain.add(new Block(0, "Bloc de genèse - Billetterie Spectacle", "0"));
    }

    public void setConsensusMechanism(ConsensusMechanism mechanism) {
        this.consensusMechanism = mechanism;
    }

    public void addBlock(String data) {
        Block lastBlock = chain.getLast();
        Block newBlock = new Block(chain.size(), data, lastBlock.hash);
        applyConsensus(newBlock);
        chain.add(newBlock);
    }

    public void addBlock(String data, String eventId, String artist, String status, String owner) {
        Block lastBlock = chain.getLast();
        Block newBlock = new Block(chain.size(), data, lastBlock.hash, eventId, artist, status, owner);
        applyConsensus(newBlock);
        chain.add(newBlock);
    }

    private void applyConsensus(Block block) {
        if (consensusMechanism != null) {
            consensusMechanism.validate(block);
        }
    }

    /**
     * Vérification d'intégrité de la blockchain.
     * Parcourt la chaîne et vérifie que chaque hash est correct
     * et que le chaînage est respecté.
     */
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            // Vérifier que le hash du bloc est correct
            if (!current.hash.equals(current.calculateHash())) {
                System.out.println("Hash invalide au bloc #" + i);
                return false;
            }

            // Vérifier le chaînage avec le bloc précédent
            if (!current.previousHash.equals(previous.hash)) {
                System.out.println("Chaînage rompu au bloc #" + i);
                return false;
            }
        }
        return true;
    }

    /**
     * Export de la blockchain en JSON.
     */
    public String exportAsJson() throws IOException {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        return mapper.writeValueAsString(chain);
    }

    public void saveToFile(String filename) throws IOException {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        mapper.writeValue(new File(filename), chain);
        System.out.println("Blockchain exportée dans " + filename);
    }

    public void displayChain() {
        for (Block block : chain) {
            System.out.println(block);
        }
    }

    public List<Block> getChain() {
        return chain;
    }

    public Block getLastBlock() {
        return chain.getLast();
    }

    public int size() {
        return chain.size();
    }
}
