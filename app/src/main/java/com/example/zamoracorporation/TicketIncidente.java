package com.example.zamoracorporation;

public class TicketIncidente extends TicketSoporte {
    public TicketIncidente(String codigo, String titulo, String usuario, String correo, String documento,
                           String placa, String hechos, double costoBase, String analista) {
        super(codigo, titulo, usuario, correo, documento, placa, hechos, costoBase, analista);
    }

    @Override
    public double calcularCostoTotal() {
        return getCostoBase() * 1.15;
    }
}