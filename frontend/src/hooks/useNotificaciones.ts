import { useCallback, useRef, useState } from 'react';

export type TipoNotificacion = 'exito' | 'error';

export interface Notificacion {
  id: number;
  tipo: TipoNotificacion;
  titulo: string;
  detalles?: string[];
}

export function useNotificaciones() {
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>([]);
  const contador = useRef(0);

  const cerrar = useCallback((id: number) => {
    setNotificaciones((lista) => lista.filter((n) => n.id !== id));
  }, []);

  const notificar = useCallback(
    (tipo: TipoNotificacion, titulo: string, detalles?: string[]) => {
      const id = ++contador.current;
      setNotificaciones((lista) => [...lista, { id, tipo, titulo, detalles }].slice(-3));
      setTimeout(() => cerrar(id), tipo === 'exito' ? 4000 : 7000);
    },
    [cerrar]
  );

  return { notificaciones, notificar, cerrar };
}
