
package com.scorer.tennis_android_nov22;

// From pre WiFi version 15/08/2016

// 8 Inch Branch - 01/04/2018

// Nov/Dec 2022

import static android.os.SystemClock.sleep;
import static com.scorer.tennis_android_nov22.Config.context;
import static com.scorer.tennis_android_nov22.GlobalClass.getClub;
import static com.scorer.tennis_android_nov22.GlobalClass.getInc;
import static com.scorer.tennis_android_nov22.GlobalClass.getSSID;
import static com.scorer.tennis_android_nov22.GlobalClass.getStartTesting;
import static com.scorer.tennis_android_nov22.GlobalClass.getTest;
import static com.scorer.tennis_android_nov22.GlobalClass.getThresh;
import static com.scorer.tennis_android_nov22.GlobalClass.setChannel;
import static com.scorer.tennis_android_nov22.GlobalClass.setClub;
import static com.scorer.tennis_android_nov22.GlobalClass.setInc;
import static com.scorer.tennis_android_nov22.GlobalClass.setSSID;
import static com.scorer.tennis_android_nov22.GlobalClass.setTest;
import static com.scorer.tennis_android_nov22.GlobalClass.setThresh;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.scorer.tennis_android_nov22.R.id;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

// ******************************************************************************

public class MainActivity extends Activity {

    private AlertDialog ad = null;
    private com.scorer.tennis_android_nov22.DBAdapter myDb;
    private TextView t;

    private boolean no_onResume = false;

    private int c_points_h;         // current counters
    private int c_points_v;
    private int c_games_h;
    private int c_games_v;
    private int c_sets_h;
    private int c_sets_v;

    private int h_points_h;       // historic set of counters
    private int h_points_v;
    private int h_games_h;
    private int h_games_v;
    private int h_sets_h;
    private int h_sets_v;

    private String server = "Z";
    private String h_server;
    private String strPlayerButton = "Z";

    // Timers
    private resumeTimer res_counter;
    private boolean res_timerActive = false;

    private flashTimer flash_counter;

    private illegal_flashTimer illegal_flash_counter;

    private server_flashTimer server_flash_counter;

    private reset_flashTimer reset_flash_counter;

    // WiFi
    private static final int LOCATION = 1;

    private wifiTimer wifi_counter;
    private String wifi_ssid;
    private boolean wifi_connect_in_progress = false;

    public boolean wifi_connected = false;
    public boolean wifi_enabled = false;
    private Socket wifi_socket;

    private wifiSendThread wifi_sendThread;
    private boolean wifi_stopSendThread = false;

    private wifiReceiveThread wifi_receiveThread;
    private boolean wifi_stopReceiveThread = false;

    private boolean wifi_stopReceive = false;

    private DataOutputStream wifi_dataOut;
    private BufferedReader wifi_dataIn;

    // Other
    private buttonTimer button_counter;

    private batteryTimer battery_counter;
    private int batteryTestLevel;
    private int batteryMessageNo = 5;

    private ImageView i;

    private boolean flipFlag_bool = false;
    private int flipCounter_int = 2;

    private int no_Sets_int = 0;                            // No of Sets
    private boolean current_Set_Type_bool = false;
    private boolean last_Set_Type_bool = false;
    private boolean current_Game_Type_bool = false;         // 0 - current game
    private boolean this_Is_Last_Set_bool = false;

    private int min_Games_To_Win_Set_int;
    private int next_To_Last_Set_int = 0;
    private int sets_To_Win_Per_Player_int = 0;

    private boolean last_Set_is_Adv_bool = false;

    private boolean match_Complete_bool = false;

    private String c_Audio_str;
    private String c_Audio_Temp_str;

/*
    G - Game
    S - Set
    M - Match
    D - Deuce
    A - Advantage Server
    a - Advantage Receiver
    C - Change Ends
    B - Tone
    X - Nothing
*/

//    private boolean fast4_bool;

    private boolean no_Adv_bool;

    private boolean short_Sets_bool;
    private boolean ss_4_bool;
    private boolean ss_3_bool;

    private boolean match_tb_game_bool;
    private boolean mtb_7_bool;
    private boolean mtb_10_bool;

    private boolean last_Set_is_Tb_Game_bool = false;
    private int min_Points_To_Win_Tb_Game_int;

    private int reset_Flag_int;

// ******************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MainActivity.this, com.scorer.tennis_android_nov22.SplashActivity.class);
        startActivity(intent);
        setContentView(R.layout.activity_main);

        context = this;
        myDb = new com.scorer.tennis_android_nov22.DBAdapter(this);
        myDb.open();

        setupGlobals();

        if (getClub().contains(getString(R.string.illegal))) {
            start_illegal_flashTimer();

            no_onResume = true;
        }

        myDb.K_Log(getString(R.string.start_app));

        start_ResumeTimer();    // Delay onResume() for things to settle

        batteryTestLevel = getThresh();
        start_batteryTimer();

        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        wifi.setWifiEnabled(false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wifi.setWifiEnabled(true);

        wifi_ssid = getString(R.string.scorer) + getSSID();

        start_wifiTimer();

// Flash Reset on Startup
        String s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_RESET);
        reset_Flag_int = Integer.parseInt(s);

        if (reset_Flag_int == 1) {
            t = findViewById(id.id_no);
            t.setText("   ");

            t = findViewById(id.id_notice);
            t.setText(R.string.reset1);

            t = findViewById(id.id_game);
            t.setText("              ");

            start_reset_flashTimer();
        } else {
            game_Notice();
        }

// TODO
        // Data Base Viewer - To be deleted  ********************************************************

        TextView tv = findViewById(R.id.id_wifi_message);

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent db_Manager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
                startActivity(db_Manager);
            }
        });
    }

// ******************************************************************************

    @Override
    public void onBackPressed() {
    }

// ******************************************************************************

    @Override
    protected void onDestroy() {

        myDb.K_Log(getString(R.string.close_app));
        myDb.close();

        stop_batteryTimer();
        stop_wifiTimer();

        wifi_stopSendThread = true;
        wifi_stopReceiveThread = true;

        super.onDestroy();
    }

// ******************************************************************************

    private void setupGlobals() {

        setThresh(myDb.readSystem(DBAdapter.KEY_SYSTEM_BATT_THRESH));
        setInc(myDb.readSystem(DBAdapter.KEY_SYSTEM_BATT_INC));

        setSSID(myDb.readSystem(DBAdapter.KEY_SYSTEM_SSID));
        setChannel(myDb.readSystem(DBAdapter.KEY_SYSTEM_CHANNEL));

        setClub(myDb.readSystemStr(DBAdapter.KEY_SYSTEM_CLUB));
    }

// ******************************************************************************

    @Override
    protected void onResume() {
        super.onResume();

        if (res_timerActive) return;

        myDb.close();
        myDb.open();

        myDb.K_Log(getString(R.string.resume_app));

        setup_Game_Variables();

        if (reset_Flag_int == 0) {
            game_Notice();
        }
// *********** Club

        t = findViewById(id.id_club);
        t.setText(getClub());

        if (getClub().contains(getString(R.string.illegal))) {
            start_illegal_flashTimer();
        } else {
            stop_illegal_flashTimer();
        }

        if (no_onResume) return;

// *********** Player Names

        String s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_NAME_A);
        t = findViewById(id.id_name_a);
        t.setText(s);

        t = findViewById(id.id_A_Team);
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_NAME_B);
        t = findViewById(id.id_name_b);
        t.setText(s);

        t = findViewById(id.id_B_Team);
        t.setText(s);

