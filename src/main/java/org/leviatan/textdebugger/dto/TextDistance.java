package org.leviatan.textdebugger.dto;

/**
 *
 * @author Alejandro
 */
public class TextDistance {

    private int distancia;
    private String texto;

    public TextDistance(int distancia, String texto) {
        this.distancia = distancia;
        this.texto = texto;
    }

    public int getDistancia() {
        return distancia;
    }

    public String getTexto() {
        return texto;
    }
}
