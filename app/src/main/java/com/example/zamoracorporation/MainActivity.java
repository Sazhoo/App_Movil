package com.example.zamoracorporation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LinearLayout panelMenu, panelAdminLogin, panelAdminDashboard, panelUsuario;
    private EditText txtAdminUser, txtAdminPass;
    private Button btnLoginAdmin, btnVolverMenu1;
    private TextView lblSaludoAdmin;
    private ListView listViewAdminTickets;
    private Button btnCerrarSesionAdmin;

    private EditText txtNombreUsuario, txtDocumento, txtPlacaEquipo, txtDescripcion, txtDocConsulta;
    private RadioButton rbIncidente, rbMedellin;
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

        txtAdminUser = findViewById(R.id.txtAdminUser);
        txtAdminPass = findViewById(R.id.txtAdminPass);
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
        btnVolverMenu1 = findViewById(R.id.btnVolverMenu1);

        lblSaludoAdmin = findViewById(R.id.lblSaludoAdmin);
        listViewAdminTickets = findViewById(R.id.listViewAdminTickets);
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtDocumento = findViewById(R.id.txtDocumento);
        txtPlacaEquipo = findViewById(R.id.txtPlacaEquipo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        rbIncidente = findViewById(R.id.rbIncidente);
        rbMedellin = findViewById(R.id.rbMedellin);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        txtDocConsulta = findViewById(R.id.txtDocConsulta);
        btnConsultarUsuario = findViewById(R.id.btnConsultarUsuario);
        listViewUserTickets = findViewById(R.id.listViewUserTickets);
        btnVolverMenu2 = findViewById(R.id.btnVolverMenu2);

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
        String documento = txtDocumento.getText().toString().trim();
        String placa = txtPlacaEquipo.getText().toString().trim();
        String descripcion = txtDescripcion.getText().toString().trim();

        if (usuario.isEmpty() || documento.isEmpty() || placa.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación estricta: El nombre solo debe contener texto (letras y espacios)
        if (!usuario.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            Toast.makeText(this, "⚠️ El nombre de usuario solo debe contener letras", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación estricta de longitudes máximas
        if (documento.length() > 10) {
            Toast.makeText(this, "⚠️ El documento de identidad no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (placa.length() > 13) {
            Toast.makeText(this, "⚠️ La placa del equipo no puede superar los 13 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        int num = 100000 + random.nextInt(900000);
        String codigo = (rbIncidente.isChecked() ? "INC" : "WO") + num;

        String analistaAsignado = repositorio.asignarAnalistaAutomatico();
        double costoBase = 120.0;
        TicketSoporte nuevoTicket;

        if (rbMedellin.isChecked()) {
            nuevoTicket = new TicketSalud(codigo, usuario, documento, placa, descripcion, costoBase, analistaAsignado);
        } else {
            nuevoTicket = new TicketBienestar(codigo, usuario, documento, placa, descripcion, costoBase, analistaAsignado);
        }

        repositorio.registrarTicket(nuevoTicket);

        Toast.makeText(this, "¡Caso registrado! Asignado a: " + analistaAsignado, Toast.LENGTH_LONG).show();
        limpiarFormulario();
    }

    private void consultarTicketsUsuario() {
        String documento = txtDocConsulta.getText().toString().trim();
        if (documento.isEmpty()) {
            Toast.makeText(this, "⚠️ Ingrese su documento para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (documento.length() > 10) {
            Toast.makeText(this, "⚠️ El documento no puede superar los 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<TicketSoporte> misTickets = repositorio.obtenerTicketsPorUsuario(documento);
        if (misTickets.isEmpty()) {
            Toast.makeText(this, "No se encontraron tickets con este documento.", Toast.LENGTH_SHORT).show();
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
        txtDocumento.setText("");
        txtPlacaEquipo.setText("");
        txtDescripcion.setText("");
        rbIncidente.setChecked(true);
        rbMedellin.setChecked(true);
        txtNombreUsuario.requestFocus();
    }
}