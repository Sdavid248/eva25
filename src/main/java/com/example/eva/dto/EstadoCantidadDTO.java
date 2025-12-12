package com.example.eva.dto;

public class EstadoCantidadDTO {
    private String estado;
    private Long cantidad;

    public EstadoCantidadDTO(String estado, Long cantidad) {
        this.estado = estado;
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
