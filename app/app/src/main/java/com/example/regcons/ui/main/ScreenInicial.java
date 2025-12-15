package com.example.regcons.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.regcons.R;

public class ScreenInicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_inicial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //verfica sesion activa en shared preferences
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);

        if (prefs.getBoolean("activa", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuario", prefs.getString("usuario", ""));
            startActivity(intent);
            finish();
        }
    }

    public void registrarse(View v){
        Intent pagRegistrarse = new Intent(v.getContext(), Registrarse.class);
        startActivity(pagRegistrarse);
    }

    public void iniciarSesion(View v){
        Intent pagLogin = new Intent(v.getContext(), Login.class);
        startActivity(pagLogin);
    }
}