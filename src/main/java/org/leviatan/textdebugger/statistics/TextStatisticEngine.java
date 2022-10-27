package org.leviatan.textdebugger.statistics;

/**
 *
 * @author Alejandro
 */
public interface TextStatisticEngine {

    /** Añade palabras a la estadistica */
    public void addWord(String word);

    /** Devuelve un informe de la estadistica actual */
    public String obtenerInforme();

    /** Obtiene el nombre de la estadistica */
    public String obtenerNombreEstadistica();
}
