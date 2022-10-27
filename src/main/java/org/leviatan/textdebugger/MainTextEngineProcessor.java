package org.leviatan.textdebugger;

import org.leviatan.textdebugger.util.HTMLConstants;
import org.leviatan.textdebugger.detectors.MistakeDetector;
import org.leviatan.textdebugger.statistics.MainTextStatistics;

/**
 *
 * AMPLIACIONES POSIBLES
 * 
 * Tener en cueta:
 *  
 *  - No tener en cuenta las mayusculas.
 *  - Poner patrones de expresiones regulares.
 *
 * @author Alejandro
 */
public class MainTextEngineProcessor {

    /** Genera informe de procesado del texto  */
    public static void generaInforme(String text) {

        StringBuilder sb = new StringBuilder();
        sb.append(HTMLConstants.INICIO_PAGINA_HTML);

        MainTextStatistics.generaInformeEstadisticas(sb, text);
        MistakeDetector.generaInformeErrores(sb, text);

        sb.append(HTMLConstants.FIN_PAGINA_HTML);

        String informeTotal = sb.toString();
        GeneradorInforme.generaFicheroConInformeYMostrar(informeTotal);
    }
}
