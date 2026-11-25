package com.example.zamoracorporation; // Paquete base de la aplicación

import android.graphics.Color; // Permite interpretar códigos de color hexadecimales para UI

public abstract class TicketSoporte { // Clase abstracta base para incidentes, órdenes de trabajo y garantías
    private String codigoTicket; // Código único del ticket (ej. INC... o WO...)
    private String titulo; // Asunto o resumen corto del caso
    private String nombreUsuario; // Nombre de la persona que reporta
    private String correoCorporativo; // Correo institucional usado para identificar el frente
    private String documentoIdentidad; // Documento o ID corporativo del usuario
    private String placaEquipo; // Placa de inventario del equipo involucrado
    private String descripcionHechos; // Detalle narrativo de la falla o solicitud
    private String frente; // Área de negocio ("Caja de compensación", "Salud" o "No definido")
    private String prioridad; // Nivel de criticidad ("Baja", "Regular", "Alta")
    private String estado; // Estado del ciclo de vida ("Asignado", "Pendiente", "Resuelto", "Cerrado")
    private String motivoEstado; // Justificación operativa del estado actual
    private String notaCierre; // Observación registrada al resolver o cerrar el caso
    private long timestampCreacion; // Momento exacto de creación en milisegundos (epoch ms)
    private double costoBase; // Valor base de atención definido por la subclase
    private String analistaAsignado; // Nombre del analista responsable del ticket

    public TicketSoporte(String codigoTicket, String titulo, String nombreUsuario, String correoCorporativo, // Constructor base de cualquier ticket
                         String documentoIdentidad, String placaEquipo, String descripcionHechos,
                         double costoBase, String analistaAsignado) {
        this.codigoTicket = codigoTicket; // Asigna el código del ticket
        this.titulo = titulo; // Asigna el título
        this.nombreUsuario = nombreUsuario; // Asigna el nombre del usuario
        this.correoCorporativo = correoCorporativo; // Asigna el correo corporativo
        this.documentoIdentidad = documentoIdentidad; // Asigna el documento/ID
        this.placaEquipo = placaEquipo; // Asigna la placa del equipo
        this.descripcionHechos = descripcionHechos; // Asigna la descripción de los hechos
        this.costoBase = costoBase; // Asigna el costo base
        this.analistaAsignado = analistaAsignado; // Asigna el analista responsable
        this.frente = determinarFrente(correoCorporativo); // Calcula el frente según el dominio del correo
        this.prioridad = calcularPrioridadAutomatica(titulo, descripcionHechos); // Asigna prioridad inicial por palabras clave
        this.estado = "Asignado"; // Todo ticket nuevo inicia en estado "Asignado"
        this.motivoEstado = "En atención inicial"; // Motivo inicial por defecto
        this.notaCierre = ""; // Sin nota de cierre al crearse
        this.timestampCreacion = System.currentTimeMillis(); // Captura el instante real de creación del ticket
    }

    private String determinarFrente(String correo) { // Identifica el frente según el dominio del correo corporativo
        if (correo != null && correo.toLowerCase().endsWith("@compensar.com")) { // Dominio de caja de compensación
            return "Caja de compensación";
        } else if (correo != null && correo.toLowerCase().endsWith("@consorciosalud.com")) { // Dominio de salud
            return "Salud";
        }
        return "No definido"; // Si no coincide con los dominios esperados
    }

    public static String calcularPrioridadAutomatica(String titulo, String hechos) { // Clasifica prioridad inicial a partir de título y hechos
        String texto = ((titulo != null ? titulo : "") + " " + (hechos != null ? hechos : "")).toLowerCase(); // Une y pasa a minúsculas

        if (texto.contains("falla de red") || texto.contains("caida de red") || texto.contains("caída de red") // Palabras clave de impacto crítico/amplio
                || texto.contains("sin red") || texto.contains("sin internet") || texto.contains("en el departamento")
                || texto.contains("en el area") || texto.contains("en el área") || texto.contains("todo el piso")
                || texto.contains("toda la sede") || texto.contains("servidor caido") || texto.contains("servidor caído")
                || texto.contains("sistema caido") || texto.contains("sistema caído") || texto.contains("masiva")) {
            return "Alta";
        }

        if (texto.contains("aplicativo") || texto.contains("sap") || texto.contains("correo") // Palabras clave de falla operativa intermedia
                || texto.contains("impresora") || texto.contains("vpn") || texto.contains("permisos")
                || texto.contains("acceso") || texto.contains("lento") || texto.contains("error")) {
            return "Regular";
        }

        return "Baja"; // Prioridad base si no coincide con criterios anteriores
    }

