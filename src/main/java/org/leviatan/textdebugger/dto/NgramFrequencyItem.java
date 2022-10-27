package org.leviatan.textdebugger.dto;

/**
 *
 * @author Alejandro
 */
public class NgramFrequencyItem {

    private String ngram;
    private Integer frequency;

    public NgramFrequencyItem(String ngram, Integer frequency) {
        this.ngram = ngram;
        this.frequency = frequency;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public String getNgram() {
        return ngram;
    }
}
