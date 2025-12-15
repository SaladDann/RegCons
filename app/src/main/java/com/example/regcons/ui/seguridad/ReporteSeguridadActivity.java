package com.example.regcons.ui.seguridad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;

import java.util.ArrayList;
import java.util.List;

public class ReporteSeguridadActivity extends AppCompatActivity {
    private long reporteIdAEditar = -1;

    private Spinner tipoReporteSpinner;
    private Button btnTomarFoto;
    private Button btnGuardarReporte;
    private Button btnVolver;
    private static final int PICK_IMAGE_REQUEST = 1;
    private LinearLayout layoutMiniaturasFotos;
    private List<String> fotosAdjuntasUris = new ArrayList<>();
    private EditText descripcionEditText;
    private RadioGroup severidadRadioGroup;
    private Button btnProbarCrud;
    private DbRegCons db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_seguridad);

        tipoReporteSpinner = findViewById(R.id.spinner_tipo_reporte);
        btnTomarFoto = findViewById(R.id.btn_tomar_foto);
        btnGuardarReporte = findViewById(R.id.btn_guardar_reporte);
        btnVolver = findViewById(R.id.btn_volver);
        layoutMiniaturasFotos = findViewById(R.id.layout_miniaturas_fotos);
        descripcionEditText = findViewById(R.id.edit_text_descripcion);
        severidadRadioGroup = findViewById(R.id.radio_group_severidad);
        btnProbarCrud = findViewById(R.id.btn_probar_crud);

        db = new DbRegCons(this);

        setupTipoReporteSpinner();

        comprobarModoEdicion();

        btnTomarFoto.setOnClickListener(v -> {
            abrirSelectorGaleria();
        });

        btnGuardarReporte.setOnClickListener(v -> {
            guardarReporteLocal(); // Este método ahora maneja INSERT y UPDATE
        });

        btnVolver.setOnClickListener(v -> {
            finish();
        });

        btnProbarCrud.setOnClickListener(v -> {
            Intent intent = new Intent(ReporteSeguridadActivity.this, AdministrarReportesActivity.class);
            startActivity(intent);
        });
    }

    private void comprobarModoEdicion() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("ID_REPORTE_EDITAR")) {
            reporteIdAEditar = extras.getLong("ID_REPORTE_EDITAR");

            String descripcion = extras.getString("DESCRIPCION_REPORTE");
            String severidad = extras.getString("SEVERIDAD_REPORTE");
            String tipo = extras.getString("TIPO_REPORTE");
            String fotosUrisString = extras.getString("FOTOS_URIS", "");

            descripcionEditText.setText(descripcion);

            btnGuardarReporte.setText("Actualizar Reporte (ID: " + reporteIdAEditar + ")");
            setTitle("Editar Reporte de Seguridad");

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) tipoReporteSpinner.getAdapter();
            if (adapter != null) {
                int spinnerPosition = adapter.getPosition(tipo);
                tipoReporteSpinner.setSelection(spinnerPosition);
            }

            for (int i = 0; i < severidadRadioGroup.getChildCount(); i++) {
                View view = severidadRadioGroup.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton rb = (RadioButton) view;
                    if (rb.getText().toString().equalsIgnoreCase(severidad)) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }

            if (!fotosUrisString.isEmpty()) {
                String[] uris = fotosUrisString.split("\\|");
                for (String uri : uris) {
                    if (!uri.isEmpty()) {
                        Uri imageUri = Uri.parse(uri);
                        fotosAdjuntasUris.add(uri);
                        mostrarMiniatura(imageUri);
                    }
                }
            }

        } else {
            btnGuardarReporte.setText("Guardar Reporte");
            setTitle("Nuevo Reporte de Seguridad");
        }
    }


    private void setupTipoReporteSpinner() {
        String[] tipos = {"Incidente", "Condición Insegura"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoReporteSpinner.setAdapter(adapter);
    }

    private void abrirSelectorGaleria() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imagenUri = data.getData();

            fotosAdjuntasUris.add(imagenUri.toString());

            mostrarMiniatura(imagenUri);

            Toast.makeText(this, "Foto adjuntada: " + fotosAdjuntasUris.size(), Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarMiniatura(Uri uri) {
        ImageView imageView = new ImageView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        params.setMargins(0, 0, 16, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(uri);
        layoutMiniaturasFotos.addView(imageView);
    }

    private void guardarReporteLocal() {

        String tipoReporte = tipoReporteSpinner.getSelectedItem().toString();
        String descripcion = descripcionEditText.getText().toString().trim();
        int selectedId = severidadRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);

        String severidad = "";
        if (selectedRadioButton != null) {
            severidad = selectedRadioButton.getText().toString();
        }

        if (descripcion.isEmpty() || severidad.isEmpty()) {
            Toast.makeText(this, "Debe ingresar descripción y seleccionar severidad.", Toast.LENGTH_LONG).show();
            return;
        }

        if (reporteIdAEditar != -1) {

            int filasActualizadas = db.actualizarReporte(
                    reporteIdAEditar,
                    descripcion,
                    severidad
            );

            if (filasActualizadas > 0) {
                Toast.makeText(this, "✅ Reporte ID " + reporteIdAEditar + " actualizado exitosamente.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "❌ Error al actualizar el reporte.", Toast.LENGTH_LONG).show();
            }

        } else {
            long fechaTimestamp = System.currentTimeMillis();
            String fotosUrisString = TextUtils.join("|", fotosAdjuntasUris);

            long idInsertado = db.insertarReporte(
                    tipoReporte,
                    descripcion,
                    severidad,
                    fechaTimestamp,
                    fotosUrisString
            );

            if (idInsertado > 0) {
                Toast.makeText(this, "✅ Reporte de Seguridad guardado localmente (ID: " + idInsertado + ")", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "❌ Error al guardar el reporte localmente.", Toast.LENGTH_LONG).show();
            }
        }
    }
}