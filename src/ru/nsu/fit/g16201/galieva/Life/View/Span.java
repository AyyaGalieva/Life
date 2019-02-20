package ru.nsu.fit.g16201.galieva.Life.View;

public class Span {
    private int y, leftX, rightX;

    public Span(int y, int leftX, int rightX) {
        this.y = y;
        this.leftX = leftX;
        this.rightX = rightX;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Span) {
            Span span = (Span)object;
            return (span.y == y)&&(span.leftX == leftX)&&(span.rightX == rightX);
        }
        return false;
    }

    public int getY() {
        return y;
    }

    public int getLeftX() {
        return leftX;
    }

    public int getRightX() {
        return rightX;
    }
}