    public long getTiempoMaximoSlaMs() { // Devuelve la duración máxima de SLA en milisegundos según prioridad
        if ("Alta".equalsIgnoreCase(prioridad)) {
            return 2L * 60L * 60L * 1000L; // 2 horas en milisegundos
        } else if ("Regular".equalsIgnoreCase(prioridad)) {
            return 12L * 60L * 60L * 1000L; // 12 horas en milisegundos
        }
        return 24L * 60L * 60L * 1000L; // 24 horas en milisegundos para prioridad Baja
    }

    public long getHorasSla() { // Devuelve la duración de SLA expresada en horas para mostrar en textos
        if ("Alta".equalsIgnoreCase(prioridad)) return 2; // 2 horas
        if ("Regular".equalsIgnoreCase(prioridad)) return 12; // 12 horas
        return 24; // 24 horas
    }

    public int getPorcentajeSlaTranscurrido() { // Calcula qué porcentaje del SLA ya transcurrió desde timestampCreacion
        long transcurrido = Math.max(0L, System.currentTimeMillis() - timestampCreacion); // Tiempo real transcurrido (evita negativos)
        long max = getTiempoMaximoSlaMs(); // Duración total permitida del SLA
        if (max <= 0) return 100; // Evita división por cero
        int pct = (int) Math.min(100L, (transcurrido * 100L) / max); // Regla de tres acotada a máximo 100%
        return pct; // Retorna porcentaje de 0 a 100
    }

    public boolean estaSlaVencido() { // Indica si el tiempo transcurrido ya alcanzó o superó el SLA máximo
        return (System.currentTimeMillis() - timestampCreacion) >= getTiempoMaximoSlaMs();
    }

    /**
     * Evalúa automáticamente si el SLA venció para cerrar el caso.
     */
    public void verificarYActualizarVencimientoSla() { // Cierra automáticamente casos abiertos/pendientes si venció el SLA
        if (!"Cerrado".equalsIgnoreCase(estado) && !"Resuelto".equalsIgnoreCase(estado) && estaSlaVencido()) { // No altera los ya Resueltos o Cerrados aquí
            this.estado = "Cerrado"; // Cambia estado a Cerrado
            this.motivoEstado = "Resolución automática"; // Registra motivo automático
            if (this.notaCierre == null || this.notaCierre.trim().isEmpty()) { // Agrega nota automática si no existe una previa
                this.notaCierre = "Cerrado automáticamente por vencimiento de SLA (" + getHorasSla() + "h).";
            }
        }
    }

    public int getColorPrioridad() { // Asigna color distintivo según la prioridad del ticket
        if ("Alta".equalsIgnoreCase(prioridad)) {
            return Color.parseColor("#DC2626"); // Rojo para prioridad Alta
        } else if ("Regular".equalsIgnoreCase(prioridad)) {
            return Color.parseColor("#D97706"); // Ámbar para prioridad Regular
        }
        return Color.parseColor("#16A34A"); // Verde para prioridad Baja
    }

    public int getColorSlaBarra() { // Cambia el color de la barra de SLA según el porcentaje consumido
        int pct = getPorcentajeSlaTranscurrido(); // Obtiene porcentaje actual del SLA
        if (pct < 50) {
            return Color.parseColor("#16A34A"); // Verde cuando lleva menos del 50%
        } else if (pct < 85) {
            return Color.parseColor("#D97706"); // Ámbar entre 50% y 84%
        }
        return Color.parseColor("#DC2626"); // Rojo a partir del 85% o vencido
    }

