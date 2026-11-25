package com.example.zamoracorporation; // Paquete principal de la aplicación

public class SoporteSoftware extends TicketSoporte { // Subclase de TicketSoporte especializada en soporte de software
    private String ubicacion; // Área, sede o ubicación física donde se reporta el caso de software

    public SoporteSoftware(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo, // Constructor con los campos base más la ubicación
                           String documentoIdentidad, String placaEquipo, String descripcionHechos,
                           String ubicacion, double costoBase, String analistaAsignado) {
        super(codigoTicket, titulo, nombreUsuario, correoCorporativo, documentoIdentidad, // Inicializa los atributos comunes heredados de TicketSoporte
                placaEquipo, descripcionHechos, costoBase, analistaAsignado);
        this.ubicacion = ubicacion; // Guarda la ubicación del equipo/usuario
    }

    public String getUbicacion() { // Obtiene la ubicación física registrada en el ticket
        return ubicacion;
    }

    @Override // Sobrescribe el método abstracto calcularCostoTotal() de TicketSoporte
    public double calcularCostoTotal() { // Calcula el costo final de soporte de software con un 8% adicional sobre el costo base
        return getCostoBase() * 1.08; // Multiplica el costo base por 1.08 (costoBase + 8%)
    }
} // Fin de SoporteSoftware