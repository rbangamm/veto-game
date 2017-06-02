package com.example.ravin.veto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Game extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private AlertDialog.Builder builder;
    private Button[][] board = new Button[5][5];
    private boolean playerOneTurn = true;
    private boolean vetoUsed = false;
    private boolean won = false;
    private boolean twoPlayer;
    private boolean blocker;
    private Vibrator vib;
    private int turnCounter = 0;
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    reset();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
    DialogInterface.OnClickListener playerRoleListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    blocker = true;
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    blocker = false;
                    aiTurn(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builder = new AlertDialog.Builder(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            twoPlayer = extras.getBoolean("gameMode");
        }

        setContentView(R.layout.activity_game);
        vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        //...I'm so sorry...
        board = new Button[5][5];
        board[0][0] = (Button) findViewById(R.id.b1);
        board[0][1] = (Button) findViewById(R.id.b2);
        board[0][2] = (Button) findViewById(R.id.b3);
        board[0][3] = (Button) findViewById(R.id.b4);
        board[0][4] = (Button) findViewById(R.id.b5);
        board[1][0] = (Button) findViewById(R.id.b6);
        board[1][1] = (Button) findViewById(R.id.b7);
        board[1][2] = (Button) findViewById(R.id.b8);
        board[1][3] = (Button) findViewById(R.id.b9);
        board[1][4] = (Button) findViewById(R.id.b10);
        board[2][0] = (Button) findViewById(R.id.b11);
        board[2][1] = (Button) findViewById(R.id.b12);
        board[2][2] = (Button) findViewById(R.id.b13);
        board[2][3] = (Button) findViewById(R.id.b14);
        board[2][4] = (Button) findViewById(R.id.b15);
        board[3][0] = (Button) findViewById(R.id.b16);
        board[3][1] = (Button) findViewById(R.id.b17);
        board[3][2] = (Button) findViewById(R.id.b18);
        board[3][3] = (Button) findViewById(R.id.b19);
        board[3][4] = (Button) findViewById(R.id.b20);
        board[4][0] = (Button) findViewById(R.id.b21);
        board[4][1] = (Button) findViewById(R.id.b22);
        board[4][2] = (Button) findViewById(R.id.b23);
        board[4][3] = (Button) findViewById(R.id.b24);
        board[4][4] = (Button) findViewById(R.id.b25);

        for (int row = 0; row < board.length; row ++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col].setTag("" + row + " " + col);
                board[row][col].setOnClickListener(this);
                board[row][col].setOnLongClickListener(this);
                board[row][col].setTextSize(50);
            }
        }

        if (!twoPlayer) {
            builder.setMessage("Would you like to play as the Blocker or the Forcer?").setTitle("Choose a role")
                    .setPositiveButton("Blocker", playerRoleListener).setNegativeButton("Forcer", playerRoleListener)
                    .show();
            TextView textView = (TextView) findViewById(R.id.message);
            textView.setText(R.string.single_player_message);
        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.;
    }

    //Actual game methods

    public void showHelp(View view) {
        Intent intent = new Intent(Game.this, HelpScreen.class);
        startActivity(intent);
    }

    public void resetPressed(View view) {
        builder.setMessage("Are you sure you want to reset the board?").setTitle("Reset?").
        setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    public void reset() {
        TextView txt = (TextView) findViewById(R.id.message);
        playerOneTurn = true;
        vetoUsed = false;
        won = false;
        turnCounter = 0;

        txt.setText(R.string.message);
        for (Button[] aBoard : board) {
            for (Button anABoard : aBoard) {
                anABoard.setText("");
            }
        }

        if (!twoPlayer) {
            builder.setMessage("Would you like to play as the Blocker or the Forcer?").setTitle("Choose a role")
                    .setPositiveButton("Blocker", playerRoleListener).setNegativeButton("Forcer", playerRoleListener)
                    .show();
            txt.setText(R.string.single_player_message);
        }
    }

    private void aiTurn(boolean player) {
        Random rand = new Random();
        int row;
        int col;
        do {
            row = rand.nextInt(5);
            col = rand.nextInt(5);
        } while (board[row][col].getText() != "");

        if (player) {
            board[row][col].setText("X");
            if (winCheck()) {
                builder.setPositiveButton(null, null).setNegativeButton(null, null).setMessage("AI wins!").setTitle("Winner!").show();
                won = true;
            }
        } else {
            if (rand.nextInt(25) == 13 && !vetoUsed) {
                board[row][col].setText("V");
                Toast.makeText(Game.this, "AI used up their Veto!", Toast.LENGTH_SHORT).show();
                vetoUsed = true;
            } else {
                board[row][col].setText("O");
                if (winCheck()) {
                    builder.setMessage("Player wins!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                    won = true;
                }
            }
        }
        turnCounter++;
        if (turnCounter == 25 && !won) {
            if (blocker) { //AI is Forcer
                builder.setMessage("Player wins!").setTitle("Winner").setPositiveButton(null, null).setNegativeButton(null, null).show();
            } else { //AI is Blocker
                builder.setMessage("AI wins!").setTitle("Winner!").setPositiveButton(null, null).setNegativeButton(null, null).show();
            }
        }
    }

    private boolean winCheck() {
        int hCounter = 0;
        int vCounter = 0;
        int diagCounter = 0;
        int antiDiagCounter = 0;

        //Forcer wins if hCounter >= 3, vCounter >= 3, diagCounter >= 3 or antiDiagCounter >= 3
        for (int row = 0; row < board.length; row++) {
            hCounter = 0;
            vCounter = 0;
            for (int col = 0; col < board[row].length; col++) {
                    if (col + 1 < 5) {
                        if (board[row][col].getText() == "X" || board[row][col].getText() == "O")
                            hCounter = (board[row][col].getText() == board[row][col + 1].getText()) ? hCounter + 1 : 0;
                        if (board[col][row].getText() == "X" || board[col][row].getText() == "O")
                            vCounter = (board[col][row].getText() == board[col + 1][row].getText()) ? vCounter + 1 : 0;
                        if (row == 0) {
                            if (board[col][col].getText() == "X" || board[col][col].getText() == "O")
                                diagCounter = (board[col][col].getText() == board[col + 1][col + 1].getText()) ? diagCounter + 1 : 0;
                            if (board[col][4 - col].getText() == "X" || board[col][4 - col].getText() == "O")
                                antiDiagCounter = (board[col][4 - col].getText() == board[col + 1][3 - col].getText()) ? antiDiagCounter + 1 : 0;
                        }
                    }
                if (hCounter >= 3 || vCounter >= 3 || diagCounter >= 3 || antiDiagCounter >= 3) {
                    return true;
                }
            }
        }

        return checkRowEquality(board[0][1], board[1][2], board[2][3], board[3][4]) ||
                checkRowEquality(board[1][0], board[2][1], board[3][2], board[4][3]) ||
                checkRowEquality(board[3][0], board[2][1], board[1][2], board[0][3]) ||
                checkRowEquality(board[4][1], board[3][2], board[2][3], board[1][4]);

    }

    private boolean checkRowEquality(Button s1, Button s2, Button s3, Button s4) {
        return s1.getText() != "" && (s1.getText() == s2.getText() && s2.getText() == s3.getText() && s3.getText() == s4.getText());
    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) findViewById(R.id.message);
        Button b = (Button) findViewById(v.getId());
        if (b.getText() == "") {
            if (!won) {
                if (twoPlayer) { //Checks for two player game
                    if (playerOneTurn) {
                        b.setText("O");
                        playerOneTurn = false;
                        turnCounter++;
                        textView.setText(R.string.player2Turn);
                    } else {
                        b.setText("X");
                        playerOneTurn = true;
                        turnCounter++;
                        textView.setText(R.string.player1Turn);
                    }
                    if (winCheck()) {
                        builder.setTitle("Winner!").setMessage("Forcer, you win!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                        won = true;
                    } else if (turnCounter == 25) {
                        builder.setTitle("Winner!").setMessage("Blocker, you win!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                    }
                } else { //Single Player
                    if (blocker) {
                        b.setText("O"); //Player is blocker
                        if (winCheck()) {
                            builder.setMessage("AI Wins!").setTitle("Winner!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                            won = true;
                        }
                        turnCounter++;
                        if (turnCounter == 25 && !won) {
                            builder.setMessage("Player wins!").setTitle("Winner!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                        } else if (!won) {
                            aiTurn(true);
                        }
                    } else {
                        b.setText("X"); //Player is Forcer
                        if (winCheck()) {
                            builder.setMessage("Player wins!").setTitle("Winner!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                            won = true;
                        }
                        turnCounter++;
                        if (turnCounter == 25 && !won) {
                            builder.setMessage("AI wins!").setTitle("Winner!").setPositiveButton(null, null).setNegativeButton(null, null).show();
                        } else if (!won) {
                            aiTurn(false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        TextView textView = (TextView) findViewById(R.id.message);
        Button b = (Button) findViewById(v.getId());
        if (twoPlayer) {
            if (playerOneTurn) {
                if (!won) {
                    if (!vetoUsed) {
                        if (b.getText() == "") {
                            vib.vibrate(50);
                            Toast.makeText(Game.this, "You used up your veto!", Toast.LENGTH_SHORT).show();
                            b.setText("V");
                            playerOneTurn = false;
                            turnCounter++;
                            vetoUsed = true;
                            textView.setText(R.string.player2Turn);
                        }
                    } else {
                        Toast.makeText(Game.this, "No more Vetos :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            if (blocker) {
                if (!won) {
                    if (!vetoUsed) {
                        if (b.getText() == "") {
                            vib.vibrate(50);
                            Toast.makeText(Game.this, "You used up your veto!", Toast.LENGTH_SHORT).show();
                            b.setText("V");
                            turnCounter++;
                            vetoUsed = true;
                        }
                    } else {
                        Toast.makeText(Game.this, "No more Vetos :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return false;
    }
}
