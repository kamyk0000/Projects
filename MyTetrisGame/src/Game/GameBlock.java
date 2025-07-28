package Game;

import java.awt.*;
import javax.swing.*;

public class GameBlock {
    private int[][] shape;
    private int[][][] shapeList;
    private Color color;
    private Color[] colors = {Color.blue,Color.red,Color.green,Color.cyan,Color.orange,Color.yellow,Color.pink};
    private int x, y;
    private int rotation;


    public GameBlock(int[][] shape){
        this.shape = shape;

        shapes();
    }

    private void shapes(){
        shapeList = new int[4][][];

        for (int i = 0; i < 4; i++) {
            int r = shape[0].length;
            int c = shape.length;

            shapeList[i] = new int[r][c];
            for (int j = 0; j < r; j++) {
                for (int k = 0; k < c; k++) {
                    shapeList[i][j][k] = shape[c - k - 1][j];
                }

            }
            shape = shapeList[i];
        }
    }

    public void spawn(int width) {

        rotation = (int)(Math.random()*4);
        shape =shapeList[rotation];

        color = colors [ (int)(Math.random()*colors.length) ];
        //color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));

        y = -getHeight();
        x = ((int)(Math.random()*width))-getWidth();
        while (x<0){
            x++;
        }
    }

    public void fall() {
        y++;
    }

    public void left() {
        x--;
    }

    public void right() {
        x++;
    }

    public void rotate() {
        if (rotation<3)
            rotation++;
        else
            rotation = 0;
        shape = shapeList[rotation];
    }

    public void rotateBack() {
        rotation--;
        if (rotation<0) {
            rotation = 3;
        }

        shape = shapeList[rotation];
    }

    public int getBottom() {
        return y+getHeight();
    }

    public int getLeftSide()  {
        return x;
    }

    public int getRightSide() {
        return x + getWidth();
    }

    public int getTop() {
        return 0;
    }

    public int getHeight(){
        return shape.length;
    }

    public int getWidth(){
        return shape[0].length;
    }

    public int[][] getShape() {
        return shape;
    }

    public void setShape(int[][] shape) {
        this.shape = shape;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
