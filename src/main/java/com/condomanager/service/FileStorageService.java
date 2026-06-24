package com.condomanager.service;

import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Armazenamento de ficheiros no filesystem local, organizado por tenant.
 *
 * <p>Os ficheiros são guardados em {@code <base>/<idEmpresa>/<uuid>.<ext>} e a base é
 * configurável em {@code app.storage.documentos}. O caminho relativo devolvido é o que
 * deve ser persistido na coluna {@code documento.ficheiro}.</p>
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path base;

    public FileStorageService(@Value("${app.storage.documentos}") String baseDir) {
        this.base = Paths.get(baseDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
            logger.info("Armazenamento de documentos em: {}", base);
        } catch (IOException e) {
            throw new StorageException("Não foi possível criar a pasta de armazenamento: " + base, e);
        }
    }

    /**
     * Guarda o ficheiro e devolve o caminho relativo (ex.: {@code 2/uuid.pdf}).
     */
    public String guardar(MultipartFile ficheiro, Long idEmpresa) {
        String original = StringUtils.cleanPath(
                ficheiro.getOriginalFilename() != null ? ficheiro.getOriginalFilename() : "ficheiro");
        String extensao = StringUtils.getFilenameExtension(original);
        String nomeArmazenado = UUID.randomUUID() + (extensao != null ? "." + extensao : "");

        Path pastaTenant = base.resolve(String.valueOf(idEmpresa)).normalize();
        try {
            Files.createDirectories(pastaTenant);
            Path destino = pastaTenant.resolve(nomeArmazenado);
            try (var in = ficheiro.getInputStream()) {
                Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            return idEmpresa + "/" + nomeArmazenado;
        } catch (IOException e) {
            throw new StorageException("Falha ao guardar o ficheiro " + original, e);
        }
    }

    /** Carrega o ficheiro como {@link Resource} a partir do caminho relativo. */
    public Resource carregar(String ficheiroRelativo) {
        Path caminho = base.resolve(ficheiroRelativo).normalize();
        if (!caminho.startsWith(base)) {
            throw new StorageException("Caminho de ficheiro inválido: " + ficheiroRelativo, null);
        }
        try {
            Resource recurso = new UrlResource(caminho.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("Ficheiro não disponível: " + ficheiroRelativo);
            }
            return recurso;
        } catch (MalformedURLException e) {
            throw new StorageException("Caminho de ficheiro inválido: " + ficheiroRelativo, e);
        }
    }

    /** Elimina o ficheiro (não falha se já não existir). */
    public void eliminar(String ficheiroRelativo) {
        Path caminho = base.resolve(ficheiroRelativo).normalize();
        if (!caminho.startsWith(base)) {
            return;
        }
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            logger.warn("Falha ao eliminar o ficheiro {}: {}", ficheiroRelativo, e.getMessage());
        }
    }
}
