package com.github.YeahBaby666.ArchivoGestorV2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    private final ArchivoManager gestor;

    public FileManager(String rutaRaiz) throws IOException {
        File raiz = new File(rutaRaiz);
        if (raiz.exists() && !raiz.isDirectory()) {
            throw new IllegalArgumentException("La ruta especificada existe pero no es un directorio: " + rutaRaiz);
        }
        if (!raiz.exists()) {
            System.out.println("Directorio raíz no encontrado. Creándolo en: " + raiz.getAbsolutePath());
            if (!raiz.mkdirs()) {
                throw new IOException("No se pudo crear el directorio raíz en la ruta: " + rutaRaiz);
            }
        }
        this.gestor = new ArchivoManager(raiz);
        System.out.println("✅ FileManager iniciado en: " + this.gestor.getMarcadorActual().getAbsolutePath());
    }

    public String getRutaActual() { return gestor.getMarcadorActual().getAbsolutePath(); }
    
    public List<String> listarContenido() {
        File[] archivos = gestor.listarContenido();
        if (archivos == null || archivos.length == 0) return List.of("(Directorio vacío)");
        return Arrays.stream(archivos).map(f -> (f.isDirectory() ? "📁" : "📄") + " " + f.getName()).collect(Collectors.toList());
    }
    
    public void cambiarDirectorio(String nombre) throws Exception {
        gestor.cambiarMarcador(gestor.obtenerElemento(nombre));
        System.out.println("📍 Marcador cambiado a: " + getRutaActual());
    }
    
    public void subirDirectorio() throws Exception {
        File padre = gestor.getMarcadorActual().getParentFile();
        if (padre != null && padre.exists()) {
            gestor.cambiarMarcador(padre);
            System.out.println("📍 Marcador cambiado a: " + getRutaActual());
        } else {
            throw new Exception("Ya estás en el directorio raíz.");
        }
    }
    
    public String leer(String nombre) throws Exception {
        return ArchivosUtil.IntArchivo.leerTodo(gestor.obtenerElemento(nombre));
    }

    public String leer(String nombreArchivo, int linea) throws Exception {
        return ArchivosUtil.IntArchivo.leerLineaPorIndice(gestor.obtenerElemento(nombreArchivo), linea);
    }

    public String leer(String nombreArchivo, String clave, boolean soloValor) throws Exception {
        File archivo = gestor.obtenerElemento(nombreArchivo);
        if (soloValor) {
            String valor = ArchivosUtil.IntArchivo.buscarValorPorClave(archivo, clave);
            return valor != null ? valor : "Clave '" + clave + "' no encontrada.";
        } else {
            for (String lineaStr : ArchivosUtil.IntArchivo.leerLineas(archivo)) {
                if (lineaStr.trim().replaceAll("^[\"']", "").startsWith(clave)) {
                    return lineaStr;
                }
            }
            return "Línea con clave '" + clave + "' no encontrada.";
        }
    }
    
    public void crearArchivo(String nombre, String contenido) throws IOException {
        ArchivosUtil.IntArchivo.escribirArchivo(gestor.obtenerElemento(nombre), contenido, false);
        System.out.println("✅ Archivo '" + nombre + "' creado.");
    }

    /**
     * ¡MÉTODO AÑADIDO!
     * Escribe o agrega contenido a un archivo existente o nuevo.
     * @param nombre El nombre del archivo.
     * @param contenido El texto a escribir.
     * @param agregar Si es 'true', agrega el contenido al final; si es 'false', sobrescribe el archivo.
     */
    public void escribirArchivo(String nombre, String contenido, boolean agregar) throws IOException {
        File archivo = gestor.obtenerElemento(nombre);
        ArchivosUtil.IntArchivo.escribirArchivo(archivo, contenido, agregar);
        String accion = agregar ? "agregado al" : "escrito en el";
        System.out.println("✅ Contenido " + accion + " archivo '" + nombre + "'.");
    }
    
    public void crearCarpeta(String nombre) throws Exception {
        File nuevaCarpeta = gestor.obtenerElemento(nombre);
        if (nuevaCarpeta.exists()) {
            System.out.println("👍 La carpeta '" + nombre + "' ya existe.");
            return;
        }
        if (!nuevaCarpeta.mkdirs()) throw new Exception("No se pudo crear la carpeta '" + nombre + "'.");
        System.out.println("✅ Carpeta '" + nombre + "' creada.");
    }
    
    public void mover(String origenNombre, String destinoPath, boolean agregar) throws IOException {
        File origen = gestor.obtenerElemento(origenNombre);
        File destino = new File(gestor.getMarcadorActual(), destinoPath);
        ArchivosUtil.ExtArchivo.moverArchivo(origen, destino, agregar);
        System.out.println("✅ '" + origenNombre + "' movido a '" + destinoPath + "' (agregar=" + agregar + ").");
    }
    
    public void copiar(String origenNombre, String destinoPath, boolean agregar) throws IOException {
        File origen = gestor.obtenerElemento(origenNombre);
        File destino = new File(gestor.getMarcadorActual(), destinoPath);
        ArchivosUtil.ExtArchivo.copiarArchivo(origen, destino, agregar);
        System.out.println("✅ '" + origenNombre + "' copiado a '" + destinoPath + "' (agregar=" + agregar + ").");
    }
    
    public void eliminar(String nombre) throws IOException {
        ArchivosUtil.ExtArchivo.eliminarArchivo(gestor.obtenerElemento(nombre));
        System.out.println("🗑️ '" + nombre + "' ha sido eliminado.");
    }
}