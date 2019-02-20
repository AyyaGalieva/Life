package ru.nsu.fit.g16201.galieva.Life.View.Listeners;

import ru.nsu.fit.g16201.galieva.Life.Model.CellParameters;
import ru.nsu.fit.g16201.galieva.Life.Model.GameParameters;

public interface ChangeListener {
    void run(CellParameters cellParameters, GameParameters gameParameters, int width, int height, boolean isXOR);
}
