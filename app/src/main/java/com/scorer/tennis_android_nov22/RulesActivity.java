
package com.scorer.tennis_android_nov22;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class RulesActivity extends Activity {

    DBAdapter myDb;

    private int no_Sets_int = 3;        // 3 set game   default
    private boolean set_Type_bool;
    private boolean last_Set_Type_bool;

    private int no_adv;

    private int short_sets;
    private int ss_4;
    private int ss_3;

//    private int fast4;

    private int match_tb_game;
    private int mtb_7;
    private int mtb_10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        myDb = new DBAdapter(this);
        myDb.open();

        no_Sets_int = myDb.readSystem(DBAdapter.KEY_SYSTEM_NO_SETS);
        set_Type_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_SET_TYPE) == 1);
        last_Set_Type_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_LAST_SET) == 1);

        no_adv = myDb.readSystem(DBAdapter.KEY_SYSTEM_NO_ADV);

        short_sets = myDb.readSystem(DBAdapter.KEY_SYSTEM_SHORT_SETS);
        ss_4 = myDb.readSystem(DBAdapter.KEY_SYSTEM_SS_4);
        ss_3 = myDb.readSystem(DBAdapter.KEY_SYSTEM_SS_3);

//        fast4 = myDb.readSystem(DBAdapter.KEY_SYSTEM_FAST4);

        match_tb_game = myDb.readSystem(DBAdapter.KEY_SYSTEM_MATCH_TB);
        mtb_7 = myDb.readSystem(DBAdapter.KEY_SYSTEM_MTB_7);
        mtb_10 = myDb.readSystem(DBAdapter.KEY_SYSTEM_MTB_10);

        display();
    }

    // ***********************************

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDb.close();
    }

    // ***********************************

    public void display() {


//        L.d("short_sets  " + short_sets);
//        L.d("ss_4  " + ss_4);
//        L.d("ss_3  " + ss_3);
//        L.d("match_tb_game  " + match_tb_game);
//        L.d("mtb_7  " + mtb_7);
//        L.d("mtb_10  " + mtb_10);
//
//        L.d("**************************************");
//        L.d("**************************************");
//

        TextView t_head_type_of_set = findViewById(R.id.head_Type_of_Set);
        TextView t_head_last_set_type = findViewById(R.id.head_last_set_type);

        RadioGroup rg_type_of_set = findViewById(R.id.radioGroup_Type_of_Set);
        RadioGroup rg_last_set_type = findViewById(R.id.radioGroup_last_set_type);
        RadioGroup rg_match_tb_game = findViewById(R.id.radioGroup_Match_Tb_Game);

        RadioButton rb_1_set = findViewById(R.id.radioButton_1_Set);
        RadioButton rb_3_set = findViewById(R.id.radioButton_3_Set);
        RadioButton rb_5_set = findViewById(R.id.radioButton_5_Set);

//        RadioButton rb_adv_set = findViewById(R.id.radioButton_Adv_Set);
//        RadioButton rb_tb_set = findViewById(R.id.radioButton_Tb_Set);
//
//        RadioButton rb_ls_adv_set = findViewById(R.id.radioButton_LS_Adv_Set);
//        RadioButton rb_ls_tb_set = findViewById(R.id.radioButton_LS_Tb_Set);

        RadioButton rb_4_games = findViewById(R.id.radioButton_4_games);
        RadioButton rb_3_games = findViewById(R.id.radioButton_3_games);

        RadioButton rb_7_points = findViewById(R.id.radioButton_7_Points);
        RadioButton rb_10_points = findViewById(R.id.radioButton_10_Points);

//        CheckBox cb_fast4 = findViewById(R.id.checkBox_Fast4);
        CheckBox cb_no_adv = findViewById(R.id.checkBox_No_Adv);
        CheckBox cb_short_sets = findViewById(R.id.checkBox_Short_Sets);
        CheckBox cb_match_tb_game = findViewById(R.id.checkBox_Match_Tb_Game);

// No of Sets
        switch (no_Sets_int) {
            case 1:
                rb_1_set.setChecked(true);
                rb_3_set.setChecked(false);
                rb_5_set.setChecked(false);

                break;

            case 3:
                rb_1_set.setChecked(false);
                rb_3_set.setChecked(true);
                rb_5_set.setChecked(false);

                break;

            case 5:
                rb_1_set.setChecked(false);
                rb_3_set.setChecked(false);
                rb_5_set.setChecked(true);

                break;
        }

// No Advantage
        cb_no_adv.setChecked((no_adv) == 1);

        // Set Type

        if (no_adv == 1) {
            rg_last_set_type.setVisibility(View.INVISIBLE);
            t_head_last_set_type.setVisibility(View.INVISIBLE);

            t_head_type_of_set.setVisibility(View.INVISIBLE);
            rg_type_of_set.setVisibility(View.INVISIBLE);

//            cb_fast4.setVisibility(View.INVISIBLE);
            cb_short_sets.setVisibility(View.INVISIBLE);
            rb_4_games.setVisibility(View.INVISIBLE);
            rb_3_games.setVisibility(View.INVISIBLE);
            cb_match_tb_game.setVisibility(View.INVISIBLE);
            rb_7_points.setVisibility(View.INVISIBLE);
            rb_10_points.setVisibility(View.INVISIBLE);
        } else {
            showAll();
        }

// Short Sets
        cb_short_sets.setChecked((short_sets) == 1);
        rb_4_games.setChecked((ss_4) == 1);
        rb_3_games.setChecked((ss_3) == 1);

        if (short_sets == 1) {
            rb_4_games.setVisibility(View.VISIBLE);
            rb_3_games.setVisibility(View.VISIBLE);

            t_head_last_set_type.setVisibility(View.INVISIBLE);
            rg_last_set_type.setVisibility(View.INVISIBLE);

            t_head_type_of_set.setVisibility(View.INVISIBLE);
            rg_type_of_set.setVisibility(View.INVISIBLE);

            cb_no_adv.setVisibility(View.INVISIBLE);
//            cb_fast4.setVisibility(View.INVISIBLE);
            cb_match_tb_game.setVisibility(View.INVISIBLE);
            rb_7_points.setVisibility(View.INVISIBLE);
            rb_10_points.setVisibility(View.INVISIBLE);

            t_head_type_of_set.setVisibility(View.INVISIBLE);
            rg_type_of_set.setVisibility(View.INVISIBLE);

            last_Set_Type_bool = true;
        } else {
            showAll();
        }

// FAST4
//        cb_fast4.setChecked((fast4) == 1);
//
//        if (fast4 == 1) {
//            t_head_type_of_set.setVisibility(View.INVISIBLE);
//            t_head_last_set_type.setVisibility(View.INVISIBLE);
//
//            rg_type_of_set.setVisibility(View.INVISIBLE);
//            rg_last_set_type.setVisibility(View.INVISIBLE);
//            rg_match_tb_game.setVisibility(View.INVISIBLE);
//
//            cb_no_adv.setVisibility(View.INVISIBLE);
//            cb_short_sets.setVisibility(View.INVISIBLE);
//            rb_4_games.setVisibility(View.INVISIBLE);
//            rb_3_games.setVisibility(View.INVISIBLE);
//            cb_match_tb_game.setVisibility(View.INVISIBLE);
//
//        } else {
//            showAll();
//        }

// Match Tie Break
        cb_match_tb_game.setChecked(match_tb_game == 1);
        rb_7_points.setChecked((mtb_7) == 1);
        rb_10_points.setChecked((mtb_10) == 1);

        if (match_tb_game == 1) {
            set_Type_bool = false;
            last_Set_Type_bool = false;

            rb_1_set.setVisibility(View.INVISIBLE);
            rb_7_points.setVisibility(View.VISIBLE);
            rb_10_points.setVisibility(View.VISIBLE);

            t_head_type_of_set.setVisibility(View.INVISIBLE);
            t_head_last_set_type.setVisibility(View.INVISIBLE);

            rg_type_of_set.setVisibility(View.INVISIBLE);
            rg_last_set_type.setVisibility(View.INVISIBLE);

            cb_no_adv.setVisibility(View.INVISIBLE);
            cb_short_sets.setVisibility(View.INVISIBLE);
//            cb_fast4.setVisibility(View.INVISIBLE);
            rb_4_games.setVisibility(View.INVISIBLE);
            rb_3_games.setVisibility(View.INVISIBLE);

        } else {
            showAll();
        }
    }

    // ***********************************

    public void showAll() {
//        if ((no_adv == 0) && (short_sets == 0) && (fast4 == 0) && (match_tb_game == 0)) {
        if ((no_adv == 0) && (short_sets == 0) && (match_tb_game == 0)) {
            TextView t_head_no_sets = findViewById(R.id.head_No_Sets);
            TextView t_head_type_of_set = findViewById(R.id.head_Type_of_Set);
            TextView t_head_last_set_type = findViewById(R.id.head_last_set_type);

            RadioGroup rg_no_sets = findViewById(R.id.radioGroup_No_Sets);
            RadioGroup rg_type_of_set = findViewById(R.id.radioGroup_Type_of_Set);
            RadioGroup rg_last_set_type = findViewById(R.id.radioGroup_last_set_type);
            RadioGroup rg_match_tb_game = findViewById(R.id.radioGroup_Match_Tb_Game);

            RadioButton rb_1_set = findViewById(R.id.radioButton_1_Set);
//            RadioButton rb_3_set = findViewById(R.id.radioButton_3_Set);
//            RadioButton rb_5_set = findViewById(R.id.radioButton_5_Set);
            RadioButton rb_adv_set = findViewById(R.id.radioButton_Adv_Set);
            RadioButton rb_tb_set = findViewById(R.id.radioButton_Tb_Set);
            RadioButton rb_ls_adv_set = findViewById(R.id.radioButton_LS_Adv_Set);
            RadioButton rb_ls_tb_set = findViewById(R.id.radioButton_LS_Tb_Set);
            RadioButton rb_4_games = findViewById(R.id.radioButton_4_games);
            RadioButton rb_3_games = findViewById(R.id.radioButton_3_games);
            RadioButton rb_7_points = findViewById(R.id.radioButton_7_Points);
            RadioButton rb_10_points = findViewById(R.id.radioButton_10_Points);

            rb_1_set.setVisibility(View.VISIBLE);


            //            CheckBox cb_fast4 = findViewById(R.id.checkBox_Fast4);
            CheckBox cb_no_adv = findViewById(R.id.checkBox_No_Adv);
            CheckBox cb_short_sets = findViewById(R.id.checkBox_Short_Sets);
            CheckBox cb_match_tb_game = findViewById(R.id.checkBox_Match_Tb_Game);

            t_head_type_of_set.setVisibility(View.VISIBLE);
            rg_type_of_set.setVisibility(View.VISIBLE);

            cb_no_adv.setVisibility(View.VISIBLE);
//            cb_fast4.setVisibility(View.VISIBLE);
            cb_short_sets.setVisibility(View.VISIBLE);
            rb_4_games.setVisibility(View.INVISIBLE);
            rb_3_games.setVisibility(View.INVISIBLE);

            cb_match_tb_game.setVisibility(View.VISIBLE);
            rg_match_tb_game.setVisibility(View.VISIBLE);
            rb_7_points.setVisibility(View.INVISIBLE);
            rb_10_points.setVisibility(View.INVISIBLE);

            if (!set_Type_bool) {
                rg_last_set_type.setVisibility(View.INVISIBLE);
                t_head_last_set_type.setVisibility(View.INVISIBLE);
                rb_adv_set.setChecked(true);
                rb_tb_set.setChecked(false);
            } else {
                if (!(no_Sets_int == 1)) {
                    t_head_last_set_type.setVisibility(View.VISIBLE);
                    rg_last_set_type.setVisibility(View.VISIBLE);
                } else {
                    t_head_last_set_type.setVisibility(View.INVISIBLE);
                    rg_last_set_type.setVisibility(View.INVISIBLE);
                }
                rb_adv_set.setChecked(false);
                rb_tb_set.setChecked(true);

                rb_ls_adv_set.setChecked(!last_Set_Type_bool);
                rb_ls_tb_set.setChecked(last_Set_Type_bool);
            }
        }
    }

    // ***********************************

    public void onClick_1_Set(View view) {

        TextView t = findViewById(R.id.head_last_set_type);
        t.setVisibility(View.INVISIBLE);

        RadioGroup rad = findViewById(R.id.radioGroup_last_set_type);
        rad.setVisibility(View.INVISIBLE);

        no_Sets_int = 1;

        display();
    }

    // ***********************************

    public void onClick_3_Set(View view) {
        no_Sets_int = 3;

        display();
    }

    // ***********************************

    public void onClick_5_Set(View view) {
        no_Sets_int = 5;

        display();
    }

    // ***********************************

    public void onClick_Advantage(View view) {
        set_Type_bool = false;
        TextView t = findViewById(R.id.head_last_set_type);
        t.setVisibility(View.INVISIBLE);

        RadioGroup rad = findViewById(R.id.radioGroup_last_set_type);
        rad.setVisibility(View.INVISIBLE);

        display();
    }

    // ***********************************

    public void onClick_TieBreak(View view) {
        set_Type_bool = true;

        if (!(no_Sets_int == 1)) {
            TextView t = findViewById(R.id.head_last_set_type);
            t.setVisibility(View.VISIBLE);

            RadioGroup rad = findViewById(R.id.radioGroup_last_set_type);
            rad.setVisibility(View.VISIBLE);
        } else {
            TextView t = findViewById(R.id.head_last_set_type);
            t.setVisibility(View.INVISIBLE);

            RadioGroup rad = findViewById(R.id.radioGroup_last_set_type);
            rad.setVisibility(View.INVISIBLE);

            last_Set_Type_bool = true;
        }

        display();
    }

    // ***********************************

    public void onClick_Ls_Advantage(View view) {
        last_Set_Type_bool = false;

        display();
    }

    // ***********************************

    public void onClick_Ls_TieBreak(View view) {
        last_Set_Type_bool = true;

        display();
    }

    // ***********************************

    public void onClick_No_Adv(View view) {
        CheckBox cb_no_adv = findViewById(R.id.checkBox_No_Adv);
        no_adv = (cb_no_adv.isChecked() ? 1 : 0);

        set_Type_bool = false;
        last_Set_Type_bool = false;

        display();
    }

    // ***********************************

    public void onClick_Short_Sets(View view) {
        CheckBox cb_short_sets = findViewById(R.id.checkBox_Short_Sets);
        short_sets = (cb_short_sets.isChecked() ? 1 : 0);

        if (short_sets == 1) {
            set_Type_bool = true;
            last_Set_Type_bool = true;
        } else {
            set_Type_bool = false;
            last_Set_Type_bool = false;
        }

        display();
    }

    // ***********************************

    public void onClick_4_Games(View view) {
        RadioButton rb_4_games = findViewById(R.id.radioButton_4_games);
        RadioButton rb_3_games = findViewById(R.id.radioButton_3_games);
        ss_4 = (rb_4_games.isChecked() ? 1 : 0);
        ss_3 = (rb_3_games.isChecked() ? 1 : 0);

        display();
    }

    // ***********************************

    public void onClick_3_Games(View view) {
        RadioButton rb_4_games = findViewById(R.id.radioButton_4_games);
        RadioButton rb_3_games = findViewById(R.id.radioButton_3_games);
        ss_4 = (rb_4_games.isChecked() ? 1 : 0);
        ss_3 = (rb_3_games.isChecked() ? 1 : 0);

        display();
    }

    // ***********************************

