package com.example.zamoracorporation; // Paquete principal de la aplicación

public class TicketSalud extends TicketSoporte { // Subclase de TicketSoporte para casos asociados al frente de Salud
    public TicketSalud(String codigo, String titulo, String usuario, String correo, String documento, // Constructor que recibe los datos básicos del ticket de salud
                       String placa, String hechos, double costoBase, String analista) {
        super(codigo, titulo, usuario, correo, documento, placa, hechos, costoBase, analista); // Delega la inicialización de los atributos comunes a TicketSoporte
    }

    @Override // Sobrescribe el método abstracto calcularCostoTotal() de la clase padre
    public double calcularCostoTotal() { // Calcula el costo final aplicando un 10% adicional sobre el costo base
        return getCostoBase() * 1.10; // Multiplica el costo base por 1.10 (costoBase + 10%)
    }
} // Fin de TicketSalud