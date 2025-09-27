package com.example.eva.util;

import com.example.eva.model.Usuario;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class PdfGenerator {

    public void generarUsuariosPDF(List<Usuario> usuarios, OutputStream outputStream) throws IOException {
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Fuente estándar
            PdfFont font = PdfFontFactory.createFont();

            // Título
            Paragraph titulo = new Paragraph("Lista de Usuarios")
                    .setFont(font)
                    .setFontSize(18)
                    .setBold()
                    .setMarginBottom(20);
            document.add(titulo);

            // Tabla con 4 columnas
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 2}))
                    .useAllAvailableWidth();

            // Encabezados
            String[] headers = {"ID", "Nombre", "Correo", "Estado"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setFont(font));
                cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                table.addHeaderCell(cell);
            }

            // Filas
            for (Usuario usuario : usuarios) {
                table.addCell(new Paragraph(
                        usuario.getIdUser() != null ? usuario.getIdUser().toString() : ""
                ).setFont(font));

                table.addCell(new Paragraph(
                        usuario.getNombre() != null ? usuario.getNombre() : ""
                ).setFont(font));

                table.addCell(new Paragraph(
                        usuario.getCorreo() != null ? usuario.getCorreo() : ""
                ).setFont(font));

                table.addCell(new Paragraph(
                        usuario.getEstado() != null ? usuario.getEstado() : ""
                ).setFont(font));
            }

            document.add(table);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
