package com.example.eva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "centro_deportivo")
public class CentroDeportivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rut;

    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String apertura;
    private String cierre;
    private Integer capacidad;
    private String estado;

    public Integer getRut() { return rut; }
    public void setRut(Integer rut) { this.rut = rut; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getApertura() { return apertura; }
    public void setApertura(String apertura) { this.apertura = apertura; }

    public String getCierre() { return cierre; }
    public void setCierre(String cierre) { this.cierre = cierre; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
