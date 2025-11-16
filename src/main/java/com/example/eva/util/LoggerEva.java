package com.example.eva.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerEva {

    // ✔ Instancia única (Singleton)
    private static LoggerEva instancia;

    // ✔ Formato de fecha para los logs
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ✔ Constructor privado: evita crear más instancias
    private LoggerEva() { }

    // ✔ Método público que devuelve la única instancia
    public static LoggerEva getInstancia() {
        if (instancia == null) {
            instancia = new LoggerEva();
        }
        return instancia;
    }

    // ========================
    //   MÉTODOS DE LOG
    // ========================

    public void info(String mensaje) {
        imprimir("INFO", mensaje);
    }

    public void warning(String mensaje) {
        imprimir("WARNING", mensaje);
    }

    public void error(String mensaje) {
        imprimir("ERROR", mensaje);
    }

    public void debug(String mensaje) {
        imprimir("DEBUG", mensaje);
    }

    // ========================
    //   MÉTODO CENTRALIZADO
    // ========================
    private void imprimir(String nivel, String mensaje) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] [" + nivel + "] " + mensaje);
    }
}
