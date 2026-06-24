package com.condomanager.service;

import com.condomanager.dto.RecuperarPasswordDTO;
import com.condomanager.dto.RedefinirPasswordDTO;
import com.condomanager.model.PasswordResetToken;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.PasswordResetTokenRepository;
import com.condomanager.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Recuperação de password: emite tokens de uso único e repõe a password.
 *
 * <p>Por segurança, o pedido de recuperação responde sempre da mesma forma, exista ou
 * não a conta (não revela emails registados).</p>
 */
@Service
public class RecuperacaoPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(RecuperacaoPasswordService.class);

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final long expiracaoMinutos;

    public RecuperacaoPasswordService(UtilizadorRepository utilizadorRepository,
                                      PasswordResetTokenRepository tokenRepository,
                                      PasswordEncoder passwordEncoder,
                                      EmailService emailService,
                                      @Value("${app.recuperacao-password.expiracao-minutos}") long expiracaoMinutos) {
        this.utilizadorRepository = utilizadorRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    @Transactional
    public void solicitar(RecuperarPasswordDTO dto) {
        utilizadorRepository.findByEmail(dto.email()).ifPresent(utilizador -> {
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setIdUtilizador(utilizador.getId());
            token.setExpiraEm(LocalDateTime.now().plusMinutes(expiracaoMinutos));
            tokenRepository.save(token);

            emailService.enviar(utilizador.getEmail(), "Recuperação de password — CondoManager",
                    "Para repor a sua password use o seguinte token (válido " + expiracaoMinutos
                            + " minutos):\n\n" + token.getToken()
                            + "\n\nSe não solicitou esta reposição, ignore este email.");
            logger.info("Token de recuperação emitido para utilizador id={}", utilizador.getId());
        });
    }

    @Transactional
    public void redefinir(RedefinirPasswordDTO dto) {
        PasswordResetToken token = tokenRepository.findByToken(dto.token())
                .filter(PasswordResetToken::isValido)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        Utilizador utilizador = utilizadorRepository.findById(token.getIdUtilizador())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        utilizador.setPassword(passwordEncoder.encode(dto.novaPassword()));
        token.setUsado(true);
        logger.info("Password reposta para utilizador id={}", utilizador.getId());
    }
}
