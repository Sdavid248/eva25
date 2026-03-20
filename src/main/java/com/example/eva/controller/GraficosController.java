package com.example.eva.controller;

import com.example.eva.service.GraficosService;
import com.example.eva.service.GraficosService.Orden;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GraficosController {

    private final GraficosService graficosService;

    public GraficosController(GraficosService graficosService) {
        this.graficosService = graficosService;
    }

    
    @GetMapping("/graficos/centros.png")
    public ResponseEntity<byte[]> graficoCentros(
            @RequestParam(defaultValue = "DESC") String orden,
            @RequestParam(defaultValue = "0") int rot,
            @RequestParam(defaultValue = "900") int w,
            @RequestParam(defaultValue = "400") int h
    ) {
        try {
            Orden o;
            try {
                o = Orden.valueOf(orden.toUpperCase());
            } catch (Exception ex) {
                o = Orden.DESC;
            }
            byte[] grafico = graficosService.generarGraficoUsuariosEstadoPorCentro(w, h, o, rot);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"centros.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(grafico);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }


    @GetMapping("/graficos/lineal.png")
    public ResponseEntity<byte[]> graficoLinealUsuarios(
            @RequestParam(defaultValue = "0") int rot,
            @RequestParam(defaultValue = "900") int w,
            @RequestParam(defaultValue = "300") int h
    ) {
        try {
            byte[] grafico = graficosService.generarGraficoLinealUsuarios(w, h, rot);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"lineal.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(grafico);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    
    @GetMapping("/graficos/torta.png")
    public ResponseEntity<byte[]> graficoTortaCentros(
            @RequestParam(defaultValue = "600") int w,
            @RequestParam(defaultValue = "400") int h
    ) {
        try {
            byte[] grafico = graficosService.generarGraficoTortaCentros(w, h);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"torta.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(grafico);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    
    @GetMapping("/graficos/reporte.pdf")
    public ResponseEntity<byte[]> descargarPDF() {
        try {
            byte[] pdf = graficosService.generarReportePDF();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_graficos_eva.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
