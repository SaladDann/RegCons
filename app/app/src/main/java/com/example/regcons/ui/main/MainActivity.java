package com.example.regcons.ui.main;
import com.example.regcons.ui.seguridad.ReporteSeguridadActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.regcons.R;

public class MainActivity extends AppCompatActivity {

    private Button btnReportarIncidente;

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

        btnReportarIncidente = findViewById(R.id.log_mbtn2);

        btnReportarIncidente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. Crear el Intent para navegar

                // Intent(Contexto Actual, Activity Destino.class)
                Intent intent = new Intent(MainActivity.this, ReporteSeguridadActivity.class);

                // 4. Iniciar la Activity
                startActivity(intent);
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.mp_tbmenu);
        setSupportActionBar(toolbar);

        // Obtener el usuario del intent
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
            Toast.makeText(this, "Configuración", Toast.LENGTH_LONG).show();
        };
        if (item.getItemId() == R.id.mp_acerca_de) {
            Toast.makeText(this, "Acerca de", Toast.LENGTH_LONG).show();
        };
        if (item.getItemId() == R.id.mp_cerrar_sesion) {
            Toast.makeText(this, "Cerrando Sesión...", Toast.LENGTH_LONG).show();
            cerrarSesion(null);

        };
        return true;
    }

    public void cerrarSesion(View v) {
        getSharedPreferences("sesion", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        startActivity(new Intent(this, Login.class));
        finish();
    }


}