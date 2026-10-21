package org.example.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class ValidadorCarrito {

    private ValidadorCarrito() {
    }

    public static List<String> validar(List<LineaCarrito> lineas, Map<String, ProductoInventario> productos) {
        if (lineas == null || lineas.isEmpty()) {
            return List.of("El carrito está vacío, agrega al menos un producto");
        }

        List<String> errores = new ArrayList<>();
        for (int i = 0; i < lineas.size(); i++) {
            LineaCarrito linea = lineas.get(i);
            ProductoInventario producto = productos.get(linea.productoId());
            String nombre = producto != null ? "de " + producto.getNombre() : "del renglón " + (i + 1);

            if (producto == null) {
                errores.add("El producto " + linea.productoId() + " no existe en el catálogo");
            }
            if (linea.cantidad() <= 0) {
                errores.add("La cantidad " + nombre + " debe ser mayor que cero (llegó " + linea.cantidad() + ")");
            }
            if (linea.precio() <= 0) {
                errores.add("El precio " + nombre + " debe ser mayor que cero (llegó " + linea.precio() + ")");
            } else if (producto != null && Math.abs(linea.precio() - producto.getPrecio()) > 0.001) {
                errores.add("El precio " + nombre + " no coincide con el catálogo del servidor");
            }
        }
        return errores;
    }
}
