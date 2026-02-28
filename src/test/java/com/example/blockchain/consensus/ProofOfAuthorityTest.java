package com.example.blockchain.consensus;

import com.example.blockchain.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - Proof of Authority")
class ProofOfAuthorityTest {

    @Test
    @DisplayName("Les autorités valident à tour de rôle (round-robin)")
    void authoritiesValidateInRoundRobin() {
        ProofOfAuthority poa = new ProofOfAuthority();
        poa.addAuthority("Autorité-A");
        poa.addAuthority("Autorité-B");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Block block1 = new Block(1, "Data1", "prev");
        poa.validate(block1);
        Block block2 = new Block(2, "Data2", "prev");
        poa.validate(block2);
        Block block3 = new Block(3, "Data3", "prev");
        poa.validate(block3);

        System.setOut(originalOut);
        String output = outputStream.toString();

        // Vérifier le round-robin : A, B, A
        assertTrue(output.contains("Autorité-A"));
        assertTrue(output.contains("Autorité-B"));
    }

    @Test
    @DisplayName("La validation ne plante pas sans autorités")
    void validationHandlesNoAuthorities() {
        ProofOfAuthority poa = new ProofOfAuthority();
        Block block = new Block(1, "Data", "prev");

        assertDoesNotThrow(() -> poa.validate(block));
    }

    @Test
    @DisplayName("getName retourne Proof of Authority")
    void getNameReturnsCorrectName() {
        ProofOfAuthority poa = new ProofOfAuthority();
        assertEquals("Proof of Authority", poa.getName());
    }

    @Test
    @DisplayName("Une seule autorité valide tous les blocs")
    void singleAuthorityValidatesAll() {
        ProofOfAuthority poa = new ProofOfAuthority();
        poa.addAuthority("Unique");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        for (int i = 0; i < 5; i++) {
            Block block = new Block(i, "Data " + i, "prev");
            poa.validate(block);
        }

        System.setOut(originalOut);
        String output = outputStream.toString();

        // "Unique" apparaît 5 fois
        long count = output.lines().filter(l -> l.contains("Unique")).count();
        assertEquals(5, count);
    }

    @Test
    @DisplayName("Le round-robin boucle correctement après avoir parcouru toutes les autorités")
    void roundRobinWrapsAround() {
        ProofOfAuthority poa = new ProofOfAuthority();
        poa.addAuthority("A");
        poa.addAuthority("B");
        poa.addAuthority("C");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 6 blocs = 2 tours complets (A, B, C, A, B, C)
        for (int i = 0; i < 6; i++) {
            Block block = new Block(i, "Data " + i, "prev");
            poa.validate(block);
        }

        System.setOut(originalOut);
        String output = outputStream.toString();

        // Chaque autorité doit apparaître exactement 2 fois
        long countA = output.lines().filter(l -> l.contains("autorité: A")).count();
        long countB = output.lines().filter(l -> l.contains("autorité: B")).count();
        long countC = output.lines().filter(l -> l.contains("autorité: C")).count();
        assertEquals(2, countA);
        assertEquals(2, countB);
        assertEquals(2, countC);
    }
}
