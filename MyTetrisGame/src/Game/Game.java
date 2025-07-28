package Game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game
extends JFrame {
    private GameWindow window;
    private GameThread thread;
    private JLabel scoreLabel;
    private JLabel exitLabel;

    private String [] titles = {"You're doing great!", "Keep it up!", "You're on fire!!!", "Hell yea!"};


    public Game () {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10,100,10,100);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Tetris!");

        addKeyListener(listener);

        scoreLabel = new JLabel();
        scoreLabel.setText("Score: 0");
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 1;
        constraints.gridy = 1;
        add(scoreLabel,constraints);

        exitLabel = new JLabel();
        exitLabel.setText("Press ESC to exit to Main Menu");
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(exitLabel,constraints);

        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.ipady = 485;
        constraints.ipadx = 320;
        window = new GameWindow(10);
        add(window,constraints);

        pack();
        setLocationRelativeTo(null);
    }

    public void upMyScore(int score){
        scoreLabel.setText("Score: "+score);
        this.setTitle(titles[(int)(Math.random()*titles.length)]);
    }

    KeyListener listener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case 32:
                    window.dropDown();
                    break;
                case 37:
                    window.moveLeft();
                    break;
                case 38:
                    window.rotateBlock();
                    break;
                case 39:
                    window.moveRight();
                    break;
                case 40:
                    window.moveDown();
                    break;
                case 27:
                    stopGame();
                    MyTetris.showMenu();

            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    public void stopGame(){
        this.setVisible(false);
        thread.interrupt();
    }

    public void startGame (){
        window.clearPile();
        thread = new GameThread(window,this);
        thread.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Game().setVisible(true);
            }
        });
    }
}
