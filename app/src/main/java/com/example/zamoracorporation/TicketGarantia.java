package com.example.zamoracorporation;

public class TicketGarantia extends TicketSoporte {
    private String marcaEquipo;
    private String tipoEquipo;
    private String serialEquipo;

    public TicketGarantia(String codigo, String usuario, String correo, String idCorporativo, String placa,
                          String marcaEquipo, String tipoEquipo, String serialEquipo, String descripcionFalla, String analista) {
        super(codigo, usuario, correo, idCorporativo, placa, descripcionFalla, 0.0, analista);
        this.marcaEquipo = marcaEquipo;
        this.tipoEquipo = tipoEquipo;
        this.serialEquipo = serialEquipo;
    }

    @Override
    public double calcularCostoTotal() {
        return 0.0; // Cubierto por garantía de fabricante
    }

    @Override
    public String obtenerDetalleCompleto() {
        return "🛡️ SOLICITUD DE GARANTÍA\n" +
                "🎫 Código: " + getCodigoTicket() + "\n" +
                "🏢 Frente: " + getFrente() + "\n" +
                "👨‍💻 Analista: " + getAnalistaAsignado() + "\n" +
                "👤 Usuario: " + getNombreUsuario() + "\n" +
                "📧 Correo: " + getCorreoCorporativo() + "\n" +
                "🪪 ID Corporativo: " + getDocumentoIdentidad() + "\n" +
                "🏷️ Marca: " + marcaEquipo + " (" + tipoEquipo + ")\n" +
                "💻 Placa: " + getPlacaEquipo() + "\n" +
                "🔢 Serial: " + serialEquipo + "\n" +
                "📝 Falla reportada: " + getDescripcionHechos() + "\n" +
                "💰 Costo: $0.00 (Cubierto por Garantía)";
    }
}