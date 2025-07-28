package Game;

import Game.Blocks.*;
import Game.Blocks.Rectangle;

import java.awt.*;
import javax.swing.*;

public class GameWindow
extends JPanel{
    private int rows;
    private int columns;
    private int cell;

    private GameBlock block;
    private GameBlock [] blocks;

    private Color [][] background;

    public GameWindow(int columns) {
        this.setBounds(10,10,330,500);
        this.setBackground(Color.DARK_GRAY);
        this.setBorder(BorderFactory.createLineBorder(Color.black,1));

        this.columns = columns;
        this.cell = getWidth()/columns;
        this.rows = getHeight()/cell;

        this.background = new Color[this.rows][this.columns];

        this.blocks = new GameBlock []{new I(), new T(), new L(), new ReversedL(), new Z(), new ReversedZ(), new Rectangle()};
    }

    public void spawnBlock () {
        block=blocks[(int)(Math.random()*blocks.length)];
        block.spawn(columns);
    }

    public boolean blockOutOfBounds(){
        if (block.getY()<0){
            block = null;
            return true;
        }
        return false;
    }

    public boolean blockFall (){
        while (canMoveDown()) {
            block.fall();
            repaint();
            return true;
        }

        return false;
    }

    public void moveRight(){
        if (canMoveRight()&&(block!=null)) {
            block.right();
            repaint();
        }
    }

    public void moveLeft(){
        if (canMoveLeft()&&(block!=null)) {
            block.left();
            repaint();
        }
    }

    public void moveDown(){
        if (canMoveDown()&&(block!=null)) {
            block.fall();
            repaint();
        }
    }

    public void dropDown(){
        while (canMoveDown()&&(block!=null)) {
            block.fall();
            repaint();
        }
    }

    public void rotateBlock() {
        if ((block.getBottom()!=rows)&&block!=null) {

            block.rotate();

            if (block.getLeftSide()<0){
                block.setX(0);
            }
            if (block.getRightSide() > columns) {
                block.setX(columns - block.getWidth());
            }
            if (!canRotate()){
                block.rotateBack();
            }
            if (block.getBottom() >= rows){
                block.setY(rows-block.getHeight());
            }
            if (!canRotate()){
                block.rotateBack();
            }
            repaint();
        }
    }

    public boolean canRotate(){
        int[][] shape = block.getShape();
        int width = block.getWidth();
        int height = block.getHeight();

        for (int c = 0; c < width; c++) {
            for (int r = height - 1; r >= 0; r--) {
                if (shape[r][c] != 0) {
                    int x = c + block.getX();
                    int y = r + block.getY();

                    if (y < 0) break;
                    if (x > columns-1) break;
                    if (background[y][x] != null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void moveToPile(){
        int [][] shape = block.getShape();
        int height = block.getHeight();
        int width = block.getWidth();
        Color color = block.getColor();

        int x = block.getX();
        int y = block.getY();

        for (int c = 0; c < height; c++){
            for (int r = 0; r < width; r++){
                if (shape [c][r] == 1){
                    background [c+y][r+x] = color;
                }
            }
        }
    }

    public void clearPile(){
        background=new Color[rows][columns];
    }

    public int clearBlockRow() {
        int scoreForBlock=0;
        boolean lineFilled;
        for (int i = rows - 1; i >= 0; i--) {
            lineFilled = true;
            for (int j = 0; j < columns; j++) {
                if (background[i][j] == null) {
                    lineFilled = false;
                    break;
                }
            }
            if (lineFilled) {
                deleteWholeRow(i);
                shiftAll(i);
                deleteWholeRow(0);

                i++;
                scoreForBlock++;

                repaint();
            }
        }
        return scoreForBlock;
    }

    public void deleteWholeRow(int row){
        for (int k = 0; k < columns; k++) {
            background[row][k] = null;
        }

    }

    public void shiftAll (int row){
        for (int g = row; g > 0; g--) {
            for (int f = 0; f < columns; f++) {
                background[g][f] = background[g-1][f];
            }
        }
    }

    public boolean canMoveDown(){
        if (block!=null) {
            int[][] shape = block.getShape();
            int width = block.getWidth();
            int height = block.getHeight();

            if (block.getBottom() == rows) {
                return false;
            }

            for (int c = 0; c < width; c++) {
                for (int r = height - 1; r >= 0; r--) {
                    if (shape[r][c] != 0) {
                        int x = c + block.getX();
                        int y = r + block.getY() + 1;

                        if (y < 0) break;
                        if (background[y][x] != null) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    public boolean canMoveLeft(){
        if (block!=null) {
            int[][] shape = block.getShape();
            int width = block.getWidth();
            int height = block.getHeight();

            if (block.getLeftSide() == 0) {
                return false;
            }
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    if (shape[r][c] != 0) {
                        int x = c + block.getX() - 1;
                        int y = r + block.getY();

                        if (y < 0) break;
                        if (background[y][x] != null) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    public boolean canMoveRight(){
        if (block!=null) {
            int[][] shape = block.getShape();
            int width = block.getWidth();
            int height = block.getHeight();

            if (block.getRightSide() == columns) {
                return false;
            }
            for (int r = 0; r < height; r++) {
                for (int c = width - 1; c >= 0; c--) {
                    if (shape[r][c] != 0) {
                        int x = c + block.getX() + 1;
                        int y = r + block.getY();

                        if (y < 0) break;
                        if (background[y][x] != null) {
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    private void drawMyRect(Graphics g, Color color, int x, int y){
        g.setColor(color);
        g.fillRect(x, y, cell, cell);
        g.setColor(new Color(0,0,0,140));
        g.drawRect(x, y, cell, cell);
    }

    public void drawBlock (Graphics g) {
        if (block != null) {
            int height = block.getHeight();
            int width = block.getWidth();
            Color color = block.getColor();
            int[][] shape = block.getShape();


            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (shape[i][j] == 1) {
                        int x = (block.getX() + j) * cell;
                        int y = (block.getY() + i) * cell;

                        drawMyRect(g, color, x, y);
                    }
                }
            }
        }
    }

    public void drawPile (Graphics g) {
        Color color;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                color = background[i][j];
                if (color != null) {
                    int x = j * cell;
                    int y = i * cell;

                    drawMyRect(g, color, x, y);
                }
            }
        }
    }

    @Override
    public void paint (Graphics g) {
        super.paint(g);
        drawPile (g);
        drawBlock(g);
    }
}
