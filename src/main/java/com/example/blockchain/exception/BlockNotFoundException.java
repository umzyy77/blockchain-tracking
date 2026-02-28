package com.example.blockchain.exception;

/**
 * Exception levée lorsqu'un bloc n'est pas trouvé à l'index demandé.
 */
public class BlockNotFoundException extends RuntimeException {

    public BlockNotFoundException(int index) {
        super("Bloc introuvable à l'index : " + index);
    }
}
