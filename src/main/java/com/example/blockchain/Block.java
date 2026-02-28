package com.example.blockchain;

import java.security.MessageDigest;
import java.time.Instant;

/**
 * Représente un bloc individuel dans la blockchain.
 * Enrichi avec les données métier de billetterie : eventId, artiste, statut, propriétaire.
 */
public class Block {
    public int index;
    public String timestamp;
    public String data;
    public String previousHash;
    public String hash;

    // Données métier enrichies (billetterie)
    public String eventId;
    public String artist;
    public String status;
    public String owner;

    // Proof of Work
    public int nonce;

    public Block(int index, String data, String previousHash) {
        this.index = index;
        this.timestamp = Instant.now().toString();
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public Block(int index, String data, String previousHash,
                 String eventId, String artist, String status, String owner) {
        this.index = index;
        this.timestamp = Instant.now().toString();
        this.data = data;
        this.previousHash = previousHash;
        this.eventId = eventId;
        this.artist = artist;
        this.status = status;
        this.owner = owner;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            String input = index + timestamp + data + previousHash
                    + (eventId != null ? eventId : "")
                    + (artist != null ? artist : "")
                    + (status != null ? status : "")
                    + (owner != null ? owner : "")
                    + nonce;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Index       : ").append(index).append("\n");
        sb.append("Horodatage  : ").append(timestamp).append("\n");
        sb.append("Données     : ").append(data).append("\n");
        if (eventId != null) sb.append("Événement   : ").append(eventId).append("\n");
        if (artist != null) sb.append("Artiste     : ").append(artist).append("\n");
        if (status != null) sb.append("Statut      : ").append(status).append("\n");
        if (owner != null) sb.append("Propriétaire: ").append(owner).append("\n");
        sb.append("Nonce       : ").append(nonce).append("\n");
        sb.append("Hash préc.  : ").append(previousHash).append("\n");
        sb.append("Hash        : ").append(hash).append("\n");
        sb.append("----------------------------------------");
        return sb.toString();
    }
}
