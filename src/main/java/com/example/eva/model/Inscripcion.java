package com.example.eva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Usuario usuario;

    @Column(name = "fhinscripcion", nullable = false)
    private LocalDate fhInscripcion;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Integer rut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "rut",
        referencedColumnName = "rut",
        insertable = false,
        updatable = false
    )
    private CentroDeportivo centroDeportivo;

    @PrePersist
    public void prePersist() {
        this.fhInscripcion = LocalDate.now();
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFhInscripcion() {
        return fhInscripcion;
    }

    public void setFhInscripcion(LocalDate fhInscripcion) {
        this.fhInscripcion = fhInscripcion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getRut() {
        return rut;
    }

    public void setRut(Integer rut) {
        this.rut = rut;
    }

    public CentroDeportivo getCentroDeportivo() {
        return centroDeportivo;
    }

    public void setCentroDeportivo(CentroDeportivo centroDeportivo) {
        this.centroDeportivo = centroDeportivo;
    }
}