//    public void onClick_Fast4(View view) {
//        CheckBox cb_fast4 = findViewById(R.id.checkBox_Fast4);
//        fast4 = (cb_fast4.isChecked() ? 1 : 0);
//
//        display();
//    }

    // ***********************************

    public void onClick_Mtb(View view) {
        RadioButton rb_1_set = findViewById(R.id.radioButton_1_Set);
        RadioButton rb_3_set = findViewById(R.id.radioButton_3_Set);
        RadioButton rb_5_set = findViewById(R.id.radioButton_5_Set);

        CheckBox cb_match_tb_game = findViewById(R.id.checkBox_Match_Tb_Game);
        match_tb_game = (cb_match_tb_game.isChecked() ? 1 : 0);

        if (rb_1_set.isChecked() ) {
            rb_1_set.setChecked(false);
            rb_3_set.setChecked(true);
            rb_5_set.setChecked(false);
            no_Sets_int = 3;
    }

        display();
    }

    // ***********************************

    public void onClick_7_Points(View view) {
        RadioButton rb_7_points = findViewById(R.id.radioButton_7_Points);
        RadioButton rb_10_points = findViewById(R.id.radioButton_10_Points);
        mtb_7 = (rb_7_points.isChecked() ? 1 : 0);
        mtb_10 = (rb_10_points.isChecked() ? 1 : 0);

        display();
    }

    // ***********************************

    public void onClick_10_Points(View view) {
        RadioButton rb_7_points = findViewById(R.id.radioButton_7_Points);
        RadioButton rb_10_points = findViewById(R.id.radioButton_10_Points);
        mtb_7 = (rb_7_points.isChecked() ? 1 : 0);
        mtb_10 = (rb_10_points.isChecked() ? 1 : 0);

        display();
    }

    // ***********************************

    public void onClick_Store(View view) {
        Finish();
    }

    // ***********************************

    public void Finish() {

        // No of sets
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_NO_SETS, no_Sets_int);

        // Type of Set
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_TYPE, set_Type_bool ? 1 : 0);

        // Last Set
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_LAST_SET, last_Set_Type_bool ? 1 : 0);

        // No Advantage
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_NO_ADV, no_adv);

        // Short Sets
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SHORT_SETS, (short_sets));
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SS_4, ss_4);
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SS_3, ss_3);

        // FAST4
