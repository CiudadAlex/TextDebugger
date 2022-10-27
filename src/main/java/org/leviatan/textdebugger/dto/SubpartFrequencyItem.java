package org.leviatan.textdebugger.dto;

import java.util.List;

/**
 *
 * @author Alejandro
 */
public class SubpartFrequencyItem {

    private String subparte;
    private List<String> palabrasQueCompartenSubparte;

    public SubpartFrequencyItem(String subparte, List<String> palabrasQueCompartenSubparte) {
        this.subparte = subparte;
        this.palabrasQueCompartenSubparte = palabrasQueCompartenSubparte;
    }

    public List<String> getPalabrasQueCompartenSubparte() {
        return palabrasQueCompartenSubparte;
    }

    public String getSubparte() {
        return subparte;
    }
}
