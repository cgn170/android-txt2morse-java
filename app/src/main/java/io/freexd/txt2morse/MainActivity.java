package io.freexd.txt2morse;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    String TAG = "MainActivity";
    public static final String PREFS_NAME = "T2morse";
    Thread threadFlashLight;
    Thread threadAudio;
    MorseToFlashlight morseToFlashlightObj;
    MorseToAudio morseToAudioObj;
    MorseUtil morseOjb;
    EditText et_text;
    EditText et_morse;
    SwitchButton sb_output;

    boolean loop_on=false;
    int output = 0; // 0 -> SOUND, 1 -> FLASHLIGHT

    int position = 0;
    private List<MorseItem> list_morse_translate;

    ImageButton bt_loop;
    ImageButton bt_play;
    ImageButton bt_pause;
    ImageButton bt_stop;

    ImageView icon_audio;
    ImageView icon_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Se inicializan los objetos

        morseToFlashlightObj = new MorseToFlashlight(getApplicationContext());
        threadFlashLight = new Thread(morseToFlashlightObj);
        threadFlashLight.start();

        morseToAudioObj = new MorseToAudio(getApplicationContext());
        threadAudio = new Thread(morseToAudioObj);
        threadAudio.start();


        morseOjb = new MorseUtil();
        list_morse_translate = new ArrayList<MorseItem>();


        et_text = (EditText) findViewById(R.id.et_text);
        et_morse = (EditText) findViewById(R.id.et_morse);
        sb_output = (SwitchButton) findViewById(R.id.sb_output);

        icon_audio = (ImageView) findViewById(R.id.icon_audio);
        icon_light = (ImageView) findViewById(R.id.icon_light);

       /* et_morse.setEnabled(false);
        et_morse.setKeyListener( null );
        et_morse.setFocusable( false );
        et_morse.setCursorVisible(false);
*/
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        String saved_text = prefs.getString("et_text", "");
        String saved_morse = prefs.getString("et_morse", "");
        int saved_output = prefs.getInt("output", 0);


        sb_output.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    output = 1; //flashlight activated
                  //  Toast.makeText(getApplicationContext(),"FlashLight activated",Toast.LENGTH_SHORT).show();
                   // icon_light.setBackgroundColor(Color.CYAN);
                   // icon_audio.setBackgroundColor(Color.TRANSPARENT);

                    //icon_light.setColorFilter(Color.green(100));

                    icon_light.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));

                    icon_audio.clearColorFilter();



                }
                else {
                    output = 0; //sound activated
                    //Toast.makeText(getApplicationContext(),"Sound activated!",Toast.LENGTH_SHORT).show();
                 //   icon_light.setBackgroundColor(Color.TRANSPARENT);
                   // icon_audio.setBackgroundColor(Color.CYAN);


                   // icon_audio.setColorFilter(Color.green(100));
                    icon_audio.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));

                    icon_light.clearColorFilter();
                }

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("output", output);
                editor.commit();
            }
        });

        if(saved_output==1)  {
            sb_output.setChecked(true);
           // icon_light.setBackgroundColor(Color.CYAN);
           // icon_light.setColorFilter(Color.green(100));
            icon_light.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));


        }
        else{
            sb_output.setChecked(false);
           // icon_audio.setBackgroundColor(Color.CYAN);
            //icon_audio.setColorFilter(Color.green(100));
            icon_audio.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));

        }

        et_text.setText(saved_text);
        et_morse.setText(saved_morse);

        //Se convierte lo que este previamente guardado

        //onClick_Convert_Text_to_Morse(null);
        Button click_convert = (Button) findViewById(R.id.btn_convert);
        click_convert.performClick();


        //Se inicializa el banner

      //  MobileAds.initialize(this, "ca-app-pub-7610777618304000~5667345644");

       // AdView banner = (AdView) findViewById(R.id.adView);

        //MobileAds.initialize(this, "ca-app-pub-3940256099942544/5224354917");
        MobileAds.initialize(this, "ca-app-pub-7610777618304000~5667345644");


        mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

      //  bt_loop = (ImageButton) findViewById(R.id.bt_menu_reproducir_loop);
        bt_play = (ImageButton) findViewById(R.id.bt_menu_reproducir_play);
        bt_pause = (ImageButton) findViewById(R.id.bt_menu_reproducir_pause);
        bt_stop = (ImageButton) findViewById(R.id.bt_menu_reproducir_stop);

    }

    public void onClick_Convert_Text_to_Morse(View v){
        try {
            list_morse_translate.clear();

            //Se oculta soft keyboar
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            //String result=morseOjb.translateTextToMorse(et_text.getText().toString());

            String result = "";
            String text_lowerCase = et_text.getText().toString().toLowerCase();
            if (text_lowerCase.length() == 0) return;

            String character_text = "";
            String character_morse = "";
            int morse_count = 0;
            for (int i = 0; i < text_lowerCase.length(); i++) {
                //Se comprueba que exista el caracter
                character_text = String.valueOf(text_lowerCase.charAt(i));
                list_morse_translate.add(new MorseItem(character_text, morseOjb.translateCharToMorse(character_text)));
                //Se modifica el index;
                list_morse_translate.get(list_morse_translate.size() - 1).setText_position(i);
                list_morse_translate.get(list_morse_translate.size() - 1).setMorse_position_a(morse_count);
                morse_count = morse_count + list_morse_translate.get(list_morse_translate.size() - 1).getMorse().length() + 1;
                list_morse_translate.get(list_morse_translate.size() - 1).setMorse_position_b(morse_count - 1);

                result = result + list_morse_translate.get(list_morse_translate.size() - 1).getMorse() + " ";
            }

            et_morse.setText(result);
            position = 0;

            //Se guarda info de texto en el dispositivo
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("et_text", et_text.getText().toString());
            editor.putString("et_morse", et_morse.getText().toString());
            editor.commit();
            Toast.makeText(getApplicationContext(), "Morse generated!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());

        }
    }

    public void onClick_Copy_Morse_to_Clipboard(View v) {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Morse code", et_morse.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Morse code copied!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void onClick_Play_Control(View v){
        try {
            //Log.d(TAG, "position actual = "+position);
            //Log.d(TAG, "tama#o text= "+et_text.getText().toString().length());
            if(et_text.getText().toString().length()==0) return;
            if((position+1)>=(et_text.getText().toString().length())) position = 0;
            //Flashlight
            if(output==1){
               // Log.d(TAG, "entro a play control= "+morseToFlashlightObj.running_thread_morseToFlashLight());

                    if (!threadFlashLight.isAlive()) {
                        if (morseToFlashlightObj.morseToFlashLightCheck(list_morse_translate,position)) {
                            threadFlashLight = new Thread(morseToFlashlightObj);
                            threadFlashLight.start();
                            bt_pause.setColorFilter(Color.BLACK);
                            bt_play.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));

                            //bt_pause.setBackgroundColor(Color.WHITE);
                           // bt_play.setBackgroundColor(Color.CYAN);
                        }
                    }


            }
            else{ //Sound
                if (!threadAudio.isAlive()) {
                    if (morseToAudioObj.morseToAudioCheck(list_morse_translate,position)) {
                        threadAudio = new Thread(morseToAudioObj);
                        threadAudio.start();
                       // bt_pause.setBackgroundColor(Color.WHITE);
                        //bt_play.setBackgroundColor(Color.CYAN);
                        bt_pause.setColorFilter(Color.BLACK);
                        bt_play.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));
                    }
                }
            }
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }
    public void onClick_Pause_Control(View v){
        try{
        //Flashlight
        if(output==1){
            if (threadFlashLight.isAlive()) {
                morseToFlashlightObj.disable_loop();
                morseToFlashlightObj.turnOff();
                threadFlashLight.interrupt();
                if(loop_on){
                    morseToFlashlightObj.enable_loop();
                }
              //  bt_play.setBackgroundColor(Color.WHITE);
               // bt_pause.setBackgroundColor(Color.CYAN);
                bt_play.setColorFilter(Color.BLACK);
                bt_pause.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));

            }
        }
        else{ //Sound
            if (threadAudio.isAlive()) {
                morseToAudioObj.disable_loop();
                //morseToAudioObj.turnOff();
                threadAudio.interrupt();
                if(loop_on){
                    morseToAudioObj.enable_loop();
                }
                //bt_play.setBackgroundColor(Color.WHITE);
                //bt_pause.setBackgroundColor(Color.CYAN);
                bt_play.setColorFilter(Color.BLACK);
                bt_pause.setColorFilter(getApplicationContext().getResources().getColor(R.color.colorAccent));

            }
        }
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void onClick_Stop_Control(View v){
        //Flashlight
        //if(output==1){
    //    bt_stop.setBackgroundColor(Color.CYAN);
        try {
            if (threadFlashLight.isAlive()) {
                morseToFlashlightObj.disable_loop();
                morseToFlashlightObj.turnOff();
                threadFlashLight.interrupt();
                if (loop_on) {
                    morseToFlashlightObj.enable_loop();
                }
            }
            //}
            //else{ //Sound
            if (threadAudio.isAlive()) {
                morseToAudioObj.disable_loop();
                //morseToAudioObj.turnOff();
                threadAudio.interrupt();
                if (loop_on) {
                    morseToAudioObj.enable_loop();
                }
            }
            //}
            position = 0;
            et_text.setSelection(0, 1);
            //bt_stop.setBackgroundColor(Color.WHITE);
            //bt_play.setBackgroundColor(Color.WHITE);
            //bt_pause.setBackgroundColor(Color.WHITE);
            bt_play.setColorFilter(Color.BLACK);
            bt_pause.setColorFilter(Color.BLACK);

        }catch (Exception e){
            Log.d(TAG, e.getMessage());

        }
    }

    public void onClick_Loop_Control(View v){
        //Flashlight
        if(output==1){

            if (!threadFlashLight.isAlive()) {

                if(morseToFlashlightObj.getLoop()){
                    morseToFlashlightObj.disable_loop();
                 //   Toast.makeText(getApplicationContext(),"Loop disabled",Toast.LENGTH_SHORT).show();
                    loop_on = false;

                    //Cambia boton de loop a inactivo
                    bt_loop.setBackgroundColor(Color.WHITE);

                }
                else{
                    morseToFlashlightObj.enable_loop();
                   // Toast.makeText(getApplicationContext(),"Loop enabled",Toast.LENGTH_SHORT).show();
                    loop_on = true;
                    //Cambia boton de loop a activo
                    bt_loop.setBackgroundColor(Color.CYAN);

                }
            }
        }
        else{ //Sound
            if (!threadAudio.isAlive()) {

                if(morseToAudioObj.getLoop()){
                    morseToAudioObj.disable_loop();
                    Toast.makeText(getApplicationContext(),"Loop disabled",Toast.LENGTH_SHORT).show();
                    loop_on = false;
                    //Cambia boton de loop a inactivo
                    bt_loop.setBackgroundColor(Color.WHITE);

                }
                else{
                    morseToAudioObj.enable_loop();
                    Toast.makeText(getApplicationContext(),"Loop enabled",Toast.LENGTH_SHORT).show();
                    loop_on = true;
                    //Cambia boton de loop a activo
                    bt_loop.setBackgroundColor(Color.CYAN);

                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusSelectedText event) throws InterruptedException {

        //Se guarda posicion
        position = event.position_text_original;

        et_text.requestFocus();
        et_text.setSelection(event.position_text_original,event.position_text_original+1);

        Log.d(TAG, "Posicion actual en morse = "+event.position_text_morse_begin);

        if(!event.morse.equals("|"))
            Toast.makeText(getApplicationContext(),event.text.toUpperCase() +" = "+ event.morse,Toast.LENGTH_SHORT).show();

        //Se verifica cuando llegue al final
        if(position==(et_text.getText().length()-1)) {
            //  bt_play.setBackgroundColor(Color.WHITE);
            bt_play.setColorFilter(Color.BLACK);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        et_text.clearFocus();

    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        et_text.clearFocus();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        et_text.clearFocus();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        et_text.clearFocus();

        super.onDestroy();
    }


}
