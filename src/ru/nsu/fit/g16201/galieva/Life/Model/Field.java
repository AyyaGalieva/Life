package ru.nsu.fit.g16201.galieva.Life.Model;

public class Field {
    static private final int SIZE = 10;
    private int width, height;

    private boolean states[][];
    private double impacts[][];

    public Field(int width, int height) {
        this.width = width;
        this.height = height;

        this.states = new boolean[width][height];
        this.impacts = new double[width][height];
    }

    public Field() {
        this(10,10);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getCellImpact(int x, int y) {
        return impacts[x][y];
    }

    public void setCellImpact(int x, int y, double impact) {
        this.impacts[x][y] = impact;
    }

    public boolean getCellState(int x, int y) {
        return states[x][y];
    }

    public void setCellState(int x, int y, boolean state) {
        states[x][y] = state;
    }
}
