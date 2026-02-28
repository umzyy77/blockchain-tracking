package com.example.blockchain;

import com.example.blockchain.dto.BlockRequest;
import com.example.blockchain.dto.BlockResponse;
import com.example.blockchain.dto.TicketRequest;
import com.example.blockchain.dto.ValidationResponse;
import com.example.blockchain.mapper.BlockMapper;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * TP 4 (Bonus) : API REST pour manipuler la blockchain.
 */
@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final Blockchain blockchain;
    private final BlockMapper blockMapper;

    public BlockchainController(Blockchain blockchain, BlockMapper blockMapper) {
        this.blockchain = blockchain;
        this.blockMapper = blockMapper;
    }

    /**
     * GET /api/blockchain/chain - Afficher toute la chaîne
     */
    @GetMapping("/chain")
    public List<BlockResponse> getChain() {
        return blockMapper.toResponseList(blockchain.getChain());
    }

    /**
     * POST /api/blockchain/block - Ajouter un bloc simple
     */
    @PostMapping("/block")
    public BlockResponse addBlock(@RequestBody BlockRequest request) {
        blockchain.addBlock(request.data());
        return blockMapper.toResponse(blockchain.getLastBlock());
    }

    /**
     * POST /api/blockchain/ticket - Ajouter un bloc avec données de billetterie
     */
    @PostMapping("/ticket")
    public BlockResponse addTicketBlock(@RequestBody TicketRequest request) {
        blockchain.addBlock(
                request.data(),
                request.eventId(),
                request.artist(),
                request.status(),
                request.owner()
        );
        return blockMapper.toResponse(blockchain.getLastBlock());
    }

    /**
     * GET /api/blockchain/validate - Vérifier l'intégrité de la chaîne
     */
    @GetMapping("/validate")
    public ValidationResponse validate() {
        boolean valid = blockchain.isChainValid();
        return blockMapper.toValidationResponse(valid, blockchain.size());
    }

    /**
     * GET /api/blockchain/export - Exporter la blockchain en JSON
     */
    @GetMapping("/export")
    public String exportJson() throws IOException {
        return blockchain.exportAsJson();
    }
}
