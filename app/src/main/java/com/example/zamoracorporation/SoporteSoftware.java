package com.example.zamoracorporation;

public class SoporteSoftware extends TicketSoporte {
    private double costoLicenciamiento;
    private boolean incluyeRespaldoDatos;

    public SoporteSoftware(String codigoTicket, String nombreUsuario, String documentoIdentidad,
                           String placaEquipo, String descripcionHechos, String ubicacion,
                           double costoBase, String analistaAsignado, double costoLicenciamiento, boolean respaldo) {
        super(codigoTicket, nombreUsuario, documentoIdentidad, placaEquipo, descripcionHechos, ubicacion, costoBase, analistaAsignado);
        this.costoLicenciamiento = costoLicenciamiento;
        this.incluyeRespaldoDatos = respaldo;
    }

    @Override
    public double calcularCostoTotal() {
        double total = getCostoBase() + costoLicenciamiento;
        if (incluyeRespaldoDatos) {
            total += 25.0; // Tarifa de respaldo
        }
        return total;
    }
}