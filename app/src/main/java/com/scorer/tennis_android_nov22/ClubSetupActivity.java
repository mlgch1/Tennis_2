
package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static com.scorer.tennis_android_nov22.GlobalClass.getClub;
import static com.scorer.tennis_android_nov22.GlobalClass.setClub;

public class ClubSetupActivity extends Activity {

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        myDb = new DBAdapter(this);
        myDb.open();

        EditText text = findViewById(R.id.club);
        text.setText("" + getClub());

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
                setClub(strString);
        }

        finish();
    }
}
