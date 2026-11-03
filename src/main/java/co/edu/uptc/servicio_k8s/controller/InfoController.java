package co.edu.uptc.servicio_k8s.controller;

import co.edu.uptc.servicio_k8s.model.ContainerInfoResponseDTO;
import co.edu.uptc.servicio_k8s.service.InfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class InfoController {

    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/info")
    public ResponseEntity<ContainerInfoResponseDTO> getContainerInfo() {
        return ResponseEntity.ok(infoService.obtenerInformacionContenedor());
    }
}