// *********** Scores

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_POINTS_A);
        t = findViewById(id.id_points_a);
        if (!current_Game_Type_bool) {
            c_points_h = Integer.parseInt(s);
            s = convert_Points(c_points_h);
        }
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_POINTS_B);
        t = findViewById(id.id_points_b);
        if (!current_Game_Type_bool) {
            c_points_v = Integer.parseInt(s);
            s = convert_Points(c_points_v);
        }
        t.setText(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_GAMES_A);
        t = findViewById(id.id_games_a);
        t.setText(s);
        c_games_h = Integer.parseInt(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_GAMES_B);
        t = findViewById(id.id_games_b);
        t.setText(s);
        c_games_v = Integer.parseInt(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_SETS_A);
        t = findViewById(id.id_sets_a);
        t.setText(s);
        c_sets_h = Integer.parseInt(s);

        s = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_SETS_B);
        t = findViewById(id.id_sets_b);
        t.setText(s);
        c_sets_v = Integer.parseInt(s);

// *********** Server

        server = myDb.readSystemStr(DBAdapter.KEY_SYSTEM_SERVER);

        if (server == null) server = getString(R.string.z);

        if (server.equals(getString(R.string.z))) {
            start_server_flashTimer();
        }

        switch (server) {
            case "H":
                t = findViewById(id.id_server_mark_a);
                t.setVisibility(View.VISIBLE);

                t = findViewById(id.id_server_mark_b);
                t.setVisibility(View.INVISIBLE);

                break;

            case "V":
                t = findViewById(id.id_server_mark_a);
                t.setVisibility(View.INVISIBLE);

                t = findViewById(id.id_server_mark_b);
                t.setVisibility(View.VISIBLE);

                break;

            default:
                t = findViewById(id.id_server_mark_a);
                t.setVisibility(View.INVISIBLE);

                t = findViewById(id.id_server_mark_b);
                t.setVisibility(View.INVISIBLE);
        }

// *********** Rules

        TextView r1 = findViewById(id.id_rules_1);
        TextView r2 = findViewById(id.id_rules_2);
        TextView r3 = findViewById(id.id_rules_3);
        TextView r4 = findViewById(id.id_rules_4);

        // No of Sets

        String setNo = "   ";

        if (no_Sets_int == 1) {
            setNo = getString(R.string.set1);
        }

        if (no_Sets_int == 3) {
            setNo = getString(R.string.set3);
        }

        if (no_Sets_int == 5) {
            setNo = getString(R.string.set5);
        }

        r1.setText(setNo);

        // Type of Set

        r2.setVisibility(View.VISIBLE);

        if (!current_Set_Type_bool) {
            r2.setText(R.string.advsets);
        } else {
            r2.setText(R.string.tbsets);
        }

        // Last Set

        r3.setVisibility(View.VISIBLE);

        if (current_Set_Type_bool) {
            if (!last_Set_Type_bool) {
                r3.setText(R.string.lastsetadv);
            } else {
                r3.setText(R.string.lastsettb);
            }
        } else {
            r3.setVisibility(View.INVISIBLE);
        }

        // No Advantage

        if (no_Adv_bool) {
            r2.setVisibility(View.INVISIBLE);
            r3.setVisibility(View.INVISIBLE);
            r4.setVisibility(View.VISIBLE);

            r4.setText(R.string.no_adv);
        }

        // Short Sets

        if (short_Sets_bool) {
            r2.setVisibility(View.INVISIBLE);
            r3.setVisibility(View.INVISIBLE);
            r4.setVisibility(View.VISIBLE);

            if (ss_4_bool) r4.setText(R.string.ss_4);
            if (ss_3_bool) r4.setText(R.string.ss_3);
        }

        // FAST4

//        if (fast4_bool) {
//            r2.setVisibility(View.INVISIBLE);
//            r3.setVisibility(View.INVISIBLE);
//            r4.setVisibility(View.VISIBLE);
//
//            r4.setText(R.string.fast4_format);
//        }

        // *********** Match Tie Break

        if (match_tb_game_bool) {
            r4.setVisibility(View.VISIBLE);

            if (mtb_7_bool) {
                r4.setText(R.string.tb7);
            }

            if (mtb_10_bool) {
                r4.setText(R.string.tb10);
            }
        }

//        if ((!no_Adv_bool) && (!short_Sets_bool) && (!fast4_bool) && (!match_tb_game_bool)) {
        if ((!no_Adv_bool) && (!short_Sets_bool) && (!match_tb_game_bool)) {
            r4.setVisibility(View.INVISIBLE);
            r2.setVisibility(View.VISIBLE);
        }

//        L.d("fast4_bool  " + fast4_bool);

//        L.d("no_Adv_bool  " + no_Adv_bool);
//        L.d("short_Sets_bool  " + short_Sets_bool);
//        L.d("ss_4_bool  " + ss_4_bool);
//        L.d("ss_3_bool  " + ss_3_bool);
//        L.d("match_tb_game_bool  " + match_tb_game_bool);
//        L.d("mtb_7_bool  " + mtb_7_bool);
//        L.d("mtb_10_bool  " + mtb_10_bool);
//
//        L.d("**************************************");
//
//        L.d("Advantage = false or 0   Tb = true or 1");
//        L.d("  ");
//        L.d("no_Sets_int   " + no_Sets_int);
//        L.d("this_Is_Last_Set_bool   " + this_Is_Last_Set_bool);
//        L.d("current_Game_Type_bool   " + current_Game_Type_bool);
//        L.d("last_Set_Type_bool   " + last_Set_Type_bool);
//        L.d("min_Games_To_Win_Set_int   " + min_Games_To_Win_Set_int);
//        L.d("next_To_Last_Set_int   " + next_To_Last_Set_int);
//        L.d("sets_To_Win_Per_Player_int   " + sets_To_Win_Per_Player_int);
//        L.d("current_Set_Type_bool   " + current_Set_Type_bool);
//        L.d("last_Set_is_Adv_bool   " + last_Set_is_Adv_bool);

//        L.d("match_Complete_bool   " + match_Complete_bool);
//        L.d("min_Points_To_Win_Tb_Game_int   " + min_Points_To_Win_Tb_Game_int);
//        L.d("last_Set_is_Tb_Game_bool   " + last_Set_is_Tb_Game_bool);
//
//        L.d("-----------------------------------------------------");
//        L.d("-----------------------------------------------------");

        myDb.K_Log("no_Sets_int   " + no_Sets_int);
        myDb.K_Log("this_Is_Last_Set_bool   " + this_Is_Last_Set_bool);
        myDb.K_Log("current_Game_Type_bool   " + current_Game_Type_bool);
        myDb.K_Log("last_Set_Type_bool   " + last_Set_Type_bool);
        myDb.K_Log("min_Games_To_Win_Set_int   " + min_Games_To_Win_Set_int);
        myDb.K_Log("next_To_Last_Set_int   " + next_To_Last_Set_int);
        myDb.K_Log("sets_To_Win_Per_Player_int   " + sets_To_Win_Per_Player_int);
        myDb.K_Log("current_Set_Type_bool   " + current_Set_Type_bool);
        myDb.K_Log("last_Set_is_Adv_bool   " + last_Set_is_Adv_bool);
        myDb.K_Log("match_Complete_bool   " + match_Complete_bool);
        myDb.K_Log("min_Points_To_Win_Tb_Game_int   " + min_Points_To_Win_Tb_Game_int);
        myDb.K_Log("last_Set_is_Tb_Game_bool   " + last_Set_is_Tb_Game_bool);

    }

