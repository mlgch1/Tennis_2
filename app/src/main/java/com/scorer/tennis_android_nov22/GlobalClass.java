
package com.scorer.tennis_android_nov22;

import android.app.Application;

public class GlobalClass extends Application {

    // ***********************************

    private static boolean test = false;

    public static boolean getTest() {
        return test;
    }

    public static void setTest(boolean aTest) {
        test = aTest;
    }

    // ***********************************

    private static boolean testing = false;

    public static boolean getStartTesting() {
        return testing;
    }

    public static void setStartTesting(boolean aTesting) {
        testing = aTesting;
    }

    // ***********************************

    private static int intThresh = 0;

    public static int getThresh() {
        return intThresh;
    }

    public static void setThresh(int aThresh) {
        intThresh = aThresh;
    }

    // ***********************************

    private static int intInc = 0;

    public static int getInc() {
        return intInc;
    }

    public static void setInc(int aInc) {
        intInc = aInc;
    }

    // ***********************************

    private static int intSSID = 0;

    public static int getSSID() {
        return intSSID;
    }

    public static void setSSID(int aSSID) {
        intSSID = aSSID;
    }

    // ***********************************

    private static int intChannel = 0;

    public static int getChannel() {
        return intChannel;
    }

    public static void setChannel(int aChannel) {
        intChannel = aChannel;
    }
}
