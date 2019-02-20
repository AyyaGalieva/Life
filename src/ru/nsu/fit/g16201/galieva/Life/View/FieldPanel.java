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
    private BufferedImage image;
    private CellParameters cellParameters;
    private boolean impactsMode;

    private static final Color borderColor = Color.BLACK;

    private int startX = 0, startY = 0;
    private int cellSize = 0;
    private int halfCellSize = 0;
    private int halfCellWidth = 0;

    private boolean stateChanged;

    public FieldPanel(Field field, FieldPanelClickListener clickListener) {
        this.field = field;
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
                Point cell = defineCell(new Point(event.getX(), event.getY()));
                if ((cell != null) && (prevCell == null || !prevCell.equals(cell))) {
                    prevCell = cell;
                    stateChanged = true;
                    clickListener.onClick(cell);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                super.mouseReleased(event);
                prevCell = null;
            }

            private Point defineCell(Point p) {
                if (p.x<0 || p.y<0 || p.x >= image.getWidth() || p.y >= image.getHeight())
                    return null;
                if (image.getRGB(p.x, p.y) != borderColor.getRGB()) {
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
                    if (realPoint.x >= 0 && realPoint.y >= 0 && realPoint.x <= field.getWidth() - realPoint.y%2 && realPoint.y <= field.getHeight()) {
                        return realPoint;
                    }
                }
                return null;
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void drawCells(GraphicsPresenter presenter) {
        presenter.setLineWidth(cellParameters.getLineWidth());
        presenter.setColor(borderColor);
        int y0 = startY;
        for (int y = 0; y < field.getHeight(); ++y) {
            int x0 = startX - 2*halfCellWidth;
            x0 += y%2==1?halfCellWidth:0;
            for (int x = 0; x < field.getWidth() - y%2; ++x) {
                x0 += 2*halfCellWidth;

                presenter.drawLine(new Point(x0, y0+halfCellSize), new Point(x0, y0+halfCellSize+cellSize));
                presenter.drawLine(new Point(x0, y0+halfCellSize), new Point(x0+halfCellWidth, y0));
                if (y==0 || x==field.getWidth()-1){}
                    presenter.drawLine(new Point(x0+halfCellWidth, y0), new Point(x0+2*halfCellWidth, y0+halfCellSize));
                presenter.drawLine(new Point(x0, y0+cellSize+halfCellSize), new Point(x0+halfCellWidth, y0+2*cellSize));
                if (y==field.getHeight()-1)
                    presenter.drawLine(new Point(x0+halfCellWidth, y0+2*cellSize), new Point(x0+2*halfCellWidth, y0+cellSize+halfCellSize));
            }
            presenter.drawLine(new Point(x0+2*halfCellWidth, y0+halfCellSize), new Point(x0+2*halfCellWidth, y0+cellSize+halfCellSize));
            if (y%2==0 && y!=field.getHeight()-1) {
                presenter.drawLine(new Point(x0+2*halfCellWidth, y0+cellSize+halfCellSize), new Point(x0+halfCellWidth, y0+2*cellSize));
            }
            y0 += cellSize+halfCellSize;
        }

        fillCells(presenter);
    }

    public void fillCells(GraphicsPresenter presenter) {
        int y0 = startY;
        for (int y = 0; y < field.getHeight(); ++y) {
            int x0 = startX-2*halfCellWidth;
            x0 += y%2==1?halfCellWidth:0;
            for (int x = 0; x < field.getWidth() - y%2; ++x) {
                x0 += 2*halfCellWidth;
                if (field.getCellState(x, y)) {
                    presenter.setColor(Color.cyan);
                    presenter.spanFilling(new Point(x0+cellParameters.getLineWidth()/2+1, y0+cellSize+1));
                    presenter.setColor(Color.black);
                }
                if (impactsMode && cellParameters.getSize() >= 10) {
                    String str;
                    double impact = field.getCellImpact(x, y);
                    if (impact - (long)impact < 0.0001)
                        str = String.format("%.0f", impact);
                    else str = String.format("%.1f", impact);

                    presenter.printString(str, new Point((int) (x0+halfCellWidth-(str.length()/2.0)*(GraphicsPresenter.fontSize/2.0)), (int) y0+cellSize+ GraphicsPresenter.fontSize/2));
                }
            }
            y0 += cellSize+halfCellSize;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        startX = cellParameters.getLineWidth()/2;
        startY = cellParameters.getLineWidth()/2;
        cellSize = cellParameters.getSize() + cellParameters.getLineWidth()/2;
        halfCellSize = cellSize/2;
        halfCellWidth = (int) Math.round(cellSize*Math.sqrt(3)/2);

        int width = 2*field.getWidth()*halfCellWidth+cellParameters.getLineWidth();
        int height = (cellSize+halfCellSize)*(field.getHeight()+1);

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        GraphicsPresenter representer = new GraphicsPresenter(image.createGraphics(), image);
        drawCells(representer);
        setPreferredSize(new Dimension(width, height));
        g.drawImage(image, 0, 0, width, height, this);
    }

    public void showImpacts(boolean enabled) {
        impactsMode = enabled;
    }

    public CellParameters getCellParameters() {
        return cellParameters.getClone();
    }

    public void setCellParameters(CellParameters cellParameters) {
        this.cellParameters = cellParameters;
    }

    public void updateCellState(Field field) {
        this.field = field;
        this.repaint();
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }
}
