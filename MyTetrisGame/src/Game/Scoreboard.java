package Game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Scoreboard
extends JFrame {
    private JButton btnMenu;
    private JTable scoreboard;
    private DefaultTableModel model;
    private String scorefile = "scores.txt";

    public Scoreboard(){

        btnMenu = new javax.swing.JButton("Main Menu");
        scoreboard = new javax.swing.JTable();

        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)
        );

        btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                MyTetris.showMenu();
            }
        });
        add(btnMenu);

        scoreboard.setModel(new DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Name", "Score"
                }) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });

        scoreboard.getTableHeader().setReorderingAllowed(false);

        model = (DefaultTableModel) scoreboard.getModel();

        add(scoreboard);
        add(new JScrollPane(scoreboard));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Scores!");
        pack();
        setLocationRelativeTo(null);
        loadData();
    }

    public void addScore (String name, int score){
        model.addRow(new Object[] {name, score} );
        saveData();
        System.out.println(name + " " + score);
        this.setVisible(true);
    }

    private void loadData (){
        model = (DefaultTableModel) scoreboard.getModel();
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(scorefile));
            String line;
            while((line=br.readLine()) != null){
                String[]lines = line.split("\\t");
                model.addRow(new Object[]{ lines[0], lines[1] });
            }
            br.close();
        }catch (Exception ignored){ }
    }

    private void saveData () {
        model = (DefaultTableModel) scoreboard.getModel();
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(scorefile));
            for (int i=0;i<model.getRowCount();i++) {
                bw.write(model.getValueAt(i,0)+"\t"+model.getValueAt(i,1)+"\n");
            }
            bw.close();
        }catch (Exception ignored){ }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Scoreboard().setVisible(true);
            }
        });
    }
}
