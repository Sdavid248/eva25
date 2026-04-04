package com.example.eva.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "centro_deportivo")
public class CentroDeportivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rut;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "[0-9]+", message = "Solo números")
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "Hora apertura obligatoria")
    private String apertura;

    @NotBlank(message = "Hora cierre obligatoria")
    private String cierre;

    @NotNull(message = "Capacidad obligatoria")
    @Min(value = 1, message = "Debe ser mayor a 0")
    private Integer capacidad;

    @NotBlank(message = "Estado obligatorio")
    private String estado;

    private Double lat;
    private Double lng;
    private String tipo;

    // 🔥 RELACIÓN INVERSA PARA VER INSCRITOS
    @OneToMany(mappedBy = "centroDeportivo", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones;
// 🔥 ADMIN DEL CENTRO
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario admin;

    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    // ===== GETTERS Y SETTERS =====
    public Integer getRut() {
        return rut;
    }

    public void setRut(Integer rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getApertura() {
        return apertura;
    }

    public void setApertura(String apertura) {
        this.apertura = apertura;
    }

    public String getCierre() {
        return cierre;
    }

    public void setCierre(String cierre) {
        this.cierre = cierre;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }
}
