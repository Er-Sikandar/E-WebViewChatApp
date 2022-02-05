package com.shailersolutions.chatapp.ui.splash;

import static com.shailersolutions.chatapp.utils.Consts.INTERVAL_TIME;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.shailersolutions.chatapp.MainActivity;
import com.shailersolutions.chatapp.R;
import com.shailersolutions.chatapp.baseui.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switchActivity(MainActivity.class);
                finish();
            }
        },INTERVAL_TIME);
    }
}