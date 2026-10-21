package org.example.server;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class Inventario {

    private static final int MAX_ORDENES = 100;

    private final Map<String, ProductoInventario> productos = new LinkedHashMap<>();
    private final List<RegistroOrden> ordenes = new ArrayList<>();
    private int siguienteFolio = 1;

    public Inventario() {
        reabastecer();
    }

    public synchronized void reabastecer() {
        productos.clear();
        agregar(new ProductoInventario("P001", "Laptop 15 pulgadas", 14999, "laptop", 5));
        agregar(new ProductoInventario("P002", "Monitor 24 pulgadas", 3199, "monitor", 3));
        agregar(new ProductoInventario("P003", "Mouse inalámbrico", 349, "mouse", 20));
        agregar(new ProductoInventario("P004", "Teclado mecánico", 1299, "teclado", 10));
        agregar(new ProductoInventario("P005", "Cámara web HD", 749, "camara", 6));
        agregar(new ProductoInventario("P006", "Audífonos Bluetooth", 899, "audifonos", 8));
        agregar(new ProductoInventario("P007", "Bocina portátil", 1099, "bocina", 4));
        agregar(new ProductoInventario("P008", "Memoria USB 64 GB", 189, "usb", 25));
    }

    private void agregar(ProductoInventario p) {
        productos.put(p.getId(), p);
    }


    public synchronized List<ProductoInventario> listar() {
        return productos.values().stream().map(ProductoInventario::copia).toList();
    }

    public synchronized List<RegistroOrden> ordenes() {
        return List.copyOf(ordenes);
    }


    public synchronized RegistroOrden procesarCarrito(String cliente, List<LineaCarrito> lineas) {
        int folio = siguienteFolio++;
        String resumen = resumir(lineas);
        RegistroOrden registro;

        List<String> errores = ValidadorCarrito.validar(lineas, productos);
        if (!errores.isEmpty()) {
            registro = cancelada(folio, cliente, resumen, "El carrito no es válido y fue rechazado", errores, errores);
        } else {
            Map<String, Integer> solicitados = new LinkedHashMap<>();
            for (LineaCarrito l : lineas) {
                solicitados.merge(l.productoId(), l.cantidad(), Integer::sum);
            }

            List<String> faltantesCliente = new ArrayList<>();
            List<String> faltantesServidor = new ArrayList<>();
            solicitados.forEach((id, cantidad) -> {
                ProductoInventario p = productos.get(id);
                if (p.getExistencia() < cantidad) {
                    faltantesCliente.add("No hay suficiente existencia de " + p.getNombre() + " (solicitaste " + cantidad + ")");
                    faltantesServidor.add(p.getNombre() + " solicitó " + cantidad + " y solo hay " + p.getExistencia());
                }
            });

            if (!faltantesCliente.isEmpty()) {
                registro = cancelada(folio, cliente, resumen, "Inventario insuficiente, se canceló toda la orden",
                        faltantesCliente, faltantesServidor);
            } else {
                double total = 0;
                int piezas = 0;
                for (Map.Entry<String, Integer> e : solicitados.entrySet()) {
                    ProductoInventario p = productos.get(e.getKey());
                    p.descontar(e.getValue());
                    total += e.getValue() * p.getPrecio();
                    piezas += e.getValue();
                }
                total = Math.round(total * 100) / 100.0;
                registro = new RegistroOrden(folio, cliente, LocalTime.now(), true, total, resumen,
                        "Tu pedido de " + piezas + " pieza(s) fue procesado correctamente", List.of(), List.of());
            }
        }

        ordenes.add(0, registro);
        if (ordenes.size() > MAX_ORDENES) {
            ordenes.remove(ordenes.size() - 1);
        }
        return registro;
    }

    private RegistroOrden cancelada(int folio, String cliente, String resumen, String mensaje,
                                    List<String> detallesCliente, List<String> detallesServidor) {
        return new RegistroOrden(folio, cliente, LocalTime.now(), false, 0, resumen, mensaje,
                List.copyOf(detallesCliente), List.copyOf(detallesServidor));
    }

    private String resumir(List<LineaCarrito> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            return "Sin productos";
        }
        return lineas.stream().map(l -> {
            ProductoInventario p = productos.get(l.productoId());
            String nombre = p != null ? p.getNombre() : l.productoId();
            return l.cantidad() + " x " + nombre;
        }).collect(Collectors.joining(", "));
    }
}
