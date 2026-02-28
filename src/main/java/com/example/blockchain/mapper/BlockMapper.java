package com.example.blockchain.mapper;

import com.example.blockchain.Block;
import com.example.blockchain.dto.BlockResponse;
import com.example.blockchain.dto.ValidationResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper entre les entités Block et les DTOs.
 */
@Component
public class BlockMapper {

    public BlockResponse toResponse(Block block) {
        return new BlockResponse(
                block.getIndex(),
                block.getTimestamp(),
                block.getData(),
                block.getPreviousHash(),
                block.getHash(),
                block.getEventId(),
                block.getArtist(),
                block.getStatus(),
                block.getOwner(),
                block.getNonce()
        );
    }

    public List<BlockResponse> toResponseList(List<Block> blocks) {
        return blocks.stream().map(this::toResponse).toList();
    }

    public ValidationResponse toValidationResponse(boolean valid, int size) {
        return new ValidationResponse(
                valid,
                size,
                valid ? "La chaîne est valide" : "ATTENTION: La chaîne est corrompue !"
        );
    }
}
