package io.freexd.txt2morse;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Camera cam;
    Camera.Parameters cam_parameters;
    boolean on;
    int times=2;
    int delay=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void activar_flash(View v){
        //Se verifica si existe flash en el telefono
        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            //cam = Camera.open();
           /* Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();*/


          /*  cam.stopPreview();
            cam.release();*/

        //Se debe ejecutar en un hilo para evitar que el hilo principal este muy ocupado
            Thread t = new Thread() {
                public void run() {
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

                        for (int i=0; i < times*2; i++) {
                            toggleFlashLight();
                            sleep(delay);
                        }

                        if (cam != null) {
                            cam.stopPreview();
                            cam.release();
                            cam = null;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

            t.start();
        }
    }

    /** Turn the devices FlashLight on */
    public void turnOn() {
        if (cam != null) {
            // Turn on LED
            cam_parameters = cam.getParameters();
            cam_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(cam_parameters);

            on = true;
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
        on = false;
    }

    /** Toggle the flashlight on/off status */
    public void toggleFlashLight() {
        if (!on) { // Off, turn it on
            turnOn();
        } else { // On, turn it off
            turnOff();
        }
    }
}
