package com.example.zamoracorporation; // Paquete principal de la aplicación

public class TicketOrdenTrabajo extends TicketSoporte { // Subclase de TicketSoporte para casos tipo Orden de Trabajo (WO)
    public TicketOrdenTrabajo(String codigo, String titulo, String usuario, String correo, String documento, // Constructor que recibe los datos base de la orden de trabajo
                              String placa, String hechos, double costoBase, String analista) {
        super(codigo, titulo, usuario, correo, documento, placa, hechos, costoBase, analista); // Inicializa los campos comunes usando el constructor de TicketSoporte
    }

    @Override // Sobrescribe el método abstracto calcularCostoTotal() de la clase padre
    public double calcularCostoTotal() { // Calcula el costo final de una orden de trabajo con un recargo del 5% sobre el costo base
        return getCostoBase() * 1.05; // Multiplica el costo base por 1.05 (costoBase + 5%)
    }
} // Fin de TicketOrdenTrabajo