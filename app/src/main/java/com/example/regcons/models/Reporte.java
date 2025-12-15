package com.example.regcons.models;

public class Reporte {
    private long id;
    private String tipo;
    private String descripcion;
    private String severidad;
    private long fechaTimestamp;
    private String fotosUris;

    // Constructor
    public Reporte(long id, String tipo, String descripcion, String severidad, long fechaTimestamp, String fotosUris) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.severidad = severidad;
        this.fechaTimestamp = fechaTimestamp;
        this.fotosUris = fotosUris;
    }

    // Getters
    public long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getSeveridad() { return severidad; }
    public long getFechaTimestamp() { return fechaTimestamp; }
    public String getFotosUris() { return fotosUris; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }
}