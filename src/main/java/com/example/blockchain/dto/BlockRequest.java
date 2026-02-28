package com.example.blockchain.dto;

public record BlockRequest(String data) {

    public BlockRequest {
        if (data == null || data.isBlank()) {
            data = "Bloc sans données";
        }
    }
}
