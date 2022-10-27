package org.leviatan.textdebugger.detectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.leviatan.textdebugger.util.HTMLConstants;

/**
 *
 * @author Alejandro
 */
public class MistakeDetector {

    private static final int NOT_FOUND = -1;
    private static final String[] PATRONES_ERRONEOS = {"  ", " ,", " .", " )", "( ", " ;"};
    private static final String[] PATRONES_ERRONEOS_REGULAR_EXPRESSION = {"\\p{Punct}\\p{Alpha}"};

    /** Margen anterior y posterior máximo a visualizar en los informes */
    public static final int MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO = 30;

    /** Margen anterior y posterior máximo a visualizar en los informes de dorma realzada */
    public static final int MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO_REALZADO = 7;

    /** Genera un informe de errores */
    public static void generaInformeErrores(StringBuilder sb, String text) {

        sb.append("ERRORES DE FORMA EN EL TEXTO").append(HTMLConstants.SALTO_DE_LINEA_X2);
        sb.append(HTMLConstants.TABLA_INI);
        appendFilaDelInforme(sb, "Patron", "Ejemplo", HTMLConstants.COLOR_GRIS);

        for (int i = 0; i < PATRONES_ERRONEOS.length; i++) {

            String patronErroneo = PATRONES_ERRONEOS[i];
            generaLineasDelInformeConPatron(sb, text, patronErroneo);
        }

        for (int i = 0; i < PATRONES_ERRONEOS_REGULAR_EXPRESSION.length; i++) {

            String patronErroneo = PATRONES_ERRONEOS_REGULAR_EXPRESSION[i];
            generaLineasDelInformeConPatronRegEx(sb, text, patronErroneo);
        }

        sb.append(HTMLConstants.TABLA_FIN);
    }

    /** Genera lineas del informe con un patron */
    private static void generaLineasDelInformeConPatron(StringBuilder sb, String text, String patronErroneo) {

        int index = text.indexOf(patronErroneo);

        while (index != NOT_FOUND && index < text.length()) {
            
            appendLineaAlInforme(sb, text, patronErroneo, index);
            index = text.indexOf(patronErroneo, index + 1);
        }
    }

    /** Genera lineas del informe con un patron de expresion regular */
    private static void generaLineasDelInformeConPatronRegEx(StringBuilder sb, String text, String patronErroneo) {

        Matcher matcher = Pattern.compile(patronErroneo).matcher(text);

        boolean found =  matcher.find(0);

        if (!found) {
            // Si el patron erroneo no se encuentra se vuelve
            return;
        }

        int index = matcher.start();

        while (index != NOT_FOUND && index < text.length() && found) {

            appendLineaAlInforme(sb, text, patronErroneo, index);

            found =  matcher.find(index + 1);

            if (found) {
                index = matcher.start();
            }
        }
    }

    /** Añade una linea al informe de errores */
    private static void appendLineaAlInforme(StringBuilder sb, String text, String patronErroneo, int index) {

        int indiceMasBajo = Math.max(0, index - MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO);
        int indiceMasAlto = Math.min(text.length(), index + MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO);
        
        int indiceMasBajoRealzado = Math.max(0, index - MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO_REALZADO);
        int indiceMasAltoRealzado = Math.min(text.length(), index + MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO_REALZADO);

        StringBuilder sbEjemplo = new StringBuilder();

        sbEjemplo.append(HTMLConstants.LETRA_APAGADA_INI);
        sbEjemplo.append(text.substring(indiceMasBajo, indiceMasBajoRealzado));
        sbEjemplo.append(HTMLConstants.LETRA_APAGADA_FIN);

        sbEjemplo.append(HTMLConstants.LETRA_DESTACADA_INI);
        sbEjemplo.append(text.substring(indiceMasBajoRealzado, indiceMasAltoRealzado));
        sbEjemplo.append(HTMLConstants.LETRA_DESTACADA_FIN);

        sbEjemplo.append(HTMLConstants.LETRA_APAGADA_INI);
        sbEjemplo.append(text.substring(indiceMasAltoRealzado, indiceMasAlto));
        sbEjemplo.append(HTMLConstants.LETRA_APAGADA_FIN);

        appendFilaDelInforme(sb, "\"" + patronErroneo + "\"", sbEjemplo.toString(), HTMLConstants.COLOR_BLANCO);
    }

    /** Hace un append de la fila del informe */
    private static void appendFilaDelInforme(StringBuilder sb, String patron, String ejemplo, String colorBackground) {

        String inicioCelda = HTMLConstants.getTablaCeldaINI(colorBackground);

        sb.append(HTMLConstants.TABLA_FILA_INI);

        sb.append(inicioCelda);
        sb.append(patron);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(ejemplo);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(HTMLConstants.TABLA_FILA_FIN);
    }
}
