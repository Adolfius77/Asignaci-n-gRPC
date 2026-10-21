import { Minus, Plus, ShoppingCart, Trash2 } from 'lucide-react';
import { dinero } from '../formato';
import type { LineaCarrito } from '../tipos';

interface Props {
  lineas: LineaCarrito[];
  enviando: boolean;
  conectado: boolean;
  onCambiar: (id: string, cantidad: number) => void;
  onEnviar: () => void;
}

export default function Carrito({ lineas, enviando, conectado, onCambiar, onEnviar }: Props) {
  const total = lineas.reduce((s, l) => s + l.cantidad * l.producto.precio, 0);

  return (
    <section className="panel">
      <h2>
        <ShoppingCart size={18} /> Carrito
      </h2>

      {lineas.length === 0 ? (
        <p className="vacio">El carrito está vacío</p>
      ) : (
        <ul className="lineas">
          {lineas.map((l) => (
            <li key={l.producto.id}>
              <span className="linea-nombre">{l.producto.nombre}</span>
              <div className="cantidad">
                <button onClick={() => onCambiar(l.producto.id, l.cantidad - 1)} aria-label="Quitar uno">
                  <Minus size={14} />
                </button>
                <span>{l.cantidad}</span>
                <button onClick={() => onCambiar(l.producto.id, l.cantidad + 1)} aria-label="Agregar uno">
                  <Plus size={14} />
                </button>
              </div>
              <span className="linea-subtotal">{dinero(l.cantidad * l.producto.precio)}</span>
              <button className="quitar" onClick={() => onCambiar(l.producto.id, 0)} aria-label="Eliminar">
                <Trash2 size={16} />
              </button>
            </li>
          ))}
        </ul>
      )}

      <div className="total">
        <span>Total</span>
        <strong>{dinero(total)}</strong>
      </div>

      <button className="btn primario ancho" onClick={onEnviar} disabled={lineas.length === 0 || enviando || !conectado}>
        {enviando ? 'Enviando…' : 'Enviar pedido'}
      </button>
    </section>
  );
}
