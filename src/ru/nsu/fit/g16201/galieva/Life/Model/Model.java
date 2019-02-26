package ru.nsu.fit.g16201.galieva.Life.Model;

import ru.nsu.fit.g16201.galieva.Life.View.GUI;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model extends TimerTask {
    private GUI view;
    private Field field = new Field();
    private GameParameters parameters = new GameParameters();
    private boolean XORMode = false;
    private boolean runMode = false;

    public Model(){}

    public void setView(GUI view) {
        this.view = view;
    }

    @Override
    public void  run() {
        if (runMode)
            makeStep();
    }

    public void clearField() {
        for (int y = 0; y < field.getHeight(); ++y) {
            for (int x = 0; x < field.getWidth(); ++x) {
                field.setCellState(x, y, false);
            }
        }
        setNewImpacts();

        if (view!=null)
            view.updateCellState(field);
    }

    private boolean isCellExists(int x, int y) {
        return (x >= 0)&&(y >= 0)&&(y < field.getHeight())&&(x < (field.getWidth() - y % 2));
    }

    public void clickCell(int x, int y) {
        if (isCellExists(x, y)) {
            if (XORMode)
                field.setCellState(x, y, !field.getCellState(x, y));
            else field.setCellState(x, y, true);
            setNewImpacts();

            if (view!= null)
                view.updateCellState(field);
        }
    }

    public synchronized void makeStep() {
        Field newField = new Field(field.getWidth(), field.getHeight());
        for (int y = 0; y < field.getHeight(); ++y) {
            for (int x = 0; x < field.getWidth() - y%2; ++x) {
                newField.setCellState(x, y, calculateCellState(x, y, field.getCellImpact(x, y)));
            }
        }
        field = newField;
        setNewImpacts();

        if (view!=null)
            view.updateCellState(field);
    }

    private double calculateCellImpact(int x, int y) {
        double impact = 0.0;
        double fst_impact = parameters.getFST_IMPACT();
        double snd_impact = parameters.getSND_IMPACT();

        int dx, dy;
        int fstOffsetX[] = {-1, 1, -1, 0, -1, 0};
        int fstOffsetY[] = {0, 1, -1};
        int sndOffsetX[] = {0, 0, -2, 1, -2, 1};
        int sndOffsetY[] = {2, -2, 1, 1, -1, -1};

        for (int i = 0; i < 6; ++i) {
            dx = x + fstOffsetX[i];
            dy = y + fstOffsetY[i/2];
            dx += (y%2 == 1 && Math.abs(fstOffsetY[i/2])%2 == 1)?1:0;
            impact += (isCellExists(dx, dy) && field.getCellState(dx, dy))?fst_impact:0;
        }

        for (int i = 0; i < 6; ++i) {
            dx = x + sndOffsetX[i];
            dy = y + sndOffsetY[i];
            dx += (y%2 == 1 && Math.abs(sndOffsetY[i])%2 == 1)?1:0;
            impact += (isCellExists(dx, dy) && field.getCellState(dx, dy))?snd_impact:0;
        }

        return impact;
    }

    private void setNewImpacts() {
        for (int y = 0; y < field.getHeight(); ++y)
            for (int x = 0; x < field.getWidth() - y%2; ++x)
                field.setCellImpact(x, y, calculateCellImpact(x, y));
    }

    private boolean calculateCellState(int x, int y, double impact) {
        boolean oldState = field.getCellState(x, y);
        if (!oldState && (parameters.getBIRTH_BEGIN() <= impact) && (impact <= parameters.getBIRTH_END()))
            return true;
        if (oldState && (impact < parameters.getLIVE_BEGIN() || impact > parameters.getLIVE_END()))
            return false;
        return oldState;
    }

    public void setParameters(GameParameters parameters) {
        this.parameters = parameters;
        setNewImpacts();
    }

    public GameParameters getParameters() {
        try {
            return parameters.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void loadParametersFromFile(String path) {
        try {
            Scanner in = new Scanner(new File(path));

            int width = getNextParameter(in);
            int height = getNextParameter(in);
            int lineWidth = getNextParameter(in);
            int size = getNextParameter(in);
            int alive = getNextParameter(in);
            Field newField = new Field(width, height);

            for (int i = 0; i < alive; ++i) {
                 int x = getNextParameter(in);
                 int y = getNextParameter(in);
                 if (x < width && y < height)
                    newField.setCellState(x, y, true);
            }
            in.close();

            CellParameters newCellParameters = new CellParameters(size, lineWidth);

            field = newField;
            setNewImpacts();

            if (view!=null) {
                view.updateCellParameters(newCellParameters);
                view.updateCellState(field);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private int getNextParameter(Scanner in) {
        while (!in.hasNextInt()) {
            String str[] = (in.nextLine()).split("//");
            try {
                return Integer.parseInt(str[0]);
            } catch (NumberFormatException e) {}
        }
        return in.nextInt();
    }

    public void saveParametersInFile(String path, CellParameters parameters) {
        try {
            PrintWriter out = new PrintWriter(new File(path));

            out.printf("%d %d\n%d\n%d\n", field.getWidth(), field.getHeight(), parameters.getLineWidth(), parameters.getSize());
            List<Point> alive = new ArrayList<>();
            for (int y = 0; y < field.getHeight(); ++y) {
                for (int x = 0; x < field.getWidth() - y%2; ++x) {
                    if (field.getCellState(x, y))
                        alive.add(new Point(x, y));
                }
            }
            out.printf("%d\n", alive.size());

            for (int i = 0; i < alive.size(); ++i)
                out.printf("%d %d\n", alive.get(i).x, alive.get(i).y);
            out.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void setFieldSize(int width, int height) {
        Field newField = new Field(width, height);
        for (int y = 0; y < Math.min(field.getHeight(), newField.getHeight()); y++) {
            for (int x = 0; x < Math.min(field.getWidth(), newField.getWidth()); x++) {
                newField.setCellState(x, y, field.getCellState(x, y));
            }
        }
        field = newField;
        setNewImpacts();
        if (view!=null)
            view.updateCellState(field);
    }

    public void turnOnXORMode() {
        XORMode = true;
    }

    public void turnOnReplaceMode() {
        XORMode = false;
    }

    public void setRunMode(boolean enabled) {
        runMode = enabled;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
