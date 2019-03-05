package ru.nsu.fit.g16201.galieva.Life.View;

import ru.nsu.fit.g16201.galieva.Life.Model.CellParameters;
import ru.nsu.fit.g16201.galieva.Life.Model.Field;
import ru.nsu.fit.g16201.galieva.Life.View.Listeners.FieldPanelClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FieldPanel extends JPanel{

    private Field field;
    private BufferedImage fieldImage, impactImage;
    private GraphicsPresenter fieldPresenter;
    private int imageWidth, imageHeight;
    private CellParameters cellParameters;
    private boolean impactsMode;
    private boolean xorMode;
    private boolean runMode;

    private static final Color borderColor = Color.black;
    private static final Color fontColor = Color.darkGray;
    private static final Color spanColor = Color.cyan;
    private static final Color backgroundColor = new Color(238, 238, 238);

    private int startX = 0, startY = 0;
    private int cellSize = 0;
    private int halfCellSize = 0;
    private int halfCellWidth = 0;

    private boolean stateChanged;

    public FieldPanel(Field _field, FieldPanelClickListener clickListener) {
        this.field = _field;
        cellParameters = new CellParameters();
        impactsMode = false;
        stateChanged = false;

        if (clickListener == null)
            return;

        MouseAdapter mouseAdapter = new MouseAdapter() {
            Point prevCell;

            @Override
            public void mousePressed(MouseEvent event) {
                mouseDragged(event);
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                super.mouseDragged(event);
                if (!runMode) {
                    Point cell = defineCell(new Point(event.getX(), event.getY()));
                    if ((cell != null) && (prevCell == null || !prevCell.equals(cell))) {
                        prevCell = cell;
                        stateChanged = true;
                        clickListener.onClick(cell);
                        if (xorMode && fieldImage.getRGB(event.getX(), event.getY()) == spanColor.getRGB()) {
                            fieldPresenter.setColor(backgroundColor);
                            fieldPresenter.spanFilling(new Point(event.getX(), event.getY()));
                            fieldPresenter.setColor(borderColor);
                        } else if (fieldImage.getRGB(event.getX(), event.getY()) != spanColor.getRGB()) {
                            fieldPresenter.setColor(spanColor);
                            fieldPresenter.spanFilling(new Point(event.getX(), event.getY()));
                            fieldPresenter.setColor(borderColor);
                        }
                        printImpacts();
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                super.mouseReleased(event);
                prevCell = null;
            }

            private Point defineCell(Point p) {
                if (p.x<0 || p.y<0 || p.x >= fieldImage.getWidth() || p.y >= fieldImage.getHeight())
                    return null;
                if (fieldImage.getRGB(p.x, p.y) != borderColor.getRGB()) {
                    int approximateX = (p.x-startX)/(2*halfCellWidth);
                    int approximateY = (p.y-startY)/(cellSize+halfCellSize);

                    double minDistance =  -1.0;
                    Point realPoint = new Point(-1, -1);

                    for (int y = approximateY - 1; y <= approximateY + 1; ++y) {
                        for (int x = approximateX - 1; x <= approximateX + 1; ++x) {
                            Point center = new Point(startX+2*halfCellWidth*x+halfCellWidth, startY+y*(cellSize+halfCellSize)+cellSize);
                            center.x += (y%2==1)?halfCellWidth:0;

                            double distance = Math.sqrt((p.x-center.x)*(p.x-center.x)+(p.y-center.y)*(p.y-center.y));
                            if (distance < minDistance || minDistance < -0.0001) {
                                minDistance = distance;
                                realPoint.x = x;
                                realPoint.y = y;
                            }
                        }
                    }
                    if (realPoint.x >= 0 && realPoint.y >= 0 && realPoint.x < field.getWidth() - realPoint.y%2 && realPoint.y < field.getHeight()) {
                        return realPoint;
                    }
                }
                return null;
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        this.prepareImage();
    }

    private void drawCells() {
        fieldPresenter.setLineWidth(cellParameters.getLineWidth());
        fieldPresenter.setColor(borderColor);
        int y0 = startY;
        for (int y = 0; y < field.getHeight(); ++y) {
            int x0 = startX - 2*halfCellWidth;
            x0 += y%2==1?halfCellWidth:0;
            for (int x = 0; x < field.getWidth() - y%2; ++x) {
                x0 += 2*halfCellWidth;

                fieldPresenter.drawLine(new Point(x0, y0+halfCellSize), new Point(x0, y0+halfCellSize+cellSize));
                fieldPresenter.drawLine(new Point(x0, y0+halfCellSize), new Point(x0+halfCellWidth, y0));
                fieldPresenter.drawLine(new Point(x0+halfCellWidth, y0), new Point(x0+2*halfCellWidth, y0+halfCellSize));
                if (y==field.getHeight()-1 || x==0)
                fieldPresenter.drawLine(new Point(x0, y0+cellSize+halfCellSize), new Point(x0+halfCellWidth, y0+2*cellSize));
                if (y==field.getHeight()-1)
                    fieldPresenter.drawLine(new Point(x0+halfCellWidth, y0+2*cellSize), new Point(x0+2*halfCellWidth, y0+cellSize+halfCellSize));
            }
            fieldPresenter.drawLine(new Point(x0+2*halfCellWidth, y0+halfCellSize), new Point(x0+2*halfCellWidth, y0+cellSize+halfCellSize));
            if (y%2==0 && y!=field.getHeight()-1) {
                fieldPresenter.drawLine(new Point(x0+2*halfCellWidth, y0+cellSize+halfCellSize), new Point(x0+halfCellWidth, y0+2*cellSize));
            }
            y0 += cellSize+halfCellSize;
        }
    }

    public void fillCells() {
        int y0 = startY;
        impactImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics impactGraphics = impactImage.createGraphics();
        impactGraphics.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        impactGraphics.setColor(fontColor);
        for (int y = 0; y < field.getHeight(); ++y) {
            int x0 = startX-2*halfCellWidth;
            x0 += y%2==1?halfCellWidth:0;
            for (int x = 0; x < field.getWidth() - y%2; ++x) {
                x0 += 2*halfCellWidth;
                if (field.getCellState(x, y) && fieldImage.getRGB(x0+cellParameters.getLineWidth()/2+1, y0+cellSize+1) != spanColor.getRGB()) {
                    fieldPresenter.setColor(spanColor);
                    fieldPresenter.spanFilling(new Point(x0+cellParameters.getLineWidth()/2+1, y0+cellSize+1));
                    fieldPresenter.setColor(borderColor);
                }
                if (!field.getCellState(x, y) && fieldImage.getRGB(x0+cellParameters.getLineWidth()/2+1, y0+cellSize+1) == spanColor.getRGB()) {
                    fieldPresenter.setColor(backgroundColor);
                    fieldPresenter.spanFilling(new Point(x0+cellParameters.getLineWidth()/2+1, y0+cellSize+1));
                    fieldPresenter.setColor(borderColor);
                }

                printImpactString(x0, y0, x, y, impactGraphics);
            }
            y0 += cellSize+halfCellSize;
        }
    }

    public void printImpacts() {
        int y0 = startY;
        impactImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics impactGraphics = impactImage.createGraphics();
        impactGraphics.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        impactGraphics.setColor(fontColor);
        for (int y = 0; y < field.getHeight(); ++y) {
            int x0 = startX-2*halfCellWidth;
            x0 += y%2==1?halfCellWidth:0;
            for (int x = 0; x < field.getWidth() - y%2; ++x) {
                x0 += 2*halfCellWidth;

                printImpactString(x0, y0, x, y, impactGraphics);
            }
            y0 += cellSize+halfCellSize;
        }
    }

    private void printImpactString(int x0, int y0, int x, int y, Graphics impactGraphics){
        if (impactsMode && cellParameters.getSize() >= 10) {
            String str;
            double impact = field.getCellImpact(x, y);
            if (impact - (long)impact < 0.0001)
                str = String.format("%.0f", impact);
            else str = String.format("%.1f", impact);
            impactGraphics.drawString(str, (int)(x0+halfCellWidth-(str.length()/2.0)*(GraphicsPresenter.fontSize/2.0)), (int) y0+cellSize+ GraphicsPresenter.fontSize/2);
        }
    }

    public void prepareImage() {
        startX = cellParameters.getLineWidth()/2;
        startY = cellParameters.getLineWidth()/2;
        cellSize = cellParameters.getSize() + cellParameters.getLineWidth()/2;
        halfCellSize = cellSize/2;
        halfCellWidth = (int) Math.round(cellSize*Math.sqrt(3)/2);

        int newImageWidth = 2*field.getWidth()*halfCellWidth+cellParameters.getLineWidth();
        int newImageHeight = (cellSize+halfCellSize)*(field.getHeight()+1) - cellSize + cellParameters.getLineWidth() + 1;
        if (newImageWidth != imageWidth || newImageHeight != imageHeight) {
            imageWidth = newImageWidth;
            imageHeight = newImageHeight;
            fieldImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            impactImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

            fieldPresenter = new GraphicsPresenter(fieldImage.createGraphics(), fieldImage);
            drawCells();

            setPreferredSize(new Dimension(imageWidth, imageHeight));
        }
        fillCells();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fieldImage, 0, 0, imageWidth, imageHeight, this);
        g.drawImage(impactImage, 0, 0, imageWidth, imageHeight, this);
    }

    public void showImpacts(boolean enabled) {
        impactsMode = enabled;
        if (enabled) {
            printImpacts();
        } else {
            impactImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public CellParameters getCellParameters() {
        return cellParameters.getClone();
    }

    public void setCellParameters(CellParameters cellParameters) {
        this.cellParameters = cellParameters;
    }

    public void updateFieldState(Field field) {
        this.field = field;
        this.repaint();
    }

    public void updateFieldParameters(Field field) {
        this.field = field;
        this.prepareImage();
        this.repaint();
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void setRunMode(boolean runMode) {
        this.runMode = runMode;
    }

    public void setXorMode(boolean xorMode) {
        this.xorMode = xorMode;
    }
}
