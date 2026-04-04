package com.example.eva.controller;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Inscripcion;
import com.example.eva.model.Usuario;
import com.example.eva.model.Valoracion;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.service.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private CentroDeportivoService centroService;

    // --- LISTAR CENTROS ---
    @GetMapping("")
    public String listarCentros(Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centrosdeportivos";
    }

    // --- ADMIN: GESTIÓN ---
    @GetMapping("/gestion")
    @PreAuthorize("hasRole('ADMIN')")
    public String gestion(Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centros_gestion";
    }

    // --- FORMULARIO NUEVO ---
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoCentro(Model model) {
        model.addAttribute("centro", new CentroDeportivo());
        return "centros_form";
    }

    // --- EDITAR ---
    @GetMapping("/editar/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarCentro(@PathVariable Integer rut, Model model) {
        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        model.addAttribute("centro", centro);
        return "centros_form";
    }

    // --- GUARDAR ---
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarCentro(
            @Valid @ModelAttribute("centro") CentroDeportivo centro,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "centros_form";
        }

        if (centro.getRut() == null
                && repo.existsByNombreAndDireccion(centro.getNombre(), centro.getDireccion())) {

            model.addAttribute("error", "Ya existe un centro con ese nombre y dirección");
            return "centros_form";
        }

        double[] coords = nominatim.obtenerCoordenadas(centro.getDireccion());
        if (coords == null) {
            model.addAttribute("error", "Dirección no válida");
            return "centros_form";
        }

        centro.setLat(coords[0]);
        centro.setLng(coords[1]);

        repo.save(centro);

        ra.addFlashAttribute("ok", "Centro deportivo registrado correctamente");

        return "redirect:/centrosdeportivos/gestion";
    }

    // --- ELIMINAR ---
    @GetMapping("/eliminar/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarCentro(@PathVariable Integer rut) {
        repo.deleteById(rut);
        return "redirect:/centrosdeportivos/gestion";
    }

    // --- INSCRIPCIÓN MASIVA ADMIN ---
    @GetMapping("/inscribir/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
    public String inscripcionMasiva(@PathVariable Integer rut, Model model) {

        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        model.addAttribute("centro", centro);
        model.addAttribute("usuarios", usuarioRepo.findAll());

        return "inscripcion_masiva";
    }

    // 🔥 CORREGIDO PARA JSON
    @PostMapping("/inscribir/{rut}")
    public String procesarInscripcion(
            @PathVariable Integer rut,
            @RequestBody Map<String, List<Long>> body) {

        List<Long> usuariosSeleccionados = body.get("usuarios");

        if (usuariosSeleccionados != null && !usuariosSeleccionados.isEmpty()) {
            inscripcionService.inscribirMasivo(rut, usuariosSeleccionados);
        }

        return "redirect:/centrosdeportivos";
    }

    // 🔥 NUEVO: VER INSCRITOS
    @GetMapping("/admin/inscritos/{rut}")
    @PreAuthorize("hasRole('ADMIN')")
    public String verInscritos(@PathVariable Integer rut, Model model) {

        CentroDeportivo centro = repo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        List<Inscripcion> inscripciones = inscripcionService.obtenerPorCentro(rut);

        model.addAttribute("centro", centro);
        model.addAttribute("inscripciones", inscripciones);

        return "admin_inscritos";
    }

    // --- MAPA ---
    // --- MAPA ---
    @GetMapping("/mapa")
    public String mapa(Model model, Authentication auth) {

        boolean esAdmin = auth != null
                && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("esAdmin", esAdmin);

        List<Map<String, Object>> lista = new ArrayList<>();

        for (CentroDeportivo c : repo.findAll()) {

            if (c.getLat() == null || c.getLng() == null) {
                continue;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("rut", c.getRut());
            data.put("nombre", c.getNombre());
            data.put("direccion", c.getDireccion());
            data.put("lat", c.getLat());
            data.put("lng", c.getLng());

            lista.add(data);
        }

        model.addAttribute("centros", lista);
        return "mapa";
    }

    // 🔥 HU-14
    @GetMapping("/cercanos")
    @ResponseBody
    public List<Map<String, Object>> obtenerCercanos(
            @RequestParam double lat,
            @RequestParam double lng) {

        List<CentroDeportivo> centros = centroService.obtenerCercanos(lat, lng);

        List<Map<String, Object>> response = new ArrayList<>();

        for (CentroDeportivo c : centros) {

            double distancia = centroService.calcularDistanciaPublica(
                    lat, lng, c.getLat(), c.getLng()
            );

            double promedio = valoracionService.promedio(c.getRut()); // 🔥 NUEVO

            Map<String, Object> data = new HashMap<>();
            data.put("rut", c.getRut());
            data.put("nombre", c.getNombre());
            data.put("direccion", c.getDireccion());
            data.put("lat", c.getLat());
            data.put("lng", c.getLng());
            data.put("tipo", c.getTipo());
            data.put("distancia", Math.round(distancia * 100.0) / 100.0);

            data.put("promedio", Math.round(promedio * 10.0) / 10.0); // 🔥 NUEVO

            response.add(data);
        }

        return response;
    }

    // 🔥 HU-8
    @GetMapping("/buscar")
    @ResponseBody
    public List<Map<String, Object>> buscarCentro(@RequestParam String nombre) {

        List<CentroDeportivo> centros = centroService.buscarPorNombre(nombre);

        List<Map<String, Object>> response = new ArrayList<>();

        for (CentroDeportivo c : centros) {

            Map<String, Object> data = new HashMap<>();
            data.put("rut", c.getRut());
            data.put("nombre", c.getNombre());
            data.put("direccion", c.getDireccion());
            data.put("lat", c.getLat());
            data.put("lng", c.getLng());

            response.add(data);
        }

        return response;
    }

    // --- INSCRIBIRSE USUARIO ---
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

    // --- MIS INSCRIPCIONES ---
    @GetMapping("/usuario/mis-inscripciones")
    public String misInscripciones(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Inscripcion> inscripciones
                = inscripcionService.obtenerInscripcionesPorUsuario(usuario.getIdUser());

        model.addAttribute("inscripciones", inscripciones);

        return "mis_inscripciones";
    }

    // --- CANCELAR ---
    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id, RedirectAttributes ra) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            inscripcionService.cancelar(id, usuario.getIdUser());
            ra.addFlashAttribute("ok", "Inscripción cancelada correctamente");

        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/centrosdeportivos/usuario/mis-inscripciones";
    }
    @Autowired
    private ValoracionService valoracionService;

