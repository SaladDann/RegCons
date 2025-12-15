package com.example.regcons.ui.seguridad;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.example.regcons.models.Reporte;

import java.util.ArrayList;
import java.util.List;

public class AdministrarReportesActivity extends AppCompatActivity
        implements ReporteAdapter.OnItemActionListener {

    private DbRegCons db;
    private RecyclerView recyclerView;
    private ReporteAdapter adapter;
    private List<Reporte> reportesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_reportes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reportes Pendientes");
        }

        db = new DbRegCons(this);
        reportesList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_reportes_pendientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReporteAdapter(reportesList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarReportesPendientes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarReportesPendientes() {
        reportesList.clear();
        Cursor cursor = db.obtenerReportesPendientes();

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DbRegCons.COL_REPORTE_ID));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_TIPO));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_DESCRIPCION));
                String severidad = cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_SEVERIDAD));
                long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(DbRegCons.COL_FECHA));
                String uris = cursor.getString(cursor.getColumnIndexOrThrow(DbRegCons.COL_FOTOS_URIS));

                Reporte reporte = new Reporte(id, tipo, descripcion, severidad, fecha, uris);
                reportesList.add(reporte);

            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.updateList(reportesList);

        if (reportesList.isEmpty()) {
            Toast.makeText(this, "No hay reportes pendientes.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(Reporte reporte) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar el reporte con ID: " + reporte.getId() + "? Esto es irreversible.")
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    int filasBorradas = db.eliminarReporte(reporte.getId());
                    if (filasBorradas > 0) {
                        Toast.makeText(this, "Reporte eliminado.", Toast.LENGTH_SHORT).show();
                        cargarReportesPendientes();
                    } else {
                        Toast.makeText(this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEditClick(Reporte reporte) {
        Intent intent = new Intent(this, ReporteSeguridadActivity.class);
        intent.putExtra("ID_REPORTE_EDITAR", reporte.getId());
        intent.putExtra("TIPO_REPORTE", reporte.getTipo());
        intent.putExtra("DESCRIPCION_REPORTE", reporte.getDescripcion());
        intent.putExtra("SEVERIDAD_REPORTE", reporte.getSeveridad());
        intent.putExtra("FOTOS_URIS", reporte.getFotosUris());

        startActivity(intent);
    }

}