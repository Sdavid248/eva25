package com.example.eva.service;

import java.awt.Font;
import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.springframework.stereotype.Service;

import com.example.eva.repository.InscripcionRepository;

@Service
public class GraficosService {

    public enum Orden { ASC, DESC, ALFA }

    private final InscripcionRepository inscripcionRepository;

    public GraficosService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

  
    private Paint[] paletaEVA() {
        return new Paint[]{
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(241, 196, 15),
                new Color(231, 76, 60),
                new Color(155, 89, 182)
        };
    }

    public byte[] generarGraficoUsuariosEstadoPorCentro(int width, int height) throws Exception {
        return generarGraficoUsuariosEstadoPorCentro(width, height, Orden.DESC, 0);
    }

    public byte[] generarGraficoUsuariosEstadoPorCentro(int width, int height, Orden orden, int rotacionGrados) throws Exception {

        List<Object[]> rows = inscripcionRepository.countUsuariosEstadoPorCentro();

        Map<String, Map<String, Long>> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String centro = r[0] == null ? "Sin Centro" : r[0].toString();
            String estado = r[1] == null ? "DESCONOCIDO" : r[1].toString();
            long cantidad = ((Number) r[2]).longValue();

            map.computeIfAbsent(centro, k -> new LinkedHashMap<>());
            map.get(centro).put(estado, map.get(centro).getOrDefault(estado, 0L) + cantidad);
        }

        List<String> centros = new ArrayList<>(map.keySet());
        if (orden != null) {
            if (orden == Orden.ALFA) {
                centros.sort(String::compareToIgnoreCase);
            } else {
                Map<String, Long> totales = new HashMap<>();
                for (String c : centros) {
                    totales.put(c, map.get(c).values().stream().mapToLong(Long::longValue).sum());
                }
                centros.sort((a, b) -> orden == Orden.DESC ?
                        Long.compare(totales.get(b), totales.get(a)) :
                        Long.compare(totales.get(a), totales.get(b)));
            }
        }

        LinkedHashSet<String> estadosOrder = new LinkedHashSet<>();
        for (Map<String, Long> m : map.values()) estadosOrder.addAll(m.keySet());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        long maxValor = 0;

        for (String centro : centros) {
            Map<String, Long> inner = map.get(centro);
            for (String estado : estadosOrder) {
                long val = inner.getOrDefault(estado, 0L);
                dataset.addValue(val, estado, centro);
                maxValor = Math.max(maxValor, val);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Usuarios por Estado en Centros Deportivos",
                "Centro Deportivo",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        Paint[] colors = paletaEVA();
        for (int i = 0; i < plot.getDataset().getRowCount(); i++) {
            renderer.setSeriesPaint(i, colors[i % colors.length]);
        }

        renderer.setShadowVisible(false);
        renderer.setItemMargin(0.05);
        renderer.setMaximumBarWidth(0.10);

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        if (rotacionGrados == 45) {
            plot.getDomainAxis().setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        } else if (rotacionGrados == 90) {
            plot.getDomainAxis().setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));
        }

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        int step = Math.max(1, (int) Math.ceil(maxValor / 10.0));
        rangeAxis.setTickUnit(new NumberTickUnit(step));
        rangeAxis.setRange(0, (Math.ceil(maxValor / (double) step) * step));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, width, height);
        return baos.toByteArray();
    }


    public byte[] generarGraficoLinealUsuarios(int width, int height) throws Exception {
        return generarGraficoLinealUsuarios(width, height, 0);
    }

    public byte[] generarGraficoLinealUsuarios(int width, int height, int rotacionGrados) throws Exception {
        List<Object[]> rows = inscripcionRepository.countInscripcionesPorUsuario();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        long maxValor = 0;

        for (Object[] r : rows) {
            String usuario = r[0] == null ? "Sin Nombre" : r[0].toString();
            long cantidad = ((Number) r[1]).longValue();
            dataset.addValue(cantidad, "Inscripciones", usuario);
            maxValor = Math.max(maxValor, cantidad);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Inscripciones por Usuario",
                "Usuario",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, paletaEVA()[0]);
        renderer.setShadowVisible(false);

        if (rotacionGrados == 45)
            plot.getDomainAxis().setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4));
        else if (rotacionGrados == 90)
            plot.getDomainAxis().setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2));

        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        int step = Math.max(1, (int) Math.ceil(maxValor / 10.0));
        range.setTickUnit(new NumberTickUnit(step));
        range.setRange(0, (Math.ceil(maxValor / (double) step) * step));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, width, height);
        return baos.toByteArray();
    }

   
    public byte[] generarGraficoTortaCentros(int width, int height) throws Exception {

        List<Object[]> rows = inscripcionRepository.countUsuariosEstadoPorCentro();

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        rows.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r[0] == null ? "Sin Centro" : r[0].toString(),
                        java.util.stream.Collectors.summingLong(r -> ((Number) r[2]).longValue())
                ))
                .forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Usuarios por Centro Deportivo",
                dataset,
                true,
                true,
                false
        );

        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();

        Paint[] colors = paletaEVA();
        int i = 0;
        for (Comparable<?> key : dataset.getKeys())
            plot.setSectionPaint(key, colors[i++ % colors.length]);

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} usuarios ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0.0%")
        ));

        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, width, height);
        return baos.toByteArray();
    }


    public byte[] generarReportePDF() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("%PDF-1.4\n% Reporte generado por EVA\n%%EOF".getBytes());
        return baos.toByteArray();
    }
}
