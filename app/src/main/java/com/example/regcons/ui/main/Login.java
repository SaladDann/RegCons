package com.example.regcons.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText txtUsuario, txtPassword;
    private CheckBox chbMantenerSesion;
    private DbRegCons db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DbRegCons(this);
        txtUsuario = findViewById(R.id.log_txtUsuario);
        txtPassword = findViewById(R.id.log_txtContrasenia);
        chbMantenerSesion = findViewById(R.id.log_chbMantenerSesion);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);

        if (prefs.getBoolean("activa", false)) {
            String usuarioGuardado = prefs.getString("usuario", "");

            txtUsuario.setText(usuarioGuardado);
            chbMantenerSesion.setChecked(true);
        }
    }

    public void logIniciarSesion(View v){
        String usuario = txtUsuario.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean valido = db.verificarUsuario(usuario, password);

        if (valido) {
            if (chbMantenerSesion.isChecked()) {
                getSharedPreferences("sesion", MODE_PRIVATE)
                        .edit()
                        .putBoolean("activa", true)
                        .putString("usuario", usuario)
                        .apply();
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    public void logCancelar(View v){
        finish();
    }

}