//        myDb.updateSystem(DBAdapter.KEY_SYSTEM_FAST4, (fast4));

        // Match Tie Break
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_MATCH_TB, match_tb_game);
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_MTB_7, mtb_7);
        myDb.updateSystem(DBAdapter.KEY_SYSTEM_MTB_10, mtb_10);

        logParams();


//        myDb.K_Log("no_adv   " + no_adv);
//        myDb.K_Log("short_sets   " + short_sets);
//        myDb.K_Log("match_tb_game   " + match_tb_game);
//        myDb.K_Log("mtb_7   " + mtb_7);
//        myDb.K_Log("mtb_10   " + mtb_10);

        super.finish();
    }
    public void logParams() {
        myDb.K_Log("---------------------------------");
        myDb.K_Log("Rules Activity");
        myDb.K_Log("No. of Sets                     " + no_Sets_int);
        myDb.K_Log("Set type                        " + set_Type_bool);
        myDb.K_Log("Last Set type                   " + last_Set_Type_bool);

//        myDb.K_Log("No. of Sets to win              " + sets_To_Win_Per_Player_int);
//        myDb.K_Log("Minimum Games to win Set        " + min_Games_To_Win_Set_int);
//        myDb.K_Log("Current Game type               " + current_Game_Type_bool);
//        myDb.K_Log("Is this the next to last Set    " + next_To_Last_Set_int);
//        myDb.K_Log("Minimum Points to win Tb Game   " + min_Points_To_Win_Tb_Game_int);
//        myDb.K_Log("This is the last Set            " + this_Is_Last_Set_bool);
//        myDb.K_Log("Match is complete               " + match_Complete_bool);
    }
}
