package io.freexd.txt2morse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    MorseToFlashlight flashlight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flashlight = new MorseToFlashlight(getApplicationContext());
    }


    public void activar_flash(View v){
        flashlight.activarFlashLight();
    }


}