    // Getters y Setters
    public String getCodigoTicket() { return codigoTicket; } // Obtiene código del ticket
    public String getTitulo() { return titulo; } // Obtiene título del ticket
    public void setTitulo(String titulo) { this.titulo = titulo; } // Actualiza título del ticket
    public String getNombreUsuario() { return nombreUsuario; } // Obtiene nombre del usuario
    public String getCorreoCorporativo() { return correoCorporativo; } // Obtiene correo corporativo
    public String getDocumentoIdentidad() { return documentoIdentidad; } // Obtiene documento de identidad
    public String getPlacaEquipo() { return placaEquipo; } // Obtiene placa del equipo
    public String getDescripcionHechos() { return descripcionHechos; } // Obtiene descripción de los hechos
    public void setDescripcionHechos(String descripcionHechos) { this.descripcionHechos = descripcionHechos; } // Actualiza descripción
    public String getFrente() { return frente; } // Obtiene frente asignado
    public void setFrente(String frente) { this.frente = frente; } // Actualiza frente asignado
    public String getPrioridad() { return prioridad; } // Obtiene prioridad actual
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; } // Actualiza prioridad actual
    public String getEstado() { return estado; } // Obtiene estado actual
    public void setEstado(String estado) { this.estado = estado; } // Actualiza estado actual
    public String getMotivoEstado() { return motivoEstado; } // Obtiene motivo del estado
    public void setMotivoEstado(String motivoEstado) { this.motivoEstado = motivoEstado; } // Actualiza motivo del estado
    public String getNotaCierre() { return notaCierre; } // Obtiene nota de cierre
    public void setNotaCierre(String notaCierre) { this.notaCierre = notaCierre; } // Actualiza nota de cierre
    public long getTimestampCreacion() { return timestampCreacion; } // Obtiene timestamp de creación en ms
    public double getCostoBase() { return costoBase; } // Obtiene costo base
    public String getAnalistaAsignado() { return analistaAsignado; } // Obtiene analista asignado
    public void setAnalistaAsignado(String analistaAsignado) { this.analistaAsignado = analistaAsignado; } // Actualiza analista asignado

    public abstract double calcularCostoTotal(); // Método abstracto: cada tipo de ticket calcula su costo total

    // Vista para el usuario (no expone prioridad ni controles internos de admin)
    public String toUsuarioString() { // Formato resumido seguro para consulta del usuario
        return codigoTicket + " - " + titulo + " [" + estado + "]";
    }

    // Vista para el administrador: "Baja  INC00054596"
    public String toAdminCabecera() { // Formato corto de prioridad y código para administración
        return prioridad + "  " + codigoTicket;
    }

    public String obtenerDetalleCompleto() { // Construye un texto formateado con toda la información del ticket
        StringBuilder sb = new StringBuilder(); // Acumulador eficiente de cadenas
        sb.append("🎫 Caso: ").append(codigoTicket).append("\n"); // Código
        sb.append("📌 Título: ").append(titulo).append("\n"); // Título
        sb.append("🚦 Prioridad: ").append(prioridad).append(" (SLA máx: ").append(getHorasSla()).append("h)\n"); // Prioridad y SLA
        sb.append("📊 Estado: ").append(estado).append("\n"); // Estado
        sb.append("🗂️ Motivo de Estado: ").append(motivoEstado).append("\n"); // Motivo de estado
        sb.append("🏢 Frente: ").append(frente).append("\n"); // Frente
        sb.append("👨‍💻 Analista: ").append(analistaAsignado).append("\n"); // Analista
        sb.append("👤 Usuario: ").append(nombreUsuario).append("\n"); // Usuario
        sb.append("📧 Correo: ").append(correoCorporativo).append("\n"); // Correo
        sb.append("🪪 ID Corporativo: ").append(documentoIdentidad).append("\n"); // Documento/ID
        sb.append("💻 Placa Equipo: ").append(placaEquipo).append("\n"); // Placa
        sb.append("📝 Descripción: ").append(descripcionHechos).append("\n"); // Descripción
        if (notaCierre != null && !notaCierre.trim().isEmpty()) { // Incluye nota de cierre solo si existe
            sb.append("✅ Nota de cierre: ").append(notaCierre).append("\n");
        }
        sb.append("💰 Costo Estimado: $").append(String.format("%.2f", calcularCostoTotal())); // Costo total formateado a 2 decimales
        return sb.toString(); // Devuelve el resumen completo
    }
}