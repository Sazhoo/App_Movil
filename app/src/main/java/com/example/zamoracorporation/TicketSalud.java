package com.example.zamoracorporation;

public class TicketSalud extends TicketSoporte {
    public TicketSalud(String codigo, String usuario, String documento, String placa, String hechos, double costoBase, String analista) {
        super(codigo, usuario, documento, placa, hechos, "Cesde Sede Medellín (Salud)", costoBase, analista);
    }

    @Override
    public double calcularCostoTotal() {
        return getCostoBase() * 1.15;
    }
}