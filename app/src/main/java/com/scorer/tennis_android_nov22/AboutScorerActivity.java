package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

public class AboutScorerActivity extends Activity {

    // ***********************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_scorer);

        TextView t = (TextView) findViewById(R.id.about_Scorer);

        int versionCode = com.scorer.tennis_android_nov22.BuildConfig.VERSION_CODE;

        try {
            t.setText("Version  " + versionCode + "      " + "Build  " + getAppTimeStamp(getApplicationContext()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getAppTimeStamp(Context context) throws PackageManager.NameNotFoundException {
        String timeStamp = "";

        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
        String appFile = appInfo.sourceDir;
        long time = new File(appFile).lastModified();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        timeStamp = formatter.format(time);

        return timeStamp;
    }

    // ***********************************

    public void onClick(View view) {
        finish();
    }

}
