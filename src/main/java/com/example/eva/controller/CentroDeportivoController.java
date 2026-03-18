package com.example.eva.controller;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Usuario;
import com.example.eva.service.InscripcionArchivoService;
import com.example.eva.service.InscripcionService;
import com.example.eva.service.UsuarioService;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.service.NominatimService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/centrosdeportivos")
public class CentroDeportivoController {

    @Autowired
    private CentroDeportivoRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private InscripcionArchivoService archivoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NominatimService nominatim;

    @GetMapping("")
    public String listarCentros(Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centrosdeportivos";
    }

    @GetMapping("/gestion")
    public String gestion(Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centros_gestion";
    }

    @GetMapping("/nuevo")
    public String nuevoCentro(Model model) {
        model.addAttribute("centro", new CentroDeportivo());
        return "centros_form";
    }

    @PostMapping("/nuevo")
    @ResponseBody
    public ResponseEntity<?> guardarNuevoCentro(@RequestBody Map<String, String> body) {

        try {
            String nombre = body.get("nombre");
            String direccion = body.get("direccion");
            String telefono = body.get("telefono");
            String apertura = body.get("apertura");
            String cierre = body.get("cierre");

            if (nombre == null || direccion == null) {
                return ResponseEntity.badRequest().body("Faltan datos obligatorios");
            }

            double[] coords = nominatim.obtenerCoordenadas(direccion);
            if (coords == null) {
                return ResponseEntity.badRequest().body("Dirección no encontrada");
            }

            CentroDeportivo cd = new CentroDeportivo();
            cd.setNombre(nombre);
            cd.setDireccion(direccion);
            cd.setTelefono(telefono);
            cd.setApertura(apertura);
            cd.setCierre(cierre);
            cd.setEstado("activo");
            cd.setCapacidad(0);

            repo.save(cd);

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/editar/{rut}")
    public String editarCentro(@PathVariable Integer rut, Model model) {
        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));
        model.addAttribute("centro", centro);
        return "centros_form";
    }

    @PostMapping("/guardar")
    public String guardarCentro(CentroDeportivo centro) {
        repo.save(centro);
        return "redirect:/centrosdeportivos/gestion";
    }

    @GetMapping("/eliminar/{rut}")
    public String eliminarCentro(@PathVariable Integer rut) {
        repo.deleteById(rut);
        return "redirect:/centrosdeportivos/gestion";
    }

    @GetMapping("/inscribir/{rut}")
    public String inscripcionMasiva(@PathVariable Integer rut, Model model) {

        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        List<Usuario> usuarios = usuarioRepo.findAll();

        model.addAttribute("centro", centro);
        model.addAttribute("usuarios", usuarios);

        return "inscripcion_masiva";
    }

    @PostMapping("/inscribir/{rut}")
    public String procesarInscripcion(
            @PathVariable Integer rut,
            @RequestParam(name = "usuariosSeleccionados", required = false) List<Long> usuariosSeleccionados) {

        if (usuariosSeleccionados != null && !usuariosSeleccionados.isEmpty()) {
            inscripcionService.inscribirMasivo(rut, usuariosSeleccionados);
        }

        return "redirect:/centrosdeportivos";
    }

    @PostMapping(value = "/inscribir/{rut}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> procesarInscripcionJson(
            @PathVariable Integer rut,
            @RequestBody Map<String, List<Long>> body) {

        List<Long> usuarios = body.get("usuarios");

        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.badRequest().body("No hay usuarios seleccionados");
        }

        inscripcionService.inscribirMasivo(rut, usuarios);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/inscribir/{rut}/csv")
    public String inscripcionMasivaCSV(
            @PathVariable Integer rut,
            @RequestParam("csv") String csv,
            Model model
    ) {

        if (!usuarioService.esAdmin()) {
            return "redirect:/";
        }

        if (csv == null || csv.trim().isEmpty()) {
            model.addAttribute("creados", 0);
            model.addAttribute("inscritos", 0);
            model.addAttribute("repetidos", 0);
            return "resultado_masivo";
        }

        Map<String, Integer> resultado
                = archivoService.procesarCSVTexto(csv, rut);

        if (resultado == null) {
            resultado = new HashMap<>();
        }

        model.addAttribute("creados", resultado.getOrDefault("creados", 0));
        model.addAttribute("inscritos", resultado.getOrDefault("inscritos", 0));
        model.addAttribute("repetidos", resultado.getOrDefault("repetidos", 0));

        return "resultado_masivo";
    }

@GetMapping("/mapa")
public String mapa(Model model, Authentication auth) {

    boolean esAdmin = auth != null &&
            auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    model.addAttribute("esAdmin", esAdmin);

    List<CentroDeportivo> centros = repo.findAll();
    List<Map<String, Object>> lista = new ArrayList<>();

    for (CentroDeportivo c : centros) {

        if (c.getDireccion() != null && !c.getDireccion().isEmpty()) {

            double[] coords = nominatim.obtenerCoordenadas(c.getDireccion());

            if (coords != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("nombre", c.getNombre());
                data.put("direccion", c.getDireccion());
                data.put("lat", coords[0]);
                data.put("lng", coords[1]);
                lista.add(data);
            }
        }
    }

    model.addAttribute("centros", lista);
    return "mapa";
}
}
