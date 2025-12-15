package com.example.regcons.ui.avance;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.example.regcons.models.Obra;
import com.example.regcons.ui.main.AcercaDe;
import com.example.regcons.ui.main.Ajustes;
import com.example.regcons.ui.main.Login;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScreenObras extends AppCompatActivity {

    //variables
    private RecyclerView rvObras;
    private ObraAdapter obraAdapter;
    private List<Obra> listaObras;
    private TextView txtSinObras;
    private DbRegCons db;
    private EditText txtBuscar;
    private Button btnBuscar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_obras);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.mp_tbavances);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Avances Obra");

        if (actionBar != null) {
            // Muestra el icono de la flecha de regreso
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        db = new DbRegCons(this);
        listaObras = new ArrayList<>();

        rvObras = findViewById(R.id.rvObras);
        txtSinObras = findViewById(R.id.txtSinObras);
        rvObras.setLayoutManager(new LinearLayoutManager(this));

        obraAdapter = new ObraAdapter(this, listaObras);
        rvObras.setAdapter(obraAdapter);

        txtBuscar = findViewById(R.id.cu_txtConsulta);
        btnBuscar = findViewById(R.id.cu_btnConsultarObra);

        cargarObras(null);


    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarObras(null);
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

    private void cargarObras(String filtro) {

        listaObras.clear();

        Cursor cursor;

        if (filtro == null) {
            cursor = db.obtenerObras();
        } else {
            cursor = db.buscarObrasPorNombre(filtro);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {

                Obra obra = new Obra();
                obra.setId(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_ID)
                ));
                obra.setNombre(cursor.getString(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_NOMBRE)
                ));
                obra.setDescripcion(cursor.getString(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_DESCRIPCION)
                ));
                obra.setEstado(cursor.getString(
                        cursor.getColumnIndexOrThrow(DbRegCons.COL_OBRA_ESTADO)
                ));

                listaObras.add(obra);
            }
            cursor.close();
        }

        // Estado vacío
        if (listaObras.isEmpty()) {
            txtSinObras.setVisibility(View.VISIBLE);
            txtSinObras.setText("No hay obras registradas");
            rvObras.setVisibility(View.GONE);
        } else {
            txtSinObras.setVisibility(View.GONE);
            rvObras.setVisibility(View.VISIBLE);
        }

        obraAdapter.notifyDataSetChanged();
    }

    public void buscarObras(View v) {
        String filtro = txtBuscar.getText().toString();
        cargarObras(filtro);
        ocultarTeclado(v);
        if (filtro.isEmpty()) {
            cargarObras(null);
            Toast.makeText(this, "Mostrando todas las obras", Toast.LENGTH_SHORT).show();
            return;
        }

        cargarObras(filtro);
        Toast.makeText(this, "Buscando obras con nombre: " + filtro, Toast.LENGTH_SHORT).show();
        txtBuscar.setText("");
    }

    private void ocultarTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void nuevaObra(View view) {
        startActivity(new Intent(this, FormularioObra.class));
    }

}