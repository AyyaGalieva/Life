package ru.nsu.fit.g16201.galieva.Life.Model;

public class CellParameters implements Cloneable {
    private int size = 20;
    private int lineWidth = 3;

    public CellParameters(){}

    public CellParameters(int size, int lineWidth) {
        this.size = size;
        this.lineWidth = lineWidth;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public CellParameters getClone(){
        try {
            return (CellParameters) this.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
