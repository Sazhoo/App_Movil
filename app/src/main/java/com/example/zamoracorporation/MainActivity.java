package com.example.zamoracorporation; // Define el paquete principal de la aplicación

import android.content.Context; // Permite acceder al contexto de la aplicación para preferencias y recursos
import android.content.SharedPreferences; // Guarda y recupera datos persistentes en el almacenamiento local del dispositivo
import android.content.res.ColorStateList; // Permite aplicar colores dinámicos a botones y barras de progreso
import android.graphics.Color; // Provee constantes y métodos para trabajar con colores RGB/HEX
import android.graphics.Typeface; // Permite dar estilo de fuente, como negrita (BOLD)
import android.graphics.drawable.ColorDrawable; // Crea fondos de color sólido o transparente para ventanas y diálogos
import android.os.Bundle; // Contiene el estado guardado de la actividad al crearse
import android.os.Handler; // Programa tareas repetitivas en un hilo (como actualizar la barra cada segundo)
import android.os.Looper; // Asocia el Handler al hilo principal de interfaz (UI thread)
import android.text.InputType; // Define tipos de entrada y capitalización para campos EditText
import android.view.Gravity; // Controla la alineación horizontal o vertical de las vistas
import android.view.LayoutInflater; // Infla archivos XML de diseño en objetos View en tiempo de ejecución
import android.view.View; // Clase base de cualquier componente visual en Android
import android.view.ViewGroup; // Contenedor base que agrupa y organiza vistas hijas
import android.widget.AdapterView; // Escucha eventos de selección en componentes como Spinner
import android.widget.ArrayAdapter; // Adapta listas o arreglos de datos para mostrarlos en Spinner o ListView
import android.widget.Button; // Botón interactivo en pantalla
import android.widget.EditText; // Campo de texto editable para ingreso de datos
import android.widget.LinearLayout; // Contenedor que organiza vistas en fila (horizontal) o columna (vertical)
import android.widget.ListView; // Lista desplazable para mostrar múltiples registros
import android.widget.ProgressBar; // Barra de progreso visual para representar el avance del SLA
import android.widget.RadioButton; // Opción seleccionable dentro de un RadioGroup
import android.widget.RadioGroup; // Agrupa RadioButtons para selección excluyente
import android.widget.ScrollView; // Permite desplazamiento vertical cuando el contenido supera la altura de la pantalla
import android.widget.Spinner; // Lista desplegable de selección
import android.widget.TextView; // Componente para mostrar textos o etiquetas en pantalla
import android.widget.Toast; // Muestra mensajes breves flotantes al usuario

import androidx.annotation.NonNull; // Indica que un parámetro o valor de retorno no debe ser nulo
import androidx.annotation.Nullable; // Indica que un parámetro o valor de retorno puede ser nulo
import androidx.appcompat.app.AlertDialog; // Diálogo modal estándar de AndroidX
import androidx.appcompat.app.AppCompatActivity; // Clase base de actividad compatible con temas modernos de AndroidX

import org.json.JSONArray; // Arreglo JSON para serializar la lista completa de tickets guardados
import org.json.JSONException; // Excepción lanzada ante errores de lectura o escritura JSON
import org.json.JSONObject; // Objeto JSON clave-valor para almacenar cada ticket

import java.lang.reflect.Field; // Permite leer o asignar campos de una clase mediante reflexión
import java.lang.reflect.Method; // Permite invocar métodos dinámicamente mediante reflexión
import java.text.Normalizer; // Normaliza texto para eliminar tildes al evaluar palabras clave
import java.util.ArrayList; // Lista dinámica para manejar colecciones de tickets
import java.util.Locale; // Define configuración regional para minúsculas, mayúsculas y formatos

public class MainActivity extends AppCompatActivity { // Actividad principal del sistema de soporte de Zamora Corporation

    private static final String PREFS_NAME = "zamoracorporation_tickets_prefs"; // Nombre del archivo de SharedPreferences
    private static final String KEY_TICKETS_JSON = "tickets_guardados_json"; // Clave para almacenar el JSON de tickets persistentes
    private static final String KEY_COUNTER_INC = "contador_inc"; // Clave para persistir el último consecutivo de INC
    private static final String KEY_COUNTER_WO = "contador_wo"; // Clave para persistir el último consecutivo de WO

    private static final int LONGITUD_DIGITOS_INC = 12; // Cantidad de dígitos con ceros a la izquierda para INC (ej. INC000000179848)
    private static final int LONGITUD_DIGITOS_WO = 13; // Cantidad de dígitos con ceros a la izquierda para WO (ej. WO0000000360566)

    private static final int COLOR_FONDO_MORADO = Color.parseColor("#160A26"); // Color morado oscuro principal para fondos y diálogos
    private static final int COLOR_POPUP_MORADO = Color.parseColor("#1E0E38"); // Color morado oscuro para menús desplegables de Spinner
    private static final int COLOR_INPUT_MORADO = Color.parseColor("#24103D"); // Color morado para tarjetas, filas y campos de texto
    private static final int COLOR_TEXTO_LILA = Color.parseColor("#E9D5FF"); // Color lila claro de alto contraste sobre fondo morado
    private static final int COLOR_TEXTO_SUAVE = Color.parseColor("#C4B5FD"); // Color lila suave para hints y textos secundarios

    private static final int SLA_BAJA_MINUTOS = 30; // Duración de SLA en minutos para prioridad Baja
    private static final int SLA_REGULAR_MINUTOS = 15; // Duración de SLA en minutos para prioridad Regular
    private static final int SLA_ALTA_MINUTOS = 5; // Duración de SLA en minutos para prioridad Alta
    private static final String NOTA_CIERRE_EXITOSO = "Resuelto con éxito"; // Nota automática al cerrar por vencimiento de SLA un caso Resuelto

    private LinearLayout panelMenu, panelAdminLogin, panelAdminDashboard, panelUsuario, layoutCamposGarantia, layoutUserTicketsLista; // Contenedores principales y de listas del XML
    private EditText txtAdminUser, txtAdminPass; // Campos de usuario y contraseña del login de administrador
    private Button btnLoginAdmin, btnVolverMenu1; // Botones de ingreso del administrador y retorno al menú
    private TextView lblSaludoAdmin, lblResultadoConsulta; // Etiquetas de bienvenida del admin y estado de búsqueda del usuario
    private ListView listViewAdminTickets, listViewUsuarioTickets; // Listas para mostrar tickets en admin y (opcionalmente) en usuario
    private Button btnCerrarSesionAdmin; // Botón para cerrar la sesión del administrador

    private RadioGroup rgTipoSolicitud; // Grupo de selección entre Incidente, Orden de Trabajo y Garantía
    private RadioButton rbIncidente, rbOrdenTrabajo, rbGarantia; // Opciones individuales del tipo de solicitud
    private EditText txtTituloCaso, txtNombreUsuario, txtCorreoCorporativo, txtDocumento, txtPlacaEquipo, txtSerialEquipo, txtDescripcion, txtDocConsulta; // Campos de registro y consulta
    private Spinner spinnerMarca, spinnerTipoEquipo; // Spinners para marca y formato del equipo en garantía
    private Button btnRegistrar, btnLimpiar, btnConsultarUsuario, btnVolverMenu2; // Botones de acción del portal de usuario

    private String adminActual = ""; // Nombre del administrador autenticado actualmente
    private static RepositorioSoporte repositorio = new RepositorioSoporte(); // Repositorio compartido en memoria de los tickets
    private static boolean persistenciaInicializada = false; // Evita recargar dos veces los datos persistidos en la misma sesión de proceso

    @Override // Sobrescribe el método onCreate del ciclo de vida de la actividad
    protected void onCreate(Bundle savedInstanceState) { // Inicializa la pantalla y conecta vistas y eventos
        super.onCreate(savedInstanceState); // Ejecuta la inicialización base de AppCompatActivity
        setContentView(R.layout.activity_main); // Carga el diseño XML activity_main

        cargarTicketsPersistentes(); // Restaura de SharedPreferences los tickets guardados previamente

        panelMenu = findViewById(R.id.panelMenu); // Obtiene referencia al panel del menú principal
        panelAdminLogin = findViewById(R.id.panelAdminLogin); // Obtiene referencia al panel de inicio de sesión de admin
        panelAdminDashboard = findViewById(R.id.panelAdminDashboard); // Obtiene referencia al panel del dashboard de admin
        panelUsuario = findViewById(R.id.panelUsuario); // Obtiene referencia al panel del portal de usuario
        layoutCamposGarantia = findViewById(R.id.layoutCamposGarantia); // Obtiene referencia al contenedor de campos de garantía

        layoutUserTicketsLista = findViewById(getIdentifierSafe("layoutUserTicketsLista", "id")); // Busca contenedor dinámico de tickets de usuario
        lblResultadoConsulta = findViewById(getIdentifierSafe("lblResultadoConsulta", "id")); // Busca etiqueta de resultado de búsqueda de usuario
        listViewUsuarioTickets = findViewById(getIdentifierSafe("listViewUsuarioTickets", "id")); // Busca ListView alternativo de tickets de usuario

        txtAdminUser = findViewById(R.id.txtAdminUser); // Obtiene campo de usuario del administrador
        txtAdminPass = findViewById(R.id.txtAdminPass); // Obtiene campo de contraseña del administrador
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin); // Obtiene botón para iniciar sesión de administrador
        btnVolverMenu1 = findViewById(R.id.btnVolverMenu1); // Obtiene botón para volver al menú desde login

