import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  root: path.resolve(__dirname),
  build: {
    outDir: path.resolve(__dirname, '../resources/static/react'),
    emptyOutDir: false,
    rollupOptions: {
      input: {
        register: path.resolve(__dirname, 'register.jsx'),
      },
      output: {
        entryFileNames: '[name].js',
      },
    },
  },
});