// ******************************************************************************

    public String convert_Points(int points) {
        String ss;

        if (!current_Game_Type_bool) {
            switch (points) {
                case 0:
                    ss = getString(R.string.oo);
                    break;
                case 1:
                    ss = getString(R.string.onefive);
                    break;
                case 2:
                    ss = getString(R.string.thirty);
                    break;
                case 3:
                    ss = getString(R.string.forty);
                    break;
                default:
                    ss = getString(R.string.ad);
            }
        } else {
            ss = Integer.toString(points);
        }

        return ss;
    }

// ******************************************************************************

    public String convert_Points_Transmit(int points) {
        String ss;

        if (!current_Game_Type_bool) {
            switch (points) {
                case 0:
                    ss = getString(R.string.oo);
                    break;
                case 1:
                    ss = getString(R.string.onefive);
                    break;
                case 2:
                    ss = getString(R.string.thirty);
                    break;
                case 3:
                    ss = getString(R.string.forty);
                    break;
                default:
                    ss = getString(R.string.ad);
            }
        } else {
            ss = points + getString(R.string.b);
            if (ss.length() > 2) {
                ss = ss.substring(0, 2);

                ss = ss.charAt(1) + ss.substring(0, 1);
            }
        }
        return ss;
    }

// ******************************************************************************
// Buttons in Action Bar
// ******************************************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // ******************************************************************************

    public void onClick_Reset(View view) {

        resetAlertDialog();

//        onPause();

        myDb.K_Log(getString(R.string.reset));
    }

// ******************************************************************************

    public void onClick_Rules(View view) {

        Intent intent = new Intent(MainActivity.this, PDFActivity.class);
        startActivity(intent);

        myDb.K_Log(getString(R.string.open_rules));
    }

// ******************************************************************************

    public void onClick_Setup(View view) {

        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(intent);

//        onPause();

        myDb.K_Log(getString(R.string.open_setup));
    }

// ******************************************************************************

    public void onClick_Results(View view) {

        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        startActivity(intent);

        myDb.K_Log(getString(R.string.open_results));
    }

// ******************************************************************************

    public void onClick_Exit(View view) {

        quitAlertDialog();

        myDb.K_Log(getString(R.string.quit));
    }

// ******************************************************************************
// Other Buttons
// ******************************************************************************

    public void onClick_name_a(View view) {

        Intent intent = new Intent(MainActivity.this, Name_A_Activity.class);
        startActivity(intent);

        myDb.K_Log(getString(R.string.open_name_a));
    }

// ******************************************************************************

    public void onClick_name_b(View view) {

        Intent intent = new Intent(this, Name_B_Activity.class);
        startActivity(intent);

        myDb.K_Log(getString(R.string.open_name_b));
    }

// ******************************************************************************

    public void onClick_server_a(View view) {

        server = getString(R.string.h);
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SERVER, server);

        t = findViewById(id.id_server_mark_a);
        t.setVisibility(View.VISIBLE);
        t = findViewById(id.id_server_mark_b);
        t.setVisibility(View.INVISIBLE);

        stop_server_flashTimer();
    }

// ******************************************************************************

    public void onClick_server_b(View view) {

        server = getString(R.string.v);
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SERVER, server);

        t = findViewById(id.id_server_mark_a);
        t.setVisibility(View.INVISIBLE);
        t = findViewById(id.id_server_mark_b);
        t.setVisibility(View.VISIBLE);

        stop_server_flashTimer();
    }

// ******************************************************************************

    public void flip_server() {

        switch (server) {
            case "H":
                server = getString(R.string.v);

                myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SERVER, server);

                t = findViewById(id.id_server_mark_a);
                t.setVisibility(View.INVISIBLE);

                t = findViewById(id.id_server_mark_b);
                t.setVisibility(View.VISIBLE);
                break;

            case "V":

                server = getString(R.string.h);

                myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SERVER, server);

                t = findViewById(id.id_server_mark_a);
                t.setVisibility(View.VISIBLE);

                t = findViewById(id.id_server_mark_b);
                t.setVisibility(View.INVISIBLE);

                break;

            default:
        }
    }

// ******************************************************************************

    public void setup_Game_Variables() {

        int no_sets = myDb.readSystem(DBAdapter.KEY_SYSTEM_NO_SETS);
        current_Set_Type_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_SET_TYPE) == 1);
        last_Set_Type_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_LAST_SET) == 1);

        no_Adv_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_NO_ADV) == 1);

//        fast4_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_FAST4) == 1);

        short_Sets_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_SHORT_SETS) == 1);
        ss_4_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_SS_4) == 1);
        ss_3_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_SS_3) == 1);

        match_tb_game_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_MATCH_TB) == 1);
        mtb_7_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_MTB_7) == 1);
        mtb_10_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_MTB_10) == 1);

        this_Is_Last_Set_bool = false;

        min_Games_To_Win_Set_int = 6;

        if (no_sets == 1) {
            no_Sets_int = 1;
            sets_To_Win_Per_Player_int = 1;
            next_To_Last_Set_int = 0;
            this_Is_Last_Set_bool = true;

            if (match_tb_game_bool) {
                last_Set_is_Tb_Game_bool = true;
                if (mtb_7_bool) {
                    min_Points_To_Win_Tb_Game_int = 7;
                } else {
                    min_Points_To_Win_Tb_Game_int = 10;
                }
            }
        }

        if (no_sets == 3) {
            no_Sets_int = 3;
            sets_To_Win_Per_Player_int = 2;
            next_To_Last_Set_int = 1;
        }

        if (no_sets == 5) {
            no_Sets_int = 5;
            sets_To_Win_Per_Player_int = 3;
            next_To_Last_Set_int = 2;
        }

        if ((c_sets_h == next_To_Last_Set_int) || (c_sets_v == next_To_Last_Set_int)) {       // Last Set
            this_Is_Last_Set_bool = true;

            if (match_tb_game_bool) {
                last_Set_is_Tb_Game_bool = true;
                if (mtb_7_bool) {
                    min_Points_To_Win_Tb_Game_int = 7;
                } else {
                    min_Points_To_Win_Tb_Game_int = 10;
                }
            }
        }

        if (!current_Set_Type_bool) {
            last_Set_is_Adv_bool = (myDb.readSystem(DBAdapter.KEY_SYSTEM_LAST_SET) == 0);
        }

        if (short_Sets_bool) {
            if (ss_4_bool) {
                min_Games_To_Win_Set_int = 4;
            } else {
                min_Games_To_Win_Set_int = 3;
            }
        }

        if (match_tb_game_bool) {
            last_Set_is_Tb_Game_bool = true;

            if (mtb_7_bool) {
                min_Points_To_Win_Tb_Game_int = 7;
            }

            if (mtb_10_bool) {
                min_Points_To_Win_Tb_Game_int = 10;
            }
        } else {
            last_Set_is_Tb_Game_bool = false;
            min_Points_To_Win_Tb_Game_int = 7;
        }

    }

