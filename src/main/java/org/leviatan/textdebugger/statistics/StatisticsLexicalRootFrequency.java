package org.leviatan.textdebugger.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.leviatan.textdebugger.dto.SubpartFrequencyItem;
import org.leviatan.textdebugger.dto.TextDistance;
import org.leviatan.textdebugger.dto.WordIndex;
import org.leviatan.textdebugger.util.HTMLConstants;

/**
 *
 * @author Alejandro
 */
public class StatisticsLexicalRootFrequency implements TextStatisticEngine {

    /** Tamaño mínimo de la raiz a buscar */
    public static final int TALLA_RAIZ_MINIMA = 3;

    /** Número mínimo de palabras que comparten una subparte que son intereantes en el informe */
    public static final int NUMERO_MINIMO_PALABRAS_DE_INTERES = 2;

    /** Margen anterior y posterior máximo a visualizar en los informes */
    public static final int MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO = 30;

    /** Separador de palabras */
    public static final String WORD_SEPARATOR = "_";

    /** Distancia mínima de dos repeticiones para que sea relevante ponerlo en el informe */
    public static final int DISTANCIA_MINIMA_PARA_QUE_REPETICION_SEA_RELEVANTE = 2000;

    /** Listado de las palabras */
    private List<String> listWords = new ArrayList<String>();
    
    /** Map de las palabras que comparten subpartes */
    private Map<String, Set<String>> mapSubparte2SetPalabrasQueLaComparten = new HashMap<String, Set<String>>();

    @Override
    public void addWord(String word) {
        listWords.add(word);
    }

    /** Procesa la estadística */
    private void process() {

        int numWords = listWords.size();
        for(int i=0; i<numWords; i++) {
            String word = listWords.get(i);
            buscarFuturasOcurrenciasDeSubpartes(i, word);
        }
    }

    /** Busca futuras ocurrencias de subpartes de la palabra */
    private void buscarFuturasOcurrenciasDeSubpartes(int indexListWords, String word) {

        List<String> listaSubpartsOfString = getSubpartsOfString(word);

        for(String subparte : listaSubpartsOfString) {
            // Guarda la palabra original
            guardaSubparte(subparte, word);
            buscarFuturasOcurrenciasDeUnaSubparte(indexListWords, subparte);
        }
    }

    /** Busca futuras ocurrencias de una subparte */
    private void buscarFuturasOcurrenciasDeUnaSubparte(int indexListWords, String subparte) {

        int numWords = listWords.size();

        for(int i=indexListWords + 1; i<numWords; i++) {
            String wordItem = listWords.get(i);

            if (wordItem.contains(subparte)) {
                // Si la palabraItem contiene la subparte la guardamos
                guardaSubparte(subparte, wordItem);
            }
        }
    }

    /** Añade una relacion subparte-palabra al map */
    private void guardaSubparte(String subparte, String wordItem) {

        if (mapSubparte2SetPalabrasQueLaComparten.containsKey(subparte)) {
            // Si ya estaba incluida añadimos la palabra
            Set<String> setPalabras = mapSubparte2SetPalabrasQueLaComparten.get(subparte);
            setPalabras.add(wordItem);
        }
        else {
            // Si no estaba incluida la incluimos con frecuencia 1
            Set<String> setPalabras = new HashSet<String>();
            setPalabras.add(wordItem);
            mapSubparte2SetPalabrasQueLaComparten.put(subparte, setPalabras);
        }
    }

    /** Devuelve todas las posibles subpartes del string (de tamaños diferentes y posiciones diferentes) */
    private List<String> getSubpartsOfString(String str) {

        int tallaString = str.length();

        if (tallaString < TALLA_RAIZ_MINIMA) {
            return new ArrayList<String>();
        }

        List<String> listaSubpartsOfString = new ArrayList<String>();

        for (int length=TALLA_RAIZ_MINIMA; length<=tallaString; length++) {
            List<String> listaSubpartsOfStringOfLength = getSubpartsOfStringOfLength(str, length);
            listaSubpartsOfString.addAll(listaSubpartsOfStringOfLength);
        }

        return listaSubpartsOfString;
    }

    /** Devuelve todas las posibles subpartes del string (del tamaño dado y posiciones diferentes) */
    private List<String> getSubpartsOfStringOfLength(String str, int length) {

        int tallaString = str.length();
        List<String> listaSubpartsOfStringOfLength = new ArrayList<String>();

        for (int index = 0; index + length <= tallaString; index++) {
            String subpart = str.substring(index, index + length);
            listaSubpartsOfStringOfLength.add(subpart);
        }

        return listaSubpartsOfStringOfLength;
    }

