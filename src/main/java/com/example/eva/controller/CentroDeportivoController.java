package com.example.eva.controller;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Inscripcion;
import com.example.eva.model.Usuario;
import com.example.eva.service.InscripcionArchivoService;
import com.example.eva.service.InscripcionService;
import com.example.eva.service.UsuarioService;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.service.NominatimService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // --- Listar centros ---
    @GetMapping("")
    public String listarCentros(Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centrosdeportivos";
    }

    // --- ADMIN: Gestión de centros ---
    @GetMapping("/gestion")
    @PreAuthorize("hasRole('ADMIN')")
    public String gestion(Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centros_gestion";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoCentro(Model model) {
        model.addAttribute("centro", new CentroDeportivo());
        return "centros_form";
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> guardarNuevoCentro(@RequestBody Map<String, String> body) {
        try {
            String nombre = body.get("nombre");
            String direccion = body.get("direccion");
            String telefono = body.get("telefono");
            String apertura = body.get("apertura");
            String cierre = body.get("cierre");

            if (nombre == null || nombre.isBlank() || direccion == null || direccion.isBlank()) {
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

            cd.setLat(coords[0]);
            cd.setLng(coords[1]);

            repo.save(cd);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/editar/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarCentro(@PathVariable Integer rut, Model model) {
        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));
        model.addAttribute("centro", centro);
        return "centros_form";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarCentro(@RequestParam Map<String, String> params) {
        CentroDeportivo centro = new CentroDeportivo();

        if (params.get("rut") != null && !params.get("rut").isBlank()) {
            centro.setRut(Integer.parseInt(params.get("rut")));
        }

        centro.setNombre(params.get("nombre"));
        centro.setDireccion(params.get("direccion"));
        centro.setTelefono(params.getOrDefault("telefono", ""));
        centro.setCorreo(params.getOrDefault("correo", ""));
        centro.setApertura(params.getOrDefault("apertura", ""));
        centro.setCierre(params.getOrDefault("cierre", ""));

        try {
            centro.setCapacidad(Integer.parseInt(params.getOrDefault("capacidad", "0")));
        } catch (Exception e) {
            centro.setCapacidad(0);
        }

        centro.setEstado(params.getOrDefault("estado", "activo"));
        repo.save(centro);

        return "redirect:/centrosdeportivos/gestion";
    }

    @GetMapping("/eliminar/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarCentro(@PathVariable Integer rut) {
        repo.deleteById(rut);
        return "redirect:/centrosdeportivos/gestion";
    }

    // --- Inscripción masiva por ADMIN ---
    @GetMapping("/inscribir/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
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

    // --- Mapa ---
    @GetMapping("/mapa")
    public String mapa(Model model, Authentication auth) {
        boolean esAdmin = auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("esAdmin", esAdmin);

        List<CentroDeportivo> centros = repo.findAll();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (CentroDeportivo c : centros) {
            double lat;
            double lng;

            if (c.getLat() != null && c.getLng() != null) {
                lat = c.getLat();
                lng = c.getLng();
            } else {
                double[] coords = nominatim.obtenerCoordenadas(c.getDireccion());
                if (coords == null) continue;
                lat = coords[0];
                lng = coords[1];
                c.setLat(lat);
                c.setLng(lng);
                repo.save(c);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("rut", c.getRut());
            data.put("nombre", c.getNombre());
            data.put("direccion", c.getDireccion());
            data.put("telefono", c.getTelefono());
            data.put("lat", lat);
            data.put("lng", lng);
            lista.add(data);
        }

        model.addAttribute("centros", lista);
        return "mapa";
    }

    // --- Inscribirse como usuario normal ---
    @PostMapping("/inscribirme/{rut}")
    public String inscribirme(@PathVariable Integer rut, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioLogueado();
        if (usuario == null) {
            return "redirect:/login";
        }

        if (!inscripcionService.estaInscrito(usuario.getIdUser(), rut)) {
            inscripcionService.inscribir(usuario.getIdUser(), rut);
        }

        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        model.addAttribute("centro", centro);
        return "inscripcion_exitosa";
    }

    // --- Mis inscripciones ---
    @GetMapping("/usuario/mis-inscripciones")
    public String misInscripciones(Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioLogueado();
        if (usuario == null) return "redirect:/login";

        List<Inscripcion> inscripciones = inscripcionService.obtenerInscripcionesPorUsuario(usuario.getIdUser());
        model.addAttribute("inscripciones", inscripciones);
        return "mis_inscripciones";
    }
}