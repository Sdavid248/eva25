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

            PdfFont font = PdfFontFactory.createFont();

            
            Paragraph titulo = new Paragraph("Lista de Usuarios")
                    .setFont(font)
                    .setFontSize(18)
                    .setBold()
                    .setMarginBottom(20);
            document.add(titulo);

            
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 2, 2, 3}))
                    .useAllAvailableWidth();

            String[] headers = {"ID", "Documento", "Nombre", "Correo", "Estado", "Teléfono"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setFont(font));
                cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                table.addHeaderCell(cell);
            }

            for (Usuario usuario : usuarios) {
                table.addCell(String.valueOf(usuario.getIdUser()));
                table.addCell(usuario.getDocumento() != null ? usuario.getDocumento() : "");
                table.addCell(usuario.getNombre() != null ? usuario.getNombre() : "");
                table.addCell(usuario.getCorreo() != null ? usuario.getCorreo() : "");
                table.addCell(usuario.getEstado() != null ? usuario.getEstado() : "");
                table.addCell(usuario.getTelefono() != null ? usuario.getTelefono() : "");
            }

            document.add(table);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
