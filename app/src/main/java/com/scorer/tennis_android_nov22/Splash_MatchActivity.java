
package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class Splash_MatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_match);

        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {

                // close this activity
                finish();
            }
        }, 2000); // wait for 2 seconds
    }
}
