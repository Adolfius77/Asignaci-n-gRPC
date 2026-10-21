package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.CarritoRequest;
import org.example.grpc.CarritoResponse;
import org.example.grpc.CarritoServiceGrpc;
import org.example.grpc.ItemCarrito;
import org.example.grpc.ListaProductos;
import org.example.grpc.Producto;
import org.example.grpc.Vacio;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class PruebaVariosClientes {

    public static void main(String[] args) throws InterruptedException {
        int numeroClientes = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        ExecutorService hilos = Executors.newFixedThreadPool(numeroClientes);
        CountDownLatch listos = new CountDownLatch(numeroClientes);
        CountDownLatch salida = new CountDownLatch(1);

        System.out.println("Conectando " + numeroClientes + " clientes al servidor gRPC\n");

        for (int i = 1; i <= numeroClientes; i++) {
            String nombre = "Cliente-" + i;
            hilos.submit(() -> {
                ManagedChannel canal = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
                try {
                    CarritoServiceGrpc.CarritoServiceBlockingStub stub = CarritoServiceGrpc.newBlockingStub(canal);
                    List<Producto> catalogo = stub.obtenerProductos(Vacio.getDefaultInstance()).getProductosList();

                    Producto monitor = catalogo.stream().filter(p -> p.getId().equals("P002")).findFirst().orElseThrow();
                    Producto otro = catalogo.get(new Random().nextInt(catalogo.size()));
                    CarritoRequest carrito = CarritoRequest.newBuilder()
                            .setCliente(nombre)
                            .addItems(item(monitor, 1 + new Random().nextInt(2)))
                            .addItems(item(otro, 1))
                            .build();

                    listos.countDown();
                    salida.await();   // todos mandan su carrito al mismo tiempo

                    imprimir(nombre, stub.procesarCarrito(carrito));
                } catch (Exception e) {
                    System.out.println(nombre + " no pudo conectarse " + e.getMessage());
                    listos.countDown();
                } finally {
                    canal.shutdownNow();
                }
            });
        }

        listos.await(10, TimeUnit.SECONDS);
        System.out.println("Enviando carritos al mismo tiempo\n");
        salida.countDown();
        hilos.shutdown();
        hilos.awaitTermination(30, TimeUnit.SECONDS);

        probarValidaciones();
    }

    private static void probarValidaciones() {
        System.out.println("\nProbando validaciones\n");
        ManagedChannel canal = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        try {
            CarritoServiceGrpc.CarritoServiceBlockingStub stub = CarritoServiceGrpc.newBlockingStub(canal);
            ListaProductos lista = stub.obtenerProductos(Vacio.getDefaultInstance());
            Producto mouse = lista.getProductos(2);

            enviar(stub, "Carrito vacío", CarritoRequest.newBuilder().setCliente("Prueba vacío").build());
            enviar(stub, "Cantidad 0", CarritoRequest.newBuilder().setCliente("Prueba cantidad")
                    .addItems(item(mouse, 0)).build());
            enviar(stub, "Precio 0", CarritoRequest.newBuilder().setCliente("Prueba precio")
                    .addItems(ItemCarrito.newBuilder().setProductoId(mouse.getId()).setCantidad(1).setPrecio(0)).build());
            enviar(stub, "Negativos", CarritoRequest.newBuilder().setCliente("Prueba negativos")
                    .addItems(ItemCarrito.newBuilder().setProductoId(mouse.getId()).setCantidad(-3).setPrecio(-10)).build());
        } finally {
            canal.shutdownNow();
        }
    }

    private static ItemCarrito item(Producto p, int cantidad) {
        return ItemCarrito.newBuilder().setProductoId(p.getId()).setCantidad(cantidad).setPrecio(p.getPrecio()).build();
    }

    private static void enviar(CarritoServiceGrpc.CarritoServiceBlockingStub stub, String nombre, CarritoRequest carrito) {
        imprimir(nombre, stub.procesarCarrito(carrito));
    }

    private static synchronized void imprimir(String nombre, CarritoResponse r) {
        System.out.printf("#%-3d %-10s %-15s %s%n", r.getFolio(), r.getExito() ? "ACEPTADA" : "CANCELADA", nombre, r.getMensaje());
        r.getDetallesList().forEach(d -> System.out.println("                                - " + d));
    }
}
