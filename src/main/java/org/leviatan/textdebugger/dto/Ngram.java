package org.leviatan.textdebugger.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alejandro
 */
public class Ngram {

    /** Separador de palabras en la representacion String */
    public static final String SEPARATOR = "_";

    /** Lista de strings del Ngrama */
    private List<String> listaStringsInterna = new ArrayList<String>();

    public Ngram(List<String> listaStrings) {
        listaStringsInterna.addAll(listaStrings);
    }

    /** Obtiene la representacion en string del Ngrama */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String string : listaStringsInterna) {
            sb.append(string).append(SEPARATOR);
        }

        return sb.toString();
    }

    /** Añade al final la palabra dada y quita la primera. Permite mover el ngrama como una ventana sobre texto */
    public void itera(String next) {
        listaStringsInterna.remove(0);
        listaStringsInterna.add(next);
    }
}
