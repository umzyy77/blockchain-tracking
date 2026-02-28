package com.example.blockchain.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - BlockRequest DTO")
class BlockRequestTest {

    @Test
    @DisplayName("data est conservé quand non null et non vide")
    void dataIsKeptWhenValid() {
        BlockRequest request = new BlockRequest("Mon bloc");
        assertEquals("Mon bloc", request.data());
    }

    @Test
    @DisplayName("data null est remplacé par la valeur par défaut")
    void nullDataIsReplacedByDefault() {
        BlockRequest request = new BlockRequest(null);
        assertEquals("Bloc sans données", request.data());
    }

    @Test
    @DisplayName("data vide est remplacé par la valeur par défaut")
    void blankDataIsReplacedByDefault() {
        BlockRequest request = new BlockRequest("   ");
        assertEquals("Bloc sans données", request.data());
    }
}
