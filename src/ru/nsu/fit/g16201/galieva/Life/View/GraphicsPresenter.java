package ru.nsu.fit.g16201.galieva.Life.View;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class GraphicsPresenter {
    private Graphics2D g;
    private BufferedImage image;
    private Color color = Color.black;
    private int lineWidth = 1;

    public static final int fontSize = 12;

    public GraphicsPresenter(Graphics2D graphics2D, BufferedImage image) {
        g = graphics2D;
        g.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        this.image = image;
    }

    private Span defineSpan(Point seed, int color) {
        int leftX = seed.x;
        int rightX = seed.x;

        while ((leftX - 1 > 0)&&(image.getRGB(leftX - 1, seed.y) == color)) {
            --leftX;
        }
        while ((rightX + 1 < image.getWidth())&&(image.getRGB(rightX+1, seed.y)==color)) {
            ++rightX;
        }
        return new Span(seed.y, leftX, rightX);
    }

    public void spanFilling(Point seed) {
        int oldColor = image.getRGB(seed.x, seed.y);

        Stack<Span> spans = new Stack<>();
        spans.push(defineSpan(seed, oldColor));

        while (!spans.empty()) {
            Span span = spans.pop();
            drawSingleLine(new Point(span.getLeftX(), span.getY()), new Point(span.getRightX(), span.getY()));
            searchNextSpan(-1, span, oldColor, spans);
            searchNextSpan(1, span, oldColor, spans);
        }
    }

    private void searchNextSpan(int direction, Span curSpan, int oldColor, Stack<Span> spans) {
        if ((curSpan.getY() + direction > 0)&&(curSpan.getY() + direction < image.getHeight())) {
            for (int x = curSpan.getLeftX(); x < curSpan.getRightX(); ++x) {
                if (image.getRGB(x, curSpan.getY() + direction) == oldColor) {
                    Span newSpan = defineSpan(new Point(x, curSpan.getY() + direction), oldColor);
                    spans.push(newSpan);
                    x = newSpan.getRightX();
                }
            }
        }
    }

    public void drawSingleLine(Point p1, Point p2) {
        boolean swapXY = Math.abs(p2.y - p1.y) > Math.abs(p2.x - p1.x);
        if (swapXY) {
            int tmp = p1.x;
            p1.x = p1.y;
            p1.y = tmp;
            tmp = p2.x;
            p2.x = p2.y;
            p2.y = tmp;
        }
        if (p1.x > p2.x) {
            Point tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        int stepY = (p1.y > p2.y) ? -1 : 1;
        int dx = Math.abs(p2.x - p1.x);
        int dy = Math.abs(p2.y - p1.y);
        int err = dx/2;
        int y = p1.y;
        for (int x = p1.x; x <= p2.x; ++x) {
            image.setRGB(swapXY ? y : x, swapXY ? x : y, color.getRGB());
            err -= dy;
            if (err < 0) {
                y+=stepY;
                err += dx;
            }
        }
    }

    public void drawLine(Point p1, Point p2) {
        if (lineWidth != 1) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            return;
        }

        drawSingleLine(p1, p2);
    }

    public void printString(String str, Point p) {
        g.drawString(str, p.x, p.y);
    }

    public void setColor(Color color) {
        this.color = color;
        g.setColor(color);
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }
}
