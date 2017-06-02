package com.example.ravin.veto;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Title extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void startGameSingle(View view) {
        Intent start = new Intent(Title.this, Game.class);
        start.putExtra("gameMode", false);
        startActivity(start);
    }

    public void startGameDouble(View view) {
        Intent start = new Intent(Title.this, Game.class);
        start.putExtra("gameMode", true);
        startActivity(start);
    }

    public void showHelp(View view) {
        Intent start = new Intent(Title.this, HelpScreen.class);
        startActivity(start);
    }
}
