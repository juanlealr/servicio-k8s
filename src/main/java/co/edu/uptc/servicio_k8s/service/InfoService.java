package co.edu.uptc.servicio_k8s.service;

import co.edu.uptc.servicio_k8s.model.ContainerInfoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InfoService {

    @Value("${app.integrantes}")
    private List<String> integrantes;

    @Value("${app.profesor:Ing. Nombre Del Profesor}")
    private String profesor;

    public ContainerInfoResponseDTO obtenerInformacionContenedor() {
        String horaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String idContenedor;
        try {
            idContenedor = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            idContenedor = System.getenv().getOrDefault("HOSTNAME", "Contenedor-Desconocido");
        }

        File discoRaiz = new File("/");
        long totalStorageBytes = discoRaiz.getTotalSpace();
        long freeStorageBytes = discoRaiz.getFreeSpace();
        long usedStorageBytes = totalStorageBytes - freeStorageBytes;

        String almacenamientoInfo = String.format("Usado: %.2f GB / Total: %.2f GB (Libre: %.2f GB)",
                (double) usedStorageBytes / (1024 * 1024 * 1024),
                (double) totalStorageBytes / (1024 * 1024 * 1024),
                (double) freeStorageBytes / (1024 * 1024 * 1024));

        Runtime runtime = Runtime.getRuntime();
        long maxMemoryBytes = runtime.maxMemory();
        long totalMemoryBytes = runtime.totalMemory();
        long freeMemoryBytes = runtime.freeMemory();
        long usedMemoryBytes = totalMemoryBytes - freeMemoryBytes;

        String ramInfo = String.format("Usada: %.2f MB / Reservada: %.2f MB / Máxima Permitida: %.2f MB",
                (double) usedMemoryBytes / (1024 * 1024),
                (double) totalMemoryBytes / (1024 * 1024),
                (double) maxMemoryBytes / (1024 * 1024));

        return new ContainerInfoResponseDTO(
                horaActual,
                integrantes,
                profesor,
                idContenedor,
                almacenamientoInfo,
                ramInfo);
    }
}