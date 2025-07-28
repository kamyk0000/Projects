package Game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Menu
extends JFrame{
    private JButton btnPlay;
    private JButton btnQuit;
    private JButton btnScore;

    public Menu(){
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Tetris!");

        constraints.insets = new Insets(200,100,10,100);
        constraints.ipadx = 300;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        
        btnPlay = new JButton();
        btnPlay.setText("Play!");
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTetris.start();
                setVisible(false);
            }
        });
        
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 0;
        
        add(btnPlay,constraints);
        constraints.insets = new Insets(10,100,10,100);
        
        btnScore = new JButton();
        btnScore.setText("Scoreboard");
        btnScore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyTetris.showScores();
                setVisible(false);
            }
        });
        
        constraints.gridy = 1;
        add(btnScore,constraints);
        constraints.insets = new Insets(10,100,100,100);
        
        btnQuit = new JButton();
        btnQuit.setText("Quit");
        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        constraints.gridy = 2;
        add(btnQuit,constraints);
        
        pack();
        setLocationRelativeTo(null);
    }

        public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }
}
