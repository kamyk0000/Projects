package Game;

import java.awt.*;
import javax.swing.*;

public class GameThread
extends Thread {
    private Game game;
    private GameWindow window;
    private int score;
    private int time;

    public GameThread(GameWindow window, Game game) {
        this.window=window;
        this.game=game;
        this.score=0;
    }

    @Override
    public void run () {
        while (true){
            window.spawnBlock ();
            while (window.blockFall()) {
                try {
                        Thread.sleep(speedUpThread(score));
                } catch (InterruptedException e) {
                    return;
                }
            }
            if(window.blockOutOfBounds()){
                MyTetris.gameOver(score);
                break;
            }
            window.moveToPile();
            score += window.clearBlockRow();
            game.upMyScore(score);
        }
    }

    public int speedUpThread (int score){
        switch (score) {
            case 0 -> time = 900;
            case 1 -> time = 800;
            case 2 -> time = 700;
            case 3 -> time = 600;
            case 4 -> time = 500;
            case 5 -> time = 400;
            default -> time = 300;
        }
        return time;
    }
}
