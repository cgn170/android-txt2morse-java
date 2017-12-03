package io.freexd.txt2morse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlos on 11/25/17.
 */

public class MorseUtil {

    private Map<String, String> morse_map = new HashMap<String, String>();

    public MorseUtil(){
        morse_map.put("a",".-");
        morse_map.put("b","-...");
        morse_map.put("c","-.-.");
        morse_map.put("d","-..");
        morse_map.put("e",".");
        morse_map.put("f","..-.");
        morse_map.put("g","--.");
        morse_map.put("h","....");
        morse_map.put("i","..");
        morse_map.put("j",".---");
        morse_map.put("k","-.-");
        morse_map.put("l",".-..");
        morse_map.put("m","--");
        morse_map.put("n","-.");
        morse_map.put("o","---");
        morse_map.put("p",".--.");
        morse_map.put("q","--.-");
        morse_map.put("r",".-.");
        morse_map.put("s","...");
        morse_map.put("t","-");
        morse_map.put("u","..-");
        morse_map.put("v","...-");
        morse_map.put("w",".--");
        morse_map.put("x","-..-");
        morse_map.put("y","-.--");
        morse_map.put("z","--..");
        morse_map.put("1",".----");
        morse_map.put("2","..---");
        morse_map.put("3","...--");
        morse_map.put("4","....-");
        morse_map.put("5",".....");
        morse_map.put("6","-....");
        morse_map.put("7","--...");
        morse_map.put("8","---..");
        morse_map.put("9","----.");
        morse_map.put("0","-----");
        morse_map.put(".",".-.-.-");
        morse_map.put(",","--..--");
        morse_map.put("?","..--..");
        morse_map.put("'",".----.");
        morse_map.put("!","-.-.--");
        morse_map.put("/","-..-.");
        morse_map.put("(","-.--.");
        morse_map.put(")","-.--.-");
        morse_map.put("&",".-...");
        morse_map.put(":","---...");
        morse_map.put(";","-.-.-.");
        morse_map.put("=","-...-");
        morse_map.put("+",".-.-.");
        morse_map.put("-","-....-");
        morse_map.put("_","..--.-");
        morse_map.put("\"",".-..-.");
        morse_map.put("$","...-..-");
        morse_map.put("end_of_work","...-.-");
        morse_map.put("error","........");
        morse_map.put("intivation_to_transmit","-.-");
        morse_map.put("starting_signal","-.-.-");
        morse_map.put("new_page_signal",".-.-.");
        morse_map.put("understood","...-.");
        morse_map.put("wait",".-...");
        morse_map.put(" ","|"); // -> Vacio
    }

    //Retorna codigo morse de un string
    public String translateTextToMorse(String text){
        String morse="";
        String text_lowerCase = text.toLowerCase();

        for(int i=0;i<text_lowerCase.length();i++){
            //Se comprueba que exista el caracter
            if(morse_map.containsKey(String.valueOf(text_lowerCase.charAt(i)))){
                //Se agrega el valor
                morse+=morse_map.get(String.valueOf(text_lowerCase.charAt(i)));
                //Se agrega espacio
            }
            else{ //Sino existe se agrega el signo ?
                morse+=morse_map.get("?");
                //Se agrega espacio
//                morse+=" ";
            }
            morse+=" ";

        }
        return morse;
    }


    //Retorna codigo morse de un caracter
    public String translateCharToMorse(String text){
        String morse="";
        String text_lowerCase = text.toLowerCase();

            //Se comprueba que exista el caracter
            if(morse_map.containsKey(text)){
                //Se agrega el valor
                return morse_map.get(text);

            }
            else{ //Sino existe se agrega el signo ?
                return morse_map.get("?");
            }
    }

}
