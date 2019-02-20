package ru.nsu.fit.g16201.galieva.Life;

import ru.nsu.fit.g16201.galieva.Life.Model.Model;
import ru.nsu.fit.g16201.galieva.Life.View.GUI;

import java.util.Timer;

public class Life {
    public static void main(String[] args) {
        Model m = new Model();
        GUI view = new GUI(m);
        m.setView(view);
        view.setVisible(true);

        Timer timer = new Timer();
        timer.schedule(m, 0, 1000);
    }
}