// ******************************************************************************

    public void pointsPlus_a() {
        if (!match_Complete_bool) {
            if (server()) {
                buttons_Off(getString(R.string.h));
                history();

                c_Audio_Temp_str = getString(R.string.b);

                c_points_h++;
                points();
            }
        }
    }

// ******************************************************************************

    public void pointsPlus_b() {
        if (!match_Complete_bool) {
            if (server()) {
                buttons_Off(getString(R.string.v));
                history();

                c_Audio_Temp_str = getString(R.string.b);

                c_points_v++;
                points();
            }
        }
    }

// ******************************************************************************

    public void onClick_pointsPlus_a(View view) {
        if (!match_Complete_bool) {
            if (server()) {
                buttons_Off(getString(R.string.h));
                history();

                c_points_h++;
                points();
            }
        }
    }

// ******************************************************************************

    public void onClick_pointsNeg_a(View view) {
        if (!match_Complete_bool) {
            if (server()) {
                t = findViewById(id.id_b_pointsNeg_a);
                t.setVisibility(View.INVISIBLE);

                restore();
                points();
            }
        }
    }

// ******************************************************************************

    public void onClick_pointsPlus_b(View view) {
        if (!match_Complete_bool) {
            if (server()) {
                buttons_Off(getString(R.string.v));
                history();

                c_points_v++;
                points();
            }
        }
    }

// ******************************************************************************

    public void onClick_pointsNeg_b(View view) {
        if (!match_Complete_bool) {
            if (server()) {

                t = findViewById(id.id_b_pointsNeg_b);
                t.setVisibility(View.INVISIBLE);

                restore();
                points();
            }
        }
    }

// ******************************************************************************

    private void points() {

        if (c_points_h < 0) c_points_h = 0;
        if (c_points_v < 0) c_points_v = 0;

        if (!current_Game_Type_bool) {
            points_Adv();
        } else {
            points_Tb();
        }

        c_Audio_str = c_Audio_Temp_str;
        c_Audio_Temp_str = getString(R.string.x);

//        if (match_Complete_bool) {
//            c_points_h = 0;
//            c_points_v = 0;
//
//            c_games_h = 0;
//            c_games_v = 0;
//
//            c_sets_h = 0;
//            c_sets_v = 0;
//        }

        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_POINTS_A, String.valueOf(c_points_h));
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_POINTS_B, String.valueOf(c_points_v));

        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_GAMES_A, String.valueOf(c_games_h));
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_GAMES_B, String.valueOf(c_games_v));

        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SETS_A, String.valueOf(c_sets_h));
        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SETS_B, String.valueOf(c_sets_v));

        onResume();
    }

// ******************************************************************************

    private void points_Adv() {
        if (no_Adv_bool) points_No_Adv();

        if ((!no_Adv_bool)) {                          // && (!match_tb_game_bool)

            // Normal Game

            int[][] res_arr = {{0, 0, 0, 0, 1, 5}, {0, 0, 0, 0, 1, 5}, {0, 0, 0, 0, 1, 5}, {0, 0, 0, 2, 3, 1}, {1, 1, 1, 6, 4, 5}, {5, 5, 5, 1, 5, 5}};

            // 0 - Add to score
            // 1 - Win game
            // 2 - Deuce
            // 3 - Advantage B
            // 4 - Back to deuce
            // 5 - No action
            // 6 - Advantage A

            int result = res_arr[c_points_h][c_points_v];

            switch (result) {
                case 0:
                case 5:
                    break;
                case 1:
                    win_Adv_Game();
                    break;
                case 2:
                    deuce();
                    break;
                case 3:
                    advantage_B();
                    break;
                case 4:
                    back_to_Deuce();
                    break;
                case 6:
                    advantage_A();
                    break;
            }
        }
    }

// ******************************************************************************

    private void win_Adv_Game() {

        if (c_points_h > c_points_v) {
            c_games_h++;
        } else {
            c_games_v++;
        }
        c_points_h = 0;
        c_points_v = 0;

        Intent intent = new Intent(MainActivity.this, Splash_GameActivity.class);
        startActivity(intent);

//        L.d(getString(R.string.game_adv));

        c_Audio_Temp_str = getString(R.string.g);

        flip_server();

        // Advantage Set

        if ((c_games_h < min_Games_To_Win_Set_int) && (c_games_v < min_Games_To_Win_Set_int)) {
            return;
        }

        if ((c_games_h >= min_Games_To_Win_Set_int) && (c_games_v <= (c_games_h - 2))) {
            c_sets_h++;

            results();

            c_games_h = 0;
            c_games_v = 0;

            if (!match()) {
                intent = new Intent(MainActivity.this, Splash_SetActivity.class);
                startActivity(intent);

//                L.d(getString(R.string.sets_adv));

                c_Audio_Temp_str = getString(R.string.s);
            }
        }

        if ((c_games_v >= min_Games_To_Win_Set_int) && (c_games_h <= (c_games_v - 2))) {
            c_sets_v++;

            results();

            c_games_h = 0;
            c_games_v = 0;

            if (!match()) {
                intent = new Intent(MainActivity.this, Splash_SetActivity.class);
                startActivity(intent);

//                L.d(getString(R.string.sets_adv));

                c_Audio_Temp_str = getString(R.string.s);
            }
        }

        next_Game();
    }

// ******************************************************************************

    private void points_No_Adv() {

        // No Advantage Game

        int[][] res_arr = {{0, 0, 0, 0, 1, 5}, {0, 0, 0, 0, 1, 5}, {0, 0, 0, 0, 1, 5}, {0, 0, 0, 2, 3, 1}, {1, 1, 1, 6, 4, 5}, {5, 5, 5, 1, 5, 5}};

        // 0 - Add to score
        // 1 - Win game
        // 2 - Deuce
        // 3 - Win game
        // 4 - Back to deuce
        // 5 - No action
        // 6 - Win game

        int result = res_arr[c_points_h][c_points_v];

        switch (result) {
            case 0:
            case 5:
                break;
            case 1:
                win_Adv_Game();
                break;
            case 2:
                deuce();
                break;
            case 3:
                win_Adv_Game();
                break;
            case 4:
                back_to_Deuce();
                break;
            case 6:
                win_Adv_Game();
                break;
        }
    }

