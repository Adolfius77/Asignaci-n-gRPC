package org.example.server;

import java.time.LocalTime;
import java.util.List;


public record RegistroOrden(
        int folio,
        String cliente,
        LocalTime hora,
        boolean aceptada,
        double total,
        String productos,
        String mensaje,
        List<String> detallesCliente,
        List<String> detallesServidor) {
}
