package com.github.YeahBaby666.ArchivoGestorV2;

public class ExploradorArchivos {

    public static void main(String[] args) {
        System.out.println("--- SIMULADOR CON LECTURA POR LÍNEA CORREGIDA ---");
        
        try {
            FileManager fm = new FileManager("simulacion_raiz");

            // --- PASO 1: Crear un archivo ---
            System.out.println("\n--- PASO 1: Creando archivo 'datos.txt' ---");
            String contenido = "Línea 1\nLínea 2\nLínea 3\nLínea 4";
            fm.crearArchivo("datos.txt", contenido);
            System.out.println("Contenido del archivo:\n" + fm.leer("datos.txt"));

            // --- PASO 2: Probar la nueva lógica de lectura por línea ---
            System.out.println("\n--- PASO 2: Probando la nueva lógica ---");
            System.out.println("Leyendo línea 0: " + fm.leer("datos.txt", 0) + " (Debe ser vacío)");
            System.out.println("Leyendo línea 1: " + fm.leer("datos.txt", 1));
            System.out.println("Leyendo línea 4: " + fm.leer("datos.txt", 4));
            System.out.println("Leyendo línea -1 (última): " + fm.leer("datos.txt", -1));
            System.out.println("Leyendo línea -2 (penúltima): " + fm.leer("datos.txt", -2));
            

        } catch (Exception e) {
            System.err.println("❌ ERROR EN LA SIMULACIÓN: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- FIN DE LA SIMULACIÓN ---");
    }
}