package elnino.enums;

public enum NivelAlerta {
    VERDE, AMARILLA, NARANJA, ROJA;

    public boolean requiereEquipo() {
        return this == NARANJA || this == ROJA;
    }
}
