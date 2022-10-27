package org.leviatan.textdebugger.util;

/**
 *
 * @author Alejandro
 */
public class HTMLConstants {

    public static final String COLOR_BLANCO = "white";
    public static final String COLOR_NEGRO = "black";
    public static final String COLOR_GRIS = "grey";

    public static final String TIPO_FUENTE_ARIAL = "arial";

    public static final String INICIO_PAGINA_HTML = "<html>\n<head>\n<title>\nInforme\n</title>\n</head>\n<body>\n";
    public static final String FIN_PAGINA_HTML = "</body>\n</html>";

    public static final String SALTO_DE_LINEA = "<BR/>\n";
    public static final String SALTO_DE_LINEA_X2 = SALTO_DE_LINEA + SALTO_DE_LINEA;
    public static final String SALTO_DE_LINEA_X3 = SALTO_DE_LINEA_X2 + SALTO_DE_LINEA;

    public static final String TABLA_INI = "<table border=\"1\">\n";
    public static final String TABLA_FIN = "</table>\n";

    public static final String TABLA_FILA_INI = "<tr>\n";
    public static final String TABLA_FILA_FIN = "</tr>\n";

    public static String getTablaCeldaINI(String color) {
        return "<td bgcolor=" + color + ">\n";
    }
    public static final String TABLA_CELDA_INI = "<td>\n";
    public static final String TABLA_CELDA_FIN = "</td>\n";

    public static final String BOLD_INI = "<b>";
    public static final String BOLD_FIN = "</b>";

    public static String getFontIni(String tipoFuente, String color) {
        return "<font face=\"" + tipoFuente + "\" color=" + color + ">";
    }
    public static final String FONT_INI = "<font>";
    public static final String FONT_FIN = "</font>";

    public static final String LETRA_DESTACADA_INI = BOLD_INI + getFontIni(TIPO_FUENTE_ARIAL, COLOR_NEGRO);
    public static final String LETRA_DESTACADA_FIN = FONT_FIN + BOLD_FIN;

    public static final String LETRA_APAGADA_INI = getFontIni(TIPO_FUENTE_ARIAL, COLOR_GRIS);
    public static final String LETRA_APAGADA_FIN = FONT_FIN;

    
}
