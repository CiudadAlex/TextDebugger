package org.leviatan.textdebugger;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

/**
 *
 * @author Alejandro
 */
public class GeneradorInforme {

    public static final String NOMBRE_FICHERO_INFORME = "informe.html";

    /** Genera un fichero con el informe y lo muestra */
    public static void generaFicheroConInformeYMostrar(String informeTotal) {

        try {

            log("Generando fichero HTML del informe");
            String pathInforme = generaFicheroYDevuelvePath(informeTotal);

            log("Lanzando el navegador");
            lanzaNavegador(pathInforme);
        }
        catch(Exception e) {
            log("Excepcion generando el fichero del informe o lanzando el navegador");
            e.printStackTrace();
        }
    }

    /** Genera el fichero y devuelve su path */
    private static String generaFicheroYDevuelvePath(String informeTotal) throws Exception {

        File file = new File("./" + NOMBRE_FICHERO_INFORME);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(informeTotal.getBytes());
        fos.flush();
        fos.close();

        return file.getCanonicalPath();
    }

    /** Lanza el navegador para ver el informe */
    private static void lanzaNavegador(String pathInforme) throws Exception {
        String pathInformeClean = pathInforme.replace("\\", "/");
        Desktop.getDesktop().browse(new URI("file:/" + pathInformeClean));
    }

    /** Loga en la consola */
    private static void log(String txt) {
        System.out.println(txt);
    }
}