// 🔥 GUARDAR VALORACIÓN
    // 🔥 GUARDAR VALORACIÓN
    @PostMapping("/valorar/{rut}")
    @ResponseBody // 🔥 IMPORTANTE PARA FETCH
    public Map<String, Object> valorar(
            @PathVariable Integer rut,
            @RequestParam int estrellas,
            @RequestParam String comentario) {

        Map<String, Object> response = new HashMap<>();

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            response.put("error", "No autenticado");
            return response;
        }

        try {
            valoracionService.guardar(usuario, rut, estrellas, comentario);
            response.put("ok", "Valoración guardada");

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }
// 🔥 VER VALORACIONES (JSON)

    @GetMapping("/valoraciones/{rut}")
    @ResponseBody
    public List<Map<String, Object>> verValoraciones(@PathVariable Integer rut) {

        List<Valoracion> lista = valoracionService.obtenerPorCentro(rut);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Valoracion v : lista) {
            Map<String, Object> data = new HashMap<>();
            data.put("usuario", v.getUsuario().getNombre());
            data.put("valoracion", v.getValoracion());
            data.put("comentario", v.getCometario());
            data.put("fecha", v.getFhValoracion());
            response.add(data);
        }

        return response;
    }

    @GetMapping("/top")
    @ResponseBody
    public List<Map<String, Object>> topCentros() {
        return valoracionService.topCentros();
    }
    @GetMapping("/api")
@ResponseBody
public List<Map<String, Object>> obtenerCentrosJSON() {

    List<CentroDeportivo> centros = repo.findAll();
    List<Map<String, Object>> response = new ArrayList<>();

    for (CentroDeportivo c : centros) {

        if (c.getLat() == null || c.getLng() == null) continue;

        double promedio = valoracionService.promedio(c.getRut());

        Map<String, Object> data = new HashMap<>();
        data.put("rut", c.getRut());
        data.put("nombre", c.getNombre());
        data.put("direccion", c.getDireccion());
        data.put("lat", c.getLat());
        data.put("lng", c.getLng());
        data.put("tipo", c.getTipo());
        data.put("promedio", Math.round(promedio * 10.0) / 10.0);

        response.add(data);
    }

    return response;
}
}
