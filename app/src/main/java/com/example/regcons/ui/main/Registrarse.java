package com.example.regcons.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.google.android.material.textfield.TextInputEditText;

public class Registrarse extends AppCompatActivity {

    private TextInputEditText txtUsuario, txtCorreo, txtPassword, txtConfirmPassword;
    private CheckBox chbTerminos;
    private DbRegCons db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Cambia el color de la barra de estado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(this, R.color.advertencia);
            getWindow().setStatusBarColor(color);
        }

        db = new DbRegCons(this);

        txtUsuario = findViewById(R.id.reg_txtUsuario);
        txtCorreo = findViewById(R.id.reg_txtCorreo);
        txtPassword = findViewById(R.id.reg_txtContraseña);
        txtConfirmPassword = findViewById(R.id.reg_txtConfContraseña);
        chbTerminos = findViewById(R.id.reg_chbTerminosCondiciones);

    }


    public void regRegistrar(View v){
        String usuario = txtUsuario.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!chbTerminos.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insertado = db.insertarUsuario(usuario, password, correo);

        if (insertado) {
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
        } else {
            Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
        }
    }

    public void regCancelar(View v) {
        finish();
    }
}