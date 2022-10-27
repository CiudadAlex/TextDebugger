package org.leviatan.textdebugger.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.leviatan.textdebugger.util.HTMLConstants;

/**
 *
 * @author Alejandro
 */
public class MainTextStatistics {

    public static final String SIMBOL_PUNCTUATION = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    public static final String SIMBOL_WHITESPACE = " \t\n\f\r";
    public static final String EXCLUDED_CHARACTERS = SIMBOL_PUNCTUATION + SIMBOL_WHITESPACE;

    /** Genera el informe de las estadisticas y lo añade al StringBuilder */
    public static void generaInformeEstadisticas(StringBuilder sb, String text) {
        
        List<TextStatisticEngine> listaStatisticEngine = generateListOfStatisticEngine(text);

        for (TextStatisticEngine statisticEngine : listaStatisticEngine) {

            String nombreEstadistica = statisticEngine.obtenerNombreEstadistica();

            log("Generando informe de " + nombreEstadistica + "...");
            sb.append(statisticEngine.obtenerInforme()).append(HTMLConstants.SALTO_DE_LINEA_X3);
            log("Informe generado...");
        }
    }

    /** Genera las estadisticas del texto */
    private static List<TextStatisticEngine> generateListOfStatisticEngine(String text) {

        StatisticsNgramFrequency statisticsNgramFrequency1 = new StatisticsNgramFrequency(1);
        StatisticsNgramFrequency statisticsNgramFrequency2 = new StatisticsNgramFrequency(2);
        StatisticsNgramFrequency statisticsNgramFrequency3 = new StatisticsNgramFrequency(3);
        StatisticsNgramFrequency statisticsNgramFrequency4 = new StatisticsNgramFrequency(4);
        StatisticsLexicalRootFrequency statisticsLexicalRootFrequency = new StatisticsLexicalRootFrequency();

        StringTokenizer stok = new StringTokenizer(text, EXCLUDED_CHARACTERS);
        log("Iniciando el reparto de palabras...");

        while (stok.hasMoreTokens()) {

            String word = stok.nextToken();
            statisticsNgramFrequency1.addWord(word);
            statisticsNgramFrequency2.addWord(word);
            statisticsNgramFrequency3.addWord(word);
            statisticsNgramFrequency4.addWord(word);
            statisticsLexicalRootFrequency.addWord(word);
        }

        log("Finalizado el reparto de palabras");

        List<TextStatisticEngine> listaStatisticsNgramFrequency = new ArrayList<TextStatisticEngine>();
        listaStatisticsNgramFrequency.add(statisticsNgramFrequency1);
        listaStatisticsNgramFrequency.add(statisticsNgramFrequency2);
        listaStatisticsNgramFrequency.add(statisticsNgramFrequency3);
        listaStatisticsNgramFrequency.add(statisticsNgramFrequency4);
        listaStatisticsNgramFrequency.add(statisticsLexicalRootFrequency);

        return listaStatisticsNgramFrequency;
    }

    /** Loga en la consola */
    private static void log(String txt) {
        System.out.println(txt);
    }
}
