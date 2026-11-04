package elnino.excepciones;

public class LecturaInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public LecturaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
