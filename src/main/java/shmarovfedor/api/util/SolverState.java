package shmarovfedor.api.util;

import java.awt.*;

public enum SolverState {

    WAITING("Waiting to start", Color.BLACK),
    INITIALIZATION("Generating model", Color.ORANGE),
    IN_PROGRESS("Calculating", Color.GREEN),
    TERMINATED("Terminated", Color.RED),
    RECALCULATION("Re-calculating", Color.CYAN);

    private final String displayName;
    private final Color color;

    SolverState(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public String displayName() {
        return displayName;
    }
}
