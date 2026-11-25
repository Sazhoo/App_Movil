package com.example.zamoracorporation; // Paquete principal del proyecto

public class TicketGarantia extends TicketSoporte { // Subclase de TicketSoporte para casos de garantía de hardware
    private String marcaEquipo; // Marca comercial del equipo (ej. Lenovo, HP, DELL)
    private String tipoEquipo; // Formato o tipo de equipo (ej. Portátil, DESKTOP)
    private String serialEquipo; // Número de serie físico del equipo (S/N)

    public TicketGarantia(String codigo, String titulo, String usuario, String correo, String idCorporativo, // Constructor de garantía con datos adicionales del equipo
                          String placa, String marcaEquipo, String tipoEquipo, String serialEquipo,
                          String descripcionFalla, String analista) {
        super(codigo, titulo, usuario, correo, idCorporativo, placa, descripcionFalla, 0.0, analista); // Llama al constructor base con costo base 0.0
        this.marcaEquipo = marcaEquipo; // Guarda la marca del equipo
        this.tipoEquipo = tipoEquipo; // Guarda el tipo o formato del equipo
        this.serialEquipo = serialEquipo; // Guarda el serial del equipo
    }

    public String getMarcaEquipo() { return marcaEquipo; } // Obtiene la marca del equipo
    public String getTipoEquipo() { return tipoEquipo; } // Obtiene el formato o tipo del equipo
    public String getSerialEquipo() { return serialEquipo; } // Obtiene el número de serie del equipo

    @Override // Sobrescribe calcularCostoTotal() de TicketSoporte
    public double calcularCostoTotal() { // En garantías el costo para el usuario/operación es 0.0
        return 0.0; // Retorna cero sin recargos
    }

    @Override // Sobrescribe obtenerDetalleCompleto() para incluir marca, formato y serial
    public String obtenerDetalleCompleto() { // Construye el texto completo del ticket de garantía
        StringBuilder sb = new StringBuilder(); // Acumulador de texto
        sb.append("🛡️ GARANTÍA - ").append(getCodigoTicket()).append("\n"); // Encabezado de garantía y código
        sb.append("📌 Título: ").append(getTitulo()).append("\n"); // Título del caso
        sb.append("🚦 Prioridad: ").append(getPrioridad()).append(" (SLA máx: ").append(getHorasSla()).append("h)\n"); // Prioridad y horas de SLA
        sb.append("📊 Estado: ").append(getEstado()).append("\n"); // Estado actual
        sb.append("🗂️ Motivo de Estado: ").append(getMotivoEstado()).append("\n"); // Motivo del estado
        sb.append("🏢 Frente: ").append(getFrente()).append("\n"); // Frente asignado
        sb.append("👨‍💻 Analista: ").append(getAnalistaAsignado()).append("\n"); // Analista responsable
        sb.append("👤 Usuario: ").append(getNombreUsuario()).append("\n"); // Nombre del usuario
        sb.append("📧 Correo: ").append(getCorreoCorporativo()).append("\n"); // Correo corporativo
        sb.append("🪪 ID Corporativo: ").append(getDocumentoIdentidad()).append("\n"); // Documento o ID corporativo
        sb.append("🏷️ Marca / Formato: ").append(marcaEquipo).append(" - ").append(tipoEquipo).append("\n"); // Marca y tipo de equipo
        sb.append("💻 Placa: ").append(getPlacaEquipo()).append("\n"); // Placa de inventario
        sb.append("🔢 Serial: ").append(serialEquipo).append("\n"); // Número de serie
        sb.append("📝 Falla reportada: ").append(getDescripcionHechos()).append("\n"); // Descripción de la falla
        if (getNotaCierre() != null && !getNotaCierre().trim().isEmpty()) { // Agrega nota de cierre solo si existe
            sb.append("✅ Nota de cierre: ").append(getNotaCierre()).append("\n");
        }
        sb.append("💰 Costo: $0.00 (Garantía)"); // Indica costo cero por garantía
        return sb.toString(); // Devuelve el detalle completo formateado
    }
} // Fin de TicketGarantia