package com.condomanager.exception;

/**
 * Erro ao guardar, ler ou eliminar um ficheiro no armazenamento local.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
