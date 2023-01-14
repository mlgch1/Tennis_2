
package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ClubSetupActivity extends Activity {

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        myDb = new DBAdapter(this);
        myDb.open();

        EditText text = findViewById(R.id.club);
        text.setText("" + myDb.readSystemStr(DBAdapter.KEY_SYSTEM_CLUB));

        myDb.K_Log("Club Name Setup");
    }

    // ******************************************************************************

    @Override
    protected void onDestroy() {

        super.onDestroy();
        myDb.close();
    }

    // ******************************************************************************

     public void onClick(View view) {

        EditText text = findViewById(R.id.club);
         String strString = text.getText().toString();

         strString = strString.trim();

         if (!strString.equals("")) {
                myDb.K_Log("Club " + strString);
                myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_CLUB, strString);
        }
        finish();
    }
}
