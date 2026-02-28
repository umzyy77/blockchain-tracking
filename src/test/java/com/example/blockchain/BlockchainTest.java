package com.example.blockchain;

import com.example.blockchain.consensus.ProofOfWork;
import com.example.blockchain.consensus.ProofOfStake;
import com.example.blockchain.consensus.PBFT;
import com.example.blockchain.consensus.ProofOfAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires - Blockchain")
class BlockchainTest {

    private Blockchain blockchain;

    @BeforeEach
    void setUp() {
        blockchain = new Blockchain();
    }

    @Nested
    @DisplayName("Initialisation")
    class InitTests {

        @Test
        @DisplayName("La chaîne contient le bloc de genèse à la création")
        void chainContainsGenesisBlock() {
            assertEquals(1, blockchain.size());
            assertEquals(0, blockchain.getChain().getFirst().index);
            assertEquals("0", blockchain.getChain().getFirst().previousHash);
        }

        @Test
        @DisplayName("Le bloc de genèse contient le message attendu")
        void genesisBlockHasExpectedData() {
            Block genesis = blockchain.getChain().getFirst();
            assertTrue(genesis.data.contains("genèse"));
        }
    }

    @Nested
    @DisplayName("Ajout de blocs")
    class AddBlockTests {

        @Test
        @DisplayName("Ajouter un bloc simple incrémente la taille")
        void addSimpleBlockIncreasesSize() {
            blockchain.addBlock("Événement 1");
            assertEquals(2, blockchain.size());
        }

        @Test
        @DisplayName("Ajouter plusieurs blocs maintient l'ordre")
        void addMultipleBlocksMaintainsOrder() {
            blockchain.addBlock("Premier");
            blockchain.addBlock("Deuxième");
            blockchain.addBlock("Troisième");

            assertEquals(4, blockchain.size());
            assertEquals("Premier", blockchain.getChain().get(1).data);
            assertEquals("Deuxième", blockchain.getChain().get(2).data);
            assertEquals("Troisième", blockchain.getChain().get(3).data);
        }

        @Test
        @DisplayName("Le chaînage des hashs est correct après ajout")
        void hashChainingIsCorrectAfterAdd() {
            blockchain.addBlock("Bloc A");
            blockchain.addBlock("Bloc B");

            Block blockA = blockchain.getChain().get(1);
            Block blockB = blockchain.getChain().get(2);

            assertEquals(blockA.hash, blockB.previousHash);
        }

        @Test
        @DisplayName("Ajouter un bloc enrichi stocke les données métier")
        void addEnrichedBlockStoresBusinessData() {
            blockchain.addBlock("Ticket acheté", "EVT-001", "Stromae", "ACHETE", "Alice");

            Block last = blockchain.getLastBlock();
            assertEquals("EVT-001", last.eventId);
            assertEquals("Stromae", last.artist);
            assertEquals("ACHETE", last.status);
            assertEquals("Alice", last.owner);
        }

        @Test
        @DisplayName("getLastBlock retourne le dernier bloc ajouté")
        void getLastBlockReturnsLatest() {
            blockchain.addBlock("Ancien");
            blockchain.addBlock("Récent");

            assertEquals("Récent", blockchain.getLastBlock().data);
        }
    }

    @Nested
    @DisplayName("Vérification d'intégrité (isChainValid)")
    class ValidationTests {

