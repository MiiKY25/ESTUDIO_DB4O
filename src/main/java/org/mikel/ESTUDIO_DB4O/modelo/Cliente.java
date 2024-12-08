package org.mikel.ESTUDIO_DB4O.modelo;

import java.util.Objects;

public class Cliente {

    private int id;
    private String nombre;
    private Producto producto; // Relación N:M con producto

    public Cliente(int id, String nombre, Producto producto) {
        this.id = id;
        this.nombre = nombre;
        this.producto = producto;
    }

    public Cliente() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id == cliente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
