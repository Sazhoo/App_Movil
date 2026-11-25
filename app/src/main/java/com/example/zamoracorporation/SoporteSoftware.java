package com.example.zamoracorporation; // Define el paquete principal del proyecto donde reside la clase

public class SoporteSoftware extends TicketSoporte { // Subclase concreta que hereda de TicketSoporte para casos de software
    private String ubicacion; // Atributo privado encapsulado para almacenar la ubicación física o lógica del soporte

    public SoporteSoftware(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo, // Constructor con los datos base y específicos
                           String documentoIdentidad, String placaEquipo, String descripcionHechos, // Continúa la lista de parámetros de identificación y detalle
                           String ubicacion, double costoBase, String analistaAsignado) { // Recibe la ubicación, costo base y analista asignado
        super(codigoTicket, titulo, nombreUsuario, correoCorporativo, documentoIdentidad, // Invoca al constructor de la superclase TicketSoporte
                placaEquipo, descripcionHechos, costoBase, analistaAsignado); // Delega la validación e inicialización de los atributos heredados
        this.ubicacion = ValidadorTicket.validarTextoRequerido(ubicacion, "Ubicación"); // Valida y asigna la ubicación usando la clase utilitaria de reglas
    }

    public String getUbicacion() { // Método getter para consultar el valor del atributo privado ubicacion
        return ubicacion; // Retorna la ubicación registrada en el ticket
    }

    public void setUbicacion(String ubicacion) { // Método setter para actualizar la ubicación manteniendo la integridad de datos
        this.ubicacion = ValidadorTicket.validarTextoRequerido(ubicacion, "Ubicación"); // Revalida el nuevo texto antes de modificar el atributo
    }

    @Override // Sobrescribe el método abstracto calcularCostoTotal() definido en TicketSoporte (polimorfismo)
    public double calcularCostoTotal() { // Calcula el costo final específico para un ticket de soporte de software
        return getCostoBase() * 1.08; // Aplica un recargo del 8% sobre el costo base obtenido de la superclase
    }
}