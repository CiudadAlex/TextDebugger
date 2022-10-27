package org.leviatan.textdebugger.dto;

/**
 *
 * @author Alejandro
 */
public class WordIndex {

    private String word;
    private int index;

    public WordIndex(String word, int index) {
        this.word = word;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getWord() {
        return word;
    }
    
}
