package com.condomanager.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String erro;
    private String mensagem;

    public ErrorResponse(int status, String erro, String mensagem) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
}
