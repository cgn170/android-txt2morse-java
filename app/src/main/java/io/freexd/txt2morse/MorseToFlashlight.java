package io.freexd.txt2morse;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by carlos on 11/25/17.
 */

public class MorseToFlashlight implements Runnable{

    String TAG = "MorseToFlashlight";
    Camera cam;
    Camera.Parameters cam_parameters;
    boolean flashlight_on;

    private List<MorseItem> list_morse_translate;

    private volatile boolean loop;
    int delay_unit=300;
    int delay_dot=0; //-> dot .
    int delay_hyphen=0; //-> hyphen -
    int delay_inter_gap=0; //time between dot and hyphen
    int delay_short_gap=0; //time between letters
    int delay_medium_gap=0; //time between words
//    Thread thread_morseToFlashLight; //Control of the thread
    Context context;
    //EditText et_text;
    int start_position = 0;


    public MorseToFlashlight(Context context){

        this.context = context;
        //Delay time for each element
        delay_dot=delay_unit;
        delay_hyphen=delay_unit*3;
        delay_inter_gap=delay_unit;
        delay_short_gap=delay_unit*3;
        delay_medium_gap=delay_unit*7;

//        MainActivity main = (MainActivity) context;

        //et_text = (EditText) context.get .findViewById(R.id.et_text);
    }

    /** Turn the devices FlashLight on */
    public void turnOn() {
        if (cam != null) {
            // Turn on LED
            cam_parameters = cam.getParameters();
            cam_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(cam_parameters);
            flashlight_on = true;
        }
    }

    /** Turn the devices FlashLight off */
    public void turnOff() {
        // Turn off flashlight
        if (cam != null) {
            cam_parameters = cam.getParameters();
            if (cam_parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                cam_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cam.setParameters(cam_parameters);
            }
        }
        flashlight_on = false;
    }

    public void morseToFlashLightGO(int init_position){
        try {
            if (cam == null) {
                // Turn on Cam
                cam = Camera.open();
                try {
                    cam.setPreviewDisplay(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cam.startPreview();
            }
                       /*
                       * Codigo de ejecucion del codigo morse
                       * */

            Log.d(TAG,"Entro a check morseToFlashLightGo");



            for (int i = init_position; i < list_morse_translate.size(); i++) {

                  //  MainActivity.position_text_original=i;
                  //  Log.d(TAG, "Posicion actual en morse = "+morse.charAt(MainActivity.position_text_original));

                EventBus.getDefault().post(new EventBusSelectedText(list_morse_translate.get(i).getText_position(),list_morse_translate.get(i).getMorse_position_a(),list_morse_translate.get(i).getMorse_position_b(), list_morse_translate.get(i).getText(),list_morse_translate.get(i).getMorse()));

                Log.d(TAG, "Codigo morse = "+list_morse_translate.get(i).getMorse());
                for(int j=0 ; j<list_morse_translate.get(i).getMorse().length();j++) {
                    switch (list_morse_translate.get(i).getMorse().charAt(j)) {
                        case '.':
                            turnOn();
                            sleep(delay_dot);
                            turnOff();
                            break;
                        case '-':
                            turnOn();
                            sleep(delay_hyphen);
                            turnOff();
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

            if (cam != null) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean morseToFlashLightCheck(final List<MorseItem> list_morse_translate, int position){

        start_position=position;
        //Se verifica si existe flash en el telefono
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            //Se debe ejecutar en un hilo para evitar que el hilo principal este muy ocupado
            this.list_morse_translate=list_morse_translate;
            Log.d(TAG,"Entro a check morseToFlashLightCheck");
            return true;
        }
        return false;
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

            morseToFlashLightGO(start_position);

        }while (loop);

    }

}
