package io.freexd.txt2morse;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
    /*MediaPlayer short_sound_player;
    MediaPlayer long_sound_player;*/

    AudioTrack short_tone;
    AudioTrack long_tone;

    int frequency = 600;

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
        try {
            this.context = context;

            //Delay time for each element
            delay_dot=delay_unit;
            delay_hyphen=delay_unit*3;
            delay_inter_gap=delay_unit;
            delay_short_gap=delay_unit*3;
            delay_medium_gap=delay_unit*7;

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private AudioTrack generateTone(double freqHz, int durationMs)
    {
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }

    public boolean morseToAudioCheck(final List<MorseItem> list_morse_translate, int position){

        start_position=position;
        //Se verifica si se logro abrir archivo de audio en el telefono
  //      if(this.short_sound_player!=null && this.long_sound_player!=null){
            this.list_morse_translate=list_morse_translate;
            Log.d(TAG,"Entro a check morseToFlashLightCheck");
            return true;
      //  }
    //    return false;
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
                            short_tone = generateTone(frequency , delay_dot);
                            short_tone.play();
                            sleep(delay_dot);
                            break;
                        case '-':
                            long_tone = generateTone(frequency , delay_hyphen);
                            long_tone.play();
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
            short_tone.release();
            long_tone.release();
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
