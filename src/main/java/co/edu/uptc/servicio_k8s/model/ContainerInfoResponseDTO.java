package co.edu.uptc.servicio_k8s.model;

import java.util.List;

public class ContainerInfoResponseDTO {
    private String horaContenedor;
    private List<String> integrantes;
    private String idContenedor;
    private String almacenamientoContenedor;
    private String ramContenedor;

    public ContainerInfoResponseDTO() {
    }

    public ContainerInfoResponseDTO(String horaContenedor, List<String> integrantes, String idContenedor,
            String almacenamientoContenedor, String ramContenedor) {
        this.horaContenedor = horaContenedor;
        this.integrantes = integrantes;
        this.idContenedor = idContenedor;
        this.almacenamientoContenedor = almacenamientoContenedor;
        this.ramContenedor = ramContenedor;
    }

    public String getHoraContenedor() {
        return horaContenedor;
    }

    public void setHoraContenedor(String horaContenedor) {
        this.horaContenedor = horaContenedor;
    }

    public List<String> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<String> integrantes) {
        this.integrantes = integrantes;
    }

    public String getIdContenedor() {
        return idContenedor;
    }

    public void setIdContenedor(String idContenedor) {
        this.idContenedor = idContenedor;
    }

    public String getAlmacenamientoContenedor() {
        return almacenamientoContenedor;
    }

    public void setAlmacenamientoContenedor(String almacenamientoContenedor) {
        this.almacenamientoContenedor = almacenamientoContenedor;
    }

    public String getRamContenedor() {
        return ramContenedor;
    }

    public void setRamContenedor(String ramContenedor) {
        this.ramContenedor = ramContenedor;
    }
}