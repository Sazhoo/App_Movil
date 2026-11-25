package com.example.zamoracorporation;

public class TicketGarantia extends TicketSoporte {
    private String marcaEquipo;
    private String tipoEquipo;
    private String serialEquipo;

    public TicketGarantia(String codigo, String titulo, String usuario, String correo, String idCorporativo,
                          String placa, String marcaEquipo, String tipoEquipo, String serialEquipo,
                          String descripcionFalla, String analista) {
        super(codigo, titulo, usuario, correo, idCorporativo, placa, descripcionFalla, 0.0, analista);
        this.marcaEquipo = marcaEquipo;
        this.tipoEquipo = tipoEquipo;
        this.serialEquipo = serialEquipo;
    }

    public String getMarcaEquipo() { return marcaEquipo; }
    public String getTipoEquipo() { return tipoEquipo; }
    public String getSerialEquipo() { return serialEquipo; }

    @Override
    public double calcularCostoTotal() {
        return 0.0;
    }

    @Override
    public String obtenerDetalleCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("🛡️ GARANTÍA - ").append(getCodigoTicket()).append("\n");
        sb.append("📌 Título: ").append(getTitulo()).append("\n");
        sb.append("🚦 Prioridad: ").append(getPrioridad()).append(" (SLA máx: ").append(getHorasSla()).append("h)\n");
        sb.append("📊 Estado: ").append(getEstado()).append("\n");
        sb.append("🗂️ Motivo de Estado: ").append(getMotivoEstado()).append("\n");
        sb.append("🏢 Frente: ").append(getFrente()).append("\n");
        sb.append("👨‍💻 Analista: ").append(getAnalistaAsignado()).append("\n");
        sb.append("👤 Usuario: ").append(getNombreUsuario()).append("\n");
        sb.append("📧 Correo: ").append(getCorreoCorporativo()).append("\n");
        sb.append("🪪 ID Corporativo: ").append(getDocumentoIdentidad()).append("\n");
        sb.append("🏷️ Marca / Formato: ").append(marcaEquipo).append(" - ").append(tipoEquipo).append("\n");
        sb.append("💻 Placa: ").append(getPlacaEquipo()).append("\n");
        sb.append("🔢 Serial: ").append(serialEquipo).append("\n");
        sb.append("📝 Falla reportada: ").append(getDescripcionHechos()).append("\n");
        if (getNotaCierre() != null && !getNotaCierre().trim().isEmpty()) {
            sb.append("✅ Nota de cierre: ").append(getNotaCierre()).append("\n");
        }
        sb.append("💰 Costo: $0.00 (Garantía)");
        return sb.toString();
    }
}