        @Test
        @DisplayName("Une chaîne non modifiée est valide")
        void unmodifiedChainIsValid() {
            blockchain.addBlock("Bloc 1");
            blockchain.addBlock("Bloc 2");
            blockchain.addBlock("Bloc 3");

            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("La chaîne avec seulement le genèse est valide")
        void genesisOnlyChainIsValid() {
            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("Modifier les données d'un bloc rend la chaîne invalide")
        void tamperingDataInvalidatesChain() {
            blockchain.addBlock("Original");
            blockchain.addBlock("Suivant");

            blockchain.getChain().get(1).data = "Falsifié";

            assertFalse(blockchain.isChainValid());
        }

        @Test
        @DisplayName("Modifier le hash d'un bloc rompt le chaînage")
        void tamperingHashBreaksChaining() {
            blockchain.addBlock("Bloc A");
            blockchain.addBlock("Bloc B");

            blockchain.getChain().get(1).hash = "hash_falsifie";

            assertFalse(blockchain.isChainValid());
        }

        @Test
        @DisplayName("Modifier le previousHash d'un bloc rend la chaîne invalide")
        void tamperingPreviousHashInvalidatesChain() {
            blockchain.addBlock("Bloc 1");

            blockchain.getChain().get(1).previousHash = "faux_hash";

            assertFalse(blockchain.isChainValid());
        }
    }

    @Nested
    @DisplayName("Export JSON")
    class JsonExportTests {

        @Test
        @DisplayName("exportAsJson retourne du JSON valide contenant les données")
        void exportAsJsonReturnsValidJson() throws IOException {
            blockchain.addBlock("Événement test");

            String json = blockchain.exportAsJson();

            assertNotNull(json);
            assertTrue(json.contains("Événement test"));
            assertTrue(json.contains("hash"));
            assertTrue(json.contains("previousHash"));
        }

        @Test
        @DisplayName("exportAsJson contient les données métier enrichies")
        void exportAsJsonContainsBusinessData() throws IOException {
            blockchain.addBlock("Ticket", "EVT-001", "Daft Punk", "ACHETE", "Alice");

            String json = blockchain.exportAsJson();

            assertTrue(json.contains("EVT-001"));
            assertTrue(json.contains("Daft Punk"));
            assertTrue(json.contains("ACHETE"));
            assertTrue(json.contains("Alice"));
        }

        @Test
        @DisplayName("saveToFile crée un fichier JSON sur disque")
        void saveToFileCreatesJsonFile() throws IOException {
            blockchain.addBlock("Bloc pour fichier");

            Path tempFile = Files.createTempFile("blockchain-test-", ".json");
            String filePath = tempFile.toString();

            blockchain.saveToFile(filePath);

            File file = new File(filePath);
            assertTrue(file.exists());
            assertTrue(file.length() > 0);

            String content = Files.readString(tempFile);
            assertTrue(content.contains("Bloc pour fichier"));

            Files.deleteIfExists(tempFile);
        }

        @Test
        @DisplayName("exportAsJson sur chaîne vide (genèse seul) retourne du JSON")
        void exportGenesisOnlyReturnsJson() throws IOException {
            String json = blockchain.exportAsJson();
            assertNotNull(json);
            assertTrue(json.contains("genèse"));
        }
    }

    @Nested
    @DisplayName("displayChain")
    class DisplayChainTests {

        @Test
        @DisplayName("displayChain affiche tous les blocs sur la sortie standard")
        void displayChainPrintsAllBlocks() {
            blockchain.addBlock("Bloc Affiché");

            PrintStream originalOut = System.out;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));

            blockchain.displayChain();

            System.setOut(originalOut);

            String output = outputStream.toString();
            assertTrue(output.contains("genèse"));
            assertTrue(output.contains("Bloc Affiché"));
            assertTrue(output.contains("Index"));
            assertTrue(output.contains("Hash"));
        }
    }

    @Nested
    @DisplayName("Consensus")
    class ConsensusTests {

