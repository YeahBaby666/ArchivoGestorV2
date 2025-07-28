package com.github.YeahBaby666.ArchivoGestorV2;

public class ExploradorArchivos {

    public static void main(String[] args) {
        System.out.println("--- INICIO DEL TEST EN SIMULTÁNEO (MULTI-PESTAÑA) ---");

        try {
            // --- PASO 1: Preparar el entorno con dos carpetas base ---
            // Se crea una carpeta principal para la simulación
            FileManager setupManager = new FileManager("C:/simulacion_multitab");
            setupManager.crearCarpeta("MisDocumentos");
            setupManager.crearCarpeta("Descargas");

            // --- PASO 2: Crear dos instancias de FileManager en simultáneo ---
            System.out.println("\n--- Creando dos 'pestañas' de explorador ---");
            FileManager fmDocumentos = new FileManager("C:/simulacion_multitab/MisDocumentos");
            FileManager fmDescargas = new FileManager("C:/simulacion_multitab/Descargas");

            // --- PASO 3: Realizar acciones independientes en cada 'pestaña' ---
            System.out.println("\n--- Acciones independientes ---");
            fmDocumentos.crearArchivo("proyecto.txt", "Líneas del proyecto.");
            fmDescargas.crearArchivo("instalador.exe", "contenido_del_instalador");
            
            System.out.println("\nContenido de 'MisDocumentos':");
            fmDocumentos.listarContenido().forEach(System.out::println);
            
            System.out.println("Contenido de 'Descargas':");
            fmDescargas.listarContenido().forEach(System.out::println);

            // --- PASO 4: INTERACCIÓN ENTRE 'PESTAÑAS' ---
            System.out.println("\n--- Interacción: Moviendo un archivo de Descargas a Documentos ---");
            
            // Obtenemos la ruta absoluta de la 'pestaña' de Documentos
            String rutaAbsolutaDocumentos = fmDocumentos.getRutaActual();
            System.out.println("La 'pestaña' de Descargas moverá un archivo a: " + rutaAbsolutaDocumentos);

            // La 'pestaña' de Descargas ejecuta la acción usando la ruta absoluta
            fmDescargas.mover("instalador.exe", rutaAbsolutaDocumentos, true); // true = mover DENTRO de la carpeta

            // --- PASO 5: Verificación del resultado ---
            System.out.println("\n--- Verificación del estado final ---");
            
            System.out.println("\nContenido de 'MisDocumentos' (ahora debe tener el archivo movido):");
            fmDocumentos.listarContenido().forEach(System.out::println);
            
            System.out.println("Contenido de 'Descargas' (debe estar vacío):");
            fmDescargas.listarContenido().forEach(System.out::println);


        } catch (Exception e) {
            System.err.println("❌ ERROR DURANTE EL TEST: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- TEST EN SIMULTÁNEO FINALIZADO ---");
    }
}
