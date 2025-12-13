package com.example.eva.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscripcionResultadoDTO {

    private int usuariosCreados;
    private int usuariosExistentes;
    private int inscripcionesCreadas;
    private int inscripcionesDuplicadas;
    private int errores;
}
