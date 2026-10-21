import { Store } from 'lucide-react';
import { useState } from 'react';
import Tienda from './components/Tienda';

export default function App() {
  const [nombre, setNombre] = useState<string | null>(null);
  const [borrador, setBorrador] = useState(`Cliente ${Math.floor(100 + Math.random() * 900)}`);

  if (nombre) return <Tienda nombre={nombre} />;

  return (
    <div className="inicio">
      <form
        className="caja"
        onSubmit={(e) => {
          e.preventDefault();
          if (borrador.trim()) setNombre(borrador.trim());
        }}
      >
        <h1>
          <Store size={22} /> Tienda
        </h1>
        <label htmlFor="nombre">Nombre del cliente</label>
        <input id="nombre" value={borrador} onChange={(e) => setBorrador(e.target.value)} maxLength={30} autoFocus />
        <button className="btn primario" type="submit" disabled={!borrador.trim()}>
          Entrar
        </button>
      </form>
    </div>
  );
}
