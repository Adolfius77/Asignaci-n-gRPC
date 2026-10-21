import { Headphones, Keyboard, Laptop, Monitor, Mouse, Package, Speaker, Usb, Webcam, type LucideIcon } from 'lucide-react';

// El servidor manda el nombre del icono y aquí se traduce al componente
const ICONOS: Record<string, LucideIcon> = {
  laptop: Laptop,
  monitor: Monitor,
  mouse: Mouse,
  teclado: Keyboard,
  camara: Webcam,
  audifonos: Headphones,
  bocina: Speaker,
  usb: Usb
};

export function IconoProducto({ nombre, tamano = 20 }: { nombre: string; tamano?: number }) {
  const Icono = ICONOS[nombre] ?? Package;
  return <Icono size={tamano} strokeWidth={1.75} />;
}
