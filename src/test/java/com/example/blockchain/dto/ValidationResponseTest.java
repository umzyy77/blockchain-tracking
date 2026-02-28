package com.example.blockchain.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - ValidationResponse DTO")
class ValidationResponseTest {

    @Test
    @DisplayName("Les champs sont accessibles via les accesseurs du record")
    void fieldsAreAccessible() {
        ValidationResponse response = new ValidationResponse(true, 5, "La chaîne est valide");

        assertTrue(response.valid());
        assertEquals(5, response.size());
        assertEquals("La chaîne est valide", response.message());
    }

    @Test
    @DisplayName("Réponse invalide contient le bon message")
    void invalidResponseHasCorrectMessage() {
        ValidationResponse response = new ValidationResponse(false, 3, "CORROMPUE");

        assertFalse(response.valid());
        assertEquals(3, response.size());
        assertEquals("CORROMPUE", response.message());
    }
}
