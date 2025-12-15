package com.example.regcons.ui.avance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.example.regcons.models.Obra;

import java.util.List;

public class ObraAdapter extends RecyclerView.Adapter<ObraAdapter.ObraViewHolder> {

    private Context context;
    private List<Obra> listaObras;
    private DbRegCons db;

    public ObraAdapter(Context context, List<Obra> listaObras) {
        this.context = context;
        this.listaObras = listaObras;
        this.db = new DbRegCons(context);
    }

    @NonNull
    @Override
    public ObraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_obra, parent, false);
        return new ObraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObraViewHolder holder, int position) {

        Obra obra = listaObras.get(position);

        // Datos básicos
        holder.txtNombre.setText(obra.getNombre());
        holder.txtDescripcion.setText(
                obra.getDescripcion() != null ? obra.getDescripcion() : "Sin descripción"
        );
        holder.txtEstado.setText(obra.getEstado());

        // Calcular avance
        int porcentaje = calcularAvance(obra.getId());
        holder.txtAvance.setText("Avance: " + porcentaje + "%");
        holder.progressBar.setProgress(porcentaje);

        // Estado visual y botón
        if ("FINALIZADA".equalsIgnoreCase(obra.getEstado())) {
            holder.txtEstado.setTextColor(
                    context.getResources().getColor(R.color.exito)
            );
            holder.btnFinalizar.setText("Reabrir");
        } else {
            holder.txtEstado.setTextColor(
                    context.getResources().getColor(R.color.advertencia)
            );
            holder.btnFinalizar.setText("Finalizar");
        }

        // Editar obra
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormularioObra.class);
            intent.putExtra("ID_OBRA", obra.getId());
            intent.putExtra("NOMBRE_OBRA", obra.getNombre());
            context.startActivity(intent);
        });

        // Eliminar obra (con validación de avances)
        holder.btnEliminar.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Obra obraSeleccionada = listaObras.get(pos);

            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Confirmar eliminación")
                    .setMessage(
                            "Esta acción eliminará la obra y todos sus avances asociados.\n\n" +
                                    "¿Desea continuar?"
                    )
                    .setCancelable(false)
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> {

                        db.eliminarAvancesPorObra(obraSeleccionada.getId());

                        db.eliminarObra(obraSeleccionada.getId());

                        listaObras.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, listaObras.size());

                        Toast.makeText(
                                context,
                                "Obra eliminada correctamente",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Finalizar / Reabrir obra
        holder.btnFinalizar.setOnClickListener(v -> {

            String estadoActual = obra.getEstado();
            String nuevoEstado;

            if ("FINALIZADA".equalsIgnoreCase(estadoActual)) {
                nuevoEstado = DbRegCons.ESTADO_PENDIENTE;
            } else {
                nuevoEstado = "FINALIZADA";
            }

            ContentValues values = new ContentValues();
            values.put(DbRegCons.COL_OBRA_ESTADO, nuevoEstado);

            int filas = db.actualizarObra(obra.getId(), values);

            if (filas > 0) {
                obra.setEstado(nuevoEstado);
                notifyItemChanged(position);

                Toast.makeText(
                        context,
                        "Estado actualizado a " + nuevoEstado,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaObras.size();
    }

    // Cálculo de avance general de la obra
    private int calcularAvance(int idObra) {
        return db.obtenerPorcentajeAvance(idObra);
    }

    // ViewHolder
    static class ObraViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtDescripcion, txtEstado, txtAvance;
        ProgressBar progressBar;
        Button btnEditar, btnFinalizar, btnEliminar;

        public ObraViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreObra);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionObra);
            txtEstado = itemView.findViewById(R.id.txtEstadoObra);
            txtAvance = itemView.findViewById(R.id.txtAvancePorcentaje);
            progressBar = itemView.findViewById(R.id.progressBar2);

            btnEditar = itemView.findViewById(R.id.item_obra_btnEditar);
            btnFinalizar = itemView.findViewById(R.id.item_obra_btnFinalizar);
            btnEliminar = itemView.findViewById(R.id.item_obra_btnEliminar);
        }
    }
}


