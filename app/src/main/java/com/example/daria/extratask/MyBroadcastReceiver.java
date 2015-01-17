//package com.example.daria.extratask;
//
//import android.content.BroadcastReceiver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import java.util.ArrayList;
//
///**
// * Created by Daria on 16.01.2015.
// */
//public class MyBroadcastReceiver extends BroadcastReceiver {
//    private static final String DEBUG_TAG = "BroadcastReceiver";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        ArrayList<String> titles = intent.getStringArrayListExtra("TITLE");
//        for (int i = 0; i < titles.size(); i++) {
//            ContentValues cv = new ContentValues();
//            cv.put(MySQLiteDatabase.COLUMN_TITLE, titles.get(i));
//            //getContentResolver().insert(DB_URI, cv);
//        }
//        Log.d(DEBUG_TAG, "add" + titles.size());
//    }
//}
