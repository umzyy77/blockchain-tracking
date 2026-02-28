package com.example.blockchain;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.blockchain.dto.BlockResponse;
import com.example.blockchain.dto.TicketRequest;
import com.example.blockchain.mapper.BlockMapper;

/**
 * API REST pour manipuler la blockchain.
 * Endpoints : GET all, GET by id, POST.
 * La blockchain étant immuable, PUT et DELETE ne sont pas supportés.
 */
@RestController
@RequestMapping("/api/blocks")
public class BlockchainController {

    private final Blockchain blockchain;
    private final BlockMapper blockMapper;

    public BlockchainController(Blockchain blockchain, BlockMapper blockMapper) {
        this.blockchain = blockchain;
        this.blockMapper = blockMapper;
    }

    /**
     * GET /api/blocks - Récupérer tous les blocs de la chaîne.
     */
    @GetMapping
    public List<BlockResponse> getAll() {
        return blockMapper.toResponseList(blockchain.getChain());
    }

    /**
     * GET /api/blocks/{index} - Récupérer un bloc par son index.
     */
    @GetMapping("/{index}")
    public BlockResponse getById(@PathVariable int index) {
        return blockMapper.toResponse(blockchain.getBlockByIndex(index));
    }

    /**
     * POST /api/blocks - Créer un nouveau bloc.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlockResponse create(@RequestBody TicketRequest request) {
        boolean hasTicketData = !request.eventId().isEmpty()
                || !request.artist().isEmpty()
                || !request.status().isEmpty()
                || !request.owner().isEmpty();

        if (hasTicketData) {
            blockchain.addBlock(request.data(), request.eventId(), request.artist(),
                    request.status(), request.owner());
        } else {
            blockchain.addBlock(request.data());
        }

        return blockMapper.toResponse(blockchain.getLastBlock());
    }
}