// ******************************************************************************

    private void points_Tb() {

        // Tie Break Game

        if (((c_points_h >= min_Points_To_Win_Tb_Game_int) && (c_points_v <= (c_points_h - 2))) || ((c_points_v >= min_Points_To_Win_Tb_Game_int) && (c_points_h <= (c_points_v - 2)))) {

            if (c_points_h > c_points_v) {
                c_games_h++;
            } else {
                c_games_v++;
            }

            if ((c_games_h > c_games_v)) {
                c_sets_h++;
            } else {
                c_sets_v++;
            }

            results();

            if (this_Is_Last_Set_bool) {
//                    L.d(getString(R.string.match));

                match();

//                    c_points_h = 0;
//                    c_points_v = 0;
//
//                    c_games_h = 0;
//                    c_games_v = 0;
//
//                    c_sets_h = 0;
//                    c_sets_v = 0;

                return;

            } else {
                Intent intent = new Intent(MainActivity.this, Splash_SetActivity.class);
                startActivity(intent);

//                    L.d(getString(R.string.sets_tb));

                c_Audio_Temp_str = getString(R.string.s);
            }

//                if ((c_points_h >= min_Points_To_Win_Tb_Game_int) & (c_points_v <= (c_points_h - 2))) {
//                    L.d(getString(R.string.game_tb_home));
//                } else {
//                    L.d(getString(R.string.game_tb_visitor));
//                }

            c_points_h = 0;
            c_points_v = 0;

            c_games_h = 0;
            c_games_v = 0;

            flipFlag_bool = false;
            flipCounter_int = 2;

            next_Game();

            c_points_h = 0;
            c_points_v = 0;

            c_games_h = 0;
            c_games_v = 0;

        } else {
            if (!flipFlag_bool) {
                flipFlag_bool = true;

                flip_server();
            } else {
                flipCounter_int--;
                if (flipCounter_int == 0) {
                    flipCounter_int = 2;

                    flip_server();

                    c_Audio_Temp_str = getString(R.string.c);
                }
            }
        }
    }
//    }

// ******************************************************************************

    private boolean match() {
        if (c_sets_h == sets_To_Win_Per_Player_int || c_sets_v == sets_To_Win_Per_Player_int) {

//            L.d(getString(R.string.match));

            c_Audio_Temp_str = getString(R.string.m);

            all_Buttons_Off();

            match_Complete_bool = true;

            t = findViewById(id.id_no);
            t.setText("   ");

            t = findViewById(id.id_notice);
            t.setText(R.string.reset1);

            start_reset_flashTimer();

            myDb.updateSystem(DBAdapter.KEY_SYSTEM_RESET, 1);

            t = findViewById(id.id_game);
            t.setText("              ");

            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
            startActivity(intent);

            intent = new Intent(MainActivity.this, Splash_MatchActivity.class);
            startActivity(intent);

            return true;
        }
        return false;
    }

/// ******************************************************************************

    private void next_Game() {
        if (current_Game_Type_bool) {
            current_Game_Type_bool = false;
            return;
        }

        if (current_Set_Type_bool) {               // Tb Set
            if ((c_sets_h == next_To_Last_Set_int) || (c_sets_v == next_To_Last_Set_int)) {       // Last Set
                this_Is_Last_Set_bool = true;

                if (match_tb_game_bool) {
                    last_Set_is_Tb_Game_bool = true;
                    if (mtb_7_bool) {
                        min_Points_To_Win_Tb_Game_int = 7;
                    } else {
                        min_Points_To_Win_Tb_Game_int = 10;
                    }
                }

                if ((c_games_h == min_Games_To_Win_Set_int) && (c_games_v == min_Games_To_Win_Set_int)) {   // Set "Deuce"
                    current_Game_Type_bool = true;
                    return;
                }
            }
        }

        if (short_Sets_bool) {
            if ((c_games_h == min_Games_To_Win_Set_int) && (c_games_v == min_Games_To_Win_Set_int)) {   // Set "Deuce"
                if ((c_sets_h == next_To_Last_Set_int) || (c_sets_v == next_To_Last_Set_int)) {       // Last Set
                    this_Is_Last_Set_bool = true;

                    if (!last_Set_is_Adv_bool) {
                        current_Game_Type_bool = true;
                    } else {
                        current_Game_Type_bool = false;
                    }
                } else {
                    current_Game_Type_bool = true;
                }
            }
        }

        if (match_tb_game_bool) {
            this_Is_Last_Set_bool = true;
            last_Set_is_Tb_Game_bool = true;
            current_Game_Type_bool = true;
        }

        game_Notice();
    }

    // ******************************************************************************

    private void game_Notice() {
        if (!match_Complete_bool) {

// "No"
            t = findViewById(id.id_no);

            if (myDb.readSystem(DBAdapter.KEY_SYSTEM_NO_ADV) == 1) {
                t.setText(R.string.no);
            } else {
                t.setText("   ");
            }

// "Advantage or Tie Break"
            t = findViewById(id.id_notice);

            if (!current_Game_Type_bool) {
                t.setText(R.string.advantage);
            } else {
                t.setText(R.string.tb);
            }

// "Game"
            t = findViewById(id.id_game);
            t.setText(R.string.game);
        }
    }

// ******************************************************************************

    private void results() {

        int tot_sets = c_sets_h + c_sets_v;

        switch (tot_sets) {
            case 1:
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_1_H, c_games_h);
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_1_V, c_games_v);

                break;

            case 2:
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_2_H, c_games_h);
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_2_V, c_games_v);

                break;

            case 3:
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_3_H, c_games_h);
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_3_V, c_games_v);

                break;

            case 4:
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_4_H, c_games_h);
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_4_V, c_games_v);

                break;

            case 5:
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_5_H, c_games_h);
                myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_5_V, c_games_v);

                break;

            default:
        }
    }

// ******************************************************************************

    private void advantage_A() {

        Intent intent = new Intent(MainActivity.this, Splash_AdvActivity.class);
        startActivity(intent);

//        L.d(getString(R.string.adv));

        if (server.equals(getString(R.string.h))) {
            c_Audio_Temp_str = getString(R.string.A);
        } else {
            c_Audio_Temp_str = getString(R.string.a);
        }
    }

// ******************************************************************************

    private void advantage_B() {

        Intent intent = new Intent(MainActivity.this, Splash_AdvActivity.class);
        startActivity(intent);

//        L.d(getString(R.string.adv));

        if (server.equals(getString(R.string.v))) {
            c_Audio_Temp_str = getString(R.string.A);
        } else {
            c_Audio_Temp_str = getString(R.string.a);
        }
    }

// ******************************************************************************

    private void deuce() {

        Intent intent = new Intent(MainActivity.this, Splash_DeuceActivity.class);
        startActivity(intent);

//        L.d("Deuce");

        c_Audio_Temp_str = "D";
    }

// ******************************************************************************

    private void back_to_Deuce() {

        Intent intent = new Intent(MainActivity.this, Splash_DeuceActivity.class);
        startActivity(intent);

//        L.d("Back to Deuce");

        c_Audio_Temp_str = "D";

        c_points_h--;
        c_points_v--;
    }

    // ******************************************************************************

    public boolean server() {
        if ("Z".equals(server)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Cannot score " +
                    "until 1st. Server is set.", Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }
        return true;
    }

// ******************************************************************************

    public void buttons_Off(String player) {

        strPlayerButton = player;

        start_ButtonTimer();

        t = findViewById(id.id_b_pointsPlus_a);
        t.setVisibility(View.INVISIBLE);

        t = findViewById(id.id_b_pointsPlus_b);
        t.setVisibility(View.INVISIBLE);

        t = findViewById(id.id_b_pointsNeg_a);
        t.setVisibility(View.INVISIBLE);

        t = findViewById(id.id_b_pointsNeg_b);
        t.setVisibility(View.INVISIBLE);
    }

