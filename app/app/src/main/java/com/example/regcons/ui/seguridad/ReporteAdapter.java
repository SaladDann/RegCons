package com.example.regcons.ui.seguridad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.regcons.R;
import com.example.regcons.model.Reporte;

import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {

    private List<Reporte> reportesList;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEditClick(Reporte reporte);
        void onDeleteClick(Reporte reporte);
    }

    public ReporteAdapter(List<Reporte> reportesList, OnItemActionListener listener) {
        this.reportesList = reportesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reporte, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        final Reporte reporte = reportesList.get(position);

        holder.tvTipoSeveridad.setText(reporte.getTipo() + " | Severidad: " + reporte.getSeveridad());
        holder.tvDescripcion.setText(reporte.getDescripcion());

        holder.btnEditar.setOnClickListener(v -> listener.onEditClick(reporte));
        holder.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(reporte));
    }

    @Override
    public int getItemCount() {
        return reportesList.size();
    }

    public void updateList(List<Reporte> newList) {
        reportesList = newList;
        notifyDataSetChanged();
    }

    static class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipoSeveridad, tvDescripcion;
        Button btnEditar, btnEliminar;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoSeveridad = itemView.findViewById(R.id.tv_tipo_severidad);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
            btnEditar = itemView.findViewById(R.id.btn_editar_reporte);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_reporte);
        }
    }
}