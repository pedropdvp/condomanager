package com.condomanager.dto;

import java.util.List;

/**
 * Resultado do envio de uma convocatória: quantos emails foram enviados, quantos
 * condóminos ficaram sem email, e a lista detalhada dos destinatários contactados.
 */
public record ConvocatoriaEnvioResponse(
        int emailsEnviados,
        int semEmail,
        List<Destinatario> destinatarios) {

    /** Condómino que recebeu (ou deveria receber) a convocatória. */
    public record Destinatario(String nome, String email) {
    }
}
