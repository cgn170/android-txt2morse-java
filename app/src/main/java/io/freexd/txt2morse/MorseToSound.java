package io.freexd.txt2morse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlos on 11/25/17.
 */

public class MorseToSound implements Runnable {

    private Map<String, String> morse_map_sound = new HashMap<String, String>();
    /**
     *
     * Short sound
     * Long sound
     */

    public MorseToSound(){

    }

    @Override
    public void run() {

    }
}
