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

    // Se inyecta la lista desde application.yaml / application.properties
    @Value("${app.integrantes}")
    private List<String> integrantes;

    public ContainerInfoResponseDTO obtenerInformacionContenedor() {
        // 1. Obtención de la hora actual del contenedor
        String horaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 2. Obtención del nombre/ID del pod o contenedor en ejecucion
        String idContenedor;
        try {
            idContenedor = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            idContenedor = System.getenv().getOrDefault("HOSTNAME", "Contenedor-Desconocido");
        }

        // 3. Lectura del almacenamiento en la raíz del sistema de archivos
        File discoRaiz = new File("/");
        long totalStorageBytes = discoRaiz.getTotalSpace();
        long freeStorageBytes = discoRaiz.getFreeSpace();
        long usedStorageBytes = totalStorageBytes - freeStorageBytes;

        String almacenamientoInfo = String.format("Usado: %.2f GB / Total: %.2f GB (Libre: %.2f GB)",
                (double) usedStorageBytes / (1024 * 1024 * 1024),
                (double) totalStorageBytes / (1024 * 1024 * 1024),
                (double) freeStorageBytes / (1024 * 1024 * 1024));

        // 4. Lectura de la memoria RAM asignada a la JVM
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
                idContenedor,
                almacenamientoInfo,
                ramInfo);
    }
}