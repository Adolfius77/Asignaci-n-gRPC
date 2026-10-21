import { Plus } from 'lucide-react';
import { dinero } from '../formato';
import { IconoProducto } from '../iconos';
import type { Producto } from '../tipos';

interface Props {
  productos: Producto[];
  cargando: boolean;
  onAgregar: (producto: Producto) => void;
}

export default function Catalogo({ productos, cargando, onAgregar }: Props) {
  return (
    <section className="panel">
      <h2>Productos</h2>
      {productos.length === 0 ? (
        <p className="vacio">{cargando ? 'Cargando productos del servidor…' : 'No hay productos, revisa la conexión'}</p>
      ) : (
        <ul className="productos">
          {productos.map((p) => (
            <li key={p.id} className="producto">
              <span className="icono">
                <IconoProducto nombre={p.icono} />
              </span>
              <div className="producto-datos">
                <span>{p.nombre}</span>
                <strong>{dinero(p.precio)}</strong>
              </div>
              <button className="btn" onClick={() => onAgregar(p)}>
                <Plus size={16} /> Agregar
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
