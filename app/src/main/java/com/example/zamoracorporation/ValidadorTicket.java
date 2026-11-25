package com.example.zamoracorporation; // Paquete base de la aplicación

import java.util.regex.Matcher; // Permite recorrer coincidencias de un patrón regex en una cadena
import java.util.regex.Pattern; // Compila expresiones regulares para validar reglas de formato

public final class ValidadorTicket { // Clase utilitaria centralizada para validar entradas y reglas del negocio

    // Solo letras (incluye tildes y ñ) y espacios; mínimo 2 letras
    private static final Pattern PATRON_SOLO_TEXTO = Pattern.compile("^[\\p{L}]+(?:[\\s][\\p{L}]+)*$"); // Valida campos de texto sin números ni símbolos

    // Solo dígitos numéricos (0-9), sin espacios ni símbolos
    private static final Pattern PATRON_SOLO_NUMEROS = Pattern.compile("^[0-9]+$"); // Valida campos estrictamente numéricos

    // Letras y números (alfanumérico), máximo 13 caracteres para seriales
    private static final Pattern PATRON_SERIAL_MAX_13 = Pattern.compile("^[A-Za-z0-9]{1,13}$"); // Valida seriales con longitud máxima de 13

    // Descripción: solo letras, números y espacios, máximo 500 caracteres
    private static final Pattern PATRON_DESCRIPCION = Pattern.compile("^[\\p{L}0-9\\s]{1,500}$"); // Bloquea caracteres especiales y limita a 500 caracteres

    // Secuencias numéricas válidas dentro de la descripción
    private static final Pattern PATRON_NUMEROS = Pattern.compile("\\d+"); // Detecta cada número presente en el texto

    private ValidadorTicket() { // Constructor privado para evitar instanciar la clase utilitaria
    }

    public static String validarTextoRequerido(String valor, String nombreCampo) { // Valida campos obligatorios de solo texto
        String limpio = valor == null ? "" : valor.trim(); // Elimina espacios en blanco al inicio y final
        if (limpio.isEmpty()) { // Verifica que el campo no esté vacío
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        if (limpio.length() < 2) { // Exige al menos 2 caracteres
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe tener al menos 2 caracteres.");
        }
        if (!PATRON_SOLO_TEXTO.matcher(limpio).matches()) { // Verifica que contenga únicamente letras y espacios
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' solo admite texto.");
        }
        return limpio; // Devuelve el valor limpio y validado
    }

    public static String validarNumeroRequerido(String valor, String nombreCampo) { // Valida campos obligatorios de solo números
        String limpio = valor == null ? "" : valor.trim(); // Limpia espacios extremos
        if (limpio.isEmpty()) { // Verifica que el campo no esté vacío
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        if (!PATRON_SOLO_NUMEROS.matcher(limpio).matches()) { // Verifica que solo haya dígitos del 0 al 9
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' solo admite números.");
        }
        return limpio; // Devuelve la cadena numérica validada
    }

    public static String validarSerialEquipo(String serial) { // Valida serial con letras/números y máximo 13 caracteres
        String limpio = serial == null ? "" : serial.trim().toUpperCase(); // Limpia espacios y estandariza en mayúsculas
        if (limpio.isEmpty()) { // Verifica que el serial no esté vacío
            throw new IllegalArgumentException("El serial del equipo es obligatorio.");
        }
        if (limpio.length() > 13) { // Verifica límite máximo de 13 caracteres
            throw new IllegalArgumentException("El serial del equipo no puede superar los 13 caracteres.");
        }
        if (!PATRON_SERIAL_MAX_13.matcher(limpio).matches()) { // Verifica que solo contenga letras y números
            throw new IllegalArgumentException("El serial solo admite letras y números (máximo 13 caracteres).");
        }
        return limpio; // Devuelve el serial validado
    }

    public static String validarDescripcionProblema(String descripcion) { // Valida descripción: solo texto/números, máx 500 chars, máx 5 números
        String limpio = descripcion == null ? "" : descripcion.trim(); // Limpia espacios extremos
        if (limpio.isEmpty()) { // Verifica que la descripción no esté vacía
            throw new IllegalArgumentException("La descripción del problema es obligatoria.");
        }
        if (limpio.length() > 500) { // Verifica límite máximo de 500 caracteres
            throw new IllegalArgumentException("La descripción no puede superar los 500 caracteres.");
        }
        if (!PATRON_DESCRIPCION.matcher(limpio).matches()) { // Verifica que solo haya letras, números y espacios
            throw new IllegalArgumentException("La descripción solo admite texto y números (sin símbolos ni caracteres especiales).");
        }

        Matcher matcher = PATRON_NUMEROS.matcher(limpio); // Busca secuencias numéricas dentro del texto
        int cantidadNumeros = 0; // Contador de números encontrados
        while (matcher.find()) { // Recorre cada número detectado
            cantidadNumeros++; // Incrementa el conteo de números
            if (cantidadNumeros > 5) { // Valida el tope máximo de 5 números por descripción
                throw new IllegalArgumentException("La descripción permite máximo 5 números.");
            }
        }

        return limpio; // Devuelve la descripción validada
    }

    public static double validarCostoNoNegativo(double costoBase) { // Valida que el costo base sea válido y no negativo
        if (Double.isNaN(costoBase) || Double.isInfinite(costoBase) || costoBase < 0.0) { // Rechaza valores inválidos o negativos
            throw new IllegalArgumentException("El costo base no puede ser negativo.");
        }
        return costoBase; // Devuelve el costo base validado
    }

    public static String validarPrioridad(String prioridad) { // Valida que la prioridad sea una de las permitidas
        if ("Alta".equalsIgnoreCase(prioridad)) return "Alta"; // Normaliza prioridad Alta
        if ("Regular".equalsIgnoreCase(prioridad)) return "Regular"; // Normaliza prioridad Regular
        if ("Baja".equalsIgnoreCase(prioridad)) return "Baja"; // Normaliza prioridad Baja
        throw new IllegalArgumentException("Prioridad inválida. Debe ser Alta, Regular o Baja."); // Rechaza valores fuera del dominio
    }

    public static String validarEstado(String estado) { // Valida que el estado pertenezca al flujo permitido
        if ("Asignado".equalsIgnoreCase(estado)) return "Asignado"; // Estado inicial
        if ("Pendiente".equalsIgnoreCase(estado)) return "Pendiente"; // Estado en espera
        if ("Resuelto".equalsIgnoreCase(estado)) return "Resuelto"; // Estado resuelto
        if ("Cerrado".equalsIgnoreCase(estado)) return "Cerrado"; // Estado cerrado
        throw new IllegalArgumentException("Estado inválido."); // Rechaza estados no válidos
    }
}