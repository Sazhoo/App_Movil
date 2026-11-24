package com.example.zamoracorporation;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LinearLayout panelMenu, panelAdminLogin, panelAdminDashboard, panelUsuario, layoutCamposGarantia;
    private EditText txtAdminUser, txtAdminPass;
    private Button btnLoginAdmin, btnVolverMenu1;
    private TextView lblSaludoAdmin;
    private ListView listViewAdminTickets;
    private Button btnCerrarSesionAdmin;

    private RadioGroup rgTipoSolicitud;
    private RadioButton rbIncidente, rbOrdenTrabajo, rbGarantia;
    private EditText txtNombreUsuario, txtCorreoCorporativo, txtDocumento, txtPlacaEquipo, txtSerialEquipo, txtDescripcion, txtDocConsulta;
    private Spinner spinnerMarca, spinnerTipoEquipo;
    private Button btnRegistrar, btnLimpiar, btnConsultarUsuario, btnVolverMenu2;
    private ListView listViewUserTickets;

    private static RepositorioSoporte repositorio = new RepositorioSoporte();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        panelMenu = findViewById(R.id.panelMenu);
        panelAdminLogin = findViewById(R.id.panelAdminLogin);
        panelAdminDashboard = findViewById(R.id.panelAdminDashboard);
        panelUsuario = findViewById(R.id.panelUsuario);
        layoutCamposGarantia = findViewById(R.id.layoutCamposGarantia);

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

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtCorreoCorporativo = findViewById(R.id.txtCorreoCorporativo);
        txtDocumento = findViewById(R.id.txtDocumento);
        txtPlacaEquipo = findViewById(R.id.txtPlacaEquipo);
        txtSerialEquipo = findViewById(R.id.txtSerialEquipo);
        txtDescripcion = findViewById(R.id.txtDescripcion);

        spinnerMarca = findViewById(R.id.spinnerMarca);
        spinnerTipoEquipo = findViewById(R.id.spinnerTipoEquipo);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        txtDocConsulta = findViewById(R.id.txtDocConsulta);
        btnConsultarUsuario = findViewById(R.id.btnConsultarUsuario);
        listViewUserTickets = findViewById(R.id.listViewUserTickets);
        btnVolverMenu2 = findViewById(R.id.btnVolverMenu2);

        configurarSpinnersGarantia();

        // Mostrar u ocultar formulario de garantía según selección
        rgTipoSolicitud.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbGarantia) {
                layoutCamposGarantia.setVisibility(View.VISIBLE);
            } else {
                layoutCamposGarantia.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.btnIrAdmin).setOnClickListener(v -> mostrarPanel(panelAdminLogin));
        findViewById(R.id.btnIrUsuario).setOnClickListener(v -> mostrarPanel(panelUsuario));
        btnVolverMenu1.setOnClickListener(v -> mostrarPanel(panelMenu));
        btnVolverMenu2.setOnClickListener(v -> mostrarPanel(panelMenu));

        btnLoginAdmin.setOnClickListener(v -> {
            String user = txtAdminUser.getText().toString().trim();
            String pass = txtAdminPass.getText().toString().trim();

            if ((user.equalsIgnoreCase("Juan Zamora") || user.equalsIgnoreCase("Daniela Rojas")) && pass.equals("1234567890")) {
                String nombreFormateado = user.equalsIgnoreCase("Juan Zamora") ? "Juan" : "Daniela";
                lblSaludoAdmin.setText("Bienvenido, " + nombreFormateado);

                cargarDashboardAdmin(user);
                mostrarPanel(panelAdminDashboard);
                txtAdminUser.setText("");
                txtAdminPass.setText("");
            } else {
                Toast.makeText(MainActivity.this, "⚠️ Credenciales incorrectas. Verifique usuario y contraseña.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCerrarSesionAdmin.setOnClickListener(v -> mostrarPanel(panelMenu));
        btnRegistrar.setOnClickListener(v -> registrarNuevoCaso());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnConsultarUsuario.setOnClickListener(v -> consultarTicketsUsuario());
    }

    private void configurarSpinnersGarantia() {
        String[] marcas = {"Lenovo", "HP", "DELL"};
        ArrayAdapter<String> adapterMarca = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, marcas);
        spinnerMarca.setAdapter(adapterMarca);

        String[] tipos = {"Portátil", "DESKTOP"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        spinnerTipoEquipo.setAdapter(adapterTipo);
    }

    private void mostrarPanel(LinearLayout panelActivo) {
        panelMenu.setVisibility(View.GONE);
        panelAdminLogin.setVisibility(View.GONE);
        panelAdminDashboard.setVisibility(View.GONE);
        panelUsuario.setVisibility(View.GONE);

        panelActivo.setVisibility(View.VISIBLE);
    }

    private void cargarDashboardAdmin(String nombreAdmin) {
        ArrayList<TicketSoporte> casosAdmin = repositorio.obtenerTicketsPorAnalista(nombreAdmin);
        ArrayAdapter<TicketSoporte> adapterAdmin = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, casosAdmin);
        listViewAdminTickets.setAdapter(adapterAdmin);

        listViewAdminTickets.setOnItemClickListener((parent, view, position, id) -> {
            TicketSoporte t = casosAdmin.get(position);
            mostrarDialogoDetalle(t);
        });
    }

    private void registrarNuevoCaso() {
        String usuario = txtNombreUsuario.getText().toString().trim();
        String correo = txtCorreoCorporativo.getText().toString().trim().toLowerCase();
        String documento = txtDocumento.getText().toString().trim();
        String placa = txtPlacaEquipo.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();
        boolean esGarantia = rbGarantia.isChecked();

        if (usuario.isEmpty() || correo.isEmpty() || documento.isEmpty() || placa.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Todos los campos principales son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación estricta: Nombre solo letras
        if (!usuario.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            Toast.makeText(this, "⚠️ El nombre de usuario solo debe contener letras", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación estricta: Correo corporativo válido (@compensar.com o @consorciosalud.com)
        if (!correo.matches("^[a-zA-Z0-9._-]+@(compensar\\.com|consorciosalud\\.com)$")) {
            Toast.makeText(this, "⚠️ Correo inválido. Debe terminar en @compensar.com o @consorciosalud.com", Toast.LENGTH_LONG).show();
            return;
        }

        // Validación estricta de longitudes máximas
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
            serial = txtSerialEquipo.getText().toString().trim();
            if (serial.isEmpty()) {
                Toast.makeText(this, "⚠️ Para casos de garantía el Serial del equipo es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Random random = new Random();
        int num = 100000 + random.nextInt(900000);
        String prefijo = esGarantia ? "GAR" : (rbIncidente.isChecked() ? "INC" : "WO");
        String codigo = prefijo + num;

        String analistaAsignado = repositorio.asignarAnalistaAutomatico();
        TicketSoporte nuevoTicket;

        if (esGarantia) {
            String marcaSeleccionada = spinnerMarca.getSelectedItem().toString();
            String tipoSeleccionado = spinnerTipoEquipo.getSelectedItem().toString();
            nuevoTicket = new TicketGarantia(codigo, usuario, correo, documento, placa, marcaSeleccionada, tipoSeleccionado, serial, descripcion, analistaAsignado);
        } else if (rbIncidente.isChecked()) {
            nuevoTicket = new TicketIncidente(codigo, usuario, correo, documento, placa, descripcion, 120.0, analistaAsignado);
        } else {
            nuevoTicket = new TicketOrdenTrabajo(codigo, usuario, correo, documento, placa, descripcion, 120.0, analistaAsignado);
        }

        repositorio.registrarTicket(nuevoTicket);

        Toast.makeText(this, "✅ Caso registrado (" + nuevoTicket.getFrente() + "). Asignado a: " + analistaAsignado, Toast.LENGTH_LONG).show();
        limpiarFormulario();
    }

    private void consultarTicketsUsuario() {
        String documento = txtDocConsulta.getText().toString().trim();
        if (documento.isEmpty()) {
            Toast.makeText(this, "⚠️ Ingrese su documento / ID para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (documento.length() > 10) {
            Toast.makeText(this, "⚠️ El ID / Documento no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<TicketSoporte> misTickets = repositorio.obtenerTicketsPorUsuario(documento);
        if (misTickets.isEmpty()) {
            Toast.makeText(this, "No se encontraron tickets con este ID.", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<TicketSoporte> adapterUser = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, misTickets);
        listViewUserTickets.setAdapter(adapterUser);

        listViewUserTickets.setOnItemClickListener((parent, view, position, id) -> {
            TicketSoporte t = misTickets.get(position);
            mostrarDialogoDetalle(t);
        });
    }

    private void mostrarDialogoDetalle(TicketSoporte t) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Detalle del Caso")
                .setMessage(t.obtenerDetalleCompleto())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void limpiarFormulario() {
        txtNombreUsuario.setText("");
        txtCorreoCorporativo.setText("");
        txtDocumento.setText("");
        txtPlacaEquipo.setText("");
        txtSerialEquipo.setText("");
        txtDescripcion.setText("");
        rbIncidente.setChecked(true);
        layoutCamposGarantia.setVisibility(View.GONE);
        spinnerMarca.setSelection(0);
        spinnerTipoEquipo.setSelection(0);
        txtNombreUsuario.requestFocus();
    }
}