    /** Devuelve una lista de SubpartFrequencyItems ordenada */
    public List<SubpartFrequencyItem> getListOfSubpartFrequencyItems() {

        process();

        List<SubpartFrequencyItem> listaSubpartFrequencyItem = new ArrayList<SubpartFrequencyItem>();
        Set<String> keySet = mapSubparte2SetPalabrasQueLaComparten.keySet();
        Iterator<String> iteratorKeySet = keySet.iterator();

        while (iteratorKeySet.hasNext()) {
            String subpart = iteratorKeySet.next();
            Set<String> setPalabrasQueCompartenSubparte = mapSubparte2SetPalabrasQueLaComparten.get(subpart);
            List<String> listaPalabrasQueCompartenSubparte = transformaSetEnLista(setPalabrasQueCompartenSubparte);
            SubpartFrequencyItem subpartFrequencyItem = new SubpartFrequencyItem(subpart, listaPalabrasQueCompartenSubparte);
            listaSubpartFrequencyItem.add(subpartFrequencyItem);
        }

        Comparator<SubpartFrequencyItem> comparatorSubpartFrequencyItem = new Comparator<SubpartFrequencyItem>() {

            /** Comparador de WordFrequencies */
            @Override
            public int compare(SubpartFrequencyItem sp1, SubpartFrequencyItem sp2) {

                int subpartlong1 = sp1.getSubparte().length();
                int subpartlong2 = sp2.getSubparte().length();

                int subpartFreq1 = sp1.getPalabrasQueCompartenSubparte().size();
                int subpartFreq2 = sp2.getPalabrasQueCompartenSubparte().size();

                int contribucion1 = 100*subpartlong1 + subpartFreq1;
                int contribucion2 = 100*subpartlong2 + subpartFreq2;

                return contribucion2 - contribucion1;
            }
        };

        Collections.sort(listaSubpartFrequencyItem, comparatorSubpartFrequencyItem);

        return listaSubpartFrequencyItem;
    }

    /** Transforma el Set de Strings en una lista de Strings */
    private List<String> transformaSetEnLista(Set<String> setPalabrasQueCompartenSubparte) {

        List<String> lista = new ArrayList<String>();
        Iterator<String> iterator = setPalabrasQueCompartenSubparte.iterator();

        while (iterator.hasNext()) {
            String word = iterator.next();
            lista.add(word);
        }

        return lista;
    }

    /** Obtiene la lista de palabras en formato String. Se le añade un separador al principio para que todas las palabras enteras tengan uno delante y otro detras */
    public String getListaPalabrasEnString() {
        StringBuilder sb = new StringBuilder(WORD_SEPARATOR);

        for (String string : listWords) {
            sb.append(string).append(WORD_SEPARATOR);
        }

        return sb.toString();
    }

    /** Obtiene el número de caracteres de distancia mínimos entre palabras de la lista */
    private TextDistance obtenerNumeroCaracteresMinimoEntre2PalabrasQueCompartenSubparte(String listaPalabrasEnString, List<String> palabrasQueCompartenSubparte) {

        int tallaTexto = listaPalabrasEnString.length();
        int distanciaMinima = Integer.MAX_VALUE;
        String textoEntreDistanciaMinima = "";

        WordIndex wordIndex1 = obtenerWordIndexMasBajoDeLasPalabrasDeLaLista(listaPalabrasEnString, palabrasQueCompartenSubparte, 0);
        WordIndex wordIndex2 = obtenerWordIndexMasBajoDeLasPalabrasDeLaLista(listaPalabrasEnString, palabrasQueCompartenSubparte, wordIndex1.getIndex() + 1);


        while (wordIndex2.getIndex() != -1 && wordIndex2.getIndex() < tallaTexto) {

            int distanciaActual = wordIndex2.getIndex() - wordIndex1.getIndex() - wordIndex1.getWord().length();

            if (distanciaActual < distanciaMinima) {
                distanciaMinima = distanciaActual;
                textoEntreDistanciaMinima = obtenerTextoEntreIndicesDadosMasMargen(listaPalabrasEnString, wordIndex1, wordIndex2);
            }

            wordIndex1 = wordIndex2;
            wordIndex2 = obtenerWordIndexMasBajoDeLasPalabrasDeLaLista(listaPalabrasEnString, palabrasQueCompartenSubparte, wordIndex2.getIndex() + 1);
        }

        return new TextDistance(distanciaMinima, textoEntreDistanciaMinima);
    }

