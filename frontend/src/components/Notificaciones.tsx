import { CircleCheck, CircleX, X } from 'lucide-react';
import type { Notificacion } from '../hooks/useNotificaciones';

interface Props {
  lista: Notificacion[];
  onCerrar: (id: number) => void;
}

/** Mensajes de éxito y error */
export default function Notificaciones({ lista, onCerrar }: Props) {
  return (
    <div className="notificaciones" aria-live="polite">
      {lista.map((n) => (
        <div key={n.id} className={`aviso ${n.tipo}`} role={n.tipo === 'error' ? 'alert' : 'status'}>
          {n.tipo === 'exito' ? <CircleCheck size={20} /> : <CircleX size={20} />}
          <div className="aviso-texto">
            <strong>{n.titulo}</strong>
            {n.detalles && n.detalles.length > 0 && (
              <ul>
                {n.detalles.map((d, i) => (
                  <li key={i}>{d}</li>
                ))}
              </ul>
            )}
          </div>
          <button onClick={() => onCerrar(n.id)} aria-label="Cerrar">
            <X size={16} />
          </button>
        </div>
      ))}
    </div>
  );
}
