package com.example.zamoracorporation; // Define el paquete principal del proyecto donde reside la clase

public class SoporteHardware extends TicketSoporte { // Subclase concreta que hereda de TicketSoporte para casos de soporte físico/hardware
    private String ubicacion; // Atributo privado encapsulado para almacenar la ubicación física donde se encuentra el equipo

    public SoporteHardware(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo, // Constructor con los datos generales y específicos
                           String documentoIdentidad, String placaEquipo, String descripcionHechos, // Continúa la lista de parámetros heredados de identificación y detalle
                           String ubicacion, double costoBase, String analistaAsignado) { // Recibe la ubicación física, el costo base y el analista asignado
        super(codigoTicket, titulo, nombreUsuario, correoCorporativo, documentoIdentidad, // Invoca al constructor de la superclase TicketSoporte
                placaEquipo, descripcionHechos, costoBase, analistaAsignado); // Delega la validación e inicialización de los atributos comunes
        this.ubicacion = ValidadorTicket.validarTextoRequerido(ubicacion, "Ubicación"); // Valida y asigna la ubicación usando las reglas de negocio
    }

    public String getUbicacion() { // Método getter para consultar el valor del atributo privado ubicacion
        return ubicacion; // Retorna la ubicación registrada en el ticket de hardware
    }

    public void setUbicacion(String ubicacion) { // Método setter para modificar la ubicación manteniendo la integridad de datos
        this.ubicacion = ValidadorTicket.validarTextoRequerido(ubicacion, "Ubicación"); // Revalida el nuevo valor antes de actualizar el atributo
    }

    @Override // Sobrescribe el método abstracto calcularCostoTotal() definido en TicketSoporte (polimorfismo)
    public double calcularCostoTotal() { // Calcula el costo final específico para una intervención de hardware
        return getCostoBase() * 1.10; // Aplica un recargo del 10% sobre el costo base obtenido de la superclase
    }
}