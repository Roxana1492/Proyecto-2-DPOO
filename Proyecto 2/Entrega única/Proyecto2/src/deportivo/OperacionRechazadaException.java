package deportivo;

public class OperacionRechazadaException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OperacionRechazadaException(String mensaje) {
        super(mensaje);
    }
}
