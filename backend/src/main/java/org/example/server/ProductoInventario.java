package org.example.server;

public class ProductoInventario {
    private final String id;
    private final String nombre;
    private final double precio;
    private final String icono;
    private final int existenciaInicial;
    private int existencia;

    public ProductoInventario(String id, String nombre, double precio, String icono, int existencia) {
        this(id, nombre, precio, icono, existencia, existencia);
    }

    private ProductoInventario(String id, String nombre, double precio, String icono, int existencia, int existenciaInicial) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.icono = icono;
        this.existencia = existencia;
        this.existenciaInicial = existenciaInicial;
    }

    public ProductoInventario copia() {
        return new ProductoInventario(id, nombre, precio, icono, existencia, existenciaInicial);
    }

    void descontar(int cantidad) {
        existencia -= cantidad;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getIcono() { return icono; }
    public int getExistencia() { return existencia; }
    public int getExistenciaInicial() { return existenciaInicial; }
}
