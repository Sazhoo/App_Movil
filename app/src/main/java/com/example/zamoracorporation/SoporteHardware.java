package com.example.zamoracorporation;

public class SoporteHardware extends TicketSoporte {
    private double costoRepuestos;
    private boolean requiereIntervencionSitio;

    public SoporteHardware(String codigoTicket, String nombreUsuario, String documentoIdentidad,
                           String placaEquipo, String descripcionHechos, String ubicacion,
                           double costoBase, String analistaAsignado, double costoRepuestos, boolean enSitio) {
        super(codigoTicket, nombreUsuario, documentoIdentidad, placaEquipo, descripcionHechos, ubicacion, costoBase, analistaAsignado);
        this.costoRepuestos = costoRepuestos;
        this.requiereIntervencionSitio = enSitio;
    }

    @Override
    public double calcularCostoTotal() {
        double total = getCostoBase() + costoRepuestos;
        if (requiereIntervencionSitio) {
            total *= 1.10; // 10% recargo en sitio
        }
        return total;
    }
}