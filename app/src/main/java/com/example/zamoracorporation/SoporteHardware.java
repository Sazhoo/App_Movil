package com.example.zamoracorporation; // Paquete principal de la aplicación

public class SoporteHardware extends TicketSoporte { // Subclase de TicketSoporte especializada en soporte de hardware
    private String ubicacion; // Lugar físico o área donde se encuentra el equipo

    public SoporteHardware(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo, // Constructor con los campos base más la ubicación física
                           String documentoIdentidad, String placaEquipo, String descripcionHechos,
                           String ubicacion, double costoBase, String analistaAsignado) {
        super(codigoTicket, titulo, nombreUsuario, correoCorporativo, documentoIdentidad, // Inicializa los atributos heredados de TicketSoporte
                placaEquipo, descripcionHechos, costoBase, analistaAsignado);
        this.ubicacion = ubicacion; // Guarda la ubicación del equipo
    }

    public String getUbicacion() { // Obtiene la ubicación física registrada
        return ubicacion;
    }

    @Override // Sobrescribe el método abstracto calcularCostoTotal() de TicketSoporte
    public double calcularCostoTotal() { // Calcula el costo final de soporte de hardware con un 10% adicional sobre el costo base
        return getCostoBase() * 1.10; // Multiplica el costo base por 1.10 (costoBase + 10%)
    }
} // Fin de SoporteHardware