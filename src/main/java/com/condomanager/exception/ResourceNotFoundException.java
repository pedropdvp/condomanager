package com.condomanager.exception;

/**
 * Lançada quando um recurso pedido não existe (ou não pertence ao tenant atual).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Object id) {
        super("%s não encontrado: %s".formatted(resource, id));
    }
}
