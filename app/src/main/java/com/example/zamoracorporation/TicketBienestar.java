package com.example.zamoracorporation;

public class TicketBienestar extends TicketSoporte {
    public TicketBienestar(String codigo, String usuario, String documento, String placa, String hechos, double costoBase, String analista) {
        super(codigo, usuario, documento, placa, hechos, "Cesde Sede Bogotá (Bienestar y Cuidado)", costoBase, analista);
    }

    @Override
    public double calcularCostoTotal() {
        return getCostoBase() * 1.05;
    }
}