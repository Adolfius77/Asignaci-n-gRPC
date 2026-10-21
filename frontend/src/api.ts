import type { Carrito, Producto, ResultadoOrden } from './tipos';

// Vite redirige /api al cliente gRPC de Spring (puerto 8080), ver vite.config.ts
const BASE = '/api';

/** Pide el catálogo, el cliente de Spring lo obtiene con la llamada gRPC ObtenerProductos */
export async function obtenerProductos(): Promise<Producto[]> {
  const res = await fetch(`${BASE}/productos`);
  if (!res.ok) throw new Error('No se pudo obtener el catálogo');
  return res.json();
}

/** Manda el carrito, el cliente de Spring lo envía con la llamada gRPC ProcesarCarrito */
export async function enviarCarrito(carrito: Carrito): Promise<ResultadoOrden> {
  const res = await fetch(`${BASE}/carrito`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(carrito)
  });
  // Si el servidor gRPC está apagado el cliente de Spring responde 503 con un mensaje
  if (!res.ok && res.status !== 503) throw new Error('Error al enviar el carrito');
  return res.json();
}
