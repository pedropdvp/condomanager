package com.condomanager.dto;

import java.util.List;

/**
 * Resultado do envio de lembretes de quotas em atraso: quantos emails saíram e
 * a quem (nome dos condóminos contactados).
 */
public record LembreteResponse(int enviados, List<String> condominos) {
}
