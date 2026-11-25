package com.example.zamoracorporation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "zamoracorporation_tickets_prefs";
    private static final String KEY_TICKETS_JSON = "tickets_guardados_json";
    private static final String KEY_COUNTER_INC = "contador_inc";
    private static final String KEY_COUNTER_WO = "contador_wo";

    // Máximo 10 dígitos numéricos tras "INC" (3) o "WO" (2) para mantenerse dentro de 13 caracteres
    private static final int LONGITUD_DIGITOS_INC = 10;
    private static final int LONGITUD_DIGITOS_WO = 10;

    private static final int COLOR_FONDO_MORADO = Color.parseColor("#160A26");
    private static final int COLOR_POPUP_MORADO = Color.parseColor("#1E0E38");
    private static final int COLOR_INPUT_MORADO = Color.parseColor("#24103D");
    private static final int COLOR_TEXTO_LILA = Color.parseColor("#E9D5FF");

    private static final int SLA_BAJA_MINUTOS = 30;
    private static final int SLA_REGULAR_MINUTOS = 15;
    private static final int SLA_ALTA_MINUTOS = 5;
    private static final String NOTA_CIERRE_EXITOSO = "Resuelto con éxito";

    private LinearLayout panelMenu, panelAdminLogin, panelAdminDashboard, panelUsuario, layoutCamposGarantia, layoutUserTicketsLista;
    private EditText txtAdminUser, txtAdminPass;
    private Button btnLoginAdmin, btnVolverMenu1;
    private TextView lblSaludoAdmin, lblResultadoConsulta;
    private ListView listViewAdminTickets, listViewUsuarioTickets;
    private Button btnCerrarSesionAdmin;

    private RadioGroup rgTipoSolicitud;
    private RadioButton rbIncidente, rbOrdenTrabajo, rbGarantia;
    private EditText txtTituloCaso, txtNombreUsuario, txtCorreoCorporativo, txtDocumento, txtPlacaEquipo, txtSerialEquipo, txtDescripcion, txtDocConsulta;
    private Spinner spinnerMarca, spinnerTipoEquipo;
    private Button btnRegistrar, btnLimpiar, btnConsultarUsuario, btnVolverMenu2;

    private String adminActual = "";
    private static RepositorioSoporte repositorio = new RepositorioSoporte();
    private static boolean persistenciaInicializada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargarTicketsPersistentes();

        panelMenu = findViewById(R.id.panelMenu);
        panelAdminLogin = findViewById(R.id.panelAdminLogin);
        panelAdminDashboard = findViewById(R.id.panelAdminDashboard);
        panelUsuario = findViewById(R.id.panelUsuario);
        layoutCamposGarantia = findViewById(R.id.layoutCamposGarantia);

        layoutUserTicketsLista = findViewById(getIdentifierSafe("layoutUserTicketsLista", "id"));
        lblResultadoConsulta = findViewById(getIdentifierSafe("lblResultadoConsulta", "id"));
        listViewUsuarioTickets = findViewById(getIdentifierSafe("listViewUsuarioTickets", "id"));

        txtAdminUser = findViewById(R.id.txtAdminUser);
        txtAdminPass = findViewById(R.id.txtAdminPass);
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
        btnVolverMenu1 = findViewById(R.id.btnVolverMenu1);

        lblSaludoAdmin = findViewById(R.id.lblSaludoAdmin);
        listViewAdminTickets = findViewById(R.id.listViewAdminTickets);
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);

        rgTipoSolicitud = findViewById(R.id.rgTipoSolicitud);
        rbIncidente = findViewById(R.id.rbIncidente);
        rbOrdenTrabajo = findViewById(R.id.rbOrdenTrabajo);
        rbGarantia = findViewById(R.id.rbGarantia);

        txtTituloCaso = findViewById(R.id.txtTituloCaso);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtCorreoCorporativo = findViewById(R.id.txtCorreoCorporativo);
        txtDocumento = findViewById(R.id.txtDocumento);
        txtPlacaEquipo = findViewById(R.id.txtPlacaEquipo);
        txtSerialEquipo = findViewById(R.id.txtSerialEquipo);
        txtDescripcion = findViewById(R.id.txtDescripcion);

        spinnerMarca = findViewById(R.id.spinnerMarca);
        spinnerTipoEquipo = findViewById(R.id.spinnerTipoEquipo);

        int idBtnRegistrar = getIdentifierSafe("btnRegistrar", "id");
        if (idBtnRegistrar == 0) idBtnRegistrar = getIdentifierSafe("btnCrearCaso", "id");
        if (idBtnRegistrar != 0) btnRegistrar = findViewById(idBtnRegistrar);

        int idBtnLimpiar = getIdentifierSafe("btnLimpiar", "id");
        if (idBtnLimpiar != 0) btnLimpiar = findViewById(idBtnLimpiar);

        int idTxtDocConsulta = getIdentifierSafe("txtDocConsulta", "id");
        if (idTxtDocConsulta == 0) idTxtDocConsulta = getIdentifierSafe("txtBuscarDocumento", "id");
        if (idTxtDocConsulta != 0) txtDocConsulta = findViewById(idTxtDocConsulta);

        int idBtnConsultar = getIdentifierSafe("btnConsultarUsuario", "id");
        if (idBtnConsultar == 0) idBtnConsultar = getIdentifierSafe("btnConsultarCasos", "id");
        if (idBtnConsultar != 0) btnConsultarUsuario = findViewById(idBtnConsultar);

        int idBtnVolverMenu2 = getIdentifierSafe("btnVolverMenu2", "id");
        if (idBtnVolverMenu2 != 0) btnVolverMenu2 = findViewById(idBtnVolverMenu2);

        configurarSpinnersGarantia();

        if (rgTipoSolicitud != null && layoutCamposGarantia != null) {
            rgTipoSolicitud.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbGarantia) {
                    layoutCamposGarantia.setVisibility(View.VISIBLE);
                } else {
                    layoutCamposGarantia.setVisibility(View.GONE);
                }
            });
        }

        View btnIrAdmin = findViewById(R.id.btnIrAdmin);
        if (btnIrAdmin != null && panelAdminLogin != null) {
            btnIrAdmin.setOnClickListener(v -> mostrarPanel(panelAdminLogin));
        }

        View btnIrUsuario = findViewById(R.id.btnIrUsuario);
        if (btnIrUsuario != null && panelUsuario != null) {
            btnIrUsuario.setOnClickListener(v -> mostrarPanel(panelUsuario));
        }

        View flechaLogin = findViewById(R.id.btnAtrasLogin);
        if (flechaLogin != null && panelMenu != null) flechaLogin.setOnClickListener(v -> mostrarPanel(panelMenu));

        View flechaDashboard = findViewById(R.id.btnAtrasDashboard);
        if (flechaDashboard != null && panelMenu != null) flechaDashboard.setOnClickListener(v -> mostrarPanel(panelMenu));

        View flechaUsuario = findViewById(R.id.btnAtrasUsuario);
        if (flechaUsuario != null && panelMenu != null) flechaUsuario.setOnClickListener(v -> mostrarPanel(panelMenu));

        if (btnVolverMenu1 != null && panelMenu != null) btnVolverMenu1.setOnClickListener(v -> mostrarPanel(panelMenu));
        if (btnVolverMenu2 != null && panelMenu != null) btnVolverMenu2.setOnClickListener(v -> mostrarPanel(panelMenu));

        if (btnLoginAdmin != null) {
            btnLoginAdmin.setOnClickListener(v -> {
                String user = txtAdminUser != null ? txtAdminUser.getText().toString().trim() : "";
                String pass = txtAdminPass != null ? txtAdminPass.getText().toString().trim() : "";

                if ((user.equalsIgnoreCase("Juan Zamora") || user.equalsIgnoreCase("Daniela Rojas")) && pass.equals("1234567890")) {
                    adminActual = user.equalsIgnoreCase("Juan Zamora") ? "Juan Zamora" : "Daniela Rojas";
                    String nombreFormateado = adminActual.equalsIgnoreCase("Juan Zamora") ? "Juan" : "Daniela";
                    if (lblSaludoAdmin != null) {
                        lblSaludoAdmin.setText("Bienvenido, " + nombreFormateado);
                    }

                    cargarDashboardAdmin(adminActual);
                    if (panelAdminDashboard != null) mostrarPanel(panelAdminDashboard);
                    if (txtAdminUser != null) txtAdminUser.setText("");
                    if (txtAdminPass != null) txtAdminPass.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "⚠️ Credenciales incorrectas. Verifique usuario y contraseña.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnCerrarSesionAdmin != null) {
            btnCerrarSesionAdmin.setOnClickListener(v -> {
                adminActual = "";
                if (panelMenu != null) mostrarPanel(panelMenu);
            });
        }

        if (btnRegistrar != null) btnRegistrar.setOnClickListener(v -> registrarNuevoCaso());
        if (btnLimpiar != null) btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        if (btnConsultarUsuario != null) btnConsultarUsuario.setOnClickListener(v -> consultarTicketsUsuario());
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (TicketSoporte t : obtenerTodosLosTicketsActuales()) {
            procesarCierreAutomaticoPorSlaSiResuelto(t);
        }
        if (panelAdminDashboard != null && panelAdminDashboard.getVisibility() == View.VISIBLE && !adminActual.isEmpty()) {
            cargarDashboardAdmin(adminActual);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarTicketsPersistentes();
    }

    private int getIdentifierSafe(String name, String defType) {
        return getResources().getIdentifier(name, defType, getPackageName());
    }

    private ArrayList<TicketSoporte> obtenerTodosLosTicketsActuales() {
        ArrayList<TicketSoporte> lista = new ArrayList<>();
        try {
            Method m = repositorio.getClass().getMethod("obtenerTodosLosTickets");
            Object res = m.invoke(repositorio);
            if (res instanceof ArrayList) {
                for (Object item : (ArrayList<?>) res) {
                    if (item instanceof TicketSoporte) lista.add((TicketSoporte) item);
                }
                return lista;
            }
        } catch (Exception ignored) { }

        lista.addAll(repositorio.obtenerTicketsPorAnalista("Juan Zamora"));
        for (TicketSoporte t : repositorio.obtenerTicketsPorAnalista("Daniela Rojas")) {
            if (!contieneCodigo(lista, t.getCodigoTicket())) {
                lista.add(t);
            }
        }
        return lista;
    }

    private boolean contieneCodigo(ArrayList<TicketSoporte> lista, String codigo) {
        if (codigo == null) return false;
        for (TicketSoporte t : lista) {
            if (codigo.equalsIgnoreCase(t.getCodigoTicket())) return true;
        }
        return false;
    }

    private String generarSiguienteCodigoConsecutivo(String prefijo) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean esInc = "INC".equalsIgnoreCase(prefijo);
        String key = esInc ? KEY_COUNTER_INC : KEY_COUNTER_WO;
        int digitos = esInc ? LONGITUD_DIGITOS_INC : LONGITUD_DIGITOS_WO;

        long actual = prefs.getLong(key, 0L);
        for (TicketSoporte t : obtenerTodosLosTicketsActuales()) {
            String cod = t.getCodigoTicket() != null ? t.getCodigoTicket().trim().toUpperCase(Locale.ROOT) : "";
            if (esInc && cod.startsWith("INC")) {
                long n = extraerNumero(cod.substring(3));
                if (n > actual) actual = n;
            } else if (!esInc && cod.startsWith("WO")) {
                long n = extraerNumero(cod.substring(2));
                if (n > actual) actual = n;
            }
        }

        long siguiente = actual + 1L;
        prefs.edit().putLong(key, siguiente).apply();
        return prefijo.toUpperCase(Locale.ROOT) + String.format(Locale.ROOT, "%0" + digitos + "d", siguiente);
    }

    private void limpiarFormulario() {
        if (txtTituloCaso != null) txtTituloCaso.setText("");
        if (txtNombreUsuario != null) txtNombreUsuario.setText("");
        if (txtCorreoCorporativo != null) txtCorreoCorporativo.setText("");
        if (txtDocumento != null) txtDocumento.setText("");
        if (txtPlacaEquipo != null) txtPlacaEquipo.setText("");
        if (txtSerialEquipo != null) txtSerialEquipo.setText("");
        if (txtDescripcion != null) txtDescripcion.setText("");
        if (spinnerMarca != null && spinnerMarca.getCount() > 0) spinnerMarca.setSelection(0);
        if (spinnerTipoEquipo != null && spinnerTipoEquipo.getCount() > 0) spinnerTipoEquipo.setSelection(0);
        if (rbIncidente != null) rbIncidente.setChecked(true);
    }

    private String obtenerPropiedadSeguro(Object objeto, String... nombresMetodos) {
        if (objeto == null || nombresMetodos == null) return "";

        for (String nombre : nombresMetodos) {
            if (nombre == null || nombre.isEmpty()) continue;
            try {
                Method m = objeto.getClass().getMethod(nombre);
                Object val = m.invoke(objeto);
                if (val != null) {
                    return String.valueOf(val);
                }
            } catch (Exception ignored) { }
        }

        Class<?> clazz = objeto.getClass();
        while (clazz != null) {
            for (String nombre : nombresMetodos) {
                String campoPosible = nombre.startsWith("get") && nombre.length() > 3
                        ? Character.toLowerCase(nombre.charAt(3)) + nombre.substring(4)
                        : nombre;
                try {
                    Field field = clazz.getDeclaredField(campoPosible);
                    field.setAccessible(true);
                    Object val = field.get(objeto);
                    if (val != null) {
                        return String.valueOf(val);
                    }
                } catch (Exception ignored) { }
            }
            clazz = clazz.getSuperclass();
        }

        return "";
    }

    private long obtenerFechaCreacionSeguro(TicketSoporte ticket) {
        if (ticket == null) return System.currentTimeMillis();
        String raw = obtenerPropiedadSeguro(ticket, "getFechaCreacionMillis", "getFechaCreacion", "getTimestampCreacion", "getHoraCreacionMillis");
        try {
            long val = Long.parseLong(raw.trim());
            if (val > 0L) return val;
        } catch (Exception ignored) { }
        return System.currentTimeMillis();
    }

    private void asignarFechaCreacionSeguro(TicketSoporte ticket, long fechaCreacionMillis) {
        if (ticket == null || fechaCreacionMillis <= 0L) return;

        String[] setters = {
                "setFechaCreacionMillis",
                "setFechaCreacion",
                "setTimestampCreacion",
                "setHoraCreacionMillis"
        };

        for (String nombre : setters) {
            try {
                Method m = ticket.getClass().getMethod(nombre, long.class);
                m.invoke(ticket, fechaCreacionMillis);
                return;
            } catch (Exception ignored) { }
        }

        Class<?> clazz = ticket.getClass();
        String[] campos = {"fechaCreacionMillis", "fechaCreacion", "timestampCreacion", "horaCreacionMillis"};
        while (clazz != null) {
            for (String campo : campos) {
                try {
                    Field f = clazz.getDeclaredField(campo);
                    f.setAccessible(true);
                    f.setLong(ticket, fechaCreacionMillis);
                    return;
                } catch (Exception ignored) { }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private void configurarSpinnersGarantia() {
        String[] marcas = {"Lenovo", "HP", "DELL"};
        setSpinnerMorado(spinnerMarca, marcas);

        String[] tipos = {"Portátil", "DESKTOP"};
        setSpinnerMorado(spinnerTipoEquipo, tipos);
    }

    private void setSpinnerMorado(Spinner spinner, String[] items) {
        if (spinner == null) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                items
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                if (tv != null) {
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(15f);
                    tv.setTypeface(null, Typeface.BOLD);
                } else if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.WHITE);
                    ((TextView) view).setTextSize(15f);
                    ((TextView) view).setTypeface(null, Typeface.BOLD);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(COLOR_POPUP_MORADO);
                TextView tv = view.findViewById(android.R.id.text1);
                if (tv != null) {
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(COLOR_POPUP_MORADO);
                    tv.setTextSize(15f);
                } else if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.WHITE);
                    ((TextView) view).setBackgroundColor(COLOR_POPUP_MORADO);
                    ((TextView) view).setTextSize(15f);
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void mostrarPanel(LinearLayout panelActivo) {
        if (panelMenu != null) panelMenu.setVisibility(View.GONE);
        if (panelAdminLogin != null) panelAdminLogin.setVisibility(View.GONE);
        if (panelAdminDashboard != null) panelAdminDashboard.setVisibility(View.GONE);
        if (panelUsuario != null) panelUsuario.setVisibility(View.GONE);

        if (panelActivo != null) panelActivo.setVisibility(View.VISIBLE);
    }

    private void cargarDashboardAdmin(String nombreAdmin) {
        if (listViewAdminTickets == null) return;
        ArrayList<TicketSoporte> casosAdmin = repositorio.obtenerTicketsPorAnalista(nombreAdmin);

        ArrayAdapter<TicketSoporte> adapterAdmin = new ArrayAdapter<TicketSoporte>(this, R.layout.item_ticket_admin, casosAdmin) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_ticket_admin, parent, false);
                }

                view.setBackgroundColor(COLOR_INPUT_MORADO);

                TicketSoporte ticket = getItem(position);
                if (ticket != null) {
                    procesarCierreAutomaticoPorSlaSiResuelto(ticket);

                    TextView txtPrio = view.findViewById(R.id.txtPrioridadBadge);
                    TextView txtCod = view.findViewById(R.id.txtCodigoCaso);
                    TextView txtEst = view.findViewById(R.id.txtEstadoPill);

                    if (txtPrio != null) {
                        txtPrio.setText(ticket.getPrioridad());
                        txtPrio.setTextColor(ticket.getColorPrioridad());
                    }
                    if (txtCod != null) {
                        txtCod.setText(ticket.getCodigoTicket());
                        txtCod.setTextColor(Color.WHITE);
                    }
                    if (txtEst != null) {
                        if ("Cerrado".equalsIgnoreCase(ticket.getEstado())) {
                            txtEst.setText("Cerrado");
                            txtEst.setTextColor(Color.parseColor("#FCA5A5"));
                        } else {
                            txtEst.setText(ticket.getEstado());
                            txtEst.setTextColor(COLOR_TEXTO_LILA);
                        }
                    }
                }
                return view;
            }
        };

        listViewAdminTickets.setAdapter(adapterAdmin);

        listViewAdminTickets.setOnItemClickListener((parent, view, position, id) -> {
            TicketSoporte t = casosAdmin.get(position);
            procesarCierreAutomaticoPorSlaSiResuelto(t);
            mostrarDetalleConSlaYEditar(t);
        });
    }

    private void procesarCierreAutomaticoPorSlaSiResuelto(TicketSoporte ticket) {
        if (ticket == null) return;

        ticket.verificarYActualizarVencimientoSla();

        String estadoActual = obtenerPropiedadSeguro(ticket, "getEstado");
        String notaActual = obtenerPropiedadSeguro(ticket, "getNotaCierre");

        if (ticket.estaSlaVencido() && "Resuelto".equalsIgnoreCase(estadoActual)) {
            ticket.setEstado("Cerrado");
            ticket.setMotivoEstado("Resolución automática");

            if (notaActual == null || notaActual.trim().isEmpty()) {
                ticket.setNotaCierre(NOTA_CIERRE_EXITOSO);
            }

            guardarTicketsPersistentes();
        }
    }

    private void guardarTicketsPersistentes() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        long maxInc = prefs.getLong(KEY_COUNTER_INC, 0L);
        long maxWo = prefs.getLong(KEY_COUNTER_WO, 0L);

        for (TicketSoporte t : obtenerTodosLosTicketsActuales()) {
            if (t == null) continue;
            try {
                JSONObject obj = new JSONObject();
                String codigoVal = safeStr(t.getCodigoTicket());
                String prioridadVal = safeStr(t.getPrioridad());
                long fechaCreacionVal = obtenerFechaCreacionSeguro(t);

                obj.put("codigoTicket", codigoVal);
                obj.put("titulo", safeStr(t.getTitulo()));
                obj.put("usuario", safeStr(t.getNombreUsuario()));
                obj.put("correo", safeStr(t.getCorreoCorporativo()));
                obj.put("documento", safeStr(t.getDocumentoIdentidad()));
                obj.put("placa", safeStr(t.getPlacaEquipo()));
                obj.put("descripcion", safeStr(t.getDescripcionHechos()));
                obj.put("estado", safeStr(t.getEstado()));
                obj.put("motivoEstado", safeStr(t.getMotivoEstado()));
                obj.put("prioridad", prioridadVal);
                obj.put("frente", safeStr(t.getFrente()));
                obj.put("analistaAsignado", safeStr(t.getAnalistaAsignado()));
                obj.put("notaCierre", safeStr(t.getNotaCierre()));
                obj.put("fechaCreacionMillis", fechaCreacionVal);
                obj.put("slaMinutos", obtenerMinutosSlaPorPrioridad(prioridadVal));
                obj.put("tipoClase", t.getClass().getSimpleName());

                if (t instanceof TicketGarantia) {
                    String marcaVal = obtenerPropiedadSeguro(t, "getMarcaEquipo", "getMarca", "getBrand");
                    String tipoEquipoVal = obtenerPropiedadSeguro(t, "getTipoEquipo", "getFormatoEquipo", "getFormato");
                    String serialVal = obtenerPropiedadSeguro(t, "getSerialEquipo", "getSerial", "getNumeroSerie");

                    obj.put("marca", safeStr(marcaVal));
                    obj.put("tipoEquipo", safeStr(tipoEquipoVal));
                    obj.put("serial", safeStr(serialVal));
                }

                array.put(obj);

                String cod = codigoVal.trim().toUpperCase(Locale.ROOT);
                if (cod.startsWith("INC")) {
                    long num = extraerNumero(cod.substring(3));
                    if (num > maxInc) maxInc = num;
                } else if (cod.startsWith("WO")) {
                    long num = extraerNumero(cod.substring(2));
                    if (num > maxWo) maxWo = num;
                }
            } catch (JSONException ignored) { }
        }

        prefs.edit()
                .putString(KEY_TICKETS_JSON, array.toString())
                .putLong(KEY_COUNTER_INC, maxInc)
                .putLong(KEY_COUNTER_WO, maxWo)
                .apply();
    }

    private void cargarTicketsPersistentes() {
        if (persistenciaInicializada) return;
        persistenciaInicializada = true;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String jsonStr = prefs.getString(KEY_TICKETS_JSON, null);
        if (jsonStr == null || jsonStr.isEmpty()) return;

        try {
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                if (obj == null) continue;

                String codigo = obj.optString("codigoTicket", "");
                if (codigo.isEmpty() || contieneCodigo(obtenerTodosLosTicketsActuales(), codigo)) {
                    continue;
                }

                String titulo = obj.optString("titulo", "");
                String usuario = obj.optString("usuario", "");
                String correo = obj.optString("correo", "");
                String documento = obj.optString("documento", "");
                String placa = obj.optString("placa", "");
                String descripcion = obj.optString("descripcion", "");
                String estado = obj.optString("estado", "Asignado");
                String motivo = obj.optString("motivoEstado", "En atención inicial");
                String prioridad = obj.optString("prioridad", "Regular");
                String frente = obj.optString("frente", "Caja de compensación");
                String analista = obj.optString("analistaAsignado", "Juan Zamora");
                String notaCierre = obj.optString("notaCierre", "");
                String tipoClase = obj.optString("tipoClase", "");
                int slaMin = obj.optInt("slaMinutos", obtenerMinutosSlaPorPrioridad(prioridad));
                long fechaCreacion = obj.optLong("fechaCreacionMillis", System.currentTimeMillis());

                try {
                    TicketSoporte ticket;
                    if ("TicketGarantia".equals(tipoClase)) {
                        String marca = obj.optString("marca", "Lenovo");
                        String tipoEquipo = obj.optString("tipoEquipo", "Portátil");
                        String serial = obj.optString("serial", "");
                        ticket = new TicketGarantia(codigo, titulo, usuario, correo, documento, placa, marca, tipoEquipo, serial, descripcion, analista);
                    } else if ("TicketOrdenTrabajo".equals(tipoClase) || codigo.toUpperCase(Locale.ROOT).startsWith("WO")) {
                        ticket = new TicketOrdenTrabajo(codigo, titulo, usuario, correo, documento, placa, descripcion, slaMin, analista);
                    } else {
                        ticket = new TicketIncidente(codigo, titulo, usuario, correo, documento, placa, descripcion, slaMin, analista);
                    }

                    asignarFechaCreacionSeguro(ticket, fechaCreacion);
                    ticket.setEstado(estado);
                    ticket.setMotivoEstado(motivo);
                    ticket.setPrioridad(prioridad);
                    ticket.setFrente(frente);
                    ticket.setAnalistaAsignado(analista);
                    if (!notaCierre.isEmpty()) {
                        ticket.setNotaCierre(notaCierre);
                    }
                    asignarSlaSegunPrioridad(ticket, prioridad);
                    procesarCierreAutomaticoPorSlaSiResuelto(ticket);

                    repositorio.registrarTicket(ticket);
                } catch (IllegalArgumentException ignored) {
                    // Omite registros antiguos que no cumplen las reglas vigentes de validación
                }
            }
        } catch (JSONException ignored) { }
    }

    private long extraerNumero(String texto) {
        String digits = texto == null ? "" : texto.replaceAll("\\D+", "");
        if (digits.isEmpty()) return 0L;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }

    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinTildes.toLowerCase(Locale.ROOT).trim();
    }

    private String calcularPrioridadAutomatica(String titulo, String descripcion, boolean esGarantia, boolean esOrdenTrabajo) {
        String texto = normalizarTexto(titulo + " " + descripcion);

        boolean mencionaEspacioAmplio = texto.contains("area")
                || texto.contains("departamento")
                || texto.contains("sede")
                || texto.contains("piso")
                || texto.contains("edificio")
                || texto.contains("vivienda");

        boolean mencionaCaidaOSinServicio = texto.contains("se cayo")
                || texto.contains("se callo")
                || texto.contains("caida")
                || texto.contains("sin red")
                || texto.contains("sin internet")
                || texto.contains("sin servicio")
                || texto.contains("no hay red")
                || texto.contains("no hay internet")
                || texto.contains("caido");

        boolean mencionaTodoElEspacio = texto.contains("todo el")
                || texto.contains("toda el")
                || texto.contains("toda la")
                || texto.contains("en todo")
                || texto.contains("en toda")
                || texto.contains("general")
                || texto.contains("masiva");

        boolean esAlta = (mencionaEspacioAmplio && mencionaCaidaOSinServicio && mencionaTodoElEspacio)
                || (texto.contains("red") && mencionaCaidaOSinServicio && mencionaTodoElEspacio)
                || texto.contains("caida general")
                || texto.contains("caida masiva")
                || texto.contains("se cayo todo")
                || texto.contains("se callo todo")
                || texto.contains("toda la sede")
                || texto.contains("todo el edificio")
                || texto.contains("sin internet en toda")
                || texto.contains("urgencias")
                || texto.contains("cirugia")
                || texto.contains("hospitalizacion")
                || texto.contains("servidor caido")
                || texto.contains("sistema caido")
                || texto.contains("ransomware")
                || texto.contains("hackeo")
                || texto.contains("fuga de datos")
                || texto.contains("no atiende pacientes")
                || texto.contains("critico");

        if (esAlta) {
            return "Alta";
        }

        boolean esRegular = texto.contains("se le cayo la red al jefe")
                || texto.contains("se cayo la red al jefe")
                || texto.contains("se callo la red al jefe")
                || texto.contains("sin red el jefe")
                || texto.contains("sin internet el jefe")
                || ((texto.contains("red") || texto.contains("internet"))
                && (texto.contains("jefe") || texto.contains("gerente") || texto.contains("director") || texto.contains("coordinador") || texto.contains("lider")))
                || texto.contains("no puede facturar")
                || texto.contains("no carga el sistema")
                || texto.contains("bloqueo de cuenta")
                || texto.contains("correo bloqueado")
                || texto.contains("impresora de red")
                || texto.contains("falla en el area")
                || texto.contains("falla en el departamento")
                || esGarantia;

        if (esRegular) {
            return "Regular";
        }

        boolean esBaja = texto.contains("consulta")
                || texto.contains("informacion")
                || texto.contains("cambio de clave")
                || texto.contains("restablecer contrasena")
                || texto.contains("solicitud de acceso")
                || texto.contains("instalar programa")
                || texto.contains("configurar firma")
                || texto.contains("mouse")
                || texto.contains("teclado")
                || esOrdenTrabajo;

        if (esBaja) {
            return "Baja";
        }

        return "Regular";
    }

    private int obtenerMinutosSlaPorPrioridad(String prioridad) {
        if ("Alta".equalsIgnoreCase(prioridad)) {
            return SLA_ALTA_MINUTOS;
        } else if ("Baja".equalsIgnoreCase(prioridad)) {
            return SLA_BAJA_MINUTOS;
        } else {
            return SLA_REGULAR_MINUTOS;
        }
    }

    private void asignarSlaSegunPrioridad(TicketSoporte ticket, String prioridad) {
        if (ticket == null) return;
        int minutosSla = obtenerMinutosSlaPorPrioridad(prioridad);

        String[] posiblesSetters = {
                "setSlaMinutos",
                "setTiempoSlaMinutos",
                "setMinutosSla",
                "setSla"
        };

        for (String nombreMetodo : posiblesSetters) {
            try {
                Method mInt = ticket.getClass().getMethod(nombreMetodo, int.class);
                mInt.invoke(ticket, minutosSla);
                return;
            } catch (Exception ignored) { }

            try {
                Method mDouble = ticket.getClass().getMethod(nombreMetodo, double.class);
                mDouble.invoke(ticket, (double) minutosSla);
                return;
            } catch (Exception ignored) { }
        }
    }

    private void registrarNuevoCaso() {
        String titulo = txtTituloCaso != null ? txtTituloCaso.getText().toString().trim() : "";
        String usuario = txtNombreUsuario != null ? txtNombreUsuario.getText().toString().trim() : "";
        String correo = txtCorreoCorporativo != null ? txtCorreoCorporativo.getText().toString().trim().toLowerCase() : "";
        String documento = txtDocumento != null ? txtDocumento.getText().toString().trim() : "";
        String placa = txtPlacaEquipo != null ? txtPlacaEquipo.getText().toString().trim() : "";
        String descripcion = txtDescripcion != null ? txtDescripcion.getText().toString().trim() : "";
        boolean esGarantia = rbGarantia != null && rbGarantia.isChecked();
        boolean esOrdenTrabajo = rbOrdenTrabajo != null && rbOrdenTrabajo.isChecked();

        if (titulo.isEmpty() || usuario.isEmpty() || correo.isEmpty() || documento.isEmpty() || placa.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Todos los campos principales son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!correo.matches("^[a-zA-Z0-9._-]+@(compensar\\.com|consorciosalud\\.com)$")) {
            Toast.makeText(this, "⚠️ Correo inválido. Debe terminar en @compensar.com o @consorciosalud.com", Toast.LENGTH_LONG).show();
            return;
        }

        if (documento.length() > 10) {
            Toast.makeText(this, "⚠️ El ID / Documento no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (placa.length() > 13) {
            Toast.makeText(this, "⚠️ La placa del equipo no puede superar los 13 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        String serial = "";
        if (esGarantia) {
            serial = txtSerialEquipo != null ? txtSerialEquipo.getText().toString().trim() : "";
            if (serial.isEmpty()) {
                Toast.makeText(this, "⚠️ Para casos de garantía el Serial del equipo es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String prefijo = (esGarantia || (rbIncidente != null && rbIncidente.isChecked())) ? "INC" : "WO";
        String codigo = generarSiguienteCodigoConsecutivo(prefijo);

        String prioridadCalculada = calcularPrioridadAutomatica(titulo, descripcion, esGarantia, esOrdenTrabajo);
        int minutosSla = obtenerMinutosSlaPorPrioridad(prioridadCalculada);

        String analistaAsignado = repositorio.asignarAnalistaAutomatico();

        try {
            TicketSoporte nuevoTicket;
            if (esGarantia) {
                String marcaSeleccionada = (spinnerMarca != null && spinnerMarca.getSelectedItem() != null)
                        ? spinnerMarca.getSelectedItem().toString() : "Lenovo";
                String tipoSeleccionado = (spinnerTipoEquipo != null && spinnerTipoEquipo.getSelectedItem() != null)
                        ? spinnerTipoEquipo.getSelectedItem().toString() : "Portátil";
                nuevoTicket = new TicketGarantia(codigo, titulo, usuario, correo, documento, placa,
                        marcaSeleccionada, tipoSeleccionado, serial, descripcion, analistaAsignado);
            } else if (rbIncidente != null && rbIncidente.isChecked()) {
                nuevoTicket = new TicketIncidente(codigo, titulo, usuario, correo, documento, placa, descripcion, minutosSla, analistaAsignado);
            } else {
                nuevoTicket = new TicketOrdenTrabajo(codigo, titulo, usuario, correo, documento, placa, descripcion, minutosSla, analistaAsignado);
            }

            asignarFechaCreacionSeguro(nuevoTicket, System.currentTimeMillis());
            nuevoTicket.setPrioridad(prioridadCalculada);
            asignarSlaSegunPrioridad(nuevoTicket, prioridadCalculada);

            repositorio.registrarTicket(nuevoTicket);
            guardarTicketsPersistentes();

            Toast.makeText(
                    this,
                    "✅ Caso " + codigo + " guardado | Prioridad: " + prioridadCalculada + " (SLA " + minutosSla + " min)",
                    Toast.LENGTH_LONG
            ).show();
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "⚠️ " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void consultarTicketsUsuario() {
        if (txtDocConsulta == null) return;

        String documento = txtDocConsulta.getText().toString().trim();
        if (layoutUserTicketsLista != null) {
            layoutUserTicketsLista.removeAllViews();
        }

        if (documento.isEmpty()) {
            if (lblResultadoConsulta != null) {
                lblResultadoConsulta.setVisibility(View.VISIBLE);
                lblResultadoConsulta.setTextColor(Color.WHITE);
                lblResultadoConsulta.setText("⚠️ Ingrese su ID / Documento para buscar.");
            }
            Toast.makeText(this, "⚠️ Ingrese su documento / ID para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (documento.length() > 10) {
            if (lblResultadoConsulta != null) {
                lblResultadoConsulta.setVisibility(View.VISIBLE);
                lblResultadoConsulta.setTextColor(Color.WHITE);
                lblResultadoConsulta.setText("⚠️ El ID / Documento no puede superar los 10 dígitos.");
            }
            Toast.makeText(this, "⚠️ El ID / Documento no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<TicketSoporte> misTickets = repositorio.obtenerTicketsPorUsuario(documento);
        if (lblResultadoConsulta != null) {
            lblResultadoConsulta.setVisibility(View.VISIBLE);
            lblResultadoConsulta.setTextColor(Color.WHITE);
        }

        if (misTickets.isEmpty()) {
            if (lblResultadoConsulta != null) {
                lblResultadoConsulta.setText("No se encontraron tickets con el ID: " + documento);
            }
            if (listViewUsuarioTickets != null) {
                listViewUsuarioTickets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
            }
            Toast.makeText(this, "No se encontraron tickets con este ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lblResultadoConsulta != null) {
            lblResultadoConsulta.setText("Tickets encontrados (" + misTickets.size() + ") — toca uno para ver detalle:");
        }

        if (layoutUserTicketsLista != null) {
            for (TicketSoporte t : misTickets) {
                procesarCierreAutomaticoPorSlaSiResuelto(t);
                TextView itemTicket = new TextView(this);
                itemTicket.setText("🎫 " + t.toUsuarioString());
                itemTicket.setTextSize(14f);
                itemTicket.setTextColor(Color.WHITE);
                itemTicket.setBackgroundColor(COLOR_INPUT_MORADO);
                itemTicket.setPadding(24, 22, 24, 22);
                itemTicket.setClickable(true);
                itemTicket.setFocusable(true);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 12);
                itemTicket.setLayoutParams(params);

                itemTicket.setOnClickListener(v -> mostrarDialogoSoloLecturaUsuario(t));
                layoutUserTicketsLista.addView(itemTicket);
            }
        } else if (listViewUsuarioTickets != null) {
            ArrayList<String> resumenes = new ArrayList<>();
            for (TicketSoporte t : misTickets) {
                procesarCierreAutomaticoPorSlaSiResuelto(t);
                resumenes.add("🎫 " + t.toUsuarioString());
            }
            ArrayAdapter<String> adapterUser = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resumenes) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    view.setBackgroundColor(COLOR_INPUT_MORADO);
                    TextView tv = view.findViewById(android.R.id.text1);
                    if (tv != null) tv.setTextColor(Color.WHITE);
                    return view;
                }
            };
            listViewUsuarioTickets.setAdapter(adapterUser);
            listViewUsuarioTickets.setOnItemClickListener((parent, view, position, id) ->
                    mostrarDialogoSoloLecturaUsuario(misTickets.get(position))
            );
        }
    }

    private void mostrarDialogoSoloLecturaUsuario(TicketSoporte t) {
        String infoUsuario = "🎫 Caso: " + t.getCodigoTicket() + "\n" +
                "📌 Título: " + t.getTitulo() + "\n" +
                "📊 Estado: " + t.getEstado() + "\n" +
                "🗂️ Motivo de Estado: " + t.getMotivoEstado() + "\n" +
                "🏢 Frente: " + t.getFrente() + "\n" +
                "👨‍💻 Analista: " + t.getAnalistaAsignado() + "\n" +
                "📝 Descripción: " + t.getDescripcionHechos();

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Estado de tu Caso")
                .setMessage(infoUsuario)
                .setPositiveButton("Cerrar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(COLOR_FONDO_MORADO));
        }
        dialog.show();
    }

    private void mostrarDetalleConSlaYEditar(TicketSoporte t) {
        procesarCierreAutomaticoPorSlaSiResuelto(t);

        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setBackgroundColor(COLOR_FONDO_MORADO);
        contenedor.setPadding(45, 24, 45, 16);

        LinearLayout filaSuperior = new LinearLayout(this);
        filaSuperior.setOrientation(LinearLayout.HORIZONTAL);
        filaSuperior.setGravity(Gravity.CENTER_VERTICAL);

        TextView lblCasoHeader = new TextView(this);
        lblCasoHeader.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        lblCasoHeader.setText(t.getCodigoTicket());
        lblCasoHeader.setTextSize(17f);
        lblCasoHeader.setTypeface(null, Typeface.BOLD);
        lblCasoHeader.setTextColor(Color.WHITE);

        Button btnEditarEsquina = new Button(this);
        btnEditarEsquina.setText("Editar");
        btnEditarEsquina.setTextSize(13f);
        btnEditarEsquina.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6D28D9")));
        btnEditarEsquina.setTextColor(Color.WHITE);

        filaSuperior.addView(lblCasoHeader);
        filaSuperior.addView(btnEditarEsquina);
        contenedor.addView(filaSuperior);

        TextView txtDetalle = new TextView(this);
        int minutosSla = obtenerMinutosSlaPorPrioridad(t.getPrioridad());
        txtDetalle.setText(t.obtenerDetalleCompleto() + "\n⏱️ SLA asignado: " + minutosSla + " minutos");
        txtDetalle.setTextColor(COLOR_TEXTO_LILA);
        txtDetalle.setTextSize(14f);
        txtDetalle.setPadding(0, 14, 0, 14);
        contenedor.addView(txtDetalle);

        LinearLayout filaSla = new LinearLayout(this);
        filaSla.setOrientation(LinearLayout.HORIZONTAL);
        filaSla.setGravity(Gravity.CENTER_VERTICAL);

        ProgressBar barraSla = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams paramsBarra = new LinearLayout.LayoutParams(0, 28, 1f);
        paramsBarra.setMarginEnd(12);
        barraSla.setLayoutParams(paramsBarra);
        barraSla.setMax(100);
        barraSla.setProgress(t.getPorcentajeSlaTranscurrido());
        barraSla.setProgressTintList(ColorStateList.valueOf(t.getColorSlaBarra()));

        TextView lblPorcentaje = new TextView(this);
        lblPorcentaje.setText(t.getPorcentajeSlaTranscurrido() + "%");
        lblPorcentaje.setTextColor(Color.WHITE);
        lblPorcentaje.setTextSize(14f);
        lblPorcentaje.setTypeface(null, Typeface.BOLD);

        filaSla.addView(barraSla);
        filaSla.addView(lblPorcentaje);
        contenedor.addView(filaSla);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(contenedor)
                .setPositiveButton("Cerrar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(COLOR_FONDO_MORADO));
        }

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                if (!dialog.isShowing()) return;
                procesarCierreAutomaticoPorSlaSiResuelto(t);
                int pct = t.getPorcentajeSlaTranscurrido();
                barraSla.setProgress(pct);
                barraSla.setProgressTintList(ColorStateList.valueOf(t.getColorSlaBarra()));
                lblPorcentaje.setText(pct + "%");
                txtDetalle.setText(t.obtenerDetalleCompleto() + "\n⏱️ SLA asignado: " + obtenerMinutosSlaPorPrioridad(t.getPrioridad()) + " minutos");
                handler.postDelayed(this, 1000L);
            }
        };

        dialog.setOnShowListener(d -> handler.postDelayed(updater, 1000L));
        dialog.setOnDismissListener(d -> {
            handler.removeCallbacks(updater);
            if (!adminActual.isEmpty()) cargarDashboardAdmin(adminActual);
        });

        btnEditarEsquina.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoEditarTicket(t);
        });

        dialog.show();
    }

    private void mostrarDialogoEditarTicket(TicketSoporte t) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(COLOR_FONDO_MORADO);
        layout.setPadding(45, 24, 45, 16);

        TextView lblEstado = new TextView(this);
        lblEstado.setText("Estado");
        lblEstado.setTextColor(Color.WHITE);
        layout.addView(lblEstado);

        Spinner spEstado = new Spinner(this);
        String[] estados = {"Asignado", "Pendiente", "Resuelto", "Cerrado"};
        setSpinnerMorado(spEstado, estados);
        for (int i = 0; i < estados.length; i++) {
            if (estados[i].equalsIgnoreCase(t.getEstado())) {
                spEstado.setSelection(i);
                break;
            }
        }
        layout.addView(spEstado);

        TextView lblMotivo = new TextView(this);
        lblMotivo.setText("Motivo de estado");
        lblMotivo.setTextColor(Color.WHITE);
        lblMotivo.setPadding(0, 12, 0, 0);
        layout.addView(lblMotivo);

        EditText edtMotivo = new EditText(this);
        edtMotivo.setText(t.getMotivoEstado());
        edtMotivo.setTextColor(Color.WHITE);
        edtMotivo.setBackgroundColor(COLOR_INPUT_MORADO);
        layout.addView(edtMotivo);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Editar " + t.getCodigoTicket())
                .setView(layout)
                .setPositiveButton("Guardar", (d, w) -> {
                    try {
                        t.setEstado(spEstado.getSelectedItem().toString());
                        t.setMotivoEstado(edtMotivo.getText().toString().trim());
                        guardarTicketsPersistentes();
                        if (!adminActual.isEmpty()) cargarDashboardAdmin(adminActual);
                        Toast.makeText(this, "✅ Ticket actualizado", Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "⚠️ " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(COLOR_FONDO_MORADO));
        }
        dialog.show();
    }
}