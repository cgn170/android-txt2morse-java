package io.freexd.txt2morse;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    MorseToFlashlight morseToFlashlightObj;
    Morse morseOjb;
    EditText et_text;
    EditText et_morse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Se inicializan los objetos
        morseToFlashlightObj = new MorseToFlashlight(getApplicationContext());
        morseOjb = new Morse();

        et_text = (EditText) findViewById(R.id.et_text);
        et_morse = (EditText) findViewById(R.id.et_morse);




    }


    public void morse_to_flashlight(View v){
        morseToFlashlightObj.activarFlashLight();
    }


    public void onClick_Convert_Text_to_Morse(View v){
        String result=morseOjb.translateTextToMorse(et_text.getText().toString());
        et_morse.setText(result);
    }

    public void onClick_Copy_Morse_to_Clipboard(View v) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Morse code", et_morse.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(),"Morse code copied!",Toast.LENGTH_SHORT).show();

    }

}