        @Test
        @DisplayName("Ajout de bloc fonctionne sans consensus (null)")
        void addBlockWorksWithoutConsensus() {
            blockchain.setConsensusMechanism(null);
            blockchain.addBlock("Sans consensus");

            assertEquals(2, blockchain.size());
            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("Ajout de bloc avec PoW produit un hash commençant par des zéros")
        void addBlockWithPoWProducesValidHash() {
            int difficulty = 2;
            blockchain.setConsensusMechanism(new ProofOfWork(difficulty));
            blockchain.addBlock("Bloc miné");

            Block last = blockchain.getLastBlock();
            assertTrue(last.hash.startsWith("00"),
                    "Le hash devrait commencer par '00' avec difficulté 2");
            assertTrue(last.nonce > 0, "Le nonce devrait avoir été incrémenté");
        }

        @Test
        @DisplayName("La chaîne reste valide après PoW")
        void chainRemainsValidAfterPoW() {
            blockchain.setConsensusMechanism(new ProofOfWork(2));
            blockchain.addBlock("Bloc PoW 1");
            blockchain.addBlock("Bloc PoW 2");

            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("La chaîne reste valide avec PoS")
        void chainRemainsValidWithPoS() {
            ProofOfStake pos = new ProofOfStake();
            pos.addValidator("Alice", 50);
            pos.addValidator("Bob", 30);
            blockchain.setConsensusMechanism(pos);
            blockchain.addBlock("Bloc PoS");

            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("La chaîne reste valide avec PBFT")
        void chainRemainsValidWithPBFT() {
            PBFT pbft = new PBFT();
            pbft.addNode("N1");
            pbft.addNode("N2");
            pbft.addNode("N3");
            pbft.addNode("N4");
            blockchain.setConsensusMechanism(pbft);
            blockchain.addBlock("Bloc PBFT");

            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("La chaîne reste valide avec PoA")
        void chainRemainsValidWithPoA() {
            ProofOfAuthority poa = new ProofOfAuthority();
            poa.addAuthority("Autorité-1");
            blockchain.setConsensusMechanism(poa);
            blockchain.addBlock("Bloc PoA");

            assertTrue(blockchain.isChainValid());
        }

        @Test
        @DisplayName("Changer de consensus entre les blocs maintient la validité")
        void switchingConsensusMaintainsValidity() {
            blockchain.setConsensusMechanism(new ProofOfWork(2));
            blockchain.addBlock("Bloc PoW");

            ProofOfStake pos = new ProofOfStake();
            pos.addValidator("V1", 100);
            blockchain.setConsensusMechanism(pos);
            blockchain.addBlock("Bloc PoS");

            blockchain.setConsensusMechanism(null);
            blockchain.addBlock("Bloc sans consensus");

            assertTrue(blockchain.isChainValid());
            assertEquals(4, blockchain.size());
        }
    }

    @Nested
    @DisplayName("Workflow ticket (bonus)")
    class TicketWorkflowTests {

        @Test
        @DisplayName("Simulation complète : achat -> 2 reventes -> utilisation -> invalidation")
        void fullTicketWorkflow() {
            blockchain.addBlock("Ticket acheté", "EVT-001", "Daft Punk", "ACHETE", "Alice");
            blockchain.addBlock("Ticket revendu", "EVT-001", "Daft Punk", "REVENDU", "Bob");
            blockchain.addBlock("Ticket revendu", "EVT-001", "Daft Punk", "REVENDU", "Charlie");
            blockchain.addBlock("Ticket utilisé", "EVT-001", "Daft Punk", "UTILISE", "Charlie");
            blockchain.addBlock("Ticket invalidé", "EVT-001", "Daft Punk", "INVALIDE", "Charlie");

            assertEquals(6, blockchain.size());
            assertTrue(blockchain.isChainValid());

            assertEquals("Alice", blockchain.getChain().get(1).owner);
            assertEquals("Bob", blockchain.getChain().get(2).owner);
            assertEquals("Charlie", blockchain.getChain().get(3).owner);

            assertEquals("INVALIDE", blockchain.getLastBlock().status);
        }

        @Test
        @DisplayName("Le ticket conserve le même eventId tout au long du workflow")
        void ticketKeepsSameEventId() {
            blockchain.addBlock("Achat", "EVT-042", "Artiste", "ACHETE", "A");
            blockchain.addBlock("Revente", "EVT-042", "Artiste", "REVENDU", "B");
            blockchain.addBlock("Utilisation", "EVT-042", "Artiste", "UTILISE", "B");

            for (int i = 1; i < blockchain.size(); i++) {
                assertEquals("EVT-042", blockchain.getChain().get(i).eventId);
            }
        }
    }
}
