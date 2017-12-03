package io.freexd.txt2morse;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by carlos on 11/25/17.
 */

public class MorseToAudio implements Runnable {

    String TAG = "MorseToFlashlight";
    boolean sound_on;
    MediaPlayer short_sound_player;
    MediaPlayer long_sound_player;

    private List<MorseItem> list_morse_translate;
    private volatile boolean loop;
    int delay_unit=200;
    int delay_dot=0; //-> dot .
    int delay_hyphen=0; //-> hyphen -
    int delay_inter_gap=0; //time between dot and hyphen
    int delay_short_gap=0; //time between letters
    int delay_medium_gap=0; //time between words
    Context context;
    int start_position = 0;

    /**
     *
     * Short sound
     * Long sound
     */

    public MorseToAudio(Context context){
        this.context = context;
        try {
            int res_id_short = context.getResources().getIdentifier("short_audio", "raw", context.getPackageName());
            short_sound_player = MediaPlayer.create(context,res_id_short);

            int res_id_long = context.getResources().getIdentifier("long_audio", "raw", context.getPackageName());
            long_sound_player = MediaPlayer.create(context,res_id_long);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void playShortSound(){
        sound_on = true;
        this.short_sound_player.start();
        //this.short_sound_player.reset();
        sound_on = false;
    }

    public void playLongSound(){
        sound_on = true;
        this.long_sound_player.start();
        sound_on = false;
     //   this.long_sound_player.reset();
    }

    public void releaseResources(){
        this.long_sound_player.release();
        this.short_sound_player.release();
    }

    public boolean morseToAudioCheck(final List<MorseItem> list_morse_translate, int position){

        start_position=position;
        //Se verifica si se logro abrir archivo de audio en el telefono
        if(this.short_sound_player!=null && this.long_sound_player!=null){
            this.list_morse_translate=list_morse_translate;
            Log.d(TAG,"Entro a check morseToFlashLightCheck");
            return true;
        }
        return false;
    }

    public void morseToAudioGO(int init_position){
            Log.d(TAG,"Entro a check morseToFlashLightGo");
        try {
            for (int i = init_position; i < list_morse_translate.size(); i++) {

                EventBus.getDefault().post(new EventBusSelectedText(list_morse_translate.get(i).getText_position(), list_morse_translate.get(i).getMorse_position_a(), list_morse_translate.get(i).getMorse_position_b(), list_morse_translate.get(i).getText(), list_morse_translate.get(i).getMorse()));

                Log.d(TAG, "Codigo morse = " + list_morse_translate.get(i).getMorse());
                for (int j = 0; j < list_morse_translate.get(i).getMorse().length(); j++) {
                    switch (list_morse_translate.get(i).getMorse().charAt(j)) {
                        case '.':
                            playShortSound();
                            sleep(delay_dot);
                            break;
                        case '-':
                            playLongSound();
                            sleep(delay_hyphen);
                            break;
                        case ' ':
                            sleep(delay_short_gap);
                            break;
                        case '|':
                            sleep(delay_medium_gap);
                            break;
                        default:
                            sleep(delay_dot);
                            break;
                    }
                    sleep(delay_inter_gap);
                }
                sleep(delay_short_gap);

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void enable_loop(){
        this.loop=true;
    }

    public void disable_loop(){
        this.loop=false;
    }

    public boolean getLoop() {
        return this.loop;
    }


    @Override
    public void run() {
        do{
            morseToAudioGO(start_position);
        }while (loop);

    }
}
