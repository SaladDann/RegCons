package com.example.regcons.ui.main;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.example.regcons.models.Obra;
import com.example.regcons.ui.avance.ScreenObras;

import java.util.ArrayList;
import java.util.List;

// Implementamos la interfaz para manejar la selección del Spinner
public class MainActivity extends AppCompatActivity {

    private int idObraSeleccionada = -1;

    private Spinner spinnerObras;
    private ProgressBar progressAvance;
    private TextView txtPorcentaje;

    private DbRegCons db;
    private List<Obra> listaObrasPendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.mp_tbmenu);
        setSupportActionBar(toolbar);

        actualizarTitulo();


        spinnerObras = findViewById(R.id.spinnerObras);
        progressAvance = findViewById(R.id.progressAvance);
        txtPorcentaje = findViewById(R.id.txtPorcentaje);

        db = new DbRegCons(this);

        cargarSpinnerObras();
        spinnerObras.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {

                        Obra obraSeleccionada = listaObrasPendientes.get(position);

                        int porcentaje = db.obtenerPorcentajeAvance(
                                obraSeleccionada.getId());

                        progressAvance.setProgress(porcentaje);
                        txtPorcentaje.setText(porcentaje + "%");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        progressAvance.setProgress(0);
                        txtPorcentaje.setText("0%");
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSpinnerObras();
        actualizarTitulo();
    }

    private void actualizarTitulo(){
        String usuario = getIntent().getStringExtra("usuario");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bienvenido, " + usuario);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuPrincipal = getMenuInflater();
        menuPrincipal.inflate(R.menu.menu_principal, menu);
        return true;
    }

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
            cerrarSesion(null);
        }
        return true;
    }

    public void cerrarSesion(View v) {
        getSharedPreferences("sesion", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
        finish();
    }

    //metodos para ir a los modulos
    public void mainAvances(View v) {
        startActivity(new Intent(this, ScreenObras.class));
    }

    public void mainIncidentes(View v) {
        //startActivity(new Intent(this, Incidentes.class));
    }

    public void mainReportes(View v) {
        //startActivity(new Intent(this, Reportes.class));
    }

    private void cargarSpinnerObras() {

        listaObrasPendientes = new ArrayList<>();

        Cursor cursor = db.obtenerObrasPendientes();

        if (cursor.moveToFirst()) {
            do {
                Obra obra = new Obra();
                obra.setId(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_ID)));
                obra.setNombre(cursor.getString(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_NOMBRE)));
                obra.setEstado(cursor.getString(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_ESTADO)));
                obra.setDescripcion(cursor.getString(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_DESCRIPCION)));

                listaObrasPendientes.add(obra);
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayAdapter<Obra> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                listaObrasPendientes
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerObras.setAdapter(adapter);
    }




}