    /** Obtiene el WordIndex mas bajo partiendo del dado de las palabras de la lista */
    private WordIndex obtenerWordIndexMasBajoDeLasPalabrasDeLaLista(String listaPalabrasEnString, List<String> palabrasQueCompartenSubparte, Integer indexFrom) {

        String word = null;
        int indexMinimo = Integer.MAX_VALUE;

        for (String wordItem : palabrasQueCompartenSubparte) {
            // Le ponemos el separator delante y detras para que coja palabras enteras
            int index = listaPalabrasEnString.indexOf(WORD_SEPARATOR + wordItem + WORD_SEPARATOR, indexFrom);

            if (index != -1 && index < indexMinimo) {
                indexMinimo = index;
                word = wordItem;
            }
        }

        // Si no se ha cambiado el valor del indexMinimo se devuelve -1
        if (word == null) {
            indexMinimo = -1;
        }

        return new WordIndex(word, indexMinimo);
    }

    /** Obtiene la distancia entre los indices dados mas un margen */
    private String obtenerTextoEntreIndicesDadosMasMargen(String listaPalabrasEnString, WordIndex wordIndex1, WordIndex wordIndex2) {

        int index1 = wordIndex1.getIndex();
        int index2 = wordIndex2.getIndex();

        // Se suma uno porque el index se ha calculado con el WORD_SEPARATOR antes
        int wordLen1Mod = wordIndex1.getWord().length() + 1;
        int wordLen2Mod = wordIndex2.getWord().length() + 1;

        String textoEntreDistanciaMinima = listaPalabrasEnString.substring(index1 + wordLen1Mod, index2);
        String palabraAntes = listaPalabrasEnString.substring(index1, index1 + wordLen1Mod);
        String palabraDespues = listaPalabrasEnString.substring(index2, index2 + wordLen2Mod);

        int indiceMasBajo = Math.max(0, index1 - MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO);
        int indiceMasAlto = Math.min(listaPalabrasEnString.length(), index2 + wordLen2Mod + MARGEN_ANTERIOR_Y_POSTERIOR_TEXTO);

        String textoPrevio = listaPalabrasEnString.substring(indiceMasBajo, index1);
        String textoPosterior = listaPalabrasEnString.substring(index2 + wordLen2Mod, indiceMasAlto);

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
        // Le quitamos los separadores
        String textoEntreIndicesDadosMasMargenVisualizable = textoEntreIndicesDadosMasMargen.replace(WORD_SEPARATOR, " ");

        return textoEntreIndicesDadosMasMargenVisualizable;
    }

    @Override
    public String obtenerInforme() {

        log("Obteniendo lista de SubpartFrequencyItem");
        List<SubpartFrequencyItem> listaSubpartFrequencyItem = getListOfSubpartFrequencyItems();
        log("Lista de SubpartFrequencyItem obtenida con una talla de " + listaSubpartFrequencyItem.size() + " items");

        List<SubpartFrequencyItem> listaSubpartFrequencyItemYaEnInforme = new ArrayList<SubpartFrequencyItem>();
        String listaPalabrasEnString = getListaPalabrasEnString();
        
        StringBuilder sb = new StringBuilder();

        sb.append("ESTADISTICA DE RAICES LEXICAS").append(HTMLConstants.SALTO_DE_LINEA_X2);
        sb.append(HTMLConstants.TABLA_INI);
        appendFilaDelInforme(sb, "Subparte", "Palabras", "Distancia Minima", "Texto Distancia Minima", HTMLConstants.COLOR_GRIS);

        for (SubpartFrequencyItem subpartFrequencyItem : listaSubpartFrequencyItem) {

            int frequency = subpartFrequencyItem.getPalabrasQueCompartenSubparte().size();

            if (frequency >= NUMERO_MINIMO_PALABRAS_DE_INTERES && !esSubpartFrequencyItemRedundanteConLoYaPuestoEnElInforme(subpartFrequencyItem, listaSubpartFrequencyItemYaEnInforme)) {

                List<String> palabrasQueCompartenSubparte = subpartFrequencyItem.getPalabrasQueCompartenSubparte();
                TextDistance textDistance = obtenerNumeroCaracteresMinimoEntre2PalabrasQueCompartenSubparte(listaPalabrasEnString, palabrasQueCompartenSubparte);
                
                if (textDistance.getDistancia() < DISTANCIA_MINIMA_PARA_QUE_REPETICION_SEA_RELEVANTE) {
                    // Añadimos al informe sólo si la distancia minima es inferior a la considerada como relevante
                    appendFilaDelInforme(sb, subpartFrequencyItem, textDistance);
                    listaSubpartFrequencyItemYaEnInforme.add(subpartFrequencyItem);
                }
            }
        }

        sb.append(HTMLConstants.TABLA_FIN);

        return sb.toString();
    }

    /** Devuelve si el SubpartFrequencyItem es redundante con lo ya puesto en el informe. El criterio es que la subparte esté contenida en la otra y que las palabras sean las mismas */
    private boolean esSubpartFrequencyItemRedundanteConLoYaPuestoEnElInforme(SubpartFrequencyItem subpartFrequencyItem, List<SubpartFrequencyItem> listaSubpartFrequencyItemYaEnInforme) {

        String subparte = subpartFrequencyItem.getSubparte();
        List<String> palabrasQueCompartenSubparte = subpartFrequencyItem.getPalabrasQueCompartenSubparte();

        for (SubpartFrequencyItem subpartFrequencyItemYaEnInforme : listaSubpartFrequencyItemYaEnInforme) {

            String subparteYaEnInforme = subpartFrequencyItemYaEnInforme.getSubparte();
            List<String> palabrasQueCompartenSubparteYaEnInforme = subpartFrequencyItemYaEnInforme.getPalabrasQueCompartenSubparte();

            // Suponemos que las subpartes ya en informe son mas largas que las nuevas ya que se han ordenado asi. Además al comparar las listas como se obtienen de la misma forma tienen el mismo orden de las palabras
            // por lo que las compararemos directamente sin tener en cuenta el orden
            if (subparteYaEnInforme.contains(subparte) && sonLasListasIguales(palabrasQueCompartenSubparte, palabrasQueCompartenSubparteYaEnInforme)) {
                return true;
            }
        }

        return false;
    }

    /** Devuelve si las listas son iguales */
    private boolean sonLasListasIguales(List<String> lista1, List<String> lista2) {
        
        int talla1 = lista1.size();
        int talla2 = lista2.size();
        
        if (talla1 != talla2) {
            return false;
        }

        for (int i=0; i<talla1; i++) {

            String word1 = lista1.get(i);
            String word2 = lista2.get(i);

            if (!word1.equals(word2)) {
                return false;
            }
        }

        return true;
    }

    /** Hace un append de la fila del informe */
    private void appendFilaDelInforme(StringBuilder sb, SubpartFrequencyItem subpartFrequencyItem, TextDistance textDistance) {

        String subparte = subpartFrequencyItem.getSubparte();
        List<String> palabrasQueCompartenSubparte = subpartFrequencyItem.getPalabrasQueCompartenSubparte();

        StringBuilder sbPalabras = new StringBuilder();

        for (String wordItem : palabrasQueCompartenSubparte) {
            String palabraConSubparteDestacada = resaltaSubparteEnPalabra(wordItem, subparte);
            sbPalabras.append(palabraConSubparteDestacada).append(", ");
        }

        String palabras = sbPalabras.toString();
        
        // Quitamos la ultima coma
        palabras = palabras.substring(0, palabras.length() - 2);

        appendFilaDelInforme(sb, subparte, palabras, "" + textDistance.getDistancia(), textDistance.getTexto(), HTMLConstants.COLOR_BLANCO);
    }

    /** Resalta con HTML la subparte en la palabra */
    private String resaltaSubparteEnPalabra(String word, String subparte) {

        StringBuilder sbPalabra = new StringBuilder();
        
        int index = word.indexOf(subparte);
        int tallaSubparte = subparte.length();
        int tallaWord = word.length();

        sbPalabra.append(HTMLConstants.LETRA_APAGADA_INI);
        sbPalabra.append(word.substring(0, index));
        sbPalabra.append(HTMLConstants.LETRA_APAGADA_FIN);

        sbPalabra.append(HTMLConstants.LETRA_DESTACADA_INI);
        sbPalabra.append(word.substring(index, index + tallaSubparte));
        sbPalabra.append(HTMLConstants.LETRA_DESTACADA_FIN);

        sbPalabra.append(HTMLConstants.LETRA_APAGADA_INI);
        sbPalabra.append(word.substring(index + tallaSubparte, tallaWord));
        sbPalabra.append(HTMLConstants.LETRA_APAGADA_FIN);

        return sbPalabra.toString();
    }

    /** Hace un append de la fila del informe */
    private void appendFilaDelInforme(StringBuilder sb, String subparte, String palabras, String distanciaMinima, String textoDistanciaMinima, String colorBackground) {

        String inicioCelda = HTMLConstants.getTablaCeldaINI(colorBackground);

        sb.append(HTMLConstants.TABLA_FILA_INI);

        sb.append(inicioCelda);
        sb.append(subparte);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(palabras);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(distanciaMinima);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(inicioCelda);
        sb.append(textoDistanciaMinima);
        sb.append(HTMLConstants.TABLA_CELDA_FIN);

        sb.append(HTMLConstants.TABLA_FILA_FIN);
    }

    /** Obtiene el nombre de la estadistica */
    @Override
    public String obtenerNombreEstadistica() {

        return "Raices Lexicas";
    }

    /** Loga en la consola */
    private static void log(String txt) {
        System.out.println(txt);
    }
}
