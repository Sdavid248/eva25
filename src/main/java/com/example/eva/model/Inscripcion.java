package com.example.eva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero") 
    private Long numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Usuario usuario;

    @Column(name = "fhinscripcion")
    private String fhInscripcion;

    private String telefono;
    private String correo;
    private String estado;

   
    private String rut;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rut", referencedColumnName = "rut", insertable = false, updatable = false)
    private CentroDeportivo centroDeportivo;

  
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

    public String getFhInscripcion() {
        return fhInscripcion;
    }

    public void setFhInscripcion(String fhInscripcion) {
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

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public CentroDeportivo getCentroDeportivo() {
        return centroDeportivo;
    }

    public void setCentroDeportivo(CentroDeportivo centroDeportivo) {
        this.centroDeportivo = centroDeportivo;
    }
}
