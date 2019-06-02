package com.meitu.mypuzzle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PuzzleView myView = new PuzzleView(this);
        setContentView(myView);
    }
}