        lblSaludoAdmin = findViewById(R.id.lblSaludoAdmin); // Obtiene etiqueta de saludo del administrador
        listViewAdminTickets = findViewById(R.id.listViewAdminTickets); // Obtiene lista de casos asignados al administrador
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin); // Obtiene botón para cerrar sesión de administrador

        rgTipoSolicitud = findViewById(R.id.rgTipoSolicitud); // Obtiene RadioGroup de tipo de solicitud
        rbIncidente = findViewById(R.id.rbIncidente); // Obtiene RadioButton de Incidente
        rbOrdenTrabajo = findViewById(R.id.rbOrdenTrabajo); // Obtiene RadioButton de Orden de Trabajo
        rbGarantia = findViewById(R.id.rbGarantia); // Obtiene RadioButton de Garantía

        txtTituloCaso = findViewById(R.id.txtTituloCaso); // Obtiene campo del título del caso
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario); // Obtiene campo del nombre del usuario
        txtCorreoCorporativo = findViewById(R.id.txtCorreoCorporativo); // Obtiene campo del correo corporativo
        txtDocumento = findViewById(R.id.txtDocumento); // Obtiene campo del documento o ID
        txtPlacaEquipo = findViewById(R.id.txtPlacaEquipo); // Obtiene campo de la placa del equipo
        txtSerialEquipo = findViewById(R.id.txtSerialEquipo); // Obtiene campo del serial del equipo
        txtDescripcion = findViewById(R.id.txtDescripcion); // Obtiene campo de descripción del caso

        spinnerMarca = findViewById(R.id.spinnerMarca); // Obtiene Spinner de marca del equipo
        spinnerTipoEquipo = findViewById(R.id.spinnerTipoEquipo); // Obtiene Spinner de tipo/formato del equipo

        int idBtnRegistrar = getIdentifierSafe("btnRegistrar", "id"); // Busca ID btnRegistrar si existe
        if (idBtnRegistrar == 0) idBtnRegistrar = getIdentifierSafe("btnCrearCaso", "id"); // Alternativa btnCrearCaso
        if (idBtnRegistrar != 0) btnRegistrar = findViewById(idBtnRegistrar); // Asocia botón de registrar caso

        int idBtnLimpiar = getIdentifierSafe("btnLimpiar", "id"); // Busca ID btnLimpiar si existe
        if (idBtnLimpiar != 0) btnLimpiar = findViewById(idBtnLimpiar); // Asocia botón de limpiar formulario

        int idTxtDocConsulta = getIdentifierSafe("txtDocConsulta", "id"); // Busca ID txtDocConsulta si existe
        if (idTxtDocConsulta == 0) idTxtDocConsulta = getIdentifierSafe("txtBuscarDocumento", "id"); // Alternativa txtBuscarDocumento
        if (idTxtDocConsulta != 0) txtDocConsulta = findViewById(idTxtDocConsulta); // Asocia campo de búsqueda por documento

        int idBtnConsultar = getIdentifierSafe("btnConsultarUsuario", "id"); // Busca ID btnConsultarUsuario si existe
        if (idBtnConsultar == 0) idBtnConsultar = getIdentifierSafe("btnConsultarCasos", "id"); // Alternativa btnConsultarCasos
        if (idBtnConsultar != 0) btnConsultarUsuario = findViewById(idBtnConsultar); // Asocia botón de consultar casos

        int idBtnVolverMenu2 = getIdentifierSafe("btnVolverMenu2", "id"); // Busca ID btnVolverMenu2 si existe
        if (idBtnVolverMenu2 != 0) btnVolverMenu2 = findViewById(idBtnVolverMenu2); // Asocia botón volver en portal usuario

        configurarSpinnersGarantia(); // Configura las opciones y estilos morados de los Spinners de garantía

        rgTipoSolicitud.setOnCheckedChangeListener((group, checkedId) -> { // Escucha cambios en el tipo de solicitud
            if (checkedId == R.id.rbGarantia) { // Si se selecciona Garantía
                layoutCamposGarantia.setVisibility(View.VISIBLE); // Muestra los campos exclusivos de garantía
            } else { // Si se selecciona Incidente u Orden de Trabajo
                layoutCamposGarantia.setVisibility(View.GONE); // Oculta los campos exclusivos de garantía
            }
        }); // Fin del listener de rgTipoSolicitud

        findViewById(R.id.btnIrAdmin).setOnClickListener(v -> mostrarPanel(panelAdminLogin)); // Abre pantalla de login admin
        findViewById(R.id.btnIrUsuario).setOnClickListener(v -> mostrarPanel(panelUsuario)); // Abre portal de usuario

        View flechaLogin = findViewById(R.id.btnAtrasLogin); // Obtiene flecha atrás de login
        if (flechaLogin != null) flechaLogin.setOnClickListener(v -> mostrarPanel(panelMenu)); // Regresa al menú desde login

        View flechaDashboard = findViewById(R.id.btnAtrasDashboard); // Obtiene flecha atrás del dashboard admin
        if (flechaDashboard != null) flechaDashboard.setOnClickListener(v -> mostrarPanel(panelMenu)); // Regresa al menú desde dashboard

        View flechaUsuario = findViewById(R.id.btnAtrasUsuario); // Obtiene flecha atrás del portal usuario
        if (flechaUsuario != null) flechaUsuario.setOnClickListener(v -> mostrarPanel(panelMenu)); // Regresa al menú desde usuario

        if (btnVolverMenu1 != null) btnVolverMenu1.setOnClickListener(v -> mostrarPanel(panelMenu)); // Configura botón Volver en login
        if (btnVolverMenu2 != null) btnVolverMenu2.setOnClickListener(v -> mostrarPanel(panelMenu)); // Configura botón Volver en usuario

        btnLoginAdmin.setOnClickListener(v -> { // Valida credenciales y abre el dashboard administrativo
            String user = txtAdminUser.getText().toString().trim(); // Lee usuario ingresado
            String pass = txtAdminPass.getText().toString().trim(); // Lee contraseña ingresada

            if ((user.equalsIgnoreCase("Juan Zamora") || user.equalsIgnoreCase("Daniela Rojas")) && pass.equals("1234567890")) { // Valida usuario y clave
                adminActual = user.equalsIgnoreCase("Juan Zamora") ? "Juan Zamora" : "Daniela Rojas"; // Guarda analista activo
                String nombreFormateado = adminActual.equalsIgnoreCase("Juan Zamora") ? "Juan" : "Daniela"; // Obtiene primer nombre
                lblSaludoAdmin.setText("Bienvenido, " + nombreFormateado); // Muestra saludo personalizado

                cargarDashboardAdmin(adminActual); // Carga casos del analista
                mostrarPanel(panelAdminDashboard); // Muestra dashboard admin
                txtAdminUser.setText(""); // Limpia campo de usuario
                txtAdminPass.setText(""); // Limpia campo de contraseña
            } else { // Si las credenciales no son válidas
                Toast.makeText(MainActivity.this, "⚠️ Credenciales incorrectas. Verifique usuario y contraseña.", Toast.LENGTH_SHORT).show(); // Informa error
            }
        }); // Fin del listener de btnLoginAdmin

        btnCerrarSesionAdmin.setOnClickListener(v -> { // Cierra sesión del administrador y vuelve al menú
            adminActual = ""; // Limpia administrador activo
            mostrarPanel(panelMenu); // Muestra menú inicial
        }); // Fin del listener de btnCerrarSesionAdmin

        if (btnRegistrar != null) btnRegistrar.setOnClickListener(v -> registrarNuevoCaso()); // Registra un nuevo caso al pulsar el botón
        if (btnLimpiar != null) btnLimpiar.setOnClickListener(v -> limpiarFormulario()); // Limpia el formulario al pulsar el botón
        if (btnConsultarUsuario != null) btnConsultarUsuario.setOnClickListener(v -> consultarTicketsUsuario()); // Consulta casos por documento al pulsar el botón
    } // Fin de onCreate

    @Override // Sobrescribe onResume para recalcular SLA al volver a abrir o enfocar la app
    protected void onResume() { // Se ejecuta cada vez que la actividad vuelve a primer plano
        super.onResume(); // Llama a onResume de AppCompatActivity
        for (TicketSoporte t : obtenerTodosLosTicketsActuales()) { // Recorre todos los tickets existentes
            procesarCierreAutomaticoPorSlaSiResuelto(t); // Evalúa tiempo transcurrido (incluso con app cerrada) y cierra si estaba Resuelto
        }
        if (panelAdminDashboard != null && panelAdminDashboard.getVisibility() == View.VISIBLE && !adminActual.isEmpty()) { // Si el dashboard admin está visible
            cargarDashboardAdmin(adminActual); // Refresca la lista con el estado y SLA actualizados
        }
    } // Fin de onResume

    @Override // Sobrescribe onPause para guardar cambios antes de salir o pasar a segundo plano
    protected void onPause() { // Se ejecuta cuando la actividad se pausa
        super.onPause(); // Llama a onPause de AppCompatActivity
        guardarTicketsPersistentes(); // Guarda todos los tickets y contadores en SharedPreferences
    } // Fin de onPause

    private int getIdentifierSafe(String name, String defType) { // Obtiene el ID de un recurso sin lanzar excepción si no existe
        return getResources().getIdentifier(name, defType, getPackageName()); // Retorna identificador del recurso o 0
    } // Fin de getIdentifierSafe

    private String obtenerPropiedadSeguro(Object objeto, String... nombresMetodos) { // Lee propiedades vía getters o campos usando reflexión
        if (objeto == null || nombresMetodos == null) return ""; // Retorna vacío si el objeto o la lista es nula

        for (String nombre : nombresMetodos) { // Intenta cada método getter candidato
            if (nombre == null || nombre.isEmpty()) continue; // Salta nombres vacíos
            try { // Intenta buscar e invocar el método
                Method m = objeto.getClass().getMethod(nombre); // Obtiene método sin argumentos
                Object val = m.invoke(objeto); // Ejecuta el método en el objeto
                if (val != null) { // Si el valor retornado no es nulo
                    return String.valueOf(val); // Devuelve el valor como String
                }
            } catch (Exception ignored) { } // Continúa con el siguiente método si falla
        }

        Class<?> clazz = objeto.getClass(); // Inicia búsqueda directa sobre campos de la clase y padres
        while (clazz != null) { // Recorre jerarquía de clases
            for (String nombre : nombresMetodos) { // Revisa nombres asociados
                String campoPosible = nombre.startsWith("get") && nombre.length() > 3 // Convierte getCampo a campo
                        ? Character.toLowerCase(nombre.charAt(3)) + nombre.substring(4) // Minúscula en primera letra
                        : nombre; // Mantiene nombre original si no inicia con get
                try { // Intenta leer el campo
                    Field field = clazz.getDeclaredField(campoPosible); // Obtiene campo declarado
                    field.setAccessible(true); // Permite acceder a campos privados
                    Object val = field.get(objeto); // Lee valor del campo
                    if (val != null) { // Si no es nulo
                        return String.valueOf(val); // Devuelve valor como String
                    }
                } catch (Exception ignored) { } // Continúa si no existe ese campo en este nivel
            }
            clazz = clazz.getSuperclass(); // Sube a la superclase
        }

        return ""; // Retorna vacío si no encontró el dato
    } // Fin de obtenerPropiedadSeguro

    private long obtenerFechaCreacionSeguro(TicketSoporte ticket) { // Obtiene la hora de creación en milisegundos del ticket
        if (ticket == null) return System.currentTimeMillis(); // Usa hora actual si el ticket es nulo
        String raw = obtenerPropiedadSeguro(ticket, "getFechaCreacionMillis", "getFechaCreacion", "getTimestampCreacion", "getHoraCreacionMillis"); // Busca propiedad de fecha de creación
        try { // Intenta convertir el valor leído a long
            long val = Long.parseLong(raw.trim()); // Parsea milisegundos
            if (val > 0L) return val; // Retorna timestamp válido
        } catch (Exception ignored) { } // Ignora si no se pudo parsear
        return System.currentTimeMillis(); // Devuelve hora actual como respaldo
    } // Fin de obtenerFechaCreacionSeguro

    private void asignarFechaCreacionSeguro(TicketSoporte ticket, long fechaCreacionMillis) { // Asigna fechaCreacionMillis al ticket para mantener el reloj real
        if (ticket == null || fechaCreacionMillis <= 0L) return; // Valida parámetros

        String[] setters = { // Posibles métodos setter para fecha de creación
                "setFechaCreacionMillis",
                "setFechaCreacion",
                "setTimestampCreacion",
                "setHoraCreacionMillis"
        };

        for (String nombre : setters) { // Intenta cada setter
            try { // Busca método con parámetro long
                Method m = ticket.getClass().getMethod(nombre, long.class); // Obtiene método setter
                m.invoke(ticket, fechaCreacionMillis); // Asigna timestamp
                return; // Termina si tuvo éxito
            } catch (Exception ignored) { } // Prueba siguiente setter
        }

        Class<?> clazz = ticket.getClass(); // Si no hay setter, intenta asignar directo al campo
        String[] campos = {"fechaCreacionMillis", "fechaCreacion", "timestampCreacion", "horaCreacionMillis"}; // Posibles nombres de campo
        while (clazz != null) { // Recorre clases en la jerarquía
            for (String campo : campos) { // Busca cada nombre de campo
                try { // Intenta asignar el valor long al campo
                    Field f = clazz.getDeclaredField(campo); // Obtiene campo declarado
                    f.setAccessible(true); // Permite modificar campo privado
                    f.setLong(ticket, fechaCreacionMillis); // Guarda milisegundos
                    return; // Termina si tuvo éxito
                } catch (Exception ignored) { } // Prueba siguiente campo
            }
            clazz = clazz.getSuperclass(); // Sube a la clase padre
        }
    } // Fin de asignarFechaCreacionSeguro

    private void configurarSpinnersGarantia() { // Carga las opciones de marca y formato del equipo con estilo morado
        String[] marcas = {"Lenovo", "HP", "DELL"}; // Marcas admitidas para garantía
        setSpinnerMorado(spinnerMarca, marcas); // Aplica estilo morado y texto blanco al Spinner de marca

        String[] tipos = {"Portátil", "DESKTOP"}; // Tipos de equipo admitidos para garantía
        setSpinnerMorado(spinnerTipoEquipo, tipos); // Aplica estilo morado y texto blanco al Spinner de tipo
    } // Fin de configurarSpinnersGarantia

    private void setSpinnerMorado(Spinner spinner, String[] items) { // Estiliza el Spinner y su menú desplegable en morado con texto blanco
        if (spinner == null) return; // Sale si el Spinner es nulo

        ArrayAdapter<String> adapter = new ArrayAdapter<String>( // Crea adaptador de texto para el Spinner
                this, // Contexto de la actividad
                android.R.layout.simple_spinner_item, // Layout base del ítem seleccionado
                items // Lista de elementos a mostrar
        ) { // Inicio de adaptador personalizado
            @NonNull // Vista no nula
            @Override // Personaliza el texto visible cuando el Spinner está cerrado
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // Construye vista cerrada
                View view = super.getView(position, convertView, parent); // Obtiene vista base
                TextView tv = view.findViewById(android.R.id.text1); // Busca TextView interno
                if (tv != null) { // Si existe text1
                    tv.setTextColor(Color.WHITE); // Texto blanco legible
                    tv.setTextSize(15f); // Tamaño de letra
                    tv.setTypeface(null, Typeface.BOLD); // Negrita para mejor contraste
                } else if (view instanceof TextView) { // Si la vista raíz ya es un TextView
                    ((TextView) view).setTextColor(Color.WHITE); // Texto blanco
                    ((TextView) view).setTextSize(15f); // Tamaño de letra
                    ((TextView) view).setTypeface(null, Typeface.BOLD); // Negrita
                }
                return view; // Devuelve vista estilizada
            } // Fin de getView

            @Override // Personaliza cada opción dentro de la lista desplegable del Spinner
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // Construye fila desplegable
                View view = super.getDropDownView(position, convertView, parent); // Obtiene fila base
                view.setBackgroundColor(COLOR_POPUP_MORADO); // Fondo morado oscuro del desplegable
                TextView tv = view.findViewById(android.R.id.text1); // Busca TextView de la opción
                if (tv != null) { // Si existe text1
                    tv.setTextColor(Color.WHITE); // Texto blanco sobre fondo oscuro
                    tv.setBackgroundColor(COLOR_POPUP_MORADO); // Fondo morado oscuro en la fila
                    tv.setTextSize(15f); // Tamaño de texto
                } else if (view instanceof TextView) { // Si la fila misma es un TextView
                    ((TextView) view).setTextColor(Color.WHITE); // Texto blanco
                    ((TextView) view).setBackgroundColor(COLOR_POPUP_MORADO); // Fondo morado oscuro
                    ((TextView) view).setTextSize(15f); // Tamaño de texto
                }
                return view; // Devuelve fila desplegable estilizada
            } // Fin de getDropDownView
        }; // Fin de ArrayAdapter personalizado

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Define layout del menú desplegable
        spinner.setAdapter(adapter); // Asigna el adaptador al Spinner
    } // Fin de setSpinnerMorado

    private void mostrarPanel(LinearLayout panelActivo) { // Muestra el panel indicado y oculta los demás
        panelMenu.setVisibility(View.GONE); // Oculta menú principal
        panelAdminLogin.setVisibility(View.GONE); // Oculta login de admin
        panelAdminDashboard.setVisibility(View.GONE); // Oculta dashboard de admin
        panelUsuario.setVisibility(View.GONE); // Oculta portal de usuario

        panelActivo.setVisibility(View.VISIBLE); // Muestra el panel seleccionado
    } // Fin de mostrarPanel

    private void cargarDashboardAdmin(String nombreAdmin) { // Carga en el ListView los tickets asignados al administrador
        ArrayList<TicketSoporte> casosAdmin = repositorio.obtenerTicketsPorAnalista(nombreAdmin); // Obtiene tickets del analista

        ArrayAdapter<TicketSoporte> adapterAdmin = new ArrayAdapter<TicketSoporte>(this, R.layout.item_ticket_admin, casosAdmin) { // Adaptador de casos para admin
            @NonNull // Vista no nula
            @Override // Renderiza cada fila de ticket en el dashboard del administrador
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // Construye o reutiliza fila
                View view = convertView; // Reutiliza vista si existe
                if (view == null) { // Si es nueva
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_ticket_admin, parent, false); // Infla layout de fila admin
                }

                view.setBackgroundColor(COLOR_INPUT_MORADO); // Aplica fondo morado oscuro a la fila

                TicketSoporte ticket = getItem(position); // Obtiene ticket de la posición actual
                if (ticket != null) { // Si el ticket existe
                    procesarCierreAutomaticoPorSlaSiResuelto(ticket); // Valida SLA y cierra si estaba Resuelto y venció el tiempo

                    TextView txtPrio = view.findViewById(R.id.txtPrioridadBadge); // Etiqueta de prioridad
                    TextView txtCod = view.findViewById(R.id.txtCodigoCaso); // Etiqueta de código de caso
                    TextView txtEst = view.findViewById(R.id.txtEstadoPill); // Etiqueta de estado

                    txtPrio.setText(ticket.getPrioridad()); // Muestra prioridad
                    txtPrio.setTextColor(ticket.getColorPrioridad()); // Aplica color según prioridad
                    txtCod.setText(ticket.getCodigoTicket()); // Muestra código consecutivo
                    txtCod.setTextColor(Color.WHITE); // Texto blanco para el código

                    if ("Cerrado".equalsIgnoreCase(ticket.getEstado())) { // Si está Cerrado
                        txtEst.setText("Cerrado"); // Muestra Cerrado
                        txtEst.setTextColor(Color.parseColor("#FCA5A5")); // Rojo claro legible sobre fondo morado
                    } else { // Si no está Cerrado
                        txtEst.setText(ticket.getEstado()); // Muestra estado actual
                        txtEst.setTextColor(COLOR_TEXTO_LILA); // Color lila claro legible
                    }
                }
                return view; // Devuelve fila lista para mostrar
            } // Fin de getView
        }; // Fin de adapterAdmin

        listViewAdminTickets.setAdapter(adapterAdmin); // Asigna adaptador al ListView de admin

        listViewAdminTickets.setOnItemClickListener((parent, view, position, id) -> { // Abre detalle y SLA al tocar un ticket
            TicketSoporte t = casosAdmin.get(position); // Obtiene ticket seleccionado
            procesarCierreAutomaticoPorSlaSiResuelto(t); // Revisa cierre automático por SLA si estaba Resuelto
            mostrarDetalleConSlaYEditar(t); // Abre diálogo de detalle con barra de SLA en vivo
        }); // Fin de setOnItemClickListener
    } // Fin de cargarDashboardAdmin

    private void procesarCierreAutomaticoPorSlaSiResuelto(TicketSoporte ticket) { // Pasa a Cerrado con nota de éxito si vence el SLA estando Resuelto
        if (ticket == null) return; // Sale si el ticket es nulo

        ticket.verificarYActualizarVencimientoSla(); // Actualiza evaluación de vencimiento del SLA contra el reloj actual

        String estadoActual = obtenerPropiedadSeguro(ticket, "getEstado"); // Obtiene estado actual
        String notaActual = obtenerPropiedadSeguro(ticket, "getNotaCierre"); // Obtiene nota de cierre actual

        if (ticket.estaSlaVencido() && "Resuelto".equalsIgnoreCase(estadoActual)) { // Si venció el SLA y estaba en estado Resuelto
            ticket.setEstado("Cerrado"); // Cambia estado a Cerrado
            ticket.setMotivoEstado("Resolución automática"); // Marca motivo de resolución automática

            if (notaActual == null || notaActual.trim().isEmpty()) { // Si no tenía nota de cierre previa
                ticket.setNotaCierre(NOTA_CIERRE_EXITOSO); // Registra la nota de que fue resuelto con éxito
            }

            guardarTicketsPersistentes(); // Guarda el cambio de forma permanente en SharedPreferences
        }
    } // Fin de procesarCierreAutomaticoPorSlaSiResuelto

    private ArrayList<TicketSoporte> obtenerTodosLosTicketsActuales() { // Obtiene todos los tickets sin repetir códigos
        ArrayList<TicketSoporte> todos = new ArrayList<>(); // Lista consolidada
        String[] analistasConocidos = {"Juan Zamora", "Daniela Rojas"}; // Analistas conocidos
        for (String analista : analistasConocidos) { // Recorre analistas
            ArrayList<TicketSoporte> lista = repositorio.obtenerTicketsPorAnalista(analista); // Consulta tickets del analista
            if (lista != null) { // Si hay lista
                for (TicketSoporte t : lista) { // Recorre tickets
                    if (t != null && !contieneCodigo(todos, t.getCodigoTicket())) { // Evita duplicados
                        todos.add(t); // Agrega ticket
                    }
                }
            }
        }
        return todos; // Devuelve colección completa
    } // Fin de obtenerTodosLosTicketsActuales

    private boolean contieneCodigo(ArrayList<TicketSoporte> lista, String codigo) { // Comprueba si una lista ya contiene un código
        if (codigo == null) return false; // Retorna falso si el código es nulo
        for (TicketSoporte t : lista) { // Recorre elementos
            if (t != null && codigo.equalsIgnoreCase(t.getCodigoTicket())) { // Compara ignorando mayúsculas/minúsculas
                return true; // Encontrado
            }
        }
        return false; // No encontrado
    } // Fin de contieneCodigo

    private String generarSiguienteCodigoConsecutivo(String prefijo) { // Genera INC o WO consecutivo rellenando con ceros a la izquierda
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // Abre preferencias
        String keyContador = "WO".equalsIgnoreCase(prefijo) ? KEY_COUNTER_WO : KEY_COUNTER_INC; // Selecciona clave según prefijo
        int longitudDigitos = "WO".equalsIgnoreCase(prefijo) ? LONGITUD_DIGITOS_WO : LONGITUD_DIGITOS_INC; // Selecciona largo de dígitos (12 para INC, 13 para WO)

        long maxExistente = prefs.getLong(keyContador, 0L); // Toma último número persistido

        for (TicketSoporte t : obtenerTodosLosTicketsActuales()) { // Revisa tickets existentes para tomar el mayor número
            if (t != null && t.getCodigoTicket() != null) { // Valida código
                String cod = t.getCodigoTicket().trim().toUpperCase(Locale.ROOT); // Normaliza código
                if (cod.startsWith(prefijo.toUpperCase(Locale.ROOT))) { // Verifica prefijo INC o WO
                    String soloDigitos = cod.substring(prefijo.length()).replaceAll("\\D+", ""); // Extrae solo dígitos
                    if (!soloDigitos.isEmpty()) { // Si hay dígitos
                        try { // Convierte a long
                            long valor = Long.parseLong(soloDigitos); // Obtiene valor numérico
                            if (valor > maxExistente) { // Si supera el máximo actual
                                maxExistente = valor; // Actualiza máximo
                            }
                        } catch (NumberFormatException ignored) { } // Ignora si excede rango
                    }
                }
            }
        }

        long siguienteNumero = maxExistente + 1L; // Calcula siguiente consecutivo
        prefs.edit().putLong(keyContador, siguienteNumero).apply(); // Persiste nuevo contador

        String formato = "%0" + longitudDigitos + "d"; // Formato con ceros a la izquierda
        return prefijo.toUpperCase(Locale.ROOT) + String.format(Locale.ROOT, formato, siguienteNumero); // Devuelve código completo
    } // Fin de generarSiguienteCodigoConsecutivo

    private void guardarTicketsPersistentes() { // Guarda todos los tickets, fechaCreacionMillis y contadores en SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // Abre preferencias
        JSONArray array = new JSONArray(); // Arreglo JSON de salida
        long maxInc = prefs.getLong(KEY_COUNTER_INC, 0L); // Contador INC actual
        long maxWo = prefs.getLong(KEY_COUNTER_WO, 0L); // Contador WO actual

        for (TicketSoporte t : obtenerTodosLosTicketsActuales()) { // Recorre cada ticket actual
            if (t == null) continue; // Salta nulos
            try { // Construye JSONObject para el ticket
                String codigoVal = obtenerPropiedadSeguro(t, "getCodigoTicket", "getCodigo"); // Obtiene código
                String tituloVal = obtenerPropiedadSeguro(t, "getTitulo"); // Obtiene título
                String usuarioVal = obtenerPropiedadSeguro(t, "getNombreUsuario", "getUsuario"); // Obtiene nombre de usuario
                String correoVal = obtenerPropiedadSeguro(t, "getCorreoCorporativo", "getCorreo"); // Obtiene correo
                String documentoVal = obtenerPropiedadSeguro(t, "getDocumento", "getDocumentoUsuario", "getIdDocumento", "getIdentificacion"); // Obtiene documento
                String placaVal = obtenerPropiedadSeguro(t, "getPlacaEquipo", "getPlaca"); // Obtiene placa
                String descripcionVal = obtenerPropiedadSeguro(t, "getDescripcionHechos", "getDescripcion"); // Obtiene descripción
                String estadoVal = obtenerPropiedadSeguro(t, "getEstado"); // Obtiene estado
                String motivoVal = obtenerPropiedadSeguro(t, "getMotivoEstado", "getMotivo"); // Obtiene motivo
                String prioridadVal = obtenerPropiedadSeguro(t, "getPrioridad"); // Obtiene prioridad
                String frenteVal = obtenerPropiedadSeguro(t, "getFrente"); // Obtiene frente
                String analistaVal = obtenerPropiedadSeguro(t, "getAnalistaAsignado", "getAnalista"); // Obtiene analista
                String notaCierreVal = obtenerPropiedadSeguro(t, "getNotaCierre"); // Obtiene nota de cierre
                long fechaCreacionVal = obtenerFechaCreacionSeguro(t); // Obtiene hora de creación en milisegundos

                JSONObject obj = new JSONObject(); // Crea objeto JSON
                obj.put("codigoTicket", safeStr(codigoVal)); // Guarda código
                obj.put("titulo", safeStr(tituloVal)); // Guarda título
                obj.put("usuario", safeStr(usuarioVal)); // Guarda usuario
                obj.put("correo", safeStr(correoVal)); // Guarda correo
                obj.put("documento", safeStr(documentoVal)); // Guarda documento
                obj.put("placa", safeStr(placaVal)); // Guarda placa
                obj.put("descripcion", safeStr(descripcionVal)); // Guarda descripción
                obj.put("estado", safeStr(estadoVal)); // Guarda estado
                obj.put("motivoEstado", safeStr(motivoVal)); // Guarda motivo
                obj.put("prioridad", safeStr(prioridadVal)); // Guarda prioridad
                obj.put("frente", safeStr(frenteVal)); // Guarda frente
                obj.put("analistaAsignado", safeStr(analistaVal)); // Guarda analista
                obj.put("notaCierre", safeStr(notaCierreVal)); // Guarda nota de cierre
                obj.put("fechaCreacionMillis", fechaCreacionVal); // Guarda hora real de creación para calcular SLA con app cerrada
                obj.put("slaMinutos", obtenerMinutosSlaPorPrioridad(prioridadVal)); // Guarda minutos de SLA
                obj.put("tipoClase", t.getClass().getSimpleName()); // Guarda tipo de clase del ticket

                if (t instanceof TicketGarantia) { // Si es garantía, guarda datos específicos del equipo
                    String marcaVal = obtenerPropiedadSeguro(t, "getMarca", "getMarcaEquipo", "getBrand"); // Obtiene marca
                    String tipoEquipoVal = obtenerPropiedadSeguro(t, "getTipoEquipo", "getFormatoEquipo", "getFormato"); // Obtiene formato
                    String serialVal = obtenerPropiedadSeguro(t, "getSerialEquipo", "getSerial", "getNumeroSerie"); // Obtiene serial

                    obj.put("marca", safeStr(marcaVal)); // Guarda marca
                    obj.put("tipoEquipo", safeStr(tipoEquipoVal)); // Guarda formato
                    obj.put("serial", safeStr(serialVal)); // Guarda serial
                }

                array.put(obj); // Agrega objeto al arreglo JSON

                String cod = codigoVal.trim().toUpperCase(Locale.ROOT); // Normaliza código para actualizar contadores
                if (cod.startsWith("INC")) { // Si es INC
                    long num = extraerNumero(cod.substring(3)); // Extrae número
                    if (num > maxInc) maxInc = num; // Actualiza máximo INC
                } else if (cod.startsWith("WO")) { // Si es WO
                    long num = extraerNumero(cod.substring(2)); // Extrae número
                    if (num > maxWo) maxWo = num; // Actualiza máximo WO
                }
            } catch (JSONException ignored) { } // Ignora errores de serialización puntuales
        }

        prefs.edit() // Edita preferencias
                .putString(KEY_TICKETS_JSON, array.toString()) // Guarda JSON de tickets
                .putLong(KEY_COUNTER_INC, maxInc) // Guarda contador INC
                .putLong(KEY_COUNTER_WO, maxWo) // Guarda contador WO
                .apply(); // Aplica cambios de forma asíncrona
    } // Fin de guardarTicketsPersistentes

    private void cargarTicketsPersistentes() { // Carga y reconstruye los tickets guardados en SharedPreferences
        if (persistenciaInicializada) return; // Evita doble carga en la misma instancia de proceso
        persistenciaInicializada = true; // Marca inicialización completada

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // Abre preferencias
        String jsonStr = prefs.getString(KEY_TICKETS_JSON, null); // Lee JSON guardado
        if (jsonStr == null || jsonStr.isEmpty()) return; // Sale si no hay datos guardados

        try { // Parsea JSONArray
            JSONArray array = new JSONArray(jsonStr); // Convierte string JSON a arreglo
            for (int i = 0; i < array.length(); i++) { // Recorre tickets guardados
                JSONObject obj = array.optJSONObject(i); // Obtiene objeto JSON
                if (obj == null) continue; // Salta nulos

                String codigo = obj.optString("codigoTicket", ""); // Lee código
                if (codigo.isEmpty() || contieneCodigo(obtenerTodosLosTicketsActuales(), codigo)) { // Evita vacíos o duplicados
                    continue; // Salta al siguiente
                }

                String titulo = obj.optString("titulo", ""); // Lee título
                String usuario = obj.optString("usuario", ""); // Lee usuario
                String correo = obj.optString("correo", ""); // Lee correo
                String documento = obj.optString("documento", ""); // Lee documento
                String placa = obj.optString("placa", ""); // Lee placa
                String descripcion = obj.optString("descripcion", ""); // Lee descripción
                String estado = obj.optString("estado", "Asignado"); // Lee estado
                String motivo = obj.optString("motivoEstado", "En atención inicial"); // Lee motivo
                String prioridad = obj.optString("prioridad", "Regular"); // Lee prioridad
                String frente = obj.optString("frente", "Caja de compensación"); // Lee frente
                String analista = obj.optString("analistaAsignado", "Juan Zamora"); // Lee analista
                String notaCierre = obj.optString("notaCierre", ""); // Lee nota de cierre
                String tipoClase = obj.optString("tipoClase", ""); // Lee clase original
                int slaMin = obj.optInt("slaMinutos", obtenerMinutosSlaPorPrioridad(prioridad)); // Lee minutos de SLA
                long fechaCreacion = obj.optLong("fechaCreacionMillis", System.currentTimeMillis()); // Lee fecha de creación original

                TicketSoporte ticket; // Instancia a reconstruir
                if ("TicketGarantia".equals(tipoClase)) { // Si fue TicketGarantia
                    String marca = obj.optString("marca", "Lenovo"); // Lee marca
                    String tipoEquipo = obj.optString("tipoEquipo", "Portátil"); // Lee formato
                    String serial = obj.optString("serial", ""); // Lee serial
                    ticket = new TicketGarantia(codigo, titulo, usuario, correo, documento, placa, marca, tipoEquipo, serial, descripcion, analista); // Crea TicketGarantia
                } else if ("TicketOrdenTrabajo".equals(tipoClase) || codigo.toUpperCase(Locale.ROOT).startsWith("WO")) { // Si fue orden de trabajo WO
                    ticket = new TicketOrdenTrabajo(codigo, titulo, usuario, correo, documento, placa, descripcion, slaMin, analista); // Crea TicketOrdenTrabajo
                } else { // Si fue incidente o garantía con prefijo INC
                    ticket = new TicketIncidente(codigo, titulo, usuario, correo, documento, placa, descripcion, slaMin, analista); // Crea TicketIncidente
                }

                asignarFechaCreacionSeguro(ticket, fechaCreacion); // Restaura fechaCreacionMillis real
                ticket.setEstado(estado); // Restaura estado
                ticket.setMotivoEstado(motivo); // Restaura motivo de estado
                ticket.setPrioridad(prioridad); // Restaura prioridad
                ticket.setFrente(frente); // Restaura frente
                ticket.setAnalistaAsignado(analista); // Restaura analista asignado
                if (!notaCierre.isEmpty()) { // Si tenía nota de cierre
                    ticket.setNotaCierre(notaCierre); // Restaura nota de cierre
                }
                asignarSlaSegunPrioridad(ticket, prioridad); // Sincroniza minutos de SLA
                procesarCierreAutomaticoPorSlaSiResuelto(ticket); // Valida si venció el SLA estando Resuelto mientras la app estaba cerrada

                repositorio.registrarTicket(ticket); // Registra ticket en repositorio
            }
        } catch (JSONException ignored) { } // Ignora errores de JSON
    } // Fin de cargarTicketsPersistentes

    private long extraerNumero(String texto) { // Extrae los dígitos de un texto y los convierte a long
        String digits = texto == null ? "" : texto.replaceAll("\\D+", ""); // Filtra solo dígitos
        if (digits.isEmpty()) return 0L; // Devuelve 0 si no hay dígitos
        try { // Intenta convertir a long
            return Long.parseLong(digits); // Retorna número
        } catch (NumberFormatException e) { // Si no cabe en long
            return 0L; // Devuelve 0
        }
    } // Fin de extraerNumero

    private String safeStr(String s) { // Convierte null en cadena vacía
        return s == null ? "" : s; // Retorna s o ""
    } // Fin de safeStr

    private String normalizarTexto(String texto) { // Quita tildes, pasa a minúsculas y recorta espacios
        if (texto == null) return ""; // Retorna vacío si es null
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD) // Separa caracteres y tildes
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // Quita tildes
        return sinTildes.toLowerCase(Locale.ROOT).trim(); // Convierte a minúsculas y limpia extremos
    } // Fin de normalizarTexto

    private String calcularPrioridadAutomatica(String titulo, String descripcion, boolean esGarantia, boolean esOrdenTrabajo) { // Determina prioridad Alta, Regular o Baja
        String texto = normalizarTexto(titulo + " " + descripcion); // Normaliza título y descripción juntos

        boolean mencionaEspacioAmplio = texto.contains("area") // Menciona área
                || texto.contains("departamento") // Menciona departamento
                || texto.contains("sede") // Menciona sede
                || texto.contains("piso") // Menciona piso
                || texto.contains("edificio") // Menciona edificio
                || texto.contains("vivienda"); // Menciona área de vivienda

        boolean mencionaCaidaOSinServicio = texto.contains("se cayo") // Caída reportada
                || texto.contains("se callo") // Variante ortográfica "calló"
                || texto.contains("caida") // Palabra caída
                || texto.contains("sin red") // Sin red
                || texto.contains("sin internet") // Sin internet
                || texto.contains("sin servicio") // Sin servicio
                || texto.contains("no hay red") // No hay red
                || texto.contains("no hay internet") // No hay internet
                || texto.contains("caido"); // Estado caído

        boolean mencionaTodoElEspacio = texto.contains("todo el") // Alcance "todo el..."
                || texto.contains("toda el") // Variante de género
                || texto.contains("toda la") // Alcance "toda la..."
                || texto.contains("en todo") // Alcance "en todo..."
                || texto.contains("en toda") // Alcance "en toda..."
                || texto.contains("general") // Caída general
                || texto.contains("masiva"); // Caída masiva

        boolean esAlta = (mencionaEspacioAmplio && mencionaCaidaOSinServicio && mencionaTodoElEspacio) // Todo un área/departamento/sede sin red o caída
                || (texto.contains("red") && mencionaCaidaOSinServicio && mencionaTodoElEspacio) // Caída de red en todo un espacio
                || texto.contains("caida general") // Caída general explícita
                || texto.contains("caida masiva") // Caída masiva explícita
                || texto.contains("se cayo todo") // Caída total
                || texto.contains("se callo todo") // Caída total (variante)
                || texto.contains("toda la sede") // Toda la sede afectada
                || texto.contains("todo el edificio") // Todo el edificio afectado
                || texto.contains("sin internet en toda") // Sin internet amplio
                || texto.contains("urgencias") // Urgencias médicas
                || texto.contains("cirugia") // Cirugía
                || texto.contains("hospitalizacion") // Hospitalización
                || texto.contains("servidor caido") // Servidor caído
                || texto.contains("sistema caido") // Sistema caído
                || texto.contains("ransomware") // Ransomware
                || texto.contains("hackeo") // Hackeo
                || texto.contains("fuga de datos") // Fuga de datos
                || texto.contains("no atiende pacientes") // No atiende pacientes
                || texto.contains("critico"); // Marcado crítico

        if (esAlta) { // Si cumple criterios de alta criticidad
            return "Alta"; // Prioridad Alta (SLA 5 min)
        }

        boolean esRegular = texto.contains("se le cayo la red al jefe") // Caída de red a un jefe puntual
                || texto.contains("se cayo la red al jefe") // Variante sin "le"
                || texto.contains("se callo la red al jefe") // Variante con "callo"
                || texto.contains("sin red el jefe") // Jefe sin red
                || texto.contains("sin internet el jefe") // Jefe sin internet
                || ((texto.contains("red") || texto.contains("internet")) // Falla de red/internet a un cargo directivo/líder
                && (texto.contains("jefe") || texto.contains("gerente") || texto.contains("director") || texto.contains("coordinador") || texto.contains("lider"))) // Roles de liderazgo
                || texto.contains("no puede facturar") // Problema de facturación puntual
                || texto.contains("no carga el sistema") // Problema puntual de carga de sistema
                || texto.contains("bloqueo de cuenta") // Bloqueo de cuenta
                || texto.contains("correo bloqueado") // Correo bloqueado
                || texto.contains("impresora de red") // Impresora en red
                || texto.contains("falla en el area") // Falla puntual de área sin caída total
                || texto.contains("falla en el departamento") // Falla puntual de departamento
                || esGarantia; // Garantías en prioridad Regular

        if (esRegular) { // Si cumple criterios regulares
            return "Regular"; // Prioridad Regular (SLA 15 min)
        }

        boolean esBaja = texto.contains("consulta") // Consultas
                || texto.contains("informacion") // Información
                || texto.contains("cambio de clave") // Cambio de clave
                || texto.contains("restablecer contrasena") // Restablecer contraseña
                || texto.contains("solicitud de acceso") // Solicitud de acceso
                || texto.contains("instalar programa") // Instalar programa
                || texto.contains("configurar firma") // Configurar firma
                || texto.contains("mouse") // Mouse
                || texto.contains("teclado") // Teclado
                || esOrdenTrabajo; // Órdenes de trabajo estándar

        if (esBaja) { // Si cumple criterios de baja prioridad
            return "Baja"; // Prioridad Baja (SLA 30 min)
        }

        return "Regular"; // Prioridad por defecto
    } // Fin de calcularPrioridadAutomatica

    private int obtenerMinutosSlaPorPrioridad(String prioridad) { // Devuelve los minutos de SLA según la prioridad
        if ("Alta".equalsIgnoreCase(prioridad)) { // Prioridad Alta
            return SLA_ALTA_MINUTOS; // 5 minutos
        } else if ("Baja".equalsIgnoreCase(prioridad)) { // Prioridad Baja
            return SLA_BAJA_MINUTOS; // 30 minutos
        } else { // Prioridad Regular
            return SLA_REGULAR_MINUTOS; // 15 minutos
        }
    } // Fin de obtenerMinutosSlaPorPrioridad

    private void asignarSlaSegunPrioridad(TicketSoporte ticket, String prioridad) { // Asigna los minutos de SLA al objeto ticket si existe setter
        if (ticket == null) return; // Sale si es nulo
        int minutosSla = obtenerMinutosSlaPorPrioridad(prioridad); // Obtiene minutos correspondientes

        String[] posiblesSetters = { // Posibles nombres de setter de SLA
                "setSlaMinutos",
                "setTiempoSlaMinutos",
                "setMinutosSla",
                "setSla"
        };

        for (String nombreMetodo : posiblesSetters) { // Prueba cada setter
            try { // Intenta setter con int
                Method mInt = ticket.getClass().getMethod(nombreMetodo, int.class); // Busca método con int
                mInt.invoke(ticket, minutosSla); // Asigna minutos
                return; // Finaliza si tuvo éxito
            } catch (Exception ignored) { } // Prueba siguiente

            try { // Intenta setter con double
                Method mDouble = ticket.getClass().getMethod(nombreMetodo, double.class); // Busca método con double
                mDouble.invoke(ticket, (double) minutosSla); // Asigna minutos como double
                return; // Finaliza si tuvo éxito
            } catch (Exception ignored) { } // Prueba siguiente
        }
    } // Fin de asignarSlaSegunPrioridad

    private void registrarNuevoCaso() { // Valida datos del formulario, crea el ticket y lo guarda
        String titulo = txtTituloCaso.getText().toString().trim(); // Lee título
        String usuario = txtNombreUsuario.getText().toString().trim(); // Lee nombre de usuario
        String correo = txtCorreoCorporativo.getText().toString().trim().toLowerCase(); // Lee y normaliza correo
        String documento = txtDocumento.getText().toString().trim(); // Lee documento/ID
        String placa = txtPlacaEquipo.getText().toString().trim(); // Lee placa del equipo
        String descripcion = txtDescripcion.getText().toString().trim(); // Lee descripción
        boolean esGarantia = rbGarantia.isChecked(); // Indica si es garantía
        boolean esOrdenTrabajo = rbOrdenTrabajo.isChecked(); // Indica si es orden de trabajo

        if (titulo.isEmpty() || usuario.isEmpty() || correo.isEmpty() || documento.isEmpty() || placa.isEmpty() || descripcion.isEmpty()) { // Valida campos vacíos
            Toast.makeText(this, "⚠️ Todos los campos principales son obligatorios", Toast.LENGTH_SHORT).show(); // Informa faltantes
            return; // Detiene registro
        }

        if (!usuario.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) { // Valida solo letras y espacios en nombre
            Toast.makeText(this, "⚠️ El nombre de usuario solo debe contener letras", Toast.LENGTH_SHORT).show(); // Informa error de formato
            return; // Detiene registro
        }

        if (!correo.matches("^[a-zA-Z0-9._-]+@(compensar\\.com|consorciosalud\\.com)$")) { // Valida dominio corporativo
            Toast.makeText(this, "⚠️ Correo inválido. Debe terminar en @compensar.com o @consorciosalud.com", Toast.LENGTH_LONG).show(); // Informa dominio inválido
            return; // Detiene registro
        }

        if (documento.length() > 10) { // Valida máximo 10 dígitos de documento
            Toast.makeText(this, "⚠️ El ID / Documento no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show(); // Informa límite
            return; // Detiene registro
        }

        if (placa.length() > 13) { // Valida máximo 13 caracteres de placa
            Toast.makeText(this, "⚠️ La placa del equipo no puede superar los 13 caracteres", Toast.LENGTH_SHORT).show(); // Informa límite
            return; // Detiene registro
        }

        String serial = ""; // Serial inicial vacío
        if (esGarantia) { // Si es garantía, exige serial
            serial = txtSerialEquipo.getText().toString().trim(); // Lee serial
            if (serial.isEmpty()) { // Verifica que no esté vacío
                Toast.makeText(this, "⚠️ Para casos de garantía el Serial del equipo es obligatorio", Toast.LENGTH_SHORT).show(); // Informa serial obligatorio
                return; // Detiene registro
            }
        }

        String prefijo = (esGarantia || rbIncidente.isChecked()) ? "INC" : "WO"; // Define prefijo INC o WO
        String codigo = generarSiguienteCodigoConsecutivo(prefijo); // Genera código consecutivo con ceros

        String prioridadCalculada = calcularPrioridadAutomatica(titulo, descripcion, esGarantia, esOrdenTrabajo); // Calcula prioridad automática
        int minutosSla = obtenerMinutosSlaPorPrioridad(prioridadCalculada); // Obtiene SLA en minutos

        String analistaAsignado = repositorio.asignarAnalistaAutomatico(); // Asigna analista automáticamente
        TicketSoporte nuevoTicket; // Instancia del ticket

        if (esGarantia) { // Crea TicketGarantia
            String marcaSeleccionada = spinnerMarca.getSelectedItem() != null ? spinnerMarca.getSelectedItem().toString() : "Lenovo"; // Marca elegida
            String tipoSeleccionado = spinnerTipoEquipo.getSelectedItem() != null ? spinnerTipoEquipo.getSelectedItem().toString() : "Portátil"; // Tipo de equipo elegido
            nuevoTicket = new TicketGarantia(codigo, titulo, usuario, correo, documento, placa, marcaSeleccionada, tipoSeleccionado, serial, descripcion, analistaAsignado); // Instancia garantía
        } else if (rbIncidente.isChecked()) { // Crea TicketIncidente
            nuevoTicket = new TicketIncidente(codigo, titulo, usuario, correo, documento, placa, descripcion, minutosSla, analistaAsignado); // Instancia incidente
        } else { // Crea TicketOrdenTrabajo
            nuevoTicket = new TicketOrdenTrabajo(codigo, titulo, usuario, correo, documento, placa, descripcion, minutosSla, analistaAsignado); // Instancia orden de trabajo
        }

        asignarFechaCreacionSeguro(nuevoTicket, System.currentTimeMillis()); // Fija hora real de creación para el conteo del SLA
        nuevoTicket.setPrioridad(prioridadCalculada); // Asigna prioridad
        asignarSlaSegunPrioridad(nuevoTicket, prioridadCalculada); // Asigna minutos de SLA

        repositorio.registrarTicket(nuevoTicket); // Registra ticket en repositorio
        guardarTicketsPersistentes(); // Persiste ticket en SharedPreferences

        Toast.makeText( // Muestra confirmación al usuario
                this,
                "✅ Caso " + codigo + " guardado | Prioridad: " + prioridadCalculada + " (SLA " + minutosSla + " min)",
                Toast.LENGTH_LONG
        ).show();
        limpiarFormulario(); // Limpia campos del formulario
    } // Fin de registrarNuevoCaso

    private void consultarTicketsUsuario() { // Consulta y muestra los casos asociados a un documento
        if (txtDocConsulta == null) return; // Sale si no existe el campo

        String documento = txtDocConsulta.getText().toString().trim(); // Lee documento ingresado
        if (layoutUserTicketsLista != null) { // Si existe contenedor dinámico
            layoutUserTicketsLista.removeAllViews(); // Limpia resultados anteriores
        }

        if (documento.isEmpty()) { // Si está vacío
            if (lblResultadoConsulta != null) { // Si existe etiqueta de resultado
                lblResultadoConsulta.setVisibility(View.VISIBLE); // Muestra etiqueta
                lblResultadoConsulta.setTextColor(Color.WHITE); // Texto blanco
                lblResultadoConsulta.setText("⚠️ Ingrese su ID / Documento para buscar."); // Mensaje de guía
            }
            Toast.makeText(this, "⚠️ Ingrese su documento / ID para buscar", Toast.LENGTH_SHORT).show(); // Toast de aviso
            return; // Detiene consulta
        }

        if (documento.length() > 10) { // Valida longitud máxima de 10 dígitos
            if (lblResultadoConsulta != null) { // Si existe etiqueta
                lblResultadoConsulta.setVisibility(View.VISIBLE); // Muestra etiqueta
                lblResultadoConsulta.setTextColor(Color.WHITE); // Texto blanco
                lblResultadoConsulta.setText("⚠️ El ID / Documento no puede superar los 10 dígitos."); // Mensaje de error
            }
            Toast.makeText(this, "⚠️ El ID / Documento no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show(); // Toast de error
            return; // Detiene consulta
        }

        ArrayList<TicketSoporte> misTickets = repositorio.obtenerTicketsPorUsuario(documento); // Busca tickets del usuario
        if (lblResultadoConsulta != null) { // Si existe etiqueta
            lblResultadoConsulta.setVisibility(View.VISIBLE); // Muestra etiqueta
            lblResultadoConsulta.setTextColor(Color.WHITE); // Texto blanco
        }

        if (misTickets.isEmpty()) { // Si no hay tickets encontrados
            if (lblResultadoConsulta != null) { // Si existe etiqueta
                lblResultadoConsulta.setText("No se encontraron tickets con el ID: " + documento); // Mensaje sin resultados
            }
            if (listViewUsuarioTickets != null) { // Si existe ListView de usuario
                listViewUsuarioTickets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>())); // Limpia lista
            }
            Toast.makeText(this, "No se encontraron tickets con este ID.", Toast.LENGTH_SHORT).show(); // Toast sin resultados
            return; // Sale
        }

        if (lblResultadoConsulta != null) { // Si hay resultados
            lblResultadoConsulta.setText("Tickets encontrados (" + misTickets.size() + ") — toca uno para ver detalle:"); // Muestra cantidad
        }

        if (layoutUserTicketsLista != null) { // Si usa contenedor dinámico en LinearLayout
            for (TicketSoporte t : misTickets) { // Recorre tickets
                procesarCierreAutomaticoPorSlaSiResuelto(t); // Evalúa cierre automático por SLA si estaba Resuelto
                TextView itemTicket = new TextView(this); // Crea fila de texto
                itemTicket.setText("🎫 " + t.toUsuarioString()); // Texto resumen para usuario
                itemTicket.setTextSize(14f); // Tamaño de texto
                itemTicket.setTextColor(Color.WHITE); // Texto blanco
                itemTicket.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
                itemTicket.setPadding(24, 22, 24, 22); // Relleno interno
                itemTicket.setClickable(true); // Permite clic
                itemTicket.setFocusable(true); // Permite foco

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( // Parámetros de diseño
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 12); // Margen inferior
                itemTicket.setLayoutParams(params); // Aplica parámetros

                itemTicket.setOnClickListener(v -> mostrarDialogoSoloLecturaUsuario(t)); // Abre vista de solo lectura
                layoutUserTicketsLista.addView(itemTicket); // Añade fila al contenedor
            }
        } else if (listViewUsuarioTickets != null) { // Si usa ListView alternativo
            ArrayList<String> resumenes = new ArrayList<>(); // Lista de resúmenes
            for (TicketSoporte t : misTickets) { // Recorre tickets
                procesarCierreAutomaticoPorSlaSiResuelto(t); // Evalúa cierre automático por SLA si estaba Resuelto
                resumenes.add("🎫 " + t.toUsuarioString()); // Añade resumen
            }
            ArrayAdapter<String> adapterUser = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resumenes) { // Adaptador con estilo morado
                @NonNull // Vista no nula
                @Override // Estiliza fila de usuario
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { // Construye fila
                    View view = super.getView(position, convertView, parent); // Vista base
                    view.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
                    TextView tv = view.findViewById(android.R.id.text1); // TextView de la fila
                    if (tv != null) tv.setTextColor(Color.WHITE); // Texto blanco
                    return view; // Devuelve fila
                }
            };
            listViewUsuarioTickets.setAdapter(adapterUser); // Asigna adaptador
            listViewUsuarioTickets.setOnItemClickListener((parent, view, position, id) -> // Al tocar fila
                    mostrarDialogoSoloLecturaUsuario(misTickets.get(position)) // Abre diálogo de solo lectura
            );
        }
    } // Fin de consultarTicketsUsuario

    private void mostrarDialogoSoloLecturaUsuario(TicketSoporte t) { // Muestra detalle de solo lectura para el usuario
        String infoUsuario = "🎫 Caso: " + t.getCodigoTicket() + "\n" + // Código
                "📌 Título: " + t.getTitulo() + "\n" + // Título
                "📊 Estado: " + t.getEstado() + "\n" + // Estado
                "🗂️ Motivo de Estado: " + t.getMotivoEstado() + "\n" + // Motivo
                "🏢 Frente: " + t.getFrente() + "\n" + // Frente
                "👨‍💻 Analista: " + t.getAnalistaAsignado() + "\n" + // Analista
                "📝 Descripción: " + t.getDescripcionHechos(); // Descripción

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this) // Crea diálogo
                .setTitle("Estado de tu Caso") // Título del diálogo
                .setMessage(infoUsuario) // Texto del caso
                .setPositiveButton("Cerrar", null) // Botón cerrar
                .create(); // Construye diálogo

        if (dialog.getWindow() != null) { // Si tiene ventana
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(COLOR_FONDO_MORADO)); // Fondo morado oscuro
        }
        dialog.show(); // Muestra diálogo
    } // Fin de mostrarDialogoSoloLecturaUsuario

    private void mostrarDetalleConSlaYEditar(TicketSoporte t) { // Muestra detalle, barra de SLA en vivo cada segundo y botón Editar
        procesarCierreAutomaticoPorSlaSiResuelto(t); // Evalúa cierre automático por SLA si estaba Resuelto

        LinearLayout contenedor = new LinearLayout(this); // Contenedor vertical del diálogo
        contenedor.setOrientation(LinearLayout.VERTICAL); // Disposición vertical
        contenedor.setBackgroundColor(COLOR_FONDO_MORADO); // Fondo morado oscuro
        contenedor.setPadding(45, 24, 45, 16); // Relleno interno

        LinearLayout filaSuperior = new LinearLayout(this); // Fila superior con código y botón Editar
        filaSuperior.setOrientation(LinearLayout.HORIZONTAL); // Disposición horizontal
        filaSuperior.setGravity(Gravity.CENTER_VERTICAL); // Alineación vertical centrada

        TextView lblCasoHeader = new TextView(this); // Etiqueta del código del ticket
        lblCasoHeader.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)); // Ocupa espacio izquierdo
        lblCasoHeader.setText(t.getCodigoTicket()); // Muestra código
        lblCasoHeader.setTextSize(17f); // Tamaño de letra
        lblCasoHeader.setTypeface(null, Typeface.BOLD); // Negrita
        lblCasoHeader.setTextColor(Color.WHITE); // Texto blanco

        Button btnEditarEsquina = new Button(this); // Botón Editar
        btnEditarEsquina.setText("Editar"); // Texto del botón
        btnEditarEsquina.setTextSize(13f); // Tamaño de texto
        btnEditarEsquina.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6D28D9"))); // Color morado intenso
        btnEditarEsquina.setTextColor(Color.WHITE); // Texto blanco

        filaSuperior.addView(lblCasoHeader); // Agrega código a la fila superior
        filaSuperior.addView(btnEditarEsquina); // Agrega botón Editar a la fila superior
        contenedor.addView(filaSuperior); // Agrega fila superior al contenedor

        TextView txtDetalle = new TextView(this); // Texto de detalles del caso
        int minutosSla = obtenerMinutosSlaPorPrioridad(t.getPrioridad()); // Minutos de SLA del ticket
        txtDetalle.setText(t.obtenerDetalleCompleto() + "\n⏱️ SLA asignado: " + minutosSla + " minutos"); // Muestra detalle y SLA
        txtDetalle.setTextColor(COLOR_TEXTO_LILA); // Texto lila claro
        txtDetalle.setTextSize(14f); // Tamaño de letra
        txtDetalle.setPadding(0, 14, 0, 14); // Espaciado vertical
        contenedor.addView(txtDetalle); // Agrega detalle al contenedor

        LinearLayout filaSla = new LinearLayout(this); // Fila de barra y porcentaje de SLA
        filaSla.setOrientation(LinearLayout.HORIZONTAL); // Disposición horizontal
        filaSla.setGravity(Gravity.CENTER_VERTICAL); // Alineación vertical centrada

        ProgressBar barraSla = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); // Barra horizontal de SLA
        LinearLayout.LayoutParams paramsBarra = new LinearLayout.LayoutParams(0, 28, 1f); // Ancho flexible y alto 28px
        paramsBarra.setMarginEnd(12); // Separación derecha
        barraSla.setLayoutParams(paramsBarra); // Aplica parámetros
        barraSla.setMax(100); // Máximo 100%
        barraSla.setProgress(t.getPorcentajeSlaTranscurrido()); // Progreso actual
        barraSla.setProgressTintList(ColorStateList.valueOf(t.getColorSlaBarra())); // Color de la barra

        TextView lblSlaDerecha = new TextView(this); // Etiqueta lateral del SLA
        lblSlaDerecha.setText("SLA (" + minutosSla + "m) — " + t.getPorcentajeSlaTranscurrido() + "%"); // Muestra minutos y porcentaje
        lblSlaDerecha.setTypeface(null, Typeface.BOLD); // Negrita
        lblSlaDerecha.setTextSize(13f); // Tamaño de fuente
        lblSlaDerecha.setTextColor(t.getColorSlaBarra()); // Color según SLA

        filaSla.addView(barraSla); // Agrega barra a la fila SLA
        filaSla.addView(lblSlaDerecha); // Agrega etiqueta a la fila SLA
        contenedor.addView(filaSla); // Agrega fila SLA al contenedor

        TextView lblVencido = new TextView(this); // Etiqueta de aviso de vencimiento o cierre
        lblVencido.setTextColor(Color.parseColor("#FCA5A5")); // Rojo claro legible
        lblVencido.setTypeface(null, Typeface.BOLD); // Negrita
        lblVencido.setTextSize(13f); // Tamaño de letra
        lblVencido.setPadding(0, 8, 0, 0); // Margen superior
        if (t.estaSlaVencido() || "Cerrado".equalsIgnoreCase(t.getEstado())) { // Si venció o está Cerrado
            lblVencido.setVisibility(View.VISIBLE); // Muestra aviso
            lblVencido.setText("SLA vencido — Estado: " + t.getEstado()); // Texto del aviso
        } else { // Si aún no vence y no está Cerrado
            lblVencido.setVisibility(View.GONE); // Oculta aviso
        }
        contenedor.addView(lblVencido); // Agrega aviso al contenedor

        TextView lblNota = new TextView(this); // Etiqueta para la nota de cierre
        lblNota.setTextColor(Color.WHITE); // Texto blanco
        lblNota.setTypeface(null, Typeface.BOLD); // Negrita
        lblNota.setTextSize(13f); // Tamaño de letra
        lblNota.setPadding(0, 6, 0, 0); // Margen superior
        if ("Cerrado".equalsIgnoreCase(t.getEstado())) { // Si el caso está Cerrado
            String notaMostrar = obtenerPropiedadSeguro(t, "getNotaCierre"); // Lee nota de cierre
            if (notaMostrar == null || notaMostrar.trim().isEmpty()) { // Si está vacía
                notaMostrar = NOTA_CIERRE_EXITOSO; // Usa nota por defecto de cierre exitoso
            }
            lblNota.setVisibility(View.VISIBLE); // Muestra nota
            lblNota.setText("📝 Nota de cierre: " + notaMostrar); // Texto de la nota
        } else { // Si no está Cerrado
            lblNota.setVisibility(View.GONE); // Oculta nota
        }
        contenedor.addView(lblNota); // Agrega etiqueta de nota al contenedor

        AlertDialog dialogDetalle = new AlertDialog.Builder(MainActivity.this) // Crea diálogo de detalle
                .setView(contenedor) // Asigna contenedor
                .setPositiveButton("Cerrar", null) // Botón cerrar
                .create(); // Construye diálogo

        if (dialogDetalle.getWindow() != null) { // Si tiene ventana
            dialogDetalle.getWindow().setBackgroundDrawable(new ColorDrawable(COLOR_FONDO_MORADO)); // Fondo morado oscuro
        }

        Handler handlerSla = new Handler(Looper.getMainLooper()); // Handler en hilo principal para animar la barra
        Runnable actualizadorSla = new Runnable() { // Tarea periódica cada 1 segundo
            @Override // Ejecuta actualización de la barra y estado SLA
            public void run() { // Lógica de refresco en vivo
                procesarCierreAutomaticoPorSlaSiResuelto(t); // Revisa cierre automático por SLA si estaba Resuelto
                int progreso = t.getPorcentajeSlaTranscurrido(); // Obtiene porcentaje actualizado
                barraSla.setProgress(progreso); // Actualiza progreso visual de la barra
                barraSla.setProgressTintList(ColorStateList.valueOf(t.getColorSlaBarra())); // Actualiza color de la barra
                lblSlaDerecha.setText("SLA (" + minutosSla + "m) — " + progreso + "%"); // Actualiza porcentaje en texto
                lblSlaDerecha.setTextColor(t.getColorSlaBarra()); // Actualiza color del texto SLA

                if (t.estaSlaVencido() || "Cerrado".equalsIgnoreCase(t.getEstado())) { // Si venció o cerró
                    lblVencido.setVisibility(View.VISIBLE); // Muestra aviso
                    lblVencido.setText("SLA vencido — Estado: " + t.getEstado()); // Actualiza texto de estado
                }

                if ("Cerrado".equalsIgnoreCase(t.getEstado())) { // Si pasó a Cerrado
                    String notaMostrar = obtenerPropiedadSeguro(t, "getNotaCierre"); // Lee nota de cierre
                    if (notaMostrar == null || notaMostrar.trim().isEmpty()) { // Si está vacía
                        notaMostrar = NOTA_CIERRE_EXITOSO; // Usa nota de cierre exitoso
                    }
                    lblNota.setVisibility(View.VISIBLE); // Muestra nota de cierre
                    lblNota.setText("📝 Nota de cierre: " + notaMostrar); // Actualiza texto de la nota
                }

                if (dialogDetalle.isShowing()) { // Si el diálogo sigue abierto
                    handlerSla.postDelayed(this, 1000L); // Programa siguiente actualización en 1 segundo
                }
            } // Fin de run
        }; // Fin de actualizadorSla

        dialogDetalle.setOnShowListener(d -> handlerSla.postDelayed(actualizadorSla, 1000L)); // Inicia refresco al mostrarse el diálogo
        dialogDetalle.setOnDismissListener(d -> { // Al cerrarse el diálogo
            handlerSla.removeCallbacks(actualizadorSla); // Detiene el temporizador para evitar fugas
            cargarDashboardAdmin(adminActual); // Refresca la lista del dashboard
        });

        btnEditarEsquina.setOnClickListener(v -> { // Al tocar Editar
            handlerSla.removeCallbacks(actualizadorSla); // Detiene temporizador del detalle
            dialogDetalle.dismiss(); // Cierra diálogo de detalle
            mostrarFormularioEdicionAdmin(t); // Abre formulario de edición
        });

        dialogDetalle.show(); // Muestra diálogo en pantalla
    } // Fin de mostrarDetalleConSlaYEditar

    private void mostrarFormularioEdicionAdmin(TicketSoporte ticket) { // Abre el formulario de edición para el administrador
        ScrollView scrollEditor = new ScrollView(this); // Contenedor desplazable
        scrollEditor.setBackgroundColor(COLOR_FONDO_MORADO); // Fondo morado oscuro

        LinearLayout form = new LinearLayout(this); // Contenedor vertical del formulario
        form.setOrientation(LinearLayout.VERTICAL); // Disposición vertical
        form.setPadding(45, 24, 45, 24); // Relleno interno
        scrollEditor.addView(form); // Agrega formulario al ScrollView

        TextView lblTituloForm = new TextView(this); // Título del diálogo de edición
        lblTituloForm.setText("Editar Caso (" + ticket.getCodigoTicket() + ")"); // Muestra código del caso
        lblTituloForm.setTextSize(18f); // Tamaño de fuente
        lblTituloForm.setTypeface(null, Typeface.BOLD); // Negrita
        lblTituloForm.setTextColor(COLOR_TEXTO_LILA); // Color lila claro
        lblTituloForm.setPadding(0, 0, 0, 14); // Margen inferior
        form.addView(lblTituloForm); // Agrega título al formulario

        TextView lblCampoTitulo = new TextView(this); // Etiqueta para Título
        lblCampoTitulo.setText("Título:"); // Texto de etiqueta
        lblCampoTitulo.setTextColor(Color.WHITE); // Texto blanco
        lblCampoTitulo.setTypeface(null, Typeface.BOLD); // Negrita
        form.addView(lblCampoTitulo); // Agrega etiqueta

        EditText edtTitulo = new EditText(this); // Campo editable de título
        edtTitulo.setText(ticket.getTitulo()); // Carga título actual
        edtTitulo.setTextColor(Color.WHITE); // Texto blanco
        edtTitulo.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        edtTitulo.setPadding(24, 20, 24, 20); // Relleno interno
        edtTitulo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES); // Capitalización de oración
        form.addView(edtTitulo); // Agrega campo al formulario

        TextView lblEstado = new TextView(this); // Etiqueta para Estado
        lblEstado.setText("Estado:"); // Texto de etiqueta
        lblEstado.setTextColor(Color.WHITE); // Texto blanco
        lblEstado.setTypeface(null, Typeface.BOLD); // Negrita
        lblEstado.setPadding(0, 12, 0, 4); // Espaciado
        form.addView(lblEstado); // Agrega etiqueta

        String[] estados = {"Asignado", "En curso", "Pendiente", "Resuelto", "Cerrado"}; // Estados disponibles
        Spinner spnEstado = new Spinner(this); // Spinner de estados
        spnEstado.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        setSpinnerMorado(spnEstado, estados); // Aplica estilo morado y texto blanco
        for (int i = 0; i < estados.length; i++) { // Selecciona estado actual
            if (estados[i].equalsIgnoreCase(ticket.getEstado())) { // Si coincide
                spnEstado.setSelection(i); // Marca selección
                break; // Sale del bucle
            }
        }
        form.addView(spnEstado); // Agrega Spinner de estado

        TextView lblMotivo = new TextView(this); // Etiqueta para Motivo de Estado
        lblMotivo.setText("Motivo de Estado:"); // Texto de etiqueta
        lblMotivo.setTextColor(Color.WHITE); // Texto blanco
        lblMotivo.setTypeface(null, Typeface.BOLD); // Negrita
        lblMotivo.setPadding(0, 12, 0, 4); // Espaciado
        form.addView(lblMotivo); // Agrega etiqueta

        Spinner spnMotivo = new Spinner(this); // Spinner dinámico de motivos
        spnMotivo.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        form.addView(spnMotivo); // Agrega Spinner de motivo

        LinearLayout layoutNotaCierre = new LinearLayout(this); // Contenedor para nota de cierre
        layoutNotaCierre.setOrientation(LinearLayout.VERTICAL); // Disposición vertical
        layoutNotaCierre.setPadding(0, 12, 0, 4); // Espaciado
        layoutNotaCierre.setVisibility(View.GONE); // Oculto por defecto

        TextView lblNotaCierre = new TextView(this); // Etiqueta de nota de cierre
        lblNotaCierre.setText("Nota de cierre:"); // Texto de etiqueta
        lblNotaCierre.setTextColor(Color.WHITE); // Texto blanco
        lblNotaCierre.setTypeface(null, Typeface.BOLD); // Negrita
        layoutNotaCierre.addView(lblNotaCierre); // Añade etiqueta al contenedor

        EditText edtNotaCierre = new EditText(this); // Campo para nota de cierre
        edtNotaCierre.setHint("Describe cómo se resolvió el caso..."); // Hint de ayuda
        edtNotaCierre.setHintTextColor(COLOR_TEXTO_SUAVE); // Color lila suave para hint
        edtNotaCierre.setText(ticket.getNotaCierre()); // Carga nota existente
        edtNotaCierre.setTextColor(Color.WHITE); // Texto blanco
        edtNotaCierre.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        edtNotaCierre.setPadding(24, 20, 24, 20); // Relleno interno
        edtNotaCierre.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE); // Multilínea
        layoutNotaCierre.addView(edtNotaCierre); // Añade campo al contenedor
        form.addView(layoutNotaCierre); // Añade contenedor de nota al formulario

        TextView lblPrioridad = new TextView(this); // Etiqueta de prioridad con tiempos SLA
        lblPrioridad.setText("Prioridad (Baja: 30m | Regular: 15m | Alta: 5m):"); // Muestra referencia de SLA
        lblPrioridad.setTextColor(Color.WHITE); // Texto blanco
        lblPrioridad.setTypeface(null, Typeface.BOLD); // Negrita
        lblPrioridad.setPadding(0, 12, 0, 4); // Espaciado
        form.addView(lblPrioridad); // Agrega etiqueta

        String[] prioridades = {"Baja", "Regular", "Alta"}; // Opciones de prioridad
        Spinner spnPrioridad = new Spinner(this); // Spinner de prioridad
        spnPrioridad.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        setSpinnerMorado(spnPrioridad, prioridades); // Aplica estilo morado y texto blanco
        for (int i = 0; i < prioridades.length; i++) { // Selecciona prioridad actual
            if (prioridades[i].equalsIgnoreCase(ticket.getPrioridad())) { // Si coincide
                spnPrioridad.setSelection(i); // Marca selección
                break; // Sale del bucle
            }
        }
        spnPrioridad.setEnabled(false); // Bloqueado en este formulario
        form.addView(spnPrioridad); // Agrega Spinner de prioridad

        TextView lblFrente = new TextView(this); // Etiqueta para Frente
        lblFrente.setText("Frente:"); // Texto de etiqueta
        lblFrente.setTextColor(Color.WHITE); // Texto blanco
        lblFrente.setTypeface(null, Typeface.BOLD); // Negrita
        lblFrente.setPadding(0, 12, 0, 4); // Espaciado
        form.addView(lblFrente); // Agrega etiqueta

        String[] frentes = {"Caja de compensación", "Salud"}; // Opciones de frente
        Spinner spnFrente = new Spinner(this); // Spinner de frente
        spnFrente.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        setSpinnerMorado(spnFrente, frentes); // Aplica estilo morado y texto blanco
        for (int i = 0; i < frentes.length; i++) { // Selecciona frente actual
            if (frentes[i].equalsIgnoreCase(ticket.getFrente())) { // Si coincide
                spnFrente.setSelection(i); // Marca selección
                break; // Sale del bucle
            }
        }
        form.addView(spnFrente); // Agrega Spinner de frente

        TextView lblAnalista = new TextView(this); // Etiqueta para Analista asignado
        lblAnalista.setText("Analista asignado:"); // Texto de etiqueta
        lblAnalista.setTextColor(Color.WHITE); // Texto blanco
        lblAnalista.setTypeface(null, Typeface.BOLD); // Negrita
        lblAnalista.setPadding(0, 12, 0, 4); // Espaciado
        form.addView(lblAnalista); // Agrega etiqueta

        String[] analistas = {"Juan Zamora", "Daniela Rojas"}; // Analistas disponibles
        Spinner spnAnalista = new Spinner(this); // Spinner de analista
        spnAnalista.setBackgroundColor(COLOR_INPUT_MORADO); // Fondo morado oscuro
        setSpinnerMorado(spnAnalista, analistas); // Aplica estilo morado y texto blanco
        for (int i = 0; i < analistas.length; i++) { // Selecciona analista actual
            if (analistas[i].equalsIgnoreCase(ticket.getAnalistaAsignado())) { // Si coincide
                spnAnalista.setSelection(i); // Marca selección
                break; // Sale del bucle
            }
        }
        spnAnalista.setEnabled(false); // Bloqueado en este formulario
        form.addView(spnAnalista); // Agrega Spinner de analista

        spnEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // Actualiza motivos y nota según estado
            @Override // Al seleccionar un estado
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // Recibe posición
                String estadoSel = estados[position]; // Estado elegido
                String[] motivos; // Lista de motivos

                if ("Pendiente".equalsIgnoreCase(estadoSel)) { // Motivos para Pendiente
                    motivos = new String[]{
                            "Pendiente por espera de usuario",
                            "Pendiente por espera de aprobación de otra área"
                    };
                } else if ("Resuelto".equalsIgnoreCase(estadoSel) || "Cerrado".equalsIgnoreCase(estadoSel)) { // Motivos para Resuelto/Cerrado
                    motivos = new String[]{
                            "Solución definitiva",
                            "Resolución automática"
                    };
                } else { // Motivos para Asignado/En curso
                    motivos = new String[]{
                            "En atención inicial",
                            "En diagnóstico técnico"
                    };
                }

                setSpinnerMorado(spnMotivo, motivos); // Estiliza motivos en morado y blanco
                for (int i = 0; i < motivos.length; i++) { // Restaura motivo previo si aplica
                    if (motivos[i].equalsIgnoreCase(ticket.getMotivoEstado())) { // Si coincide
                        spnMotivo.setSelection(i); // Selecciona
                        break; // Sale del bucle
                    }
                }

                layoutNotaCierre.setVisibility("Resuelto".equalsIgnoreCase(estadoSel) ? View.VISIBLE : View.GONE); // Muestra campo de nota solo si está Resuelto
            } // Fin de onItemSelected

            @Override // Requerido por AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) { } // Sin acción
        }); // Fin de listener de spnEstado

        LinearLayout filaBotones = new LinearLayout(this); // Fila de botones Guardar y Cancelar
        filaBotones.setOrientation(LinearLayout.HORIZONTAL); // Disposición horizontal
        filaBotones.setPadding(0, 20, 0, 0); // Margen superior

        Button btnGuardar = new Button(this); // Botón Guardar
        btnGuardar.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)); // Ancho proporcional
        btnGuardar.setText("Guardar"); // Texto del botón
        btnGuardar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6D28D9"))); // Color morado intenso
        btnGuardar.setTextColor(Color.WHITE); // Texto blanco

        Button btnCancelar = new Button(this); // Botón Cancelar
        LinearLayout.LayoutParams paramsCancelar = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // Ancho proporcional
        paramsCancelar.setMarginStart(16); // Separación izquierda
        btnCancelar.setLayoutParams(paramsCancelar); // Aplica parámetros
        btnCancelar.setText("Cancelar"); // Texto del botón
        btnCancelar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4C1D95"))); // Color morado oscuro
        btnCancelar.setTextColor(Color.WHITE); // Texto blanco

        filaBotones.addView(btnGuardar); // Agrega Guardar a la fila
        filaBotones.addView(btnCancelar); // Agrega Cancelar a la fila
        form.addView(filaBotones); // Agrega fila de botones al formulario

        AlertDialog dialogEditor = new AlertDialog.Builder(MainActivity.this) // Crea diálogo de edición
                .setView(scrollEditor) // Asigna formulario desplazable
                .setCancelable(false) // Impide cerrar tocando fuera
                .create(); // Construye diálogo

        if (dialogEditor.getWindow() != null) { // Si tiene ventana
            dialogEditor.getWindow().setBackgroundDrawable(new ColorDrawable(COLOR_FONDO_MORADO)); // Fondo morado oscuro
        }

        btnCancelar.setOnClickListener(v -> dialogEditor.dismiss()); // Cierra diálogo al cancelar

        btnGuardar.setOnClickListener(v -> { // Guarda cambios editados del caso
            String nuevoTitulo = edtTitulo.getText().toString().trim(); // Lee nuevo título
            if (nuevoTitulo.isEmpty()) { // Valida que no esté vacío
                Toast.makeText(this, "⚠️ El título no puede quedar vacío", Toast.LENGTH_SHORT).show(); // Informa error
                return; // Detiene guardado
            }

            String estadoElegido = spnEstado.getSelectedItem().toString(); // Lee estado seleccionado
            String motivoElegido = spnMotivo.getSelectedItem().toString(); // Lee motivo seleccionado
            String notaElegida = edtNotaCierre.getText().toString().trim(); // Lee nota de cierre

            if ("Resuelto".equalsIgnoreCase(estadoElegido) && notaElegida.isEmpty()) { // Exige nota si el estado es Resuelto
                Toast.makeText(this, "⚠️ Debes ingresar una nota de cierre al marcar el caso como Resuelto", Toast.LENGTH_LONG).show(); // Informa requisito
                return; // Detiene guardado
            }

            String prioridadElegida = spnPrioridad.getSelectedItem().toString(); // Lee prioridad

            ticket.setTitulo(nuevoTitulo); // Actualiza título
            ticket.setEstado(estadoElegido); // Actualiza estado
            ticket.setMotivoEstado(motivoElegido); // Actualiza motivo
            ticket.setPrioridad(prioridadElegida); // Actualiza prioridad
            asignarSlaSegunPrioridad(ticket, prioridadElegida); // Ajusta minutos de SLA
            ticket.setFrente(spnFrente.getSelectedItem().toString()); // Actualiza frente
            ticket.setAnalistaAsignado(spnAnalista.getSelectedItem().toString()); // Actualiza analista
            if ("Resuelto".equalsIgnoreCase(estadoElegido)) { // Si se guardó como Resuelto
                ticket.setNotaCierre(notaElegida); // Guarda nota de cierre
            }

            procesarCierreAutomaticoPorSlaSiResuelto(ticket); // Aplica cierre automático con nota si ya venció el SLA estando Resuelto
            guardarTicketsPersistentes(); // Guarda cambios en SharedPreferences
            dialogEditor.dismiss(); // Cierra diálogo de edición
            cargarDashboardAdmin(adminActual); // Refresca lista del dashboard admin
            Toast.makeText(this, "✅ Cambios guardados en " + ticket.getCodigoTicket(), Toast.LENGTH_SHORT).show(); // Confirma guardado
        }); // Fin de listener de btnGuardar

        dialogEditor.show(); // Muestra diálogo de edición
    } // Fin de mostrarFormularioEdicionAdmin

    private void limpiarFormulario() { // Restaura el formulario de registro a su estado inicial
        txtTituloCaso.setText(""); // Limpia título
        txtNombreUsuario.setText(""); // Limpia nombre de usuario
        txtCorreoCorporativo.setText(""); // Limpia correo
        txtDocumento.setText(""); // Limpia documento
        txtPlacaEquipo.setText(""); // Limpia placa
        txtSerialEquipo.setText(""); // Limpia serial
        txtDescripcion.setText(""); // Limpia descripción
        rbIncidente.setChecked(true); // Selecciona Incidente por defecto
        layoutCamposGarantia.setVisibility(View.GONE); // Oculta campos de garantía
        spinnerMarca.setSelection(0); // Reinicia Spinner de marca
        spinnerTipoEquipo.setSelection(0); // Reinicia Spinner de tipo
        txtTituloCaso.requestFocus(); // Enfoca campo de título
    } // Fin de limpiarFormulario
} // Fin de MainActivity