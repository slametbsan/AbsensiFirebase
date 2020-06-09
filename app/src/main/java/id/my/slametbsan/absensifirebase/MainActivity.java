package id.my.slametbsan.absensifirebase;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*  sembunyikan ActionBar, status bar, dan navigation bar
            https://developer.android.com/training/system-ui/navigation
         */
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        //splashscreen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setelah timer habis langsung pindah ke LoginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, 1000); //delayMilis ini yang mengatur lama waktu tunda, dalam mili-detik
    }
}
