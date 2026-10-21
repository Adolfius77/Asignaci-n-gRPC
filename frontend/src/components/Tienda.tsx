import { RotateCw, Store } from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';
import { enviarCarrito, obtenerProductos } from '../api';
import { useNotificaciones } from '../hooks/useNotificaciones';
import type { LineaCarrito, Producto } from '../tipos';
import Carrito from './Carrito';
import Catalogo from './Catalogo';
import Notificaciones from './Notificaciones';

export default function Tienda({ nombre }: { nombre: string }) {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [conectado, setConectado] = useState(false);
  const [cargando, setCargando] = useState(true);
  const [lineas, setLineas] = useState<LineaCarrito[]>([]);
  const [enviando, setEnviando] = useState(false);
  const { notificaciones, notificar, cerrar } = useNotificaciones();

  // Consulta remota del catálogo
  const cargarProductos = useCallback(async () => {
    setCargando(true);
    try {
      setProductos(await obtenerProductos());
      setConectado(true);
    } catch {
      setConectado(false);
      notificar('error', 'No se pudo conectar con el servidor', [
        'Revisa que ServidorApp y ClienteApp estén corriendo'
      ]);
    } finally {
      setCargando(false);
    }
  }, [notificar]);

  useEffect(() => {
    cargarProductos();
  }, [cargarProductos]);

  const agregar = (producto: Producto) => {
    setLineas((prev) =>
      prev.some((l) => l.producto.id === producto.id)
        ? prev.map((l) => (l.producto.id === producto.id ? { ...l, cantidad: l.cantidad + 1 } : l))
        : [...prev, { producto, cantidad: 1 }]
    );
  };

  const cambiarCantidad = (id: string, cantidad: number) => {
    if (cantidad <= 0) {
      setLineas((prev) => prev.filter((l) => l.producto.id !== id));
    } else {
      setLineas((prev) => prev.map((l) => (l.producto.id === id ? { ...l, cantidad } : l)));
    }
  };

  const enviar = async () => {
    setEnviando(true);
    try {
      const r = await enviarCarrito({
        cliente: nombre,
        items: lineas.map((l) => ({ productoId: l.producto.id, cantidad: l.cantidad, precio: l.producto.precio }))
      });
      if (r.exito) {
        notificar('exito', `Orden #${r.folio} aceptada`, [r.mensaje]);
        setLineas([]);
      } else {
        notificar('error', r.mensaje, r.detalles);
      }
    } catch {
      notificar('error', 'No se pudo enviar el carrito', ['Revisa que ClienteApp esté corriendo']);
    } finally {
      setEnviando(false);
    }
  };

  return (
    <>
      <header className="encabezado">
        <h1>
          <Store size={20} /> Tienda
        </h1>
        <div className="encabezado-info">
          <span>{nombre}</span>
          <span className={`punto ${conectado ? 'on' : 'off'}`}>{conectado ? 'Conectado' : 'Sin conexión'}</span>
          {!conectado && !cargando && (
            <button className="btn" onClick={cargarProductos}>
              <RotateCw size={16} /> Reintentar
            </button>
          )}
        </div>
      </header>

      <main className="principal">
        <Catalogo productos={productos} cargando={cargando} onAgregar={agregar} />
        <Carrito lineas={lineas} enviando={enviando} conectado={conectado} onCambiar={cambiarCantidad} onEnviar={enviar} />
      </main>

      <Notificaciones lista={notificaciones} onCerrar={cerrar} />
    </>
  );
}