// ******************************************************************************

    public void all_Buttons_Off() {

        stop_ButtonTimer();

        t = findViewById(id.id_b_pointsPlus_a);
        t.setVisibility(View.INVISIBLE);

        t = findViewById(id.id_b_pointsPlus_b);
        t.setVisibility(View.INVISIBLE);

        t = findViewById(id.id_b_pointsNeg_a);
        t.setVisibility(View.INVISIBLE);

        t = findViewById(id.id_b_pointsNeg_b);
        t.setVisibility(View.INVISIBLE);
    }

// ******************************************************************************

    public void buttons_On() {

        t = findViewById(id.id_b_pointsPlus_a);
        t.setVisibility(View.VISIBLE);

        t = findViewById(id.id_b_pointsPlus_b);
        t.setVisibility(View.VISIBLE);

        switch (strPlayerButton) {
            case "H":
                t = findViewById(id.id_b_pointsNeg_a);
                t.setVisibility(View.VISIBLE);
                break;
            case "V":
                t = findViewById(id.id_b_pointsNeg_b);
                t.setVisibility(View.VISIBLE);
        }
    }

// ******************************************************************************

    private void history() {

        h_points_h = c_points_h;
        h_points_v = c_points_v;
        h_games_h = c_games_h;
        h_games_v = c_games_v;
        h_sets_h = c_sets_h;
        h_sets_v = c_sets_v;

        h_server = server;
    }

// ******************************************************************************

    private void restore() {
        c_points_h = h_points_h;
        c_points_v = h_points_v;
        c_games_h = h_games_h;
        c_games_v = h_games_v;
        c_sets_h = h_sets_h;
        c_sets_v = h_sets_v;

        server = h_server;

        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SERVER, server);
    }

// ******************************************************************************

    private void resetAlertDialog() {

        Context context = MainActivity.this;
        String message = getString(R.string.really_reset);
        String button1String = getString(R.string.reset);
        String button2String = getString(R.string.cancel);
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {

                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_NAME_A, getString(R.string.home));
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_NAME_B, getString(R.string.visitors));

                        // Reset scores
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_POINTS_A, "0");
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_POINTS_B, "0");
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_GAMES_A, "0");
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_GAMES_B, "0");
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SETS_A, "0");
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SETS_B, "0");

                        // Reset Results
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_1_H, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_1_V, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_2_H, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_2_V, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_3_H, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_3_V, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_4_H, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_4_V, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_5_H, 0);
                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_SET_5_V, 0);

                        t = findViewById(id.id_b_pointsNeg_a);
                        t.setVisibility(View.INVISIBLE);
                        t = findViewById(id.id_b_pointsNeg_b);
                        t.setVisibility(View.INVISIBLE);

                        // Reset Server
                        t = findViewById(id.id_server_mark_a);
                        t.setVisibility(View.INVISIBLE);
                        t = findViewById(id.id_server_mark_b);
                        t.setVisibility(View.INVISIBLE);

                        server = "Z";
                        myDb.updateSystemStr(DBAdapter.KEY_SYSTEM_SERVER, server);

                        strPlayerButton = "Z";
                        buttons_On();

                        t = findViewById(id.id_set_server);
                        t.setVisibility(View.VISIBLE);

                        flipFlag_bool = false;
                        flipCounter_int = 2;

                        stop_reset_flashTimer();

                        myDb.updateSystem(DBAdapter.KEY_SYSTEM_RESET, 0);
                        reset_Flag_int = 0;

                        t = findViewById(id.id_notice);
                        t.setVisibility(View.VISIBLE);

                        current_Game_Type_bool = false;
                        match_Complete_bool = false;
                        this_Is_Last_Set_bool = false;


                        changeRulesDialog();







                        myDb.K_Log("Reset Yes");

                        onResume();
                    }
                }
        );

        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // do nothing
                        myDb.K_Log("Reset No");
                    }
                }
        );
        ad.show();
    }

// ******************************************************************************

    private void quitAlertDialog() {

        Context context = MainActivity.this;
        String message = "You REALLY want to Quit?";
        String button1String = "Quit";
        String button2String = "Cancel";
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                myDb.K_Log("Quit App");

                finish();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                // do nothing
                myDb.K_Log("Quit Not");
            }
        });
        ad.show();
    }

// ******************************************************************************

    private void changeRulesDialog() {

        Context context = MainActivity.this;
        String message = "Do you want to Change the Rules for the next Match?";
        String button1String = "Change";
        String button2String = "Cancel";
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                myDb.K_Log("Change Rules");

                Intent s_intent = new Intent(MainActivity.this, RulesActivity.class);
                startActivity(s_intent);
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                // do nothing
                myDb.K_Log("Don't Change Rules");
            }
        });
        ad.show();
    }

// ******************************************************************************
// Timer to delay running onResume on start up
// ******************************************************************************

    public void start_ResumeTimer() {

        if (!res_timerActive) {
            res_timerActive = true;
            res_counter = new resumeTimer();
            res_counter.start();
        }
    }

// ******************************************************************************

    private class resumeTimer extends CountDownTimer {

        private resumeTimer() {

            super(500, 500);
        }

        // ******************************************************************************

        @Override
        public void onFinish() {

            onResume();
            res_counter = null;
            res_timerActive = false;
        }

        // ******************************************************************************

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

// ******************************************************************************
// Timer to show scoring buttons after a delay
// ******************************************************************************

    public void start_ButtonTimer() {

        button_counter = new buttonTimer();
        button_counter.start();
    }

// ******************************************************************************

    public void stop_ButtonTimer() {

        if (button_counter != null) {
            button_counter.cancel();
        }
    }

// ******************************************************************************

    private class buttonTimer extends CountDownTimer {

        buttonTimer() {

            super(500, 500);
        }

        // ******************************************************************************

        @Override
        public void onFinish() {

            buttons_On();
            button_counter = null;
        }

        // ******************************************************************************

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

// ******************************************************************************
// Timer for repetitive battery level tests
// ******************************************************************************

    public void start_batteryTimer() {

        if (battery_counter != null) {
            battery_counter.cancel();
        }

        battery_counter = new batteryTimer();
        battery_counter.start();
    }

// ******************************************************************************

    public void stop_batteryTimer() {

        if (battery_counter != null) {
            battery_counter.cancel();
        }
    }

// ******************************************************************************

    private class batteryTimer extends CountDownTimer {

        batteryTimer() {

            super(30000, 30000);
        }

        // ******************************************************************************

        @Override
        public void onFinish() {

            battery_counter = null;
            start_batteryTimer();
            /*
              Computes the battery level by registering a receiver to the intent triggered
              by a battery status/level change.
             */
            BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    context.unregisterReceiver(this);
                    int raw_Level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int level;
                    if (raw_Level >= 0 && scale > 0) {
                        level = (raw_Level * 100) / scale;
                        checkBattery(level);
                    }
                }
            };

            IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        }

        // ******************************************************************************

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

// ******************************************************************************

    private void checkBattery(int level) {

        String mess = "";
        if (level <= batteryTestLevel) {
            batteryTestLevel = batteryTestLevel - getInc();
            switch (batteryMessageNo) {
                case 5:
                    mess = "The Battery is getting low.";
                    break;
                case 4:
                    mess = "The Battery is getting lower.";
                    break;
                case 3:
                    mess = "The Battery is getting lower and lower.";
                    break;
                case 2:
                    mess = "The Battery is getting very low.";
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.screenBrightness = 20 / 100.0f;
                    getWindow().setAttributes(lp);
                    break;
                case 1:
                    mess = "Scorer will SHUT DOWN in 30 seconds";
                    batteryTestLevel = batteryTestLevel + getInc();
                    break;
                case 0:
                    android.os.Process.killProcess(Process.myPid());
                    System.exit(0);
                    finish();
                    break;
            }
            myDb.K_Log("Battery Message - " + mess);

            batteryAlertDialog(mess, Integer.toString(batteryMessageNo));
            batteryMessageNo--;
        }
    }

// ******************************************************************************

    private void batteryAlertDialog(String message, String no) {

        if (ad != null) {
            ad.cancel();
            Alarm.stopAlarm();
        }
        Alarm.soundAlarm(context);

        myDb.K_Log(message + "  " + no);

        Context context = MainActivity.this;
        String button1String = "Acknowledge";
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setMessage(message);
        adb.setTitle(no);
        adb.setPositiveButton(button1String, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                Alarm.stopAlarm();
            }
        });
        ad = adb.create();
        ad.show();
    }


