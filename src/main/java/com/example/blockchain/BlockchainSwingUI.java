package com.example.blockchain;

import com.example.blockchain.consensus.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

/**
 * Interface graphique Swing pour manipuler la blockchain.
 */
public class BlockchainSwingUI extends JFrame {

    private final Blockchain blockchain;
    private final DefaultTableModel tableModel;
    private final JTextArea logArea;
    private final JComboBox<String> consensusCombo;

    public BlockchainSwingUI() {
        super("Blockchain - Billetterie Spectacle");
        this.blockchain = new Blockchain();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // Panel de saisie (haut)
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // Tableau de la chaîne (centre)
        String[] columns = {"Index", "Horodatage", "Données", "Événement", "Artiste", "Statut", "Propriétaire", "Hash"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Chaîne de blocs"));
        add(tableScroll, BorderLayout.CENTER);

        // Zone de log (bas)
        logArea = new JTextArea(8, 80);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Logs"));
        add(logScroll, BorderLayout.SOUTH);

        // Combo consensus
        consensusCombo = new JComboBox<>(new String[]{
                "Aucun", "Proof of Work", "Proof of Stake", "PBFT", "Proof of Authority"
        });

        refreshTable();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ajouter un bloc"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField dataField = new JTextField(20);
        JTextField eventField = new JTextField(10);
        JTextField artistField = new JTextField(10);
        JTextField statusField = new JTextField(10);
        JTextField ownerField = new JTextField(10);

        // Ligne 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Données:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        panel.add(dataField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 4;
        panel.add(new JLabel("Événement:"), gbc);
        gbc.gridx = 5;
        panel.add(eventField, gbc);

        // Ligne 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Artiste:"), gbc);
        gbc.gridx = 1;
        panel.add(artistField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 3;
        panel.add(statusField, gbc);

        gbc.gridx = 4;
        panel.add(new JLabel("Propriétaire:"), gbc);
        gbc.gridx = 5;
        panel.add(ownerField, gbc);

        // Ligne 3 : boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addBtn = new JButton("Ajouter Bloc");
        addBtn.addActionListener(e -> {
            String data = dataField.getText().trim();
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le champ Données est obligatoire !");
                return;
            }
            applySelectedConsensus();
            blockchain.addBlock(data,
                    eventField.getText().trim(),
                    artistField.getText().trim(),
                    statusField.getText().trim(),
                    ownerField.getText().trim());
            refreshTable();
            log("Bloc #" + (blockchain.size() - 1) + " ajouté: " + data);
            dataField.setText("");
            eventField.setText("");
            artistField.setText("");
            statusField.setText("");
            ownerField.setText("");
        });

        JButton validateBtn = new JButton("Vérifier intégrité");
        validateBtn.addActionListener(e -> {
            boolean valid = blockchain.isChainValid();
            log("Vérification d'intégrité: " + (valid ? "VALIDE" : "CORROMPUE !"));
            JOptionPane.showMessageDialog(this,
                    valid ? "La chaîne est valide !" : "ATTENTION: La chaîne est corrompue !",
                    "Intégrité", valid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        JButton exportBtn = new JButton("Exporter JSON");
        exportBtn.addActionListener(e -> {
            try {
                blockchain.saveToFile("blockchain.json");
                log("Blockchain exportée dans blockchain.json");
                JOptionPane.showMessageDialog(this, "Exporté dans blockchain.json");
            } catch (IOException ex) {
                log("Erreur export: " + ex.getMessage());
            }
        });

        JButton workflowBtn = new JButton("Simuler Workflow Ticket");
        workflowBtn.addActionListener(e -> simulateTicketWorkflow());

        buttonPanel.add(addBtn);
        buttonPanel.add(validateBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(workflowBtn);
        buttonPanel.add(new JLabel("  Consensus:"));
        buttonPanel.add(consensusCombo);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void applySelectedConsensus() {
        String selected = (String) consensusCombo.getSelectedItem();
        switch (selected) {
            case "Proof of Work" -> {
                blockchain.setConsensusMechanism(new ProofOfWork(3));
                log("Consensus: Proof of Work (difficulté=3)");
            }
            case "Proof of Stake" -> {
                ProofOfStake pos = new ProofOfStake();
                pos.addValidator("Alice", 50);
                pos.addValidator("Bob", 30);
                pos.addValidator("Charlie", 20);
                blockchain.setConsensusMechanism(pos);
                log("Consensus: Proof of Stake (Alice:50, Bob:30, Charlie:20)");
            }
            case "PBFT" -> {
                PBFT pbft = new PBFT();
                pbft.addNode("Noeud-Paris");
                pbft.addNode("Noeud-Lyon");
                pbft.addNode("Noeud-Marseille");
                pbft.addNode("Noeud-Bordeaux");
                blockchain.setConsensusMechanism(pbft);
                log("Consensus: PBFT (4 nœuds)");
            }
            case "Proof of Authority" -> {
                ProofOfAuthority poa = new ProofOfAuthority();
                poa.addAuthority("Autorité-Ministère");
                poa.addAuthority("Autorité-Douane");
                poa.addAuthority("Autorité-Organisateur");
                blockchain.setConsensusMechanism(poa);
                log("Consensus: Proof of Authority (3 autorités)");
            }
            default -> {
                blockchain.setConsensusMechanism(null);
            }
        }
    }

    /**
     * TP 5 (Bonus) : Simulation du workflow d'un ticket acheté, revendu 2 fois,
     * utilisé puis devenu inutilisable.
     */
    private void simulateTicketWorkflow() {
        log("=== Simulation Workflow Ticket ===");

        blockchain.setConsensusMechanism(null);

        blockchain.addBlock("Ticket acheté", "EVT-2025-001", "Daft Punk", "ACHETE", "Alice");
        log("1. Alice achète le ticket pour Daft Punk");

        blockchain.addBlock("Ticket revendu", "EVT-2025-001", "Daft Punk", "REVENDU", "Bob");
        log("2. Alice revend le ticket à Bob");

        blockchain.addBlock("Ticket revendu", "EVT-2025-001", "Daft Punk", "REVENDU", "Charlie");
        log("3. Bob revend le ticket à Charlie");

        blockchain.addBlock("Ticket utilisé", "EVT-2025-001", "Daft Punk", "UTILISE", "Charlie");
        log("4. Charlie utilise le ticket pour entrer au concert");

        blockchain.addBlock("Ticket invalidé", "EVT-2025-001", "Daft Punk", "INVALIDE", "Charlie");
        log("5. Le ticket est désormais INVALIDE (utilisé)");

        refreshTable();

        boolean valid = blockchain.isChainValid();
        log("Intégrité après workflow: " + (valid ? "VALIDE" : "CORROMPUE"));
        log("=== Fin Simulation ===");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Block block : blockchain.getChain()) {
            tableModel.addRow(new Object[]{
                    block.index,
                    block.timestamp,
                    block.data,
                    block.eventId != null ? block.eventId : "",
                    block.artist != null ? block.artist : "",
                    block.status != null ? block.status : "",
                    block.owner != null ? block.owner : "",
                    block.hash.substring(0, 16) + "..."
            });
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
