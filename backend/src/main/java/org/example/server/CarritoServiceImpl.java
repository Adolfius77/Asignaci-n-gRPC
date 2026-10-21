package org.example.server;

import io.grpc.stub.StreamObserver;
import org.example.grpc.CarritoRequest;
import org.example.grpc.CarritoResponse;
import org.example.grpc.CarritoServiceGrpc;
import org.example.grpc.ListaProductos;
import org.example.grpc.Producto;
import org.example.grpc.Vacio;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@GRpcService
public class CarritoServiceImpl extends CarritoServiceGrpc.CarritoServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(CarritoServiceImpl.class);
    private final Inventario inventario;

    public CarritoServiceImpl(Inventario inventario) {
        this.inventario = inventario;
    }


    @Override
    public void obtenerProductos(Vacio request, StreamObserver<ListaProductos> responseObserver) {
        logger.info("Petición de catálogo");
        ListaProductos.Builder lista = ListaProductos.newBuilder();
        for (ProductoInventario p : inventario.listar()) {
            lista.addProductos(Producto.newBuilder()
                    .setId(p.getId())
                    .setNombre(p.getNombre())
                    .setPrecio(p.getPrecio())
                    .setIcono(p.getIcono())
                    .build());
        }
        responseObserver.onNext(lista.build());
        responseObserver.onCompleted();
    }


    @Override
    public void procesarCarrito(CarritoRequest request, StreamObserver<CarritoResponse> responseObserver) {
        String cliente = request.getCliente().isBlank() ? "Anónimo" : request.getCliente();

        List<LineaCarrito> lineas = request.getItemsList().stream()
                .map(i -> new LineaCarrito(i.getProductoId(), i.getCantidad(), i.getPrecio()))
                .toList();

        RegistroOrden r = inventario.procesarCarrito(cliente, lineas);
        logger.info("Orden #{} de {} {}", r.folio(), cliente, r.aceptada() ? "ACEPTADA" : "CANCELADA " + r.mensaje());
        r.detallesServidor().forEach(d -> logger.info("   {}", d));

        CarritoResponse respuesta = CarritoResponse.newBuilder()
                .setExito(r.aceptada())
                .setFolio(r.folio())
                .setMensaje(r.mensaje())
                .setTotal(r.total())
                .addAllDetalles(r.detallesCliente())
                .build();
        responseObserver.onNext(respuesta);
        responseObserver.onCompleted();
    }
}
