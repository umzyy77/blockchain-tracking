package com.example.blockchain.dto;

public record BlockResponse(
        int index,
        String timestamp,
        String data,
        String previousHash,
        String hash,
        String eventId,
        String artist,
        String status,
        String owner,
        int nonce
) {
}