// ***************************WiFiWiFiWiFiWiFiWiFiWiFi*********************************************
// WiFi
// ***************************WiFiWiFiWiFiWiFiWiFiWiFi*********************************************

    private boolean wifiCheckWiFiEnabled() {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        if (wifi != null) {
            if (!wifi.isWifiEnabled()) {
                t = findViewById(id.id_wifi_message);
                t.setTextColor(Color.RED);
                t.setText(R.string.wifi_disabled);

                t = findViewById(id.id_wifi_connected);
                t.setTextColor(Color.RED);
                t.setText(R.string.wifi_not_connected);

                wifi.setWifiEnabled(true);
                wifi_enabled = false;

                return false;
            } else {
                t = findViewById(id.id_wifi_message);
                t.setTextColor(Color.parseColor("#336633"));
                t.setText(R.string.wifi_enabled);
                wifi_enabled = true;

                return true;
            }
        }
        return false;
    }
    // ******************************************************************************

    private boolean wifiCheckWiFiConnected() {
        final WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }

        WifiInfo wifiInfo = null;
        if (wifi != null) {

            //If requested permission isn't Granted yet
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Request permission from user
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
            } else {                            //Permission already granted

                wifiInfo = wifi.getConnectionInfo();
            }
        }

        if (networkInfo != null) {
            if (!(networkInfo.isConnected() && Objects.requireNonNull(wifiInfo).getSSID().replace("\"", "").equals(wifi_ssid))) {
                t = findViewById(id.id_wifi_connected);
                t.setTextColor(Color.RED);
                t.setText(R.string.wifi_not_connected);
                wifi_stopSendThread = true;
                wifi_stopReceiveThread = true;

                wifi_sendThread = null;
                wifi_receiveThread = null;

                stop_flashTimer();

                wifi_connected = false;

                wifi.setWifiEnabled(false);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wifi.setWifiEnabled(true);

                return false;
            } else {
                t = findViewById(id.id_wifi_connected);
                t.setTextColor(Color.parseColor("#336633"));
                t.setText(R.string.wifi_connected);
                wifi_stopSendThread = false;
                wifi_stopReceiveThread = false;

                if (wifi_sendThread == null) {
                    wifi_sendThread = new wifiSendThread();
                    new Thread(wifi_sendThread).start();
                }
                if (wifi_receiveThread == null) {
                    wifi_receiveThread = new wifiReceiveThread();
                    new Thread(wifi_receiveThread).start();
                }

                start_flashTimer();

                wifi_connected = true;

                return true;
            }
        }
        return false;
    }

    // ******************************************************************************

    /**
     * Start to connect to a specific wifi network
     */
    private void wifiConnectToSpecificNetwork() {
        if (!wifi_connect_in_progress) {

            final WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

            wifiScanReceiver scanReceiver = new wifiScanReceiver();
            registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            if (wifi != null) {
                wifi.startScan();
            }

            wifi_connect_in_progress = true;
        }
    }

// ******************************************************************************

    /**
     * Broadcast receiver for wifi scanning related events
     */
    private class wifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            List<ScanResult> scanResultList = null;
            if (wifi != null) {
                scanResultList = wifi.getScanResults();
            }
            boolean found = false;
            if (scanResultList != null) {
                for (ScanResult scanResult : scanResultList) {
                    if (scanResult.SSID.equals(wifi_ssid)) {
                        found = true;
                        break;                  // found don't need continue
                    }
                }
            }
            if (!found) {
                unregisterReceiver(wifiScanReceiver.this);
                wifi_connect_in_progress = false;

            } else {
                // configure based on security
                final WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + wifi_ssid + "\"";

                String SS = "" + getSSID();

                String wifiPass = SS + "1" + SS + "2" + SS + "3" + SS + "4";

                conf.preSharedKey = "\"" + wifiPass + "\"";

                int netId = wifi.addNetwork(conf);
                wifi.disconnect();
                wifi.enableNetwork(netId, true);
                wifi.reconnect();

                unregisterReceiver(this);
            }
        }
    }

// ******************************************************************************

    private void wifi_enable_off() {
        t = findViewById(id.id_wifi_message);
        t.setTextColor(Color.RED);
        t.setText(R.string.wifi_disabled);

        t = findViewById(id.id_wifi_connected);
        t.setTextColor(Color.RED);
        t.setText(R.string.wifi_not_connected);
    }

// ******************************************************************************
//      Send Thread
// ******************************************************************************

    private class wifiSendThread implements Runnable {
        wifiSendThread() {
//        puts the thread in the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        public void run() {

            try {
                wifi_socket = new Socket("1.2.3.4", 2000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sleep(1000);

            while (!wifi_stopSendThread) {
                if (wifi_enabled) {
                    if (wifi_connected) {

                        try {
                            wifi_dataOut = new DataOutputStream(wifi_socket.getOutputStream());
                            wifi_dataOut.writeUTF(wifiDataString());
                        } catch (IOException ignored) {
                        }

                        sleep(1000);
                    } else {
                        sleep(1000);
                    }
                } else {
                    sleep(1000);
                }
            }

            if (wifi_socket != null) {
                try {
                    wifi_socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (wifi_dataOut == null) {
                try {
                    assert false;
                    wifi_dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

// ******************************************************************************
//      Receive Thread
// ******************************************************************************

    private class wifiReceiveThread implements Runnable {
        wifiReceiveThread() {
//        puts the thread in the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }

        public void run() {

            char[] buff = new char[4];

            while (true) {
                if (!(wifi_socket == null)) break;
            }

            try {
                wifi_dataIn = new BufferedReader(new InputStreamReader(wifi_socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!wifi_stopReceiveThread) {
                try {
                    wifi_dataIn.read(buff, 0, 4);

                    final String result = new String(buff);

                    if (result.startsWith("GJC")) {
                        String receive_Result = result.substring(3, 4);

                        if (receive_Result.equals("0")) {
                            wifi_stopReceive = false;
                        }
                        if (receive_Result.equals("3") && !wifi_stopReceive) {
                            wifi_stopReceive = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pointsPlus_b();
                                }
                            });
                        }
                        if (receive_Result.equals("2") && !wifi_stopReceive) {
                            wifi_stopReceive = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pointsPlus_a();
                                }
                            });
                        }
                    }
                } catch (IOException ignored) {
                }

                sleep(500);
            }

            if (wifi_socket != null) {
                try {
                    wifi_socket.close();
                    wifi_socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (wifi_dataIn == null) {
                try {
                    assert false;
                    wifi_dataIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

// ******************************************************************************

    private String wifiDataString() {
        String s1;
        if (getStartTesting()) {
            if (getTest()) {


                setTest(false);
                s1 = "GJCXXXZ8888888888";
            } else {
                setTest(true);
                s1 = "GJCXXXZBBBBBBBBBB";
            }
        } else {
            s1 = "GJCXX"                    //+ "Z8888888888";
                    + c_Audio_str
                    + server
                    + c_sets_v
                    + padLeftZero(c_games_v, 2)
                    + convert_Points_Transmit(c_points_v)
                    + c_sets_h
                    + padLeftZero(c_games_h, 2)
                    + convert_Points_Transmit(c_points_h);

            c_Audio_str = "X";
        }
        return s1;
    }

// ******************************************************************************
// Timer to check WiFi
// ******************************************************************************

    public void start_wifiTimer() {
        if (wifi_counter != null) {
            wifi_counter.cancel();
        }

        wifi_counter = new wifiTimer();

// TODO


//        wifi_counter.start();
    }

// ******************************************************************************

    public void stop_wifiTimer() {

        if (wifi_counter != null) {
            wifi_counter.cancel();
        }
    }

// ******************************************************************************

    private class wifiTimer extends CountDownTimer {

        wifiTimer() {

            super(2000, 2000);
        }

        // ******************************************************************************

        @Override
        public void onFinish() {
            if (wifiCheckWiFiEnabled()) {
                if (wifiCheckWiFiConnected()) {

                    wifi_connect_in_progress = false;

                } else {
                    wifiConnectToSpecificNetwork();
                }
            } else {
                wifi_enable_off();
            }
            wifi_counter = null;
            start_wifiTimer();
        }

        // ******************************************************************************

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

// ******************************************************************************
// Timer for flashing WiFi Box Connected
// ******************************************************************************

    private boolean flash = true;

    public void start_flashTimer() {

        if (flash_counter != null) {
            return;
        }
        flash_counter = new flashTimer();
        flash_counter.start();

        flash = !flash;
    }

// ******************************************************************************

    public void stop_flashTimer() {

        if (flash_counter != null) {
            flash_counter.cancel();
            flash_counter = null;

            i = findViewById(R.id.id_wifi_dot);
            i.setVisibility(View.INVISIBLE);
        }
    }

// ******************************************************************************

    private class flashTimer extends CountDownTimer {
        flashTimer() {
            super(750, 750);
        }

        @Override
        public void onFinish() {
            flash_counter = null;

            if (flash) {
                i = findViewById(R.id.id_wifi_dot);
                i.setVisibility(View.VISIBLE);
            } else {
                i = findViewById(R.id.id_wifi_dot);
                i.setVisibility(View.INVISIBLE);
            }
            start_flashTimer();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

    }

// ******************************************************************************
// Timer for flashing ILLEGAL title
// ******************************************************************************

    private boolean illegal_flash = true;

    public void start_illegal_flashTimer() {

        if (illegal_flash_counter != null) {
            return;
        }
        illegal_flash_counter = new illegal_flashTimer();
        illegal_flash_counter.start();

        illegal_flash = !illegal_flash;
    }

// ******************************************************************************

    public void stop_illegal_flashTimer() {

        if (illegal_flash_counter != null) {
            illegal_flash_counter.cancel();
            illegal_flash_counter = null;

            t = findViewById(R.id.id_club);
            t.setVisibility(View.INVISIBLE);
        }
    }

// ******************************************************************************

    private class illegal_flashTimer extends CountDownTimer {
        illegal_flashTimer() {
            super(500, 500);
        }

        @Override
        public void onFinish() {
            illegal_flash_counter = null;

            if (illegal_flash) {
                t = findViewById(R.id.id_club);
                t.setVisibility(View.VISIBLE);
            } else {
                t = findViewById(R.id.id_club);
                t.setVisibility(View.INVISIBLE);
            }
            start_illegal_flashTimer();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

// ******************************************************************************
// Timer for flashing Set Server message
// ******************************************************************************

    private boolean server_flash = true;

    public void start_server_flashTimer() {

        if (server_flash_counter != null) {
            return;
        }
        server_flash_counter = new server_flashTimer();
        server_flash_counter.start();

        server_flash = !server_flash;
    }

// ******************************************************************************

    public void stop_server_flashTimer() {

        if (server_flash_counter != null) {
            server_flash_counter.cancel();
            server_flash_counter = null;
        }

        t = findViewById(R.id.id_set_server);
        t.setVisibility(View.INVISIBLE);
    }

// ******************************************************************************

    private class server_flashTimer extends CountDownTimer {
        server_flashTimer() {
            super(500, 500);
        }

        @Override
        public void onFinish() {
            server_flash_counter = null;

            if (server_flash) {
                t = findViewById(R.id.id_set_server);
                t.setVisibility(View.VISIBLE);
            } else {
                t = findViewById(R.id.id_set_server);
                t.setVisibility(View.INVISIBLE);
            }
            start_server_flashTimer();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

// ******************************************************************************
// Timer for flashing Reset message
// ******************************************************************************

    private boolean reset_flash = true;

    public void start_reset_flashTimer() {

        if (reset_flash_counter != null) {
            return;
        }
        reset_flash_counter = new reset_flashTimer();
        reset_flash_counter.start();

        reset_flash = !reset_flash;
    }

// ******************************************************************************

    public void stop_reset_flashTimer() {

        if (reset_flash_counter != null) {
            reset_flash_counter.cancel();
            reset_flash_counter = null;
        }

        t = findViewById(R.id.id_notice);
        t.setVisibility(View.INVISIBLE);
    }

// ******************************************************************************

    private class reset_flashTimer extends CountDownTimer {
        reset_flashTimer() {
            super(500, 500);
        }

        @Override
        public void onFinish() {
            reset_flash_counter = null;

            if (reset_flash) {
                t = findViewById(R.id.id_notice);
                t.setVisibility(View.VISIBLE);
            } else {
                t = findViewById(R.id.id_notice);
                t.setVisibility(View.INVISIBLE);
            }
            start_reset_flashTimer();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

    }

// ******************************************************************************

    public static String padLeftZero(int s, int n) {

        return String.format("%0" + n + "d", Integer.parseInt(String.valueOf(s)));
    }
}
