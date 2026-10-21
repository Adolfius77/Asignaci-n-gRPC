import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

// Las peticiones a /api se mandan al cliente gRPC de Spring Boot (ClienteApp, puerto 8080)
// host true permite abrir la tienda desde otras computadoras de la misma red
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
});
