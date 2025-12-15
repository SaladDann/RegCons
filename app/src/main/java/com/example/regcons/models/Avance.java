package com.example.regcons.models;

public class Avance {

    private int id;
    private int idObra;
    private String nombre;
    private boolean finalizado;
    private String fotoPath;

    private String descripcion;

    private String fecha;


    public Avance(int id, int idObra, String nombre, boolean finalizado,
                  String fotoPath, String descripcion, String fecha) {
        this.id = id;
        this.idObra = idObra;
        this.nombre = nombre;
        this.finalizado = finalizado;
        this.fotoPath = fotoPath;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Avance(int idObra, String nombre) {
        this.idObra = idObra;
        this.nombre = nombre;
        this.finalizado = false;
        this.fotoPath = null;
        this.descripcion = null;
        this.fecha = null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getIdObra() {
        return idObra;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public String getFecha() {
        return fecha;
    }



    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setIdObra(int idObra) {
        this.idObra = idObra;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


}
