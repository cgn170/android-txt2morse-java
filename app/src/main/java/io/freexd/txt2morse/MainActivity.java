package io.freexd.txt2morse;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    Thread threadFlashLight;
    MorseToFlashlight morseToFlashlightObj;
    Morse morseOjb;
    EditText et_text;
    EditText et_morse;
    SwitchButton sb_output;


    boolean loop_on=false;
    int output = 0; // 0 -> SOUND, 1 -> FLASHLIGHT

    int position = 0;
    private List<MorseItem> list_morse_translate;


   /* public static int position_text_original = 0;
    public static int position_text_morse_begin = 0;
    public static int position_text_morse_end = 0;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Se inicializan los objetos

        morseToFlashlightObj = new MorseToFlashlight(getApplicationContext());
        threadFlashLight = new Thread(morseToFlashlightObj);
        threadFlashLight.start();

        morseOjb = new Morse();
        list_morse_translate = new ArrayList<MorseItem>();


        et_text = (EditText) findViewById(R.id.et_text);
        et_morse = (EditText) findViewById(R.id.et_morse);
        sb_output = (SwitchButton) findViewById(R.id.sb_output);

        sb_output.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    output = 1; //flashlight activated
                  //  Toast.makeText(getApplicationContext(),"FlashLight activated",Toast.LENGTH_SHORT).show();
                }
                else {
                    output = 0; //sound activated
                    //Toast.makeText(getApplicationContext(),"Sound activated!",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void onClick_Convert_Text_to_Morse(View v){

        //Se oculta soft keyboar
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        //String result=morseOjb.translateTextToMorse(et_text.getText().toString());

        String result = "";
        String text_lowerCase = et_text.getText().toString().toLowerCase();
        String character_text = "";
        String character_morse = "";
        int morse_count=0;
        for(int i=0;i<text_lowerCase.length();i++){
            //Se comprueba que exista el caracter
            character_text = String.valueOf(text_lowerCase.charAt(i));
            list_morse_translate.add(new MorseItem(character_text,morseOjb.translateCharToMorse(character_text)));
            //Se modifica el index;
            list_morse_translate.get(list_morse_translate.size()-1).setText_position(i);
            list_morse_translate.get(list_morse_translate.size()-1).setMorse_position_a(morse_count);
            morse_count=morse_count+list_morse_translate.get(list_morse_translate.size()-1).getMorse().length()+1;
            list_morse_translate.get(list_morse_translate.size()-1).setMorse_position_b(morse_count-1);

            result=result+list_morse_translate.get(list_morse_translate.size()-1).getMorse()+" ";
        }


        et_morse.setText(result);
        position = 0;
    }

    public void onClick_Copy_Morse_to_Clipboard(View v) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Morse code", et_morse.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(),"Morse code copied!",Toast.LENGTH_SHORT).show();

    }

    public void onClick_Play_Control(View v){
        //Flashlight
        if(output==1){
           // Log.d(TAG, "entro a play control= "+morseToFlashlightObj.running_thread_morseToFlashLight());
            try {
                if (!threadFlashLight.isAlive()) {
                    if (morseToFlashlightObj.morseToFlashLightCheck(list_morse_translate,position)) {
                        threadFlashLight = new Thread(morseToFlashlightObj);
                        threadFlashLight.start();
                    }
                }
            }catch (Exception e){
                Log.d(TAG, e.getMessage());
            }

        }
        else{ //Sound

        }
    }
    public void onClick_Pause_Control(View v){
        //Flashlight
        if(output==1){
            if (threadFlashLight.isAlive()) {
                morseToFlashlightObj.disable_loop();
                morseToFlashlightObj.turnOff();
                threadFlashLight.interrupt();
                if(loop_on){
                    morseToFlashlightObj.enable_loop();
                }
            }
        }
        else{ //Sound

        }
    }

    public void onClick_Stop_Control(View v){
        //Flashlight
        if(output==1){
            if (threadFlashLight.isAlive()) {
                    morseToFlashlightObj.disable_loop();
                    morseToFlashlightObj.turnOff();
                    threadFlashLight.interrupt();
                    if(loop_on){
                        morseToFlashlightObj.enable_loop();
                    }
                }
            position = 0;
            et_text.setSelection(0,1);

        }
        else{ //Sound

        }
    }

    public void onClick_Loop_Control(View v){
        //Flashlight
        if(output==1){

            if (!threadFlashLight.isAlive()) {

                if(morseToFlashlightObj.getLoop()){
                    morseToFlashlightObj.disable_loop();
                    Toast.makeText(getApplicationContext(),"Loop disabled",Toast.LENGTH_SHORT).show();
                    loop_on = false;
                }
                else{
                    morseToFlashlightObj.enable_loop();
                    Toast.makeText(getApplicationContext(),"Loop enabled",Toast.LENGTH_SHORT).show();
                    loop_on = true;

                }
            }
        }
        else{ //Sound

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusSelectedText event) throws InterruptedException {

        //Se guarda posicion
        position = event.position_text_original;

        et_text.requestFocus();
        et_text.setSelection(event.position_text_original,event.position_text_original+1);

       /* Thread.sleep(200);

        et_morse.requestFocus();
        et_morse.setSelection(event.position_text_morse_begin, event.position_text_morse_end);
        et_morse.clearFocus();*/
        Log.d(TAG, "Posicion actual en morse = "+event.position_text_morse_begin);

        if(!event.morse.equals("|"))
            Toast.makeText(getApplicationContext(),event.text.toUpperCase() +" = "+ event.morse,Toast.LENGTH_SHORT).show();
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}
