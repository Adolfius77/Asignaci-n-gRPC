export interface Producto {
  id: string;
  nombre: string;
  precio: number;
  icono: string;
}

/** Renglón del carrito en la interfaz */
export interface LineaCarrito {
  producto: Producto;
  cantidad: number;
}

/** Lo que se manda al cliente de Spring */
export interface Carrito {
  cliente: string;
  items: { productoId: string; cantidad: number; precio: number }[];
}

export interface ResultadoOrden {
  exito: boolean;
  folio: number;
  mensaje: string;
  total: number;
  detalles: string[];
}
