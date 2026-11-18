package com.example.zamoracorporation;

public abstract class TicketSoporte {
    private String codigoTicket;
    private String nombreUsuario;
    private String documentoIdentidad;
    private String placaEquipo;
    private String descripcionHechos;
    private String ubicacion;
    private double costoBase;
    private String analistaAsignado; // Juan Zamora o Daniela Rojas

    public TicketSoporte(String codigoTicket, String nombreUsuario, String documentoIdentidad,
                         String placaEquipo, String descripcionHechos, String ubicacion, double costoBase, String analistaAsignado) {
        this.codigoTicket = codigoTicket;
        this.nombreUsuario = nombreUsuario;
        this.documentoIdentidad = documentoIdentidad;
        this.placaEquipo = placaEquipo;
        this.descripcionHechos = descripcionHechos;
        this.ubicacion = ubicacion;
        this.costoBase = costoBase;
        this.analistaAsignado = analistaAsignado;
    }

    public String getCodigoTicket() { return codigoTicket; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public String getPlacaEquipo() { return placaEquipo; }
    public String getDescripcionHechos() { return descripcionHechos; }
    public String getUbicacion() { return ubicacion; }
    public double getCostoBase() { return costoBase; }
    public String getAnalistaAsignado() { return analistaAsignado; }

    public abstract double calcularCostoTotal();

    @Override
    public String toString() {
        return codigoTicket + " - Asignado a " + analistaAsignado;
    }

    public String obtenerDetalleCompleto() {
        return "🎫 Código: " + codigoTicket + "\n" +
                "📌 Estado: Asignado\n" +
                "👨‍💻 Analista: " + analistaAsignado + "\n" +
                "👤 Usuario: " + nombreUsuario + "\n" +
                "🪪 Documento: " + documentoIdentidad + "\n" +
                "💻 Placa Equipo: " + placaEquipo + "\n" +
                "📍 Ubicación: " + ubicacion + "\n" +
                "📝 Hechos: " + descripcionHechos + "\n" +
                "💰 Costo Total: $" + String.format("%.2f", calcularCostoTotal());
    }
}