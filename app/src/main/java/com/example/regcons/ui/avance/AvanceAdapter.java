package com.example.regcons.ui.avance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.regcons.R;
import com.example.regcons.db.DbRegCons;
import com.example.regcons.models.Avance;

import java.io.File;
import java.util.List;

public class AvanceAdapter extends RecyclerView.Adapter<AvanceAdapter.AvanceViewHolder> {

    private final Context context;
    private final List<Avance> listaAvances;
    private final DbRegCons db;

    public AvanceAdapter(Context context, List<Avance> listaAvances) {
        this.context = context;
        this.listaAvances = listaAvances;
        this.db = new DbRegCons(context);
    }

    @NonNull
    @Override
    public AvanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_avance, parent, false);
        return new AvanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvanceViewHolder holder, int position) {

        Avance avance = listaAvances.get(position);

        // Datos básicos
        holder.txtNombre.setText(avance.getNombre());
        holder.txtDescripcion.setText(
                avance.getDescripcion() != null && !avance.getDescripcion().isEmpty()
                        ? avance.getDescripcion()
                        : "Sin descripción"
        );
        holder.txtFecha.setText(avance.getFecha());

        // Estado
        if (avance.isFinalizado()) {
            holder.txtEstado.setText("FINALIZADO");
            holder.btnToggleEstado.setText("Reabrir");
        } else {
            holder.txtEstado.setText("PENDIENTE");
            holder.btnToggleEstado.setText("Finalizar");
        }

        // Evidencia fotográfica
        String rutaFoto = avance.getFotoPath();
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            File archivo = new File(rutaFoto);
            if (archivo.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(archivo.getAbsolutePath());
                holder.imgEvidencia.setImageBitmap(bitmap);
                holder.imgEvidencia.setVisibility(View.VISIBLE);
            } else {
                holder.imgEvidencia.setVisibility(View.GONE);
            }
        } else {
            holder.imgEvidencia.setVisibility(View.GONE);
        }

        // Cambiar estado
        holder.btnToggleEstado.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Avance a = listaAvances.get(pos);
            boolean nuevoEstado = !a.isFinalizado();
            a.setFinalizado(nuevoEstado);

            db.actualizarAvance(
                    a.getId(),
                    a.getIdObra(),
                    a.getNombre(),
                    nuevoEstado,
                    a.getFotoPath()
            );

            notifyItemChanged(pos);
        });

        // Eliminar avance (BD + archivo)
        holder.btnEliminar.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Avance a = listaAvances.get(pos);

            try {
                db.eliminarAvance(a.getId());

                listaAvances.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, listaAvances.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAvances.size();
    }

    static class AvanceViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtDescripcion, txtFecha, txtEstado;
        Button btnToggleEstado, btnEliminar;
        ImageView imgEvidencia;

        public AvanceViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreAvance);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionAvance);
            txtFecha = itemView.findViewById(R.id.txtFechaAvance);
            txtEstado = itemView.findViewById(R.id.txtEstadoAvance);

            btnToggleEstado = itemView.findViewById(R.id.btnToggleEstado);
            btnEliminar = itemView.findViewById(R.id.btnEliminarAvance);
            imgEvidencia = itemView.findViewById(R.id.imgEvidencia);
        }
    }
}



