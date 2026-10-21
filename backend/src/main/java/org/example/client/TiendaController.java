package org.example.client;

import io.grpc.StatusRuntimeException;
import org.example.grpc.CarritoRequest;
import org.example.grpc.CarritoResponse;
import org.example.grpc.CarritoServiceGrpc;
import org.example.grpc.ItemCarrito;
import org.example.grpc.ListaProductos;
import org.example.grpc.Vacio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TiendaController {

    private final Logger logger = LoggerFactory.getLogger(TiendaController.class);
    private final CarritoServiceGrpc.CarritoServiceBlockingStub stub;

    public TiendaController(CarritoServiceGrpc.CarritoServiceBlockingStub stub) {
        this.stub = stub;
    }


    public record ProductoDto(String id, String nombre, double precio, String icono) {}
    public record ItemDto(String productoId, Integer cantidad, Double precio) {}
    public record CarritoDto(String cliente, List<ItemDto> items) {}
    public record RespuestaDto(boolean exito, int folio, String mensaje, double total, List<String> detalles) {}


    @GetMapping("/productos")
    public List<ProductoDto> productos() {
        ListaProductos lista = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .obtenerProductos(Vacio.getDefaultInstance());
        return lista.getProductosList().stream()
                .map(p -> new ProductoDto(p.getId(), p.getNombre(), p.getPrecio(), p.getIcono()))
                .toList();
    }


    @PostMapping("/carrito")
    public RespuestaDto carrito(@RequestBody CarritoDto carrito) {
        CarritoRequest.Builder peticion = CarritoRequest.newBuilder()
                .setCliente(carrito.cliente() == null ? "" : carrito.cliente());

        if (carrito.items() != null) {
            for (ItemDto item : carrito.items()) {
                peticion.addItems(ItemCarrito.newBuilder()
                        .setProductoId(item.productoId() == null ? "" : item.productoId())
                        .setCantidad(item.cantidad() == null ? 0 : item.cantidad())
                        .setPrecio(item.precio() == null ? 0 : item.precio())
                        .build());
            }
        }

        CarritoResponse r = stub.withDeadlineAfter(5, TimeUnit.SECONDS).procesarCarrito(peticion.build());
        logger.info("Orden #{} {}", r.getFolio(), r.getExito() ? "aceptada" : "cancelada");
        return new RespuestaDto(r.getExito(), r.getFolio(), r.getMensaje(), r.getTotal(), List.copyOf(r.getDetallesList()));
    }


    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<RespuestaDto> sinServidor(StatusRuntimeException e) {
        logger.warn("No se pudo contactar al servidor gRPC {}", e.getStatus().getCode());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new RespuestaDto(false, 0, "No hay conexión con el servidor gRPC", 0,
                        List.of("Revisa que ServidorApp esté corriendo en el puerto 6565")));
    }
}
