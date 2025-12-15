package com.example.regcons.ui.main;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.regcons.R;

public class Ajustes extends AppCompatActivity {

    private Switch switchVibracion, switchModoOscuro;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        prefs = getSharedPreferences("ajustes", MODE_PRIVATE);

        switchVibracion = findViewById(R.id.switchVibracion);
        switchModoOscuro = findViewById(R.id.switchModoOscuro);

        // Cargar estados guardados
        switchVibracion.setChecked(prefs.getBoolean("vibracion", true));
        switchModoOscuro.setChecked(prefs.getBoolean("modo_oscuro", false));

        switchVibracion.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("vibracion", isChecked).apply()
        );

        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("modo_oscuro", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }
}