package com.github.YeahBaby666.ArchivoGestorV2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

public class ArchivosUtil {

    static class IntArchivo {

        public static List<String> leerLineas(File archivo) throws IOException {
            if (!archivo.exists()) {
                throw new FileNotFoundException("El archivo no existe: " + archivo.getAbsolutePath());
            }
            return FileUtils.readLines(archivo, StandardCharsets.UTF_8);
        }

        public static String leerTodo(File archivo) throws IOException {
            return String.join("\n", leerLineas(archivo));
        }

        /**
         * ¡LÓGICA CORREGIDA! Lee una línea por su número con las nuevas reglas.
         *
         * @param archivo El archivo a leer.
         * @param linea El número de la línea (0="", 1=primera, -1=última).
         */
        public static String leerLineaPorIndice(File archivo, int linea) throws IOException {
            // Regla: Si la línea es 0, retornar vacío.
            if (linea == 0) {
                return "";
            }

            List<String> lineas = leerLineas(archivo);
            int totalLineas = lineas.size();

            // Lógica para índice ascendente (1-based) y descendente.
            int indiceReal;
            if (linea > 0) {
                // Para números positivos, se ajusta a índice 0-based.
                indiceReal = linea - 1;
            } else {
                // Para números negativos (-1, -2...), se calcula desde el final.
                indiceReal = totalLineas + linea;
            }

            if (indiceReal >= 0 && indiceReal < totalLineas) {
                return lineas.get(indiceReal);
            } else {
                throw new IndexOutOfBoundsException("Número de línea fuera de rango: " + linea + " para un archivo de " + totalLineas + " líneas.");
            }
        }

        public static String buscarValorPorClave(File archivo, String clave) throws IOException {
            List<String> lineas = leerLineas(archivo);
            String[] separadores = {":", "=", "->"};

            for (String lineaStr : lineas) {
                String claveLimpia = clave.replaceAll("[\"']", "");
                Pattern pattern = Pattern.compile("^\\s*[\"']?" + Pattern.quote(claveLimpia) + "[\"']?\\s*(?:" + String.join("|", separadores) + ")");

                if (pattern.matcher(lineaStr).find()) {
                    for (String sep : separadores) {
                        if (lineaStr.contains(sep)) {
                            String[] partes = lineaStr.split(sep, 2);
                            if (partes.length > 1) {
                                return partes[1].trim().replaceAll("^[\"']|[\"'],?$", "").trim();
                            }
                        }
                    }
                }
            }
            return null;
        }
/**
         * ¡LÓGICA CORREGIDA!
         * Ahora maneja correctamente la escritura de bloques de texto multilínea.
         */
        public static void escribirArchivo(File archivo, String contenido, boolean agregar, boolean esJson) throws IOException {
            if (agregar && esJson) {
                // Divide el contenido entrante por líneas y procesa cada una.
                List<String> lineasNuevas = Arrays.asList(contenido.split("\\R"));
                for (String linea : lineasNuevas) {
                    if (!linea.trim().isEmpty()) {
                        actualizarOAgregarClaveValor(archivo, linea);
                    }
                }
            } else {
                // La lógica de append simple o sobrescritura no cambia.
                FileUtils.writeStringToFile(archivo, contenido + (agregar ? System.lineSeparator() : ""), StandardCharsets.UTF_8, agregar);
            }
        }

        private static void actualizarOAgregarClaveValor(File archivo, String nuevaLinea) throws IOException {
            if (!archivo.exists()) {
                FileUtils.writeStringToFile(archivo, nuevaLinea, StandardCharsets.UTF_8, false);
                return;
            }

            String[] separadores = {":", "=", "->"};
            String nuevaClave = null;

            for (String sep : separadores) {
                if (nuevaLinea.contains(sep)) {
                    nuevaClave = nuevaLinea.split(sep, 2)[0].trim().replaceAll("[\"']", "");
                    break;
                }
            }

            if (nuevaClave == null) { // Si la línea no tiene clave, solo la agrega al final.
                FileUtils.writeStringToFile(archivo, System.lineSeparator() + nuevaLinea, StandardCharsets.UTF_8, true);
                return;
            }

            List<String> lineasExistentes = FileUtils.readLines(archivo, StandardCharsets.UTF_8);
            List<String> lineasActualizadas = new ArrayList<>();
            boolean claveEncontrada = false;
            
            Pattern clavePattern = Pattern.compile("^\\s*[\"']?" + Pattern.quote(nuevaClave) + "[\"']?\\s*(" + String.join("|", separadores) + ")");

            for (String lineaActual : lineasExistentes) {
                if (clavePattern.matcher(lineaActual).find()) {
                    lineasActualizadas.add(nuevaLinea); // Reemplaza la línea vieja
                    claveEncontrada = true;
                } else {
                    lineasActualizadas.add(lineaActual);
                }
            }

            if (!claveEncontrada) {
                lineasActualizadas.add(nuevaLinea); // Agrega la nueva clave al final
            }

            FileUtils.writeLines(archivo, lineasActualizadas, false);
        }
    }

    static class ExtArchivo {

        public static void copiarArchivo(File origen, File destino, boolean agregar) throws IOException {
            if (!origen.exists()) {
                throw new FileNotFoundException("El origen no existe: " + origen);
            }

            // CASO 1: Origen es un Directorio
            if (origen.isDirectory()) {
                if (!destino.exists()) {
                    destino.mkdirs();
                }
                if (!destino.isDirectory()) {
                    throw new IOException("Error: No se puede copiar un directorio sobre un archivo.");
                }

                if (!agregar) {
                    FileUtils.cleanDirectory(destino);
                }

                for (File archivoEnOrigen : origen.listFiles()) {
                    File archivoEnDestino = new File(destino, archivoEnOrigen.getName());
                    // ¡CORRECCIÓN CLAVE!
                    // En la recursión de una copia de carpeta, los archivos internos
                    // siempre se deben sobrescribir, no combinar su texto.
                    // Por eso, pasamos 'false' para el parámetro 'agregar'.
                    copiarArchivo(archivoEnOrigen, archivoEnDestino, false);
                }
            } // CASO 2: Origen es un Archivo
            else {
                File archivoDestinoFinal = destino;
                if (destino.isDirectory()) {
                    archivoDestinoFinal = new File(destino, origen.getName());
                }

                // Aquí sí respetamos el 'agregar' original, para el caso de archivo-a-archivo.
                String contenido = FileUtils.readFileToString(origen, StandardCharsets.UTF_8);
                IntArchivo.escribirArchivo(archivoDestinoFinal, contenido, agregar, false);
            }
        }

        public static void moverArchivo(File origen, File destino, boolean agregar) throws IOException {
            copiarArchivo(origen, destino, agregar);
            eliminarArchivo(origen);
        }

        public static void eliminarArchivo(File archivo) throws IOException {
            if (archivo == null || !archivo.exists()) {
                return;
            }
            if (archivo.isDirectory()) {
                FileUtils.deleteDirectory(archivo);
            } else {
                FileUtils.forceDelete(archivo);
            }
        }
    }
}
