package com.example.zamoracorporation;

import android.graphics.Color;

public abstract class TicketSoporte {
    private String codigoTicket;
    private String titulo;
    private String nombreUsuario;
    private String correoCorporativo;
    private String documentoIdentidad;
    private String placaEquipo;
    private String descripcionHechos;
    private String frente;
    private String prioridad; // "Baja", "Regular", "Alta"
    private String estado;    // "Asignado", "Pendiente", "Resuelto", "Cerrado"
    private String motivoEstado;
    private String notaCierre;
    private long timestampCreacion;
    private double costoBase;
    private String analistaAsignado;

    public TicketSoporte(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo,
                         String documentoIdentidad, String placaEquipo, String descripcionHechos,
                         double costoBase, String analistaAsignado) {
        this.codigoTicket = codigoTicket;
        this.titulo = titulo;
        this.nombreUsuario = nombreUsuario;
        this.correoCorporativo = correoCorporativo;
        this.documentoIdentidad = documentoIdentidad;
        this.placaEquipo = placaEquipo;
        this.descripcionHechos = descripcionHechos;
        this.costoBase = costoBase;
        this.analistaAsignado = analistaAsignado;
        this.frente = determinarFrente(correoCorporativo);
        this.prioridad = calcularPrioridadAutomatica(titulo, descripcionHechos);
        this.estado = "Asignado";
        this.motivoEstado = "En atención inicial";
        this.notaCierre = "";
        this.timestampCreacion = System.currentTimeMillis();
    }

    private String determinarFrente(String correo) {
        if (correo != null && correo.toLowerCase().endsWith("@compensar.com")) {
            return "Caja de compensación";
        } else if (correo != null && correo.toLowerCase().endsWith("@consorciosalud.com")) {
            return "Salud";
        }
        return "No definido";
    }

    public static String calcularPrioridadAutomatica(String titulo, String hechos) {
        String texto = ((titulo != null ? titulo : "") + " " + (hechos != null ? hechos : "")).toLowerCase();

        if (texto.contains("falla de red") || texto.contains("caida de red") || texto.contains("caída de red")
                || texto.contains("sin red") || texto.contains("sin internet") || texto.contains("en el departamento")
                || texto.contains("en el area") || texto.contains("en el área") || texto.contains("todo el piso")
                || texto.contains("toda la sede") || texto.contains("servidor caido") || texto.contains("servidor caído")
                || texto.contains("sistema caido") || texto.contains("sistema caído") || texto.contains("masiva")) {
            return "Alta";
        }

        if (texto.contains("aplicativo") || texto.contains("sap") || texto.contains("correo")
                || texto.contains("impresora") || texto.contains("vpn") || texto.contains("permisos")
                || texto.contains("acceso") || texto.contains("lento") || texto.contains("error")) {
            return "Regular";
        }

        return "Baja";
    }

    public long getTiempoMaximoSlaMs() {
        if ("Alta".equalsIgnoreCase(prioridad)) {
            return 2L * 60L * 60L * 1000L; // 2 horas
        } else if ("Regular".equalsIgnoreCase(prioridad)) {
            return 12L * 60L * 60L * 1000L; // 12 horas
        }
        return 24L * 60L * 60L * 1000L; // 24 horas
    }

    public long getHorasSla() {
        if ("Alta".equalsIgnoreCase(prioridad)) return 2;
        if ("Regular".equalsIgnoreCase(prioridad)) return 12;
        return 24;
    }

    public int getPorcentajeSlaTranscurrido() {
        long transcurrido = Math.max(0L, System.currentTimeMillis() - timestampCreacion);
        long max = getTiempoMaximoSlaMs();
        if (max <= 0) return 100;
        int pct = (int) Math.min(100L, (transcurrido * 100L) / max);
        return pct;
    }

    public boolean estaSlaVencido() {
        return (System.currentTimeMillis() - timestampCreacion) >= getTiempoMaximoSlaMs();
    }

