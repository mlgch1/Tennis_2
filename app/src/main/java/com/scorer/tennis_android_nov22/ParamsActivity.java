package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ParamsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

    }

    // ***********************************

    public void onClick_Logging(View view) {
        Intent s_intent = new Intent(this, LogActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_Setup(View view) {
        Intent s_intent = new Intent(this, SetupBatteryActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_WiFi(View view) {
        Intent s_intent = new Intent(this, WiFiSetupActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_Club(View view) {
        Intent s_intent = new Intent(this, ClubSetupActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_Display(View view) {
        Intent s_intent = new Intent(this, DisplaySetupActivity.class);
        startActivity(s_intent);
        finish();
    }

}
