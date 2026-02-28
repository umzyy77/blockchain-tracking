package com.example.blockchain.dto;

public record TicketRequest(
        String data,
        String eventId,
        String artist,
        String status,
        String owner
) {
    public TicketRequest {
        if (data == null || data.isBlank()) data = "Opération ticket";
        if (eventId == null) eventId = "";
        if (artist == null) artist = "";
        if (status == null) status = "";
        if (owner == null) owner = "";
    }
}
