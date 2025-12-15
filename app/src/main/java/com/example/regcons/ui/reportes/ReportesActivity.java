package com.example.regcons.ui.reportes;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportesActivity extends AppCompatActivity {

    private DbRegCons db;
    private TextView txtAvancePromedio, txtTotalIncidentes, txtAvanceSemanal, txtRiesgosActivos;
    private ProgressBar progressAvance;
    private TextView txtFechaInicio, txtFechaFin, txtTituloReporte;
    private Spinner spinnerFiltro, spinnerObras;
    private Button btnCambiarFechaInicio, btnCambiarFechaFin;
    private CheckBox chkMostrarDetalles;
    private RadioGroup radioGroupTipoReporte;
    private RadioButton rbSemanal, rbMensual, rbPersonalizado;

    private Date fechaInicio, fechaFin;
    private Calendar calendar;
    private SimpleDateFormat sdf;

    // SharedPreferences
    private SharedPreferences prefs;
    private static final String PREFS_REPORTES = "prefs_reportes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        // Configurar toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reportes y Visualización");

        // Inicializar SharedPreferences
        prefs = getSharedPreferences(PREFS_REPORTES, MODE_PRIVATE);

        db = new DbRegCons(this);
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();

        // Inicializar vistas
        inicializarVistas();

        // Cargar preferencias guardadas
        cargarPreferencias();

        // Configurar listeners
        configurarListeners();

        // Cargar datos iniciales
        cargarDatosReportes();
    }

    private void inicializarVistas() {
        txtAvancePromedio = findViewById(R.id.txtAvancePromedio);
        txtTotalIncidentes = findViewById(R.id.txtTotalIncidentes);
        txtAvanceSemanal = findViewById(R.id.txtAvanceSemanal);
        txtRiesgosActivos = findViewById(R.id.txtRiesgosActivos);
        progressAvance = findViewById(R.id.progressAvance);
        txtFechaInicio = findViewById(R.id.txtFechaInicio);
        txtFechaFin = findViewById(R.id.txtFechaFin);
        txtTituloReporte = findViewById(R.id.txtTituloReporte);

        // Spinners
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        spinnerObras = findViewById(R.id.spinnerObras);

        // Botones de fecha
        btnCambiarFechaInicio = findViewById(R.id.btnCambiarFechaInicio);
        btnCambiarFechaFin = findViewById(R.id.btnCambiarFechaFin);

        // CheckBox
        chkMostrarDetalles = findViewById(R.id.chkMostrarDetalles);

        // RadioGroup y RadioButtons
        radioGroupTipoReporte = findViewById(R.id.radioGroupTipoReporte);
        rbSemanal = findViewById(R.id.rbSemanal);
        rbMensual = findViewById(R.id.rbMensual);
        rbPersonalizado = findViewById(R.id.rbPersonalizado);

        // Configurar Spinners
        configurarSpinners();
    }

    private void configurarSpinners() {
        // Configurar spinner de filtro
        List<String> filtros = new ArrayList<>();
        filtros.add("Todas las obras");
        filtros.add("Por obra específica");
        filtros.add("Solo incidentes críticos");
        filtros.add("Solo avances completados");

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filtros);
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapterFiltro);

        // Configurar spinner de obras
        List<String> obras = db.obtenerListaObras();
        if (obras.isEmpty()) {
            obras.add("No hay obras registradas");
        }

        ArrayAdapter<String> adapterObras = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, obras);
        adapterObras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObras.setAdapter(adapterObras);
    }

    private void cargarPreferencias() {
        // Cargar tipo de reporte
        String tipoReporte = prefs.getString("tipo_reporte", "semanal");
        switch (tipoReporte) {
            case "semanal":
                rbSemanal.setChecked(true);
                break;
            case "mensual":
                rbMensual.setChecked(true);
                break;
            case "personalizado":
                rbPersonalizado.setChecked(true);
                break;
        }

        // Cargar preferencia de mostrar detalles
        boolean mostrarDetalles = prefs.getBoolean("mostrar_detalles", true);
        chkMostrarDetalles.setChecked(mostrarDetalles);

        // Cargar filtro seleccionado
        int filtroPosicion = prefs.getInt("filtro_posicion", 0);
        spinnerFiltro.setSelection(filtroPosicion);

        // Configurar fechas según el tipo de reporte
        configurarFechasSegunTipo();
    }

    private void configurarFechasSegunTipo() {
        calendar = Calendar.getInstance();

        if (rbSemanal.isChecked()) {
            // Última semana
            fechaFin = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, -6);
            fechaInicio = calendar.getTime();
            txtTituloReporte.setText("Reporte Semanal");
        } else if (rbMensual.isChecked()) {
            // Último mes
            fechaFin = calendar.getTime();
            calendar.add(Calendar.MONTH, -1);
            fechaInicio = calendar.getTime();
            txtTituloReporte.setText("Reporte Mensual");
        } else {
            // Personalizado (cargar de preferencias o usar último mes)
            long inicioMillis = prefs.getLong("fecha_inicio", 0);
            long finMillis = prefs.getLong("fecha_fin", 0);

            if (inicioMillis > 0 && finMillis > 0) {
                fechaInicio = new Date(inicioMillis);
                fechaFin = new Date(finMillis);
            } else {
                fechaFin = calendar.getTime();
                calendar.add(Calendar.MONTH, -1);
                fechaInicio = calendar.getTime();
            }
            txtTituloReporte.setText("Reporte Personalizado");
        }

        txtFechaInicio.setText(sdf.format(fechaInicio));
        txtFechaFin.setText(sdf.format(fechaFin));
    }

    private void guardarPreferencias() {
        SharedPreferences.Editor editor = prefs.edit();

        // Guardar tipo de reporte
        if (rbSemanal.isChecked()) {
            editor.putString("tipo_reporte", "semanal");
        } else if (rbMensual.isChecked()) {
            editor.putString("tipo_reporte", "mensual");
        } else {
            editor.putString("tipo_reporte", "personalizado");
        }

        // Guardar otras preferencias
        editor.putBoolean("mostrar_detalles", chkMostrarDetalles.isChecked());
        editor.putInt("filtro_posicion", spinnerFiltro.getSelectedItemPosition());
        editor.putLong("fecha_inicio", fechaInicio.getTime());
        editor.putLong("fecha_fin", fechaFin.getTime());

        editor.apply();
    }

    private void configurarListeners() {
        // Botones de fecha
        btnCambiarFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(true);
            }
        });

        btnCambiarFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker(false);
            }
        });

        // RadioGroup
        radioGroupTipoReporte.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                configurarFechasSegunTipo();
                cargarDatosReportes();
                guardarPreferencias();
            }
        });

        // CheckBox
        chkMostrarDetalles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(ReportesActivity.this,
                        isChecked ? "Mostrando detalles" : "Ocultando detalles",
                        Toast.LENGTH_SHORT).show();
                guardarPreferencias();
            }
        });

        // Spinner de filtro
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro(position);
                guardarPreferencias();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void aplicarFiltro(int position) {
        // Aplicar filtro según la posición
        String mensaje = "";
        switch (position) {
            case 0:
                mensaje = "Mostrando todas las obras";
                break;
            case 1:
                mensaje = "Filtrando por obra específica";
                break;
            case 2:
                mensaje = "Mostrando solo incidentes críticos";
                break;
            case 3:
                mensaje = "Mostrando solo avances completados";
                break;
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        cargarDatosReportes();
    }

    private void mostrarDatePicker(final boolean esFechaInicio) {
        Calendar calendarActual = Calendar.getInstance();
        if (esFechaInicio) {
            calendarActual.setTime(fechaInicio);
        } else {
            calendarActual.setTime(fechaFin);
        }

        int año = calendarActual.get(Calendar.YEAR);
        int mes = calendarActual.get(Calendar.MONTH);
        int dia = calendarActual.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar nuevaFecha = Calendar.getInstance();
                        nuevaFecha.set(year, month, dayOfMonth);

                        if (esFechaInicio) {
                            fechaInicio = nuevaFecha.getTime();
                            txtFechaInicio.setText(sdf.format(fechaInicio));
                        } else {
                            fechaFin = nuevaFecha.getTime();
                            txtFechaFin.setText(sdf.format(fechaFin));
                        }

                        // Marcar como personalizado
                        rbPersonalizado.setChecked(true);
                        guardarPreferencias();
                        cargarDatosReportes();
                    }
                }, año, mes, dia);

        datePickerDialog.show();
    }

    private void cargarDatosReportes() {
        // Obtener datos filtrados
        double avancePromedio = db.obtenerAvancePromedio(fechaInicio, fechaFin);
        int totalIncidentes = db.obtenerTotalIncidentes(fechaInicio, fechaFin);
        double avanceSemanal = db.obtenerAvanceSemanal(fechaInicio, fechaFin);
        int riesgosActivos = db.obtenerRiesgosActivos();

        // Aplicar filtro adicional si es necesario
        int filtroPos = spinnerFiltro.getSelectedItemPosition();
        if (filtroPos == 2) { // Solo incidentes críticos
            // Podrías tener un método específico para esto
            totalIncidentes = db.obtenerIncidentesCriticos(fechaInicio, fechaFin);
        }

        // Actualizar UI
        txtAvancePromedio.setText(String.format(Locale.getDefault(), "%.1f%%", avancePromedio));
        txtTotalIncidentes.setText(String.valueOf(totalIncidentes));
        txtAvanceSemanal.setText(String.format(Locale.getDefault(), "%.1f%%", avanceSemanal));
        txtRiesgosActivos.setText(String.valueOf(riesgosActivos));
        progressAvance.setProgress((int) avancePromedio);

        // Actualizar título con fechas
        txtTituloReporte.setText(txtTituloReporte.getText() +
                ": " + sdf.format(fechaInicio) + " al " + sdf.format(fechaFin));
    }

    // Menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reportes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_exportar) {
            exportarReporte();
            return true;
        } else if (id == R.id.menu_actualizar) {
            actualizarDatos();
            return true;
        } else if (id == R.id.menu_configurar) {
            abrirConfiguracion();
            return true;
        } else if (id == R.id.menu_ayuda) {
            mostrarAyuda();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportarReporte() {
        Toast.makeText(this, "Exportando reporte...", Toast.LENGTH_SHORT).show();
        // Aquí podrías implementar exportación a PDF o Excel
    }

    private void actualizarDatos() {
        cargarDatosReportes();
        Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
    }

    private void abrirConfiguracion() {
        Toast.makeText(this, "Configuración de reportes", Toast.LENGTH_SHORT).show();
        // Aquí podrías abrir una actividad de configuración
    }

    private void mostrarAyuda() {
        Toast.makeText(this, "Ayuda: Seleccione fechas y filtros para personalizar el reporte",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarPreferencias();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}