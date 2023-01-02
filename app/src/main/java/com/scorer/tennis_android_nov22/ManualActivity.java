package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static com.scorer.tennis_android_nov22.DBAdapter.KEY_SYSTEM_POINTS_A;
import static com.scorer.tennis_android_nov22.DBAdapter.KEY_SYSTEM_POINTS_B;
import static com.scorer.tennis_android_nov22.DBAdapter.KEY_SYSTEM_NO_SETS;

public class ManualActivity extends Activity {

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        myDb = new DBAdapter(this);
        myDb.open();

        // *********** Scores

        String s = myDb.readSystemStr(KEY_SYSTEM_POINTS_A);
        EditText t = findViewById(R.id.points_no_a);
        t.setText(s);

        s = myDb.readSystemStr(KEY_SYSTEM_POINTS_B);
        t = findViewById(R.id.points_no_b);
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_GAMES_A);
        t = findViewById(R.id.games_no_a);
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_GAMES_B);
        t = findViewById(R.id.games_no_b);
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_SETS_A);
        t = findViewById(R.id.sets_no_a);
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_SETS_B);
        t = findViewById(R.id.sets_no_b);
        t.setText(s);

        L.d("onCreate");
    }

// ***********************************

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDb.close();
    }

    // ***********************************

    public void onClick(View view) {

        // Points

        EditText t = findViewById(R.id.points_no_a);
        int intValue = Integer.parseInt(t.getText().toString());
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_POINTS_A, String.valueOf(intValue));

        t = findViewById(R.id.points_no_b);
        intValue = Integer.parseInt(t.getText().toString());
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_POINTS_B, String.valueOf(intValue));

        // Games

        t = findViewById(R.id.games_no_a);
        intValue = Integer.parseInt(t.getText().toString());
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_GAMES_A, String.valueOf(intValue));

        t = findViewById(R.id.games_no_b);
        intValue = Integer.parseInt(t.getText().toString());
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_GAMES_B, String.valueOf(intValue));

        // Sets

        t = findViewById(R.id.sets_no_a);
        intValue = Integer.parseInt(t.getText().toString());
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SETS_A, String.valueOf(intValue));

        t = findViewById(R.id.sets_no_b);
        intValue = Integer.parseInt(t.getText().toString());
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SETS_B, String.valueOf(intValue));

        finish();
    }
    // ***********************************

    public void onCheck(View view) {

        //      0 - Love,       1 - 15,         2 - 30,         3 - 40,         4 - Adv

        // Points

        EditText t = findViewById(R.id.points_no_a);
        int intValue_a = Integer.parseInt(t.getText().toString());

        t = findViewById(R.id.points_no_b);
        int intValue_b = Integer.parseInt(t.getText().toString());

        if ((intValue_a == 4) && (intValue_b != 3)) {
            intValue_a = 3;
        }
        if ((intValue_b == 4) && (intValue_a != 3)) {
            intValue_b = 3;
        }

        t = findViewById(R.id.points_no_a);
        t.setText(String.valueOf(intValue_a));

        t = findViewById(R.id.points_no_b);
        t.setText(String.valueOf(intValue_b));

        // Games

        t = findViewById(R.id.games_no_a);
        intValue_a = Integer.parseInt(t.getText().toString());

        t = findViewById(R.id.games_no_b);
        intValue_b = Integer.parseInt(t.getText().toString());

        if (intValue_a > 5) {
            if(intValue_b <= (intValue_a - 2)) {
                intValue_b = intValue_a - 1;
            }
            if(intValue_b >= (intValue_a + 2)) {
                intValue_b = intValue_a + 1;
            }
        }

        if (intValue_b > 5) {
            if(intValue_a <= (intValue_b - 2)) {
                intValue_a = intValue_b - 1;
            }
            if(intValue_a >= (intValue_b + 2)) {
                intValue_a = intValue_b + 1;
            }
        }

        t = findViewById(R.id.games_no_a);
        t.setText(String.valueOf(intValue_a));

        t = findViewById(R.id.games_no_b);
        t.setText(String.valueOf(intValue_b));

        // Sets

        int intSets = Integer.parseInt(myDb.readSystemStr(KEY_SYSTEM_NO_SETS).toString());

        t = findViewById(R.id.sets_no_a);
        intValue_a = Integer.parseInt(t.getText().toString());

        t = findViewById(R.id.sets_no_b);
        intValue_b = Integer.parseInt(t.getText().toString());

        switch (intSets) {
            case 1:
                if(intValue_a != 0){intValue_a = 0;}
                if(intValue_b != 0){intValue_b = 0;}
                break;

            case 3:
                if(intValue_a >= 2){intValue_a = 1;}
                if(intValue_b >= 2){intValue_b = 1;}

                break;

            case 5:
                if(intValue_a >= 3){intValue_a = 2;}
                if(intValue_b >= 3){intValue_b = 2;}

                break;
        }

        t = findViewById(R.id.sets_no_a);
        t.setText(String.valueOf(intValue_a));

        t = findViewById(R.id.sets_no_b);
        t.setText(String.valueOf(intValue_b));
    }
}
