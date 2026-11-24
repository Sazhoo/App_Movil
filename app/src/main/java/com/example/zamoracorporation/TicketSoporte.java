package com.example.zamoracorporation;

public abstract class TicketSoporte {
    private String codigoTicket;
    private String nombreUsuario;
    private String correoCorporativo;
    private String documentoIdentidad;
    private String placaEquipo;
    private String descripcionHechos;
    private String frente;
    private double costoBase;
    private String analistaAsignado;

    public TicketSoporte(String codigoTicket, String nombreUsuario, String correoCorporativo, String documentoIdentidad,
                         String placaEquipo, String descripcionHechos, double costoBase, String analistaAsignado) {
        this.codigoTicket = codigoTicket;
        this.nombreUsuario = nombreUsuario;
        this.correoCorporativo = correoCorporativo;
        this.documentoIdentidad = documentoIdentidad;
        this.placaEquipo = placaEquipo;
        this.descripcionHechos = descripcionHechos;
        this.costoBase = costoBase;
        this.analistaAsignado = analistaAsignado;
        this.frente = determinarFrente(correoCorporativo);
    }

    private String determinarFrente(String correo) {
        if (correo != null && correo.toLowerCase().endsWith("@compensar.com")) {
            return "Caja de compensacion";
        } else if (correo != null && correo.toLowerCase().endsWith("@consorciosalud.com")) {
            return "Salud";
        }
        return "No definido";
    }

    public String getCodigoTicket() { return codigoTicket; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreoCorporativo() { return correoCorporativo; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public String getPlacaEquipo() { return placaEquipo; }
    public String getDescripcionHechos() { return descripcionHechos; }
    public String getFrente() { return frente; }
    public double getCostoBase() { return costoBase; }
    public String getAnalistaAsignado() { return analistaAsignado; }

    public abstract double calcularCostoTotal();

    @Override
    public String toString() {
        return codigoTicket + " [" + frente + "] - " + analistaAsignado;
    }

    public String obtenerDetalleCompleto() {
        return "🎫 Código: " + codigoTicket + "\n" +
                "🏢 Frente: " + frente + "\n" +
                "📌 Estado: Asignado\n" +
                "👨‍💻 Analista: " + analistaAsignado + "\n" +
                "👤 Usuario: " + nombreUsuario + "\n" +
                "📧 Correo: " + correoCorporativo + "\n" +
                "🪪 ID Corporativo / Doc: " + documentoIdentidad + "\n" +
                "💻 Placa Equipo: " + placaEquipo + "\n" +
                "📝 Detalle / Hechos: " + descripcionHechos + "\n" +
                "💰 Costo Estimado: $" + String.format("%.2f", calcularCostoTotal());
    }
}