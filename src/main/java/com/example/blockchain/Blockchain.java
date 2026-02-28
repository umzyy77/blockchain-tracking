package com.example.blockchain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.blockchain.consensus.ConsensusMechanism;
import com.example.blockchain.exception.BlockNotFoundException;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Service métier : chaîne de blocs avec vérification d'intégrité, export JSON et consensus.
 */
@Service
public class Blockchain {

    private static final Logger logger = LoggerFactory.getLogger(Blockchain.class);

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
        Block newBlock = new Block(chain.size(), data, lastBlock.getHash());
        applyConsensus(newBlock);
        chain.add(newBlock);
    }

    public void addBlock(String data, String eventId, String artist, String status, String owner) {
        Block lastBlock = chain.getLast();
        Block newBlock = new Block(chain.size(), data, lastBlock.getHash(), eventId, artist, status, owner);
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

            if (!current.getHash().equals(current.calculateHash())) {
                logger.warn("Hash invalide au bloc #{}", i);
                return false;
            }

            if (!current.getPreviousHash().equals(previous.getHash())) {
                logger.warn("Chaînage rompu au bloc #{}", i);
                return false;
            }
        }
        return true;
    }

    /**
     * Export de la blockchain en JSON.
     */
    public String exportAsJson() {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        return mapper.writeValueAsString(chain);
    }

    public void saveToFile(String filename) {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
        mapper.writeValue(new File(filename), chain);
        logger.info("Blockchain exportée dans {}", filename);
    }

    public void displayChain() {
        for (Block block : chain) {
            logger.info("{}", block);
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

    public Block getBlockByIndex(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new BlockNotFoundException(index);
        }
        return chain.get(index);
    }
}
