package Game;

import javax.swing.*;
import java.awt.*;

public class MyTetris {
    private static Game game;
    private static Menu menu;
    private static Scoreboard scoreboard;

    public static void start() {
        game.setVisible(true);
        game.startGame();
    }

    public static void showMenu() {
        menu.setVisible(true);
    }

    public static void showScores() {
        scoreboard.setVisible(true);
    }

    public static void gameOver(int score) {
        String name = JOptionPane.showInputDialog("Game over!\nwprowadz swoje imie");
        game.setVisible(false);
        scoreboard.addScore(name,score);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                menu = new Menu();
                game = new Game();
                scoreboard = new Scoreboard();

                menu.setVisible(true);
            }
        });
    }
}
