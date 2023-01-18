package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetupActivity extends Activity {

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        myDb = new DBAdapter(this);
        myDb.open();
    }

    // ***********************************

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDb.close();
    }

    // ***********************************

    public void onClick_Manual(View view) {
        myDb.K_Log("Setup - Manual");
        Intent s_intent = new Intent(SetupActivity.this, ManualActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_Rules(View view) {
        myDb.K_Log("Setup - Rules");

// TODO
        Intent s_intent = new Intent(this, LogActivity.class);
        startActivity(s_intent);
//        Intent s_intent = new Intent(SetupActivity.this, RulesActivity.class);
//        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_Test(View view) {
        myDb.K_Log("Setup - Test");
        Intent s_intent = new Intent(SetupActivity.this, TestActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_D_T(View view) {
        myDb.K_Log("Setup - Date Time");
        Intent s_intent = new Intent(SetupActivity.this, t_dActivity.class);
        startActivity(s_intent);
        finish();
    }

// ***********************************

    public void onClick_disclaimer(View view) {
        myDb.K_Log("Setup - Disclaimer");
        Intent s_intent = new Intent(SetupActivity.this, DisclaimerActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_about(View view) {
        myDb.K_Log("Setup - About SCaT");
        Intent s_intent = new Intent(SetupActivity.this, AboutActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_about_Scorer(View view) {
        myDb.K_Log("Setup - About Scorer");
        Intent s_intent = new Intent(SetupActivity.this, AboutScorerActivity.class);
        startActivity(s_intent);
        finish();
    }

    // ***********************************

    public void onClick_Params(View view) {
        myDb.K_Log("Setup - Parameters");
        Intent s_intent = new Intent(SetupActivity.this, PasswordActivity.class);
        startActivity(s_intent);
        finish();
    }
}
