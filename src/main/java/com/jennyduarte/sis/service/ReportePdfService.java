package com.jennyduarte.sis.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jennyduarte.sis.entity.DetalleTransaccion;
import com.jennyduarte.sis.entity.Pago;
import com.jennyduarte.sis.entity.Transaccion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@Slf4j
public class ReportePdfService {

    private final TransaccionService transaccionService;
    private final DetalleTransaccionService detalleTransaccionService;
    private final PagoService pagoService;

    public ReportePdfService(TransaccionService transaccionService,
                             DetalleTransaccionService detalleTransaccionService,
                             PagoService pagoService) {
        this.transaccionService = transaccionService;
        this.detalleTransaccionService = detalleTransaccionService;
        this.pagoService = pagoService;
    }

    /**
     * Genera un PDF con el detalle de la transacción y sus items.
     * Retorna un arreglo de bytes (byte[]) que representa el PDF.
     */
    public byte[] generarReporteTransaccion(Long transaccionId) throws Exception {
        // 1) Obtener la transacción y sus detalles
        Transaccion transaccion = transaccionService.obtenerPorId(transaccionId);
        if (transaccion == null) {
            throw new IllegalArgumentException("Transacción no encontrada con ID: " + transaccionId);
        }
        List<DetalleTransaccion> detalles = detalleTransaccionService.listarPorTransaccion(transaccionId);
        List<Pago> pagos = pagoService.listarPorTransaccion(transaccionId);

        // 2) Crear stream donde se escribirá el PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 3) Crear documento y escritor PDF de iText
        Document document = new Document(PageSize.A4); // A4, puedes cambiar a tu gusto
        PdfWriter.getInstance(document, baos);

        // 4) Abrir el documento para poder escribir
        document.open();

        // 5) Agregar contenido: Título
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("REPORTE DE TRANSACCIÓN", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" ")); // Espacio en blanco

        // 6) Agregar datos principales de la transacción
        Font negrita = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        document.add(new Paragraph("ID Transacción: " + transaccion.getId(), negrita));
        document.add(new Paragraph("Fecha: " + transaccion.getFecha()));
        document.add(new Paragraph("Cliente: " + transaccion.getCliente().getNombre()));
        document.add(new Paragraph("Vendedor: " + transaccion.getVendedor().getContacto().getNombre()));
        document.add(new Paragraph("Tipo: " + transaccion.getTipo()));
        document.add(new Paragraph("Estado: " + transaccion.getEstado()));
        document.add(new Paragraph("Total: " + transaccion.getTotal()));
        document.add(new Paragraph("Pagado: " + transaccion.getPagado()));
        document.add(new Paragraph("Saldo: " + transaccion.getSaldo()));
        document.add(new Paragraph("Notas: " + (transaccion.getNotas() == null ? "" : transaccion.getNotas())));
        document.add(new Paragraph(" "));

        // 7) Crear una tabla para los detalles
        PdfPTable tablaDetalles = new PdfPTable(5);
        tablaDetalles.setWidthPercentage(100);
        tablaDetalles.setWidths(new float[]{ 2f, 4f, 2f, 2f, 2f });

        // Encabezados
        tablaDetalles.addCell(crearCeldaTitulo("Producto"));
        tablaDetalles.addCell(crearCeldaTitulo("Descripción"));
        tablaDetalles.addCell(crearCeldaTitulo("Cantidad"));
        tablaDetalles.addCell(crearCeldaTitulo("Precio Unit."));
        tablaDetalles.addCell(crearCeldaTitulo("Subtotal"));

        // Llenar con los datos
        for (DetalleTransaccion det : detalles) {
            tablaDetalles.addCell(det.getProducto().getNombre());
            tablaDetalles.addCell(det.getProducto().getDescripcion()); // Asumiendo que tienes un campo 'descripcion'
            tablaDetalles.addCell(String.valueOf(det.getCantidad()));
            tablaDetalles.addCell(String.valueOf(det.getPrecioUnitario()));
            tablaDetalles.addCell(String.valueOf(det.getSubtotal()));
        }

        // Agregar la tabla al PDF
        document.add(tablaDetalles);
        document.add(new Paragraph(" "));

        // 8) Tabla de pagos (opcional)
        if (pagos != null && !pagos.isEmpty()) {
            Paragraph subtituloPagos = new Paragraph("Pagos Realizados", negrita);
            document.add(subtituloPagos);

            PdfPTable tablaPagos = new PdfPTable(4);
            tablaPagos.setWidthPercentage(100);
            tablaPagos.setWidths(new float[]{ 2f, 2f, 3f, 3f });

            tablaPagos.addCell(crearCeldaTitulo("ID Pago"));
            tablaPagos.addCell(crearCeldaTitulo("Monto"));
            tablaPagos.addCell(crearCeldaTitulo("Método"));
            tablaPagos.addCell(crearCeldaTitulo("Fecha"));

            for (Pago p : pagos) {
                tablaPagos.addCell(String.valueOf(p.getId()));
                tablaPagos.addCell(String.valueOf(p.getMonto()));
                tablaPagos.addCell(p.getMetodo().name());
                tablaPagos.addCell(String.valueOf(p.getFechaPago()));
            }

            document.add(tablaPagos);
        }

        // 9) Cerrar el documento
        document.close();

        // 10) Retornar el byte[]
        return baos.toByteArray();
    }

    /**
     * Genera un PDF de tipo “boleta” (comprobante simple) al momento de crear la transacción.
     * Ejemplo muy similar, pero con un diseño más breve.
     */
    public byte[] generarBoleta(Long transaccionId) throws Exception {
        // Lógica muy similar a la de arriba, solo que tal vez con otro formato
        // ... (puedes personalizar el diseño a tu gusto)
        return generarReporteTransaccion(transaccionId);
    }

    // Método utilitario para celda de encabezado
    private PdfPCell crearCeldaTitulo(String contenido) {
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(contenido, fontBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
}
