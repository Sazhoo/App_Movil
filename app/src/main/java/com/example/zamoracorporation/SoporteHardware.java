package com.example.zamoracorporation;

public class SoporteHardware extends TicketSoporte {
    private String ubicacion;

    public SoporteHardware(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo,
                           String documentoIdentidad, String placaEquipo, String descripcionHechos,
                           String ubicacion, double costoBase, String analistaAsignado) {
        super(codigoTicket, titulo, nombreUsuario, correoCorporativo, documentoIdentidad,
                placaEquipo, descripcionHechos, costoBase, analistaAsignado);
        this.ubicacion = ubicacion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    public double calcularCostoTotal() {
        return getCostoBase() * 1.10;
    }
}