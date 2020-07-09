package io.freexd.txt2morse;

/**
 * Created by carlos on 12/2/17.
 */

public class EventBusSelectedText {

    public int position_text_original = 0;
    public int position_text_morse_begin = 0;
    public int position_text_morse_end = 0;
    public String text = "";
    public String morse = "";

    public EventBusSelectedText(int p_text, int p_morse_b, int p_morse_e, String t, String m){
        position_text_original=p_text;
        position_text_morse_begin=p_morse_b;
        position_text_morse_end=p_morse_e;
        text = t;
        morse = m;
    }

}
