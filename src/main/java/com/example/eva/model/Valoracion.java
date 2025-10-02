package com.example.eva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "valoraciones") // en la BD es plural
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo") // en la BD es "codigo"
    private Long codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Usuario usuario;

    @Column(name = "valoracion")
    private int valoracion;

    @Column(name = "fhvaloracion")
    private String fhValoracion;

    private String cometario; // así está en tu BD (ojo al typo!)

    private String rut;

    // Getters y Setters
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public String getFhValoracion() {
        return fhValoracion;
    }

    public void setFhValoracion(String fhValoracion) {
        this.fhValoracion = fhValoracion;
    }

    public String getCometario() {
        return cometario;
    }

    public void setCometario(String cometario) {
        this.cometario = cometario;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }
}
