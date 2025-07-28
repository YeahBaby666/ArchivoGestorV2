package com.github.YeahBaby666.ArchivoGestorV2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class FileManager {
    private ArchivoManager gestor;

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
        ArchivosUtil.IntArchivo.escribirArchivo(gestor.obtenerElemento(nombre), contenido, false, false);
        System.out.println("✅ Archivo '" + nombre + "' creado.");
    }
    
    
     /**
     * Escritura inteligente para actualizar archivos tipo JSON/config.
     * Ahora puede procesar tanto una línea como el contenido de un archivo completo.
     * @param nombreArchivo El archivo a modificar.
     * @param contenido La línea o bloque de texto a escribir.
     */
    public void escribirConfig(String nombreArchivo, String contenido) throws IOException {
        ArchivosUtil.IntArchivo.escribirArchivo(obtenerFile(nombreArchivo), contenido, true, true);
        System.out.println("✅ Configuración '" + nombreArchivo + "' actualizada.");
    }
    
    /**
     * Escritura simple para añadir a logs.
     */
    public void escribirLog(String nombreArchivo, String linea) throws IOException {
        ArchivosUtil.IntArchivo.escribirArchivo(obtenerFile(nombreArchivo), linea, true, false);
        System.out.println("✅ Línea agregada al log '" + nombreArchivo + "'.");
    }
    

    /**
     * ¡NUEVO Y CORREGIDO!
     * Devuelve el objeto File para trabajar con otras librerías.
     */
    public File obtenerFile(String nombre) {
        return gestor.obtenerElemento(nombre);
    }

    /**
     * ¡NUEVO Y CORREGIDO!
     * Abre un explorador de archivos nativo para seleccionar una carpeta.
     */
    public static File abrirExplorador() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el look and feel del sistema.");
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Selecciona una carpeta");
        int resultado = chooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * ¡NUEVO Y CORREGIDO!
     * Abre un explorador y opcionalmente marca la carpeta seleccionada como la nueva raíz.
     */
    public File abrirExplorador(boolean marcarComoRaiz) {
        File carpetaSeleccionada = abrirExplorador();
        if (marcarComoRaiz && carpetaSeleccionada != null) {
            System.out.println("Nueva carpeta raíz marcada: " + carpetaSeleccionada.getAbsolutePath());
            this.gestor = new ArchivoManager(carpetaSeleccionada);
        }
        return carpetaSeleccionada;
    }

    /**
     * ¡SOBRECARGA AÑADIDA!
     * Permite la escritura inteligente. Si esJson es true, actualiza la clave o agrega la línea.
     * Si es false, simplemente añade al final (útil para logs).
     */
    public void escribirArchivo(String nombre, String contenido, boolean agregar, boolean esJson) throws IOException {
        ArchivosUtil.IntArchivo.escribirArchivo(obtenerFile(nombre), contenido, agregar, esJson);
        String accion = esJson ? "actualizado/agregado en" : "agregado al";
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
    
    /**
     * ¡LÓGICA MEJORADA!
     * Ahora puede mover archivos/carpetas a una ruta absoluta o relativa.
     */
    public void mover(String origenNombre, String destinoPath, boolean agregar) throws IOException {
        File origen = gestor.obtenerElemento(origenNombre);
        File destino = new File(destinoPath);

        // Si la ruta de destino no es absoluta, la hace relativa a la carpeta actual.
        if (!destino.isAbsolute()) {
            destino = new File(gestor.getMarcadorActual(), destinoPath);
        }

        ArchivosUtil.ExtArchivo.moverArchivo(origen, destino, agregar);
        System.out.println("✅ '" + origenNombre + "' movido a '" + destinoPath + "'.");
    }

    /**
     * ¡LÓGICA MEJORADA!
     * Ahora puede copiar archivos/carpetas a una ruta absoluta o relativa.
     */
    public void copiar(String origenNombre, String destinoPath, boolean agregar) throws IOException {
        File origen = gestor.obtenerElemento(origenNombre);
        File destino = new File(destinoPath);

        // Si la ruta de destino no es absoluta, la hace relativa a la carpeta actual.
        if (!destino.isAbsolute()) {
            destino = new File(gestor.getMarcadorActual(), destinoPath);
        }
        
        ArchivosUtil.ExtArchivo.copiarArchivo(origen, destino, agregar);
        System.out.println("✅ '" + origenNombre + "' copiado a '" + destinoPath + "'.");
    }
    
    public void eliminar(String nombre) throws IOException {
        ArchivosUtil.ExtArchivo.eliminarArchivo(gestor.obtenerElemento(nombre));
        System.out.println("🗑️ '" + nombre + "' ha sido eliminado.");
    }
}