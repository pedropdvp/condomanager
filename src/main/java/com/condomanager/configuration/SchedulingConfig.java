package com.condomanager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ativa as tarefas agendadas (ex.: lembretes de quotas em atraso em
 * {@link com.condomanager.service.NotificacaoService}).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
