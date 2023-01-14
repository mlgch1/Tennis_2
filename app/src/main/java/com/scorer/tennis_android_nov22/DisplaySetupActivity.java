
package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class DisplaySetupActivity extends Activity {

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        myDb = new DBAdapter(this);
        myDb.open();

         EditText text = findViewById(R.id.display);
        text.setText("" + myDb.readSystemStr(DBAdapter.KEY_SYSTEM_DISPLAY));

        myDb.K_Log("Display No. Setup");
    }

    // ******************************************************************************

    @Override
    protected void onDestroy() {

        super.onDestroy();
        myDb.close();
    }

    // ******************************************************************************

    public void onClick(View view) {

        EditText text = findViewById(R.id.display);
        String strString = text.getText().toString();

        strString = strString.trim();

        if (!strString.equals("")) {
            myDb.K_Log("Display " + strString);
            myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_DISPLAY, strString);
        }
        finish();
    }
}
