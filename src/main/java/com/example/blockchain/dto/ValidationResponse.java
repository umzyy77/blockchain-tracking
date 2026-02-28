package com.example.blockchain.dto;

public record ValidationResponse(boolean valid, int size, String message) {
}
