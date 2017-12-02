package io.freexd.txt2morse;

/**
 * Created by carlos on 12/2/17.
 */

public class MorseItem {
    String text ;
    String morse ;
    int text_position = 0;
    int morse_position_a = 0;
    int morse_position_b = 0;

    public int getText_position() {
        return text_position;
    }

    public void setText_position(int text_position) {
        this.text_position = text_position;
    }

    public int getMorse_position_a() {
        return morse_position_a;
    }

    public void setMorse_position_a(int morse_position_a) {
        this.morse_position_a = morse_position_a;
    }

    public int getMorse_position_b() {
        return morse_position_b;
    }

    public void setMorse_position_b(int morse_position_b) {
        this.morse_position_b = morse_position_b;
    }

    public MorseItem(String text, String morse){
        this.text = text;
        this.morse = morse;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMorse() {
        return morse;
    }

    public void setMorse(String morse) {
        this.morse = morse;
    }


}
