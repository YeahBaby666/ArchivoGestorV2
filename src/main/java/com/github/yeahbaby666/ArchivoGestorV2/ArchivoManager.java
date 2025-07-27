package com.github.YeahBaby666.ArchivoGestorV2;

import java.io.File;

public class ArchivoManager {
    private File marcadorActual;

    public ArchivoManager(File carpetaRaiz) {
        if (!carpetaRaiz.exists()) carpetaRaiz.mkdirs();
        this.marcadorActual = carpetaRaiz;
    }

    public File getMarcadorActual() {
        return marcadorActual;
    }

    public void cambiarMarcador(File nueva) throws Exception {
        if (nueva != null && nueva.isDirectory()) {
            marcadorActual = nueva;
        } else {
            throw new Exception("No se puede cambiar al destino.");
        }
    }

    public File obtenerElemento(String nombre) {
        return new File(marcadorActual, nombre);
    }

    public File[] listarContenido() {
        return marcadorActual.listFiles();
    }
}