    /**
     * Evalúa automáticamente si el SLA venció para cerrar el caso.
     */
    public void verificarYActualizarVencimientoSla() {
        if (!"Cerrado".equalsIgnoreCase(estado) && !"Resuelto".equalsIgnoreCase(estado) && estaSlaVencido()) {
            this.estado = "Cerrado";
            this.motivoEstado = "Resolución automática";
            if (this.notaCierre == null || this.notaCierre.trim().isEmpty()) {
                this.notaCierre = "Cerrado automáticamente por vencimiento de SLA (" + getHorasSla() + "h).";
            }
        }
    }

    public int getColorPrioridad() {
        if ("Alta".equalsIgnoreCase(prioridad)) {
            return Color.parseColor("#DC2626"); // Rojo
        } else if ("Regular".equalsIgnoreCase(prioridad)) {
            return Color.parseColor("#D97706"); // Ámbar / amarillo preocupante
        }
        return Color.parseColor("#16A34A"); // Verde
    }

    public int getColorSlaBarra() {
        int pct = getPorcentajeSlaTranscurrido();
        if (pct < 50) {
            return Color.parseColor("#16A34A"); // Verde
        } else if (pct < 85) {
            return Color.parseColor("#D97706"); // Ámbar
        }
        return Color.parseColor("#DC2626"); // Rojo
    }

    // Getters y Setters
    public String getCodigoTicket() { return codigoTicket; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreoCorporativo() { return correoCorporativo; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public String getPlacaEquipo() { return placaEquipo; }
    public String getDescripcionHechos() { return descripcionHechos; }
    public void setDescripcionHechos(String descripcionHechos) { this.descripcionHechos = descripcionHechos; }
    public String getFrente() { return frente; }
    public void setFrente(String frente) { this.frente = frente; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMotivoEstado() { return motivoEstado; }
    public void setMotivoEstado(String motivoEstado) { this.motivoEstado = motivoEstado; }
    public String getNotaCierre() { return notaCierre; }
    public void setNotaCierre(String notaCierre) { this.notaCierre = notaCierre; }
    public long getTimestampCreacion() { return timestampCreacion; }
    public double getCostoBase() { return costoBase; }
    public String getAnalistaAsignado() { return analistaAsignado; }
    public void setAnalistaAsignado(String analistaAsignado) { this.analistaAsignado = analistaAsignado; }

    public abstract double calcularCostoTotal();

    // Vista para el usuario (no expone prioridad ni controles internos de admin)
    public String toUsuarioString() {
        return codigoTicket + " - " + titulo + " [" + estado + "]";
    }

    // Vista para el administrador: "Baja  INC00054596"
    public String toAdminCabecera() {
        return prioridad + "  " + codigoTicket;
    }

    public String obtenerDetalleCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎫 Caso: ").append(codigoTicket).append("\n");
        sb.append("📌 Título: ").append(titulo).append("\n");
        sb.append("🚦 Prioridad: ").append(prioridad).append(" (SLA máx: ").append(getHorasSla()).append("h)\n");
        sb.append("📊 Estado: ").append(estado).append("\n");
        sb.append("🗂️ Motivo de Estado: ").append(motivoEstado).append("\n");
        sb.append("🏢 Frente: ").append(frente).append("\n");
        sb.append("👨‍💻 Analista: ").append(analistaAsignado).append("\n");
        sb.append("👤 Usuario: ").append(nombreUsuario).append("\n");
        sb.append("📧 Correo: ").append(correoCorporativo).append("\n");
        sb.append("🪪 ID Corporativo: ").append(documentoIdentidad).append("\n");
        sb.append("💻 Placa Equipo: ").append(placaEquipo).append("\n");
        sb.append("📝 Descripción: ").append(descripcionHechos).append("\n");
        if (notaCierre != null && !notaCierre.trim().isEmpty()) {
            sb.append("✅ Nota de cierre: ").append(notaCierre).append("\n");
        }
        sb.append("💰 Costo Estimado: $").append(String.format("%.2f", calcularCostoTotal()));
        return sb.toString();
    }
}