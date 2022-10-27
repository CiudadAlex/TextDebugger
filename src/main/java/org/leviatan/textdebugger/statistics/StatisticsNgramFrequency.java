package org.leviatan.textdebugger.statistics;

import org.leviatan.textdebugger.dto.NgramFrequencyItem;
import org.leviatan.textdebugger.dto.Ngram;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.leviatan.textdebugger.util.HTMLConstants;
import org.leviatan.textdebugger.dto.TextDistance;

/**
 *
 * @author Alejandro
 */
public class StatisticsNgramFrequency implements TextStatisticEngine {

    /** Indica la frecuencia minima de interes que le vamos a pedir al informe */
    public static final int FRECUENCIA_MINIMA_DE_INTERES = 2;

    /** Margen anterior y posterior máximo a visualizar en los informes */
    public static final int MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO = 30;

    /** Distancia mínima de dos repeticiones para que sea relevante ponerlo en el informe */
    public static final int DISTANCIA_MINIMA_PARA_QUE_REPETICION_SEA_RELEVANTE = 2000;

    private int ngramNumber;
    private List<String> listWords = new ArrayList<String>();
    private Map<String, Integer> mapNgrama2NumeroDeOcurrencias = new HashMap<String, Integer>();

    public StatisticsNgramFrequency(int ngramNumber) {
        this.ngramNumber = ngramNumber;
    }

    @Override
    public void addWord(String word) {
        listWords.add(word);
    }

    /** Procesa la informacion almacenada para contar los Ngramas */
    public List<NgramFrequencyItem> process() {

        if (listWords.size() < ngramNumber) {
            return new ArrayList<NgramFrequencyItem>();
        }

        Iterator<String> iteradorWords = listWords.iterator();

        List<String> listaStringsNgram = new ArrayList<String>();
        for (int i=0; i<ngramNumber; i++) {
            listaStringsNgram.add(iteradorWords.next());
        }

        Ngram ngram = new Ngram(listaStringsNgram);

        guardaNgrama(ngram);

        while (iteradorWords.hasNext()) {

            ngram.itera(iteradorWords.next());
            guardaNgrama(ngram);
        }

        return getListOfNgramFrequencyItems();
    }

    /** Añade la representacion String del ngrama al map */
    private void guardaNgrama(Ngram ngram) {

        String strNgram = ngram.toString();

        if (mapNgrama2NumeroDeOcurrencias.containsKey(strNgram)) {
            // Si ya estaba incluida le sumamos 1 a la frecuencia
            Integer numOcurrencias = mapNgrama2NumeroDeOcurrencias.get(strNgram);
            mapNgrama2NumeroDeOcurrencias.put(strNgram, numOcurrencias + 1);
        }
        else {
            // Si no estaba incluida la incluimos con frecuencia 1
            mapNgrama2NumeroDeOcurrencias.put(strNgram, 1);
        }
    }

    /** Devuelve una lista de NgramFrequencyItems ordenada */
    private List<NgramFrequencyItem> getListOfNgramFrequencyItems() {

        List<NgramFrequencyItem> listaNgramFrequencyItem = new ArrayList<NgramFrequencyItem>();
        Set<String> keySet = mapNgrama2NumeroDeOcurrencias.keySet();
        Iterator<String> iteratorKeySet = keySet.iterator();

        while (iteratorKeySet.hasNext()) {
            String word = iteratorKeySet.next();
            Integer frequency = mapNgrama2NumeroDeOcurrencias.get(word);
            NgramFrequencyItem ngramFrequencyItem = new NgramFrequencyItem(word, frequency);
            listaNgramFrequencyItem.add(ngramFrequencyItem);
        }

        Comparator<NgramFrequencyItem> comparatorWordFrequencyItem = new Comparator<NgramFrequencyItem>() {

            /** Comparador de WordFrequencies */
            @Override
            public int compare(NgramFrequencyItem wf1, NgramFrequencyItem wf2) {

                return wf2.getFrequency() - wf1.getFrequency();
            }
        };

        Collections.sort(listaNgramFrequencyItem, comparatorWordFrequencyItem);

        return listaNgramFrequencyItem;
    }

    /** Obtiene el número de caracteres de distancia mínimos entre Ngramas */
    private TextDistance obtenerNumeroCaracteresMinimoEntre2NgramasIguales(String listaPalabrasEnString, String strNgramArg) {

        // Le ponemos el separator delante para que coja palabras enteras
        String strNgram = Ngram.SEPARATOR + strNgramArg;

        int tallaTexto = listaPalabrasEnString.length();
        int tallaNgram = strNgram.length();
        int distanciaMinima = Integer.MAX_VALUE;
        String textoEntreDistanciaMinima = "";

        int index1 = listaPalabrasEnString.indexOf(strNgram, 0);
        int index2 = listaPalabrasEnString.indexOf(strNgram, index1 + 1);
        
        while (index2 != -1 && index2 < tallaTexto) {

            int distanciaActual = index2 - index1 - tallaNgram;

            if (distanciaActual < distanciaMinima) {
                distanciaMinima = distanciaActual;
                textoEntreDistanciaMinima = obtenerTextoEntreIndicesDadosMasMargen(listaPalabrasEnString, index1, index2, tallaNgram);
            }

            index1 = index2;
            index2 = listaPalabrasEnString.indexOf(strNgram, index2 + 1);
        }
        
        return new TextDistance(distanciaMinima, textoEntreDistanciaMinima);
    }

    /** Obtiene la distancia entre los indices dados mas un margen */
    private String obtenerTextoEntreIndicesDadosMasMargen(String listaPalabrasEnString, int index1, int index2Arg, int tallaNgram) {

        int index2 = index2Arg;

        // Existe un caso particular en el que el Ngrama está repetido uno detras de otro de modo que se solapan porque estamos considerando que el Ngrama incluye
        // el Ngram.SEPARATOR antes y despues del mismo. esto hace que compartan un caracter, por ejemplo: "_Boyd_Boyd_". En estos casos peta el destacado del texto
        // ya que (index1 + tallaNgram > index2). Por ello en estos casos trucaremos el valor de index2 simplemente.
        if (index2 < index1 + tallaNgram) {
            index2 = index1 + tallaNgram;
        }

        String textoEntreDistanciaMinima = listaPalabrasEnString.substring(index1 + tallaNgram, index2);
        String palabraAntes = listaPalabrasEnString.substring(index1, index1 + tallaNgram);
        String palabraDespues = listaPalabrasEnString.substring(index2, index2 + tallaNgram);

        int indiceMasBajo = Math.max(0, index1 - MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO);
        int indiceMasAlto = Math.min(listaPalabrasEnString.length(), index2 + tallaNgram + MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO);

        String textoPrevio = listaPalabrasEnString.substring(indiceMasBajo, index1);
        String textoPosterior = listaPalabrasEnString.substring(index2 + tallaNgram, indiceMasAlto);

        StringBuilder sb = new StringBuilder();
        sb.append(HTMLConstants.LETRA_APAGADA_INI);
        sb.append(textoPrevio);
        sb.append(HTMLConstants.LETRA_APAGADA_FIN);

        sb.append(HTMLConstants.LETRA_DESTACADA_INI);
        sb.append(palabraAntes);
        sb.append(HTMLConstants.LETRA_DESTACADA_FIN);

        sb.append(HTMLConstants.LETRA_APAGADA_INI);
        sb.append(textoEntreDistanciaMinima);
        sb.append(HTMLConstants.LETRA_APAGADA_FIN);

        sb.append(HTMLConstants.LETRA_DESTACADA_INI);
        sb.append(palabraDespues);
        sb.append(HTMLConstants.LETRA_DESTACADA_FIN);

        sb.append(HTMLConstants.LETRA_APAGADA_INI);
        sb.append(textoPosterior);
        sb.append(HTMLConstants.LETRA_APAGADA_FIN);

        String textoEntreIndicesDadosMasMargen = sb.toString();
        String textoEntreIndicesDadosMasMargenVisualizable = textoEntreIndicesDadosMasMargen.replace(Ngram.SEPARATOR, " ");

        return textoEntreIndicesDadosMasMargenVisualizable;
    }

    /** Obtiene la lista de palabras en formato String */
    public String getListaPalabrasEnString() {
        StringBuilder sb = new StringBuilder(Ngram.SEPARATOR);

        for (String string : listWords) {
            sb.append(string).append(Ngram.SEPARATOR);
        }

        return sb.toString();
    }

    /** Devuelve un informe sobre esta estadistica con Ngramas */
    @Override
    public String obtenerInforme() {
        
        List<NgramFrequencyItem> listaNgramFrequencyItems = process();
        String listaPalabrasEnString = getListaPalabrasEnString();
        
        StringBuilder sb = new StringBuilder();

        sb.append("ESTADISTICA N-GRAMA PARA N=").append(ngramNumber).append(HTMLConstants.SALTO_DE_LINEA_X2);
        sb.append(HTMLConstants.TABLA_INI);
        appendFilaDelInforme(sb, "Frecuencia", "N-grama", "Distancia Minima", "Texto Entre Distancia Minima", HTMLConstants.COLOR_GRIS);

        for (NgramFrequencyItem ngramFrequencyItem : listaNgramFrequencyItems) {

            int frecuency = ngramFrequencyItem.getFrequency();
            String strNgram = ngramFrequencyItem.getNgram();

            if (frecuency >= FRECUENCIA_MINIMA_DE_INTERES) {

                TextDistance textDistance = obtenerNumeroCaracteresMinimoEntre2NgramasIguales(listaPalabrasEnString, strNgram);
                String strNgramVisualizable = strNgram.replace(Ngram.SEPARATOR, " ");
                int distanciaMinima = textDistance.getDistancia();
                String textoEntreDistanciaMinima = textDistance.getTexto().replace(Ngram.SEPARATOR, " ");

                if (distanciaMinima < DISTANCIA_MINIMA_PARA_QUE_REPETICION_SEA_RELEVANTE) {
                    // Añadimos al informe sólo si la distancia minima es inferior a la considerada como relevante
                    appendFilaDelInforme(sb, "" + frecuency, strNgramVisualizable, "" + distanciaMinima, textoEntreDistanciaMinima, HTMLConstants.COLOR_BLANCO);
                }
            }
        }

        sb.append(HTMLConstants.TABLA_FIN);

        return sb.toString();
    }

    /** Hace un append de la fila del informe */
    private void appendFilaDelInforme(StringBuilder sb, String frecuency, String strNgramVisualizable, String distanciaMinima, String textoEntreDistanciaMinima, String colorBackground) {

        String inicioCelda = HTMLConstants.getTablaCeldaINI(colorBackground);

        sb.append(HTMLConstants.TABLA_FILA_INI);

        sb.append(inicioCelda);
        sb.append(frecuency);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(strNgramVisualizable);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(distanciaMinima);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(textoEntreDistanciaMinima);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(HTMLConstants.TABLA_FILA_FIN);
    }

    /** Obtiene el nombre de la estadistica */
    @Override
    public String obtenerNombreEstadistica() {

        return ngramNumber + "-grama";
    }
}
