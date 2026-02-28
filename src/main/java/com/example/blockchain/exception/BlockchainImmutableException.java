package com.example.blockchain.exception;

/**
 * Exception levée lors d'une tentative de modification ou suppression
 * d'un bloc dans la blockchain (qui est immuable par nature).
 */
public class BlockchainImmutableException extends RuntimeException {

    public BlockchainImmutableException(String operation) {
        super(operation + " impossible : la blockchain est immuable.");
    }
}
