package com.example.regcons.models;

public class Obra {

    private int id;
    private String nombre;
    private String estado;
    private String descripcion;

    public Obra() {
    }

    public Obra(int id, String nombre, String estado, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public Obra(String nombre) {
        this.nombre = nombre;
        this.estado = "PENDIENTE";
        this.descripcion = null;
    }


    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}