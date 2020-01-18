package com.example.medproject;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

public class BasicActions {

    public static void hideKeyboard(AppCompatActivity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardWithClick(View element, final AppCompatActivity activity) {
        element.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(activity);
                return true;
            }
        });
    }

    public static void hideActionBar(AppCompatActivity activity){
        try
        {
            activity.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
    }

}
