package com.example.regcons.ui.avance;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.regcons.R;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.example.regcons.db.DbRegCons;
import com.example.regcons.models.Avance;
import com.example.regcons.ui.main.AcercaDe;
import com.example.regcons.ui.main.Ajustes;
import com.example.regcons.ui.main.Login;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FormularioObra extends AppCompatActivity {
    private DbRegCons db;

    private int idObra = -1;

    // Vistas de Obra/Pantalla
    private TextView tvIdObra;
    private LinearLayout layoutObras;
    private LinearLayout layoutAvances;
    private RecyclerView rvAvances;
    private ActionBar actionBar;

    // Variables para Guardar/Editar Obra
    private TextInputEditText etNombreObra;
    private TextInputEditText etDescripcionObra;
    private Button btnGuardarObra;
    private Button btnGuardarCambios;


    // Variables para Guardar Avance
    private TextInputEditText etNombreAvance;
    private TextInputEditText etDescripcionAvance;
    private TextInputEditText etFechaAvance;
    private Switch stchFinalizado;
    private Button btnTomarFoto;
    private Button btnGuardarAvance;

    private static final int REQUEST_TOMAR_FOTO = 100;
    private String fotoPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_obra);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.mp_tbFomularioObra);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        db = new DbRegCons(this);
        inicializarVistas();
        configurarDatePicker();
        leerModo();
        configurarPantalla();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuPrincipal = getMenuInflater();
        menuPrincipal.inflate(R.menu.menu_principal, menu);
        return true;
    }

    //metodos de la toolbar personalizada
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.mp_configuraciones) {
            Toast.makeText(this, "Configuraciónes", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Ajustes.class));

        } else if (item.getItemId() == R.id.mp_acerca_de) {
            Toast.makeText(this, "Acerca de", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, AcercaDe.class));
        } else if (item.getItemId() == R.id.mp_cerrar_sesion) {
            Toast.makeText(this, "Cerrando Sesión...", Toast.LENGTH_LONG).show();
            cerrarSesion();
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    private void cerrarSesion() {
        getSharedPreferences("sesion", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
        // Ir al Login
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
        finish();
    }

    private void inicializarVistas() {
        tvIdObra = findViewById(R.id.tvIdObra);

        layoutObras = findViewById(R.id.layoutObra);
        layoutAvances = findViewById(R.id.layoutAvances);
        rvAvances = findViewById(R.id.rvAvances);

        // Inicializar vistas para Obra
        etNombreObra = findViewById(R.id.etNombreObra);
        etDescripcionObra = findViewById(R.id.etDescripcionObra);
        btnGuardarObra = findViewById(R.id.btnGuardarObra);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // Inicializar vistas para Avance
        etFechaAvance = findViewById(R.id.etFechaAvance);
        etNombreAvance = findViewById(R.id.etNombreAvance);
        etDescripcionAvance = findViewById(R.id.etDescripcionAvance);
        stchFinalizado = findViewById(R.id.stchFinalizado);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardarAvance = findViewById(R.id.btnGuardarAvance);
    }

    private void leerModo() {
        if (getIntent() != null) {
            idObra = getIntent().getIntExtra("ID_OBRA", -1);
        }
    }

    private void configurarPantalla() {
        if (idObra == -1) {
            configurarModoCrear();
        } else {
            configurarModoEditar();
        }
    }

    private void configurarModoCrear() {
        actionBar.setTitle("Nueva Obra");

        if (tvIdObra != null) {
            tvIdObra.setVisibility(View.GONE);
        }
        if (layoutAvances != null) {
            layoutAvances.setVisibility(View.GONE);
        }
        if (btnGuardarCambios != null) {
            btnGuardarCambios.setVisibility(View.GONE);
        }

        if (btnGuardarObra != null) {
            btnGuardarObra.setVisibility(View.VISIBLE);
        }
    }

    private void configurarModoEditar() {
        actionBar.setTitle("Editar Obra");

        // Mostrar ID
        if (tvIdObra != null) {
            tvIdObra.setVisibility(View.VISIBLE);
            tvIdObra.setText("ID Obra: " + idObra);
        }

        // Ocultar Guardar (Crear) y mostrar Guardar Cambios (Editar)
        if (btnGuardarObra != null) {
            btnGuardarObra.setVisibility(View.GONE);
        }
        if (btnGuardarCambios != null) {
            btnGuardarCambios.setVisibility(View.VISIBLE);
        }

        // Mostrar área de Avances
        if (layoutAvances != null) {
            layoutAvances.setVisibility(View.VISIBLE);
        }
        if (rvAvances != null) {
            rvAvances.setVisibility(View.VISIBLE);
        }
        cargarObraPorId(idObra);
        cargarAvances();
    }

    private void configurarDatePicker() {

        etFechaAvance.setOnClickListener(v -> {

            final Calendar calendario = Calendar.getInstance();
            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    FormularioObra.this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {

                        String fechaSeleccionada = year + "-"
                                + String.format("%02d", month + 1) + "-"
                                + String.format("%02d", dayOfMonth);

                        etFechaAvance.setText(fechaSeleccionada);
                    },
                    anio,
                    mes,
                    dia
            );

            datePickerDialog.show();
        });
    }

    //Metodos para Obra
    public void guardarNuevaObra(View view) {

        String nombre = etNombreObra.getText().toString().trim();
        String descripcion = etDescripcionObra.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreObra.setError("El nombre es obligatorio");
            etNombreObra.requestFocus();
            return;
        }

        long idGenerado = db.insertarObra(nombre, descripcion);

        if (idGenerado > 0) {
            idObra = (int) idGenerado;
            configurarModoEditar();
            Toast.makeText(this, "Obra registrada.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al registrar la obra.", Toast.LENGTH_SHORT).show();
        }
    }

    // metodo para editar obra
    public void actualizarObra(View view) {
        String nombre = etNombreObra.getText().toString().trim();
        String descripcion = etDescripcionObra.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombreObra.setError("El nombre es obligatorio");
            etNombreObra.requestFocus();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DbRegCons.COL_OBRA_NOMBRE, nombre);
        values.put(DbRegCons.COL_OBRA_DESCRIPCION, descripcion);
        //values.put(DbRegCons.COL_OBRA_ESTADO, DbRegCons.ESTADO_PENDIENTE);


        int filasAfectadas = db.actualizarObra(idObra, values);

        if (filasAfectadas > 0) {
            Toast.makeText(this, "Obra actualizada correctamente.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al actualizar la obra.", Toast.LENGTH_SHORT).show();
        }
    }


    private void cargarObraPorId(int idObra) {

        Cursor cursor = db.obtenerObraPorId(idObra);

        if (cursor.moveToFirst()) {

            String nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_NOMBRE)
            );
            String descripcion = cursor.getString(
                    cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_DESCRIPCION)
            );

            etNombreObra.setText(nombre);
            etDescripcionObra.setText(descripcion);
        }

        cursor.close();
    }

    // metodos para Avance
    public void registrarNuevoAvance(View view) {

        String nombreAvance = etNombreAvance.getText().toString().trim();
        String descripcionAvance = etDescripcionAvance.getText().toString().trim();
        String fechaAvance = etFechaAvance.getText().toString().trim();
        boolean finalizado = stchFinalizado.isChecked();

        if (nombreAvance.isEmpty()) {
            etNombreAvance.setError("El nombre del avance es obligatorio.");
            etNombreAvance.requestFocus();
            return;
        }

        if (fechaAvance.isEmpty()) {
            etFechaAvance.setError("La fecha es obligatoria.");
            etFechaAvance.requestFocus();
            return;
        }

        long idGenerado = db.insertarAvance(
                idObra,
                nombreAvance,
                descripcionAvance,
                finalizado,
                fotoPath,
                fechaAvance
        );

        if (idGenerado > 0) {

            Toast.makeText(this, "Avance registrado.", Toast.LENGTH_SHORT).show();

            // Limpiar campos
            etNombreAvance.setText("");
            etDescripcionAvance.setText("");
            etFechaAvance.setText("");
            stchFinalizado.setChecked(false);
            fotoPath = ""; // resetear foto

            cargarAvances();
        } else {
            Toast.makeText(this, "Error al registrar el avance.", Toast.LENGTH_SHORT).show();
        }
    }

    // metodo para cargar avances
    private void cargarAvances() {

        List<Avance> listaAvances = new ArrayList<>();
        Cursor cursor = db.obtenerAvancesPorObra(idObra);

        if (cursor.moveToFirst()) {
            do {
                Avance avance = new Avance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_OBRA_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_NOMBRE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_FINALIZADO)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_FOTO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_DESCRIPCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_AVANCE_FECHA))
                );

                listaAvances.add(avance);
            } while (cursor.moveToNext());
        }

        cursor.close();

        rvAvances.setLayoutManager(new LinearLayoutManager(this));
        rvAvances.setAdapter(new AvanceAdapter(this, listaAvances));
    }

    public void tomarFoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TOMAR_FOTO);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TOMAR_FOTO && resultCode == RESULT_OK) {

            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            try {
                // Crear carpeta media si no existe
                File mediaDir = new File(getExternalFilesDir(null), "media");
                if (!mediaDir.exists()) {
                    mediaDir.mkdirs();
                }

                // Crear archivo
                File imageFile = new File(
                        mediaDir,
                        "avance_" + System.currentTimeMillis() + ".jpg"
                );

                // Guardar imagen
                FileOutputStream fos = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();

                // Guardar ruta
                fotoPath = imageFile.getAbsolutePath();

                Toast.makeText(this, "Foto guardada correctamente", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}