package com.example.zamoracorporation; // Paquete principal del proyecto

public class TicketIncidente extends TicketSoporte { // Subclase de TicketSoporte que representa un caso tipo Incidente (INC)
    public TicketIncidente(String codigo, String titulo, String usuario, String correo, String documento, // Constructor que recibe los datos básicos del incidente
                           String placa, String hechos, double costoBase, String analista) {
        super(codigo, titulo, usuario, correo, documento, placa, hechos, costoBase, analista); // Delega la inicialización común a la clase padre TicketSoporte
    }

    @Override // Indica que sobrescribe el metodo abstracto calcularCostoTotal() de TicketSoporte
    public double calcularCostoTotal() { // Calcula el costo final de un incidente aplicando un 15% adicional sobre el costo base
        return getCostoBase() * 1.15; // Multiplica el costo base por 1.15 (costoBase + 15%)
    }
} // Fin de TicketIncidente