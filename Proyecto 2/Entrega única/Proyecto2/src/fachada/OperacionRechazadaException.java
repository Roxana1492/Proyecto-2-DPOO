package fachada;

/**
 * Se lanza cuando la lógica rechaza una operación (cupos llenos, fecha pasada,
 * permisos, etc.). El mensaje es el que la consola le muestra al usuario.
 */
public class OperacionRechazadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperacionRechazadaException(String mensaje) {
        super(mensaje);